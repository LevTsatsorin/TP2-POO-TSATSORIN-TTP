 package LogicLayer;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio para preparar datos formateados para la capa de presentación
 */
public class UIDataService {
    private final ClientService clientService;
    private final AccountService accountService;
    private final AuthService authService;
    private final SummaryService summaryService;

    public UIDataService(ClientService clientService, AccountService accountService,
                        AuthService authService, SummaryService summaryService) {
        this.clientService = clientService;
        this.accountService = accountService;
        this.authService = authService;
        this.summaryService = summaryService;
    }

    /**
     * Obtiene el cliente actual de la sesión activa
     */
    public Client getCurrentClient() {
        Session session = authService.getActiveSession();
        if (session == null) {
            return null;
        }
        return clientService.getById(session.getClientId());
    }

    /**
     * Obtiene las cuentas del cliente actual
     */
    public List<Account> getCurrentClientAccounts() {
        Client client = getCurrentClient();
        if (client == null) {
            return null;
        }
        return accountService.listAccountsOfClient(client);
    }

    /**
     * Formatea la información de una cuenta para mostrar
     */
    public String formatAccountInfo(Account account) {
        return account.type() + " - " +
               account.getBaseCurrency().getSymbol() +
               formatAmount(account.getBalance());
    }

    /**
     * Formatea la lista completa de cuentas del cliente actual
     */
    public String formatAccountsList() {
        List<Account> accounts = getCurrentClientAccounts();

        if (accounts == null || accounts.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder("=== MIS CUENTAS ===\n\n");
        for (int i = 0; i < accounts.size(); i++) {
            Account acc = accounts.get(i);
            sb.append((i + 1)).append(". ").append(acc.type()).append("\n");
            sb.append("   Moneda: ").append(acc.getBaseCurrency()).append("\n");
            sb.append("   Saldo: ").append(acc.getBaseCurrency().getSymbol())
              .append(formatAmount(acc.getBalance())).append("\n\n");
        }

        return sb.toString();
    }

    /**
     * Formatea el historial de transacciones de una cuenta
     */
    public String formatTransactionHistory(Account account) {
        List<Transaction> transactions = accountService.getHistory(account);

        if (transactions.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder("=== HISTORIAL DE TRANSACCIONES ===\n\n");
        sb.append("Cuenta: ").append(account.type()).append("\n");
        sb.append("Saldo actual: ").append(account.getBaseCurrency().getSymbol())
          .append(formatAmount(account.getBalance())).append("\n\n");
        sb.append("-----------------------------\n\n");

        for (Transaction tx : transactions) {
            sb.append(tx.summary()).append("\n\n");
        }

        return sb.toString();
    }

    /**
     * Formatea un número con separadores de miles (2 decimales)
     * @param amount Monto a formatear
     * @return String formateado (ej: 1,234,567.89)
     */
    public String formatAmount(BigDecimal amount) {
        return String.format("%,.2f", amount);
    }

    /**
     * Formatea una tasa de cambio con decimales suficientes para mostrar el valor correctamente
     * Usa hasta 5 decimales para tasas pequeñas (como 0.00061)
     * @param rate Tasa de cambio a formatear
     * @return String formateado
     */
    public String formatExchangeRate(BigDecimal rate) {
        // Si el valor es menor que 0.01, usar hasta 5 decimales
        if (rate.compareTo(new BigDecimal("0.01")) < 0) {
            return String.format("%,.5f", rate);
        }
        // Si el valor es mayor o igual a 0.01, usar 2 decimales
        return String.format("%,.2f", rate);
    }

    /**
     * Formatea el resumen de patrimonio del cliente actual en ARS
     * Muestra activos y deudas por separado
     */
    public String formatSummary() {
        Session session = authService.getActiveSession();
        if (session == null) {
            return null;
        }

        Client client = getCurrentClient();
        BigDecimal[] assetsAndDebts = summaryService.calculateAssetsAndDebtsInARS(session.getClientId());
        BigDecimal totalAssets = assetsAndDebts[0];
        BigDecimal totalDebts = assetsAndDebts[1];
        BigDecimal netWorth = totalAssets.subtract(totalDebts);

        StringBuilder sb = new StringBuilder("=== RESUMEN DE PATRIMONIO ===\n\n");
        sb.append("Cliente: ").append(client.getName()).append("\n");
        sb.append("(Todas las cuentas convertidas a ARS)\n\n");

        sb.append("-----------------------------\n");
        sb.append(" ACTIVOS:\n");
        sb.append("   ").append(Currency.ARS.getSymbol()).append(formatAmount(totalAssets)).append("\n\n");

        if (totalDebts.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("-----------------------------\n");
            sb.append(" DEUDAS (Créditos):\n");
            sb.append("   ").append(Currency.ARS.getSymbol()).append(formatAmount(totalDebts)).append("\n\n");
        }

        sb.append("-----------------------------\n");
        sb.append(" PATRIMONIO NETO:\n");
        sb.append("   ").append(Currency.ARS.getSymbol()).append(formatAmount(netWorth)).append("\n");
        sb.append("-----------------------------");

        return sb.toString();
    }

    /**
     * Formatea la lista de clientes disponibles
     */
    public String formatAvailableClients() {
        List<Client> clients = clientService.listClients();
        StringBuilder sb = new StringBuilder("Clientes Disponibles:\n\n");

        for (Client client : clients) {
            sb.append("• ").append(client.getName())
              .append(" (@").append(client.getAlias()).append(")\n");
        }

        sb.append("\n--- Datos de Demostración ---\n");
        sb.append("juan - PIN: 1234\n");
        sb.append("maria - PIN: 5678\n");
        sb.append("carlos - PIN: 9999\n");

        return sb.toString();
    }

    /**
     * Genera opciones de cuentas para un selector (array de strings)
     * Incluye índice para identificación única
     */
    public String[] getAccountOptions() {
        List<Account> accounts = getCurrentClientAccounts();
        if (accounts == null || accounts.isEmpty()) {
            return new String[0];
        }

        String[] options = new String[accounts.size()];
        for (int i = 0; i < accounts.size(); i++) {
            options[i] = "[" + (i + 1) + "] " + formatAccountInfo(accounts.get(i));
        }
        return options;
    }

    /**
     * Busca una cuenta por su representación en string (con índice)
     * Extrae el índice del formato "[1] Cuenta..."
     */
    public Account findAccountByDisplayString(String displayString) {
        if (displayString == null || !displayString.startsWith("[")) {
            return null;
        }

        try {
            int endBracket = displayString.indexOf("]");
            if (endBracket == -1) {
                return null;
            }

            String indexStr = displayString.substring(1, endBracket);
            int index = Integer.parseInt(indexStr) - 1;

            List<Account> accounts = getCurrentClientAccounts();
            if (accounts == null || index < 0 || index >= accounts.size()) {
                return null;
            }

            return accounts.get(index);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Verifica si el cliente actual tiene cuentas
     */
    public boolean hasAccounts() {
        List<Account> accounts = getCurrentClientAccounts();
        return accounts != null && !accounts.isEmpty();
    }

    /**
     * Obtiene el nombre del cliente actual
     */
    public String getCurrentClientName() {
        Client client = getCurrentClient();
        return client != null ? client.getName() : null;
    }
}


