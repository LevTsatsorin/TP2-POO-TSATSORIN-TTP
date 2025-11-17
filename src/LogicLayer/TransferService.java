package LogicLayer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para operaciones avanzadas de transferencias
 */
public class TransferService {
    private final AccountService accountService;
    private final ClientService clientService;
    private final AuthService authService;

    public TransferService(AccountService accountService, ClientService clientService, AuthService authService) {
        this.accountService = accountService;
        this.clientService = clientService;
        this.authService = authService;
    }

    /**
     * Busca cuentas de un cliente por alias que sean compatibles con la cuenta origen
     * @param targetAlias Alias del cliente destino
     * @param sourceCurrency Moneda de la cuenta origen
     * @return Lista de cuentas compatibles
     */
    public List<Account> findCompatibleAccountsByAlias(String targetAlias, Currency sourceCurrency) {
        Client targetClient = clientService.getByAlias(targetAlias);
        if (targetClient == null) {
            throw new IllegalArgumentException("No se encontró un cliente con el alias: " + targetAlias);
        }

        // Obtener todas las cuentas del cliente destino
        List<Account> allAccounts = accountService.listAccountsOfClientWithoutAuth(targetClient.getId());

        // Filtrar solo las que tienen la misma moneda
        List<Account> compatibleAccounts = new ArrayList<>();
        for (Account account : allAccounts) {
            if (account.getBaseCurrency().equals(sourceCurrency)) {
                compatibleAccounts.add(account);
            }
        }

        return compatibleAccounts;
    }

    /**
     * Realiza una transferencia a una cuenta de un tercero
     * @param sourceAccount Cuenta origen
     * @param targetAccount Cuenta destino
     * @param amount Monto
     * @param note Nota
     * @return Transacción realizada
     */
    public Transaction transferToThirdParty(Account sourceAccount, Account targetAccount,
                                           BigDecimal amount, String note) {
        // Verificar que el usuario tenga acceso a la cuenta origen
        if (!authService.hasAccessToAccount(sourceAccount)) {
            throw new SecurityException("No tiene acceso a la cuenta origen");
        }

        // Verificar que las monedas sean iguales
        if (!sourceAccount.getBaseCurrency().equals(targetAccount.getBaseCurrency())) {
            throw new IllegalArgumentException("Las cuentas deben tener la misma moneda");
        }

        // Verificar que no sea la misma cuenta
        if (sourceAccount.getId().equals(targetAccount.getId())) {
            throw new IllegalArgumentException("No se puede transferir a la misma cuenta");
        }

        return accountService.transferWithoutOwnerCheck(sourceAccount, targetAccount, amount, note);
    }

    /**
     * Verifica si un cliente existe por alias
     * @param alias Alias a buscar
     * @return true si existe
     */
    public boolean clientExists(String alias) {
        return clientService.getByAlias(alias) != null;
    }

    /**
     * Obtiene el nombre de un cliente por alias
     * @param alias Alias del cliente
     * @return Nombre del cliente o null
     */
    public String getClientNameByAlias(String alias) {
        Client client = clientService.getByAlias(alias);
        return client != null ? client.getName() : null;
    }
}
