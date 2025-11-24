package LogicLayer;

import javax.swing.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio que maneja toda la lógica de menús y diálogos de usuario
 * Separa la lógica de presentación del Main.java
 */
public class MenuUIService {
    private final AuthService authService;
    private final AccountService accountService;
    private final RegistrationService registrationService;
    private final TransferService transferService;
    private final UIDataService uiDataService;
    private final RateProvider rateProvider;
    private final InvestmentService investmentService;

    public MenuUIService(AuthService authService, AccountService accountService,
                        RegistrationService registrationService, TransferService transferService,
                        UIDataService uiDataService, RateProvider rateProvider,
                        InvestmentService investmentService) {
        this.authService = authService;
        this.accountService = accountService;
        this.registrationService = registrationService;
        this.transferService = transferService;
        this.uiDataService = uiDataService;
        this.rateProvider = rateProvider;
        this.investmentService = investmentService;
    }

    /**
     * Muestra el menú de login y ejecuta la acción seleccionada
     * @return false si el usuario quiere salir
     */
    public boolean showLoginMenu() {
        String[] options = {"Iniciar Sesión", "Registrarse", "Ver Clientes Disponibles", "Salir"};
        int choice = JOptionPane.showOptionDialog(null,
                "Bienvenido al Sistema Bancario\n\nSeleccione una opción:",
                "Sistema Bancario - Login",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0:
                handleLogin();
                return true;
            case 1:
                handleRegistration();
                return true;
            case 2:
                showAvailableClients();
                return true;
            case 3:
            case JOptionPane.CLOSED_OPTION:
                return false;
            default:
                return true;
        }
    }

    /**
     * Muestra el menú principal y ejecuta la acción seleccionada
     */
    public void showMainMenu() {
        String clientName = uiDataService.getCurrentClientName();

        String[] options = {
                "Ver Mis Cuentas",
                "Crear Nueva Cuenta",
                "Depositar",
                "Retirar",
                "Transferir",
                "Ver Historial",
                "Resumen Total",
                "Inversiones",
                "Cerrar Sesión"
        };

        int choice = JOptionPane.showOptionDialog(null,
                "Usuario: " + clientName + "\n" +
                "Día actual: " + SimulatedClock.getCurrentDay() + "\n\n" +
                "Seleccione una operación:",
                "Sistema Bancario - Menú Principal",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0:
                showAccounts();
                break;
            case 1:
                createNewAccount();
                break;
            case 2:
                handleDeposit();
                break;
            case 3:
                handleWithdraw();
                break;
            case 4:
                handleTransfer();
                break;
            case 5:
                showTransactionHistory();
                break;
            case 6:
                showSummary();
                break;
            case 7:
                showInvestmentMenu();
                break;
            case 8:
            case JOptionPane.CLOSED_OPTION:
                authService.logout();
                JOptionPane.showMessageDialog(null,
                        "Sesión cerrada exitosamente",
                        "Cerrar Sesión",
                        JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }

    /**
     * Maneja el proceso de login
     */
    private void handleLogin() {
        String alias = null;
        boolean loginSuccessful = false;

        // Solicitar alias
        while (alias == null) {
            alias = JOptionPane.showInputDialog(null,
                    "Ingrese su alias de usuario:",
                    "Iniciar Sesión",
                    JOptionPane.QUESTION_MESSAGE);

            if (alias == null) return;

            if (alias.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "El alias no puede estar vacío",
                        "Error de Validación",
                        JOptionPane.ERROR_MESSAGE);
                alias = null;
            }
        }

        // Solicitar PIN y autenticar
        while (!loginSuccessful) {
            String pinStr = JOptionPane.showInputDialog(null,
                    "Usuario: @" + alias + "\n\nIngrese su PIN de 4 dígitos:",
                    "Iniciar Sesión",
                    JOptionPane.QUESTION_MESSAGE);

            if (pinStr == null) return;

            if (pinStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "El PIN no puede estar vacío",
                        "Error de Validación",
                        JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (!registrationService.isValidPinFormat(pinStr)) {
                JOptionPane.showMessageDialog(null,
                        "El PIN debe ser exactamente 4 dígitos numéricos",
                        "Error de Validación",
                        JOptionPane.ERROR_MESSAGE);
                continue;
            }

            try {
                byte[] pinDigits = registrationService.pinStringToBytes(pinStr);
                authService.loginWithPin(alias, pinDigits);
                String clientName = uiDataService.getCurrentClientName();

                JOptionPane.showMessageDialog(null,
                        "¡Bienvenido/a, " + clientName + "!",
                        "Inicio de Sesión Exitoso",
                        JOptionPane.INFORMATION_MESSAGE);
                loginSuccessful = true;

            } catch (SecurityException e) {
                int retry = JOptionPane.showConfirmDialog(null,
                        "Credenciales incorrectas.\n\n¿Desea intentar con otro PIN?",
                        "Error de Autenticación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE);

                if (retry != JOptionPane.YES_OPTION) return;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }

    /**
     * Maneja el proceso de registro con validación por pasos
     */
    private void handleRegistration() {
        String fullName = requestFullName();
        if (fullName == null) return;

        String alias = requestAlias(fullName);
        if (alias == null) return;

        String pinStr = requestPinWithConfirmation(fullName, alias);
        if (pinStr == null) return;

        try {
            byte[] pinDigits = registrationService.pinStringToBytes(pinStr);
            Client newClient = registrationService.registerNewClient(fullName, alias, pinDigits);

            JOptionPane.showMessageDialog(null,
                    "¡Registro exitoso!\n\n" +
                            "Nombre: " + newClient.getName() + "\n" +
                            "Alias: @" + newClient.getAlias() + "\n\n" +
                            "Puede iniciar sesión ahora.",
                    "Registro Completado",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al registrar: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Solicita el nombre completo con validación
     */
    private String requestFullName() {
        while (true) {
            String fullName = JOptionPane.showInputDialog(null,
                    "Ingrese su nombre completo:",
                    "Registro de Nuevo Usuario",
                    JOptionPane.QUESTION_MESSAGE);

            if (fullName == null) return null;

            if (!fullName.trim().isEmpty()) {
                return fullName;
            }

            JOptionPane.showMessageDialog(null,
                    "El nombre no puede estar vacío",
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Solicita el alias con validación y verificación de duplicados
     */
    private String requestAlias(String fullName) {
        while (true) {
            String alias = JOptionPane.showInputDialog(null,
                    "Nombre: " + fullName + "\n\n" +
                            "Ingrese un alias único (3-20 caracteres alfanuméricos):",
                    "Registro de Nuevo Usuario",
                    JOptionPane.QUESTION_MESSAGE);

            if (alias == null) return null;

            if (alias.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "El alias no puede estar vacío",
                        "Error de Validación",
                        JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (!alias.matches("^[a-zA-Z0-9_]{3,20}$")) {
                JOptionPane.showMessageDialog(null,
                        "El alias debe tener entre 3 y 20 caracteres alfanuméricos",
                        "Error de Validación",
                        JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (transferService.clientExists(alias)) {
                int retry = JOptionPane.showConfirmDialog(null,
                        "El alias '@" + alias + "' ya está en uso.\n\n" +
                                "¿Desea intentar con otro alias?",
                        "Alias Duplicado",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (retry != JOptionPane.YES_OPTION) return null;
                continue;
            }

            return alias;
        }
    }

    /**
     * Solicita PIN con confirmación
     */
    private String requestPinWithConfirmation(String fullName, String alias) {
        while (true) {
            String pinStr = JOptionPane.showInputDialog(null,
                    "Nombre: " + fullName + "\n" +
                            "Alias: @" + alias + "\n\n" +
                            "Ingrese un PIN de 4 dígitos:",
                    "Registro de Nuevo Usuario",
                    JOptionPane.QUESTION_MESSAGE);

            if (pinStr == null) return null;

            if (pinStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "El PIN no puede estar vacío",
                        "Error de Validación",
                        JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (!registrationService.isValidPinFormat(pinStr)) {
                JOptionPane.showMessageDialog(null,
                        "El PIN debe ser exactamente 4 dígitos numéricos",
                        "Error de Validación",
                        JOptionPane.ERROR_MESSAGE);
                continue;
            }

            String pinConfirm = JOptionPane.showInputDialog(null,
                    "Confirme su PIN:",
                    "Registro de Nuevo Usuario",
                    JOptionPane.QUESTION_MESSAGE);

            if (pinConfirm == null) {
                int retry = JOptionPane.showConfirmDialog(null,
                        "Canceló la confirmación del PIN.\n\n¿Desea intentar nuevamente?",
                        "Confirmación Cancelada",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (retry != JOptionPane.YES_OPTION) return null;
                continue;
            }

            if (!pinStr.equals(pinConfirm)) {
                JOptionPane.showMessageDialog(null,
                        "Los PINs no coinciden. Por favor, intente nuevamente.",
                        "Error de Confirmación",
                        JOptionPane.ERROR_MESSAGE);
                continue;
            }

            return pinStr;
        }
    }

    /**
     * Muestra la lista de clientes disponibles
     */
    private void showAvailableClients() {
        String clientsList = uiDataService.formatAvailableClients();
        JOptionPane.showMessageDialog(null,
                clientsList,
                "Clientes del Sistema",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra las cuentas del cliente actual
     */
    private void showAccounts() {
        if (!uiDataService.hasAccounts()) {
            JOptionPane.showMessageDialog(null,
                    "No tiene cuentas registradas.\nCree una nueva cuenta desde el menú principal.",
                    "Sin Cuentas",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String accountsList = uiDataService.formatAccountsList();
        JOptionPane.showMessageDialog(null,
                accountsList,
                "Mis Cuentas",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Crea una nueva cuenta
     */
    private void createNewAccount() {
        Client client = uiDataService.getCurrentClient();

        String[] accountTypes = {"Cuenta de Ahorro", "Cuenta de Crédito", "Cuenta de Inversión"};
        int typeChoice = JOptionPane.showOptionDialog(null,
                "Seleccione el tipo de cuenta:",
                "Nueva Cuenta",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                accountTypes,
                accountTypes[0]);

        if (typeChoice == JOptionPane.CLOSED_OPTION) return;

        Currency selectedCurrency = (Currency) JOptionPane.showInputDialog(null,
                "Seleccione la moneda:",
                "Nueva Cuenta",
                JOptionPane.QUESTION_MESSAGE,
                null,
                Currency.values(),
                Currency.values()[0]);

        if (selectedCurrency == null) return;

        String balanceStr = JOptionPane.showInputDialog(null,
                "Ingrese el saldo inicial:",
                "Nueva Cuenta",
                JOptionPane.QUESTION_MESSAGE);

        if (balanceStr == null) return;

        try {
            BigDecimal initialBalance = new BigDecimal(balanceStr);

            switch (typeChoice) {
                case 0: // Cuenta de Ahorro
                    accountService.createSavingsAccount(client, selectedCurrency, initialBalance);
                    JOptionPane.showMessageDialog(null,
                            "Cuenta de ahorro creada exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    break;

                case 1: // Cuenta de Crédito
                    String limitStr = JOptionPane.showInputDialog(null,
                            "Ingrese el límite de crédito:",
                            "Nueva Cuenta",
                            JOptionPane.QUESTION_MESSAGE);

                    if (limitStr == null) return;

                    BigDecimal creditLimit = new BigDecimal(limitStr);
                    accountService.createCreditAccount(client, selectedCurrency, initialBalance, creditLimit);
                    JOptionPane.showMessageDialog(null,
                            "Cuenta de crédito creada exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    break;

                case 2: // Cuenta de Inversión
                    accountService.createInvestmentAccount(client, selectedCurrency, initialBalance);
                    JOptionPane.showMessageDialog(null,
                            "Cuenta de inversión creada exitosamente\n\n" +
                            "El rendimiento se calculará diariamente según el mercado.\n",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    break;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                    "Formato de número inválido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al crear cuenta: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Maneja depósito en una cuenta
     */
    private void handleDeposit() {
        Account account = selectAccount("Seleccione la cuenta para depositar:");
        if (account == null) return;

        String amountStr = JOptionPane.showInputDialog(null,
                "Cuenta: " + account.type() + "\n" +
                        "Saldo actual: " + account.getBaseCurrency().getSymbol() +
                        uiDataService.formatAmount(account.getBalance()) + "\n\n" +
                        "Ingrese el monto a depositar:",
                "Depositar",
                JOptionPane.QUESTION_MESSAGE);

        if (amountStr == null || amountStr.trim().isEmpty()) return;

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            String note = JOptionPane.showInputDialog(null,
                    "Ingrese una nota (opcional):",
                    "Depositar",
                    JOptionPane.QUESTION_MESSAGE);

            Transaction tx = accountService.deposit(account, amount, note);

            if (tx.isSuccessful()) {
                JOptionPane.showMessageDialog(null,
                        "Depósito realizado exitosamente\n\n" +
                                "Monto: " + account.getBaseCurrency().getSymbol() +
                                uiDataService.formatAmount(amount) + "\n" +
                                "Nuevo saldo: " + account.getBaseCurrency().getSymbol() +
                                uiDataService.formatAmount(account.getBalance()),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "El depósito falló: " + tx.getNote(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                    "Formato de número inválido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Maneja retiro de una cuenta
     */
    private void handleWithdraw() {
        Account account = selectAccount("Seleccione la cuenta para retirar:");
        if (account == null) return;

        String amountStr = JOptionPane.showInputDialog(null,
                "Cuenta: " + account.type() + "\n" +
                        "Saldo actual: " + account.getBaseCurrency().getSymbol() +
                        uiDataService.formatAmount(account.getBalance()) + "\n\n" +
                        "Ingrese el monto a retirar:",
                "Retirar",
                JOptionPane.QUESTION_MESSAGE);

        if (amountStr == null || amountStr.trim().isEmpty()) return;

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            String note = JOptionPane.showInputDialog(null,
                    "Ingrese una nota (opcional):",
                    "Retirar",
                    JOptionPane.QUESTION_MESSAGE);

            Transaction tx = accountService.withdraw(account, amount, note);

            if (tx.isSuccessful()) {
                JOptionPane.showMessageDialog(null,
                        "Retiro realizado exitosamente\n\n" +
                                "Monto: " + account.getBaseCurrency().getSymbol() +
                                uiDataService.formatAmount(amount) + "\n" +
                                "Nuevo saldo: " + account.getBaseCurrency().getSymbol() +
                                uiDataService.formatAmount(account.getBalance()),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "El retiro falló: " + tx.getNote(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Maneja transferencias (propias o a terceros)
     */
    private void handleTransfer() {
        Account source = selectAccount("Seleccione la cuenta origen:");
        if (source == null) return;

        String[] transferOptions = {"A mi propia cuenta", "A un tercero"};
        int transferType = JOptionPane.showOptionDialog(null,
                "¿A quién desea transferir?",
                "Tipo de Transferencia",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                transferOptions,
                transferOptions[0]);

        if (transferType == JOptionPane.CLOSED_OPTION) return;

        Account target = (transferType == 0)
                ? handleOwnAccountTransfer(source)
                : handleThirdPartyTransfer(source);

        if (target == null) return;

        executeTransfer(source, target, transferType);
    }

    /**
     * Maneja selección de cuenta propia para transferencia
     */
    private Account handleOwnAccountTransfer(Account source) {
        // Obtener todas las cuentas del usuario EXCEPTO la cuenta origen
        List<Account> allAccounts = uiDataService.getCurrentClientAccounts();
        List<Account> availableAccounts = new ArrayList<>();

        for (Account acc : allAccounts) {
            if (!acc.getId().equals(source.getId())) {
                availableAccounts.add(acc);
            }
        }

        if (availableAccounts.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "No tiene otras cuentas disponibles para transferir.\n" +
                            "Cree una nueva cuenta primero.",
                    "Sin Cuentas Destino",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        // Crear opciones de cuentas
        String[] accountOptions = new String[availableAccounts.size()];
        for (int i = 0; i < availableAccounts.size(); i++) {
            Account acc = availableAccounts.get(i);
            accountOptions[i] = acc.type() + " - " +
                               acc.getBaseCurrency().getSymbol() + acc.getBalance();
        }

        String selected = (String) JOptionPane.showInputDialog(null,
                "Seleccione su cuenta destino:\n\n" +
                        "(Se permite conversión entre diferentes monedas)",
                "Seleccionar Cuenta Destino",
                JOptionPane.QUESTION_MESSAGE,
                null,
                accountOptions,
                accountOptions[0]);

        if (selected == null) return null;

        // Encontrar la cuenta seleccionada
        for (int i = 0; i < accountOptions.length; i++) {
            if (accountOptions[i].equals(selected)) {
                return availableAccounts.get(i);
            }
        }

        return null;
    }

    /**
     * Maneja selección de cuenta de tercero para transferencia
     */
    private Account handleThirdPartyTransfer(Account source) {
        String targetAlias = JOptionPane.showInputDialog(null,
                "Ingrese el alias del destinatario:",
                "Transferencia a Tercero",
                JOptionPane.QUESTION_MESSAGE);

        if (targetAlias == null || targetAlias.trim().isEmpty()) return null;

        try {
            if (!transferService.clientExists(targetAlias)) {
                JOptionPane.showMessageDialog(null,
                        "No existe un cliente con el alias: " + targetAlias,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }

            String targetClientName = transferService.getClientNameByAlias(targetAlias);
            List<Account> compatibleAccounts = transferService.findCompatibleAccountsByAlias(
                    targetAlias, source.getBaseCurrency());

            if (compatibleAccounts.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "El usuario " + targetClientName + " (@" + targetAlias + ") " +
                                "no tiene cuentas en " + source.getBaseCurrency(),
                        "Sin Cuentas Compatibles",
                        JOptionPane.WARNING_MESSAGE);
                return null;
            }

            String[] accountOptions = new String[compatibleAccounts.size()];
            for (int i = 0; i < compatibleAccounts.size(); i++) {
                Account acc = compatibleAccounts.get(i);
                accountOptions[i] = acc.type() + " - " + acc.getBaseCurrency().getSymbol() + acc.getBalance();
            }

            String selected = (String) JOptionPane.showInputDialog(null,
                    "Destinatario: " + targetClientName + " (@" + targetAlias + ")\n\n" +
                            "Seleccione la cuenta destino:",
                    "Seleccionar Cuenta",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    accountOptions,
                    accountOptions[0]);

            if (selected == null) return null;

            for (int i = 0; i < accountOptions.length; i++) {
                if (accountOptions[i].equals(selected)) {
                    return compatibleAccounts.get(i);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return null;
    }

    /**
     * Ejecuta la transferencia (con o sin conversión de moneda)
     */
    private void executeTransfer(Account source, Account target, int transferType) {
        boolean isDifferentCurrency = !source.getBaseCurrency().equals(target.getBaseCurrency());

        String promptMessage = "Origen: " + source.type() + " (" +
                              source.getBaseCurrency().getSymbol() +
                              uiDataService.formatAmount(source.getBalance()) + ")\n" +
                              "Destino: " + target.type() + " (" +
                              target.getBaseCurrency().getSymbol() +
                              uiDataService.formatAmount(target.getBalance()) + ")\n";

        if (isDifferentCurrency && transferType == 0) {
            // Obtener y mostrar la tasa de cambio
            BigDecimal exchangeRate = rateProvider.getRate(source.getBaseCurrency(), target.getBaseCurrency());
            promptMessage += "\n CONVERSIÓN DE MONEDA\n" +
                           "Tasa de cambio: 1 " + source.getBaseCurrency() + " = " +
                           uiDataService.formatExchangeRate(exchangeRate) + " " + target.getBaseCurrency() + "\n";
        }

        promptMessage += "\nIngrese el monto a transferir (en " + source.getBaseCurrency() + "):";

        String amountStr = JOptionPane.showInputDialog(null,
                promptMessage,
                "Transferir",
                JOptionPane.QUESTION_MESSAGE);

        if (amountStr == null || amountStr.trim().isEmpty()) return;

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            String note = JOptionPane.showInputDialog(null,
                    "Ingrese una nota (opcional):",
                    "Transferir",
                    JOptionPane.QUESTION_MESSAGE);

            Transaction tx = (transferType == 0)
                    ? accountService.transfer(source, target, amount, note)
                    : transferService.transferToThirdParty(source, target, amount, note);

            if (tx.isSuccessful()) {
                String successMessage = "Transferencia realizada exitosamente\n\n" +
                        "Monto debitado: " + source.getBaseCurrency().getSymbol() +
                        uiDataService.formatAmount(amount) + "\n";

                if (isDifferentCurrency && transferType == 0) {
                    // Calcular y mostrar el monto convertido
                    BigDecimal exchangeRate = rateProvider.getRate(source.getBaseCurrency(), target.getBaseCurrency());
                    BigDecimal convertedAmount = rateProvider.convert(amount, source.getBaseCurrency(), target.getBaseCurrency());

                    successMessage += "\n CONVERSIÓN APLICADA:\n" +
                                    "Tasa: 1 " + source.getBaseCurrency() + " = " +
                                    uiDataService.formatExchangeRate(exchangeRate) + " " + target.getBaseCurrency() + "\n" +
                                    "Monto acreditado: " + target.getBaseCurrency().getSymbol() +
                                    uiDataService.formatAmount(convertedAmount) + "\n";
                }

                successMessage += "\nNuevo saldo origen: " + source.getBaseCurrency().getSymbol() +
                                uiDataService.formatAmount(source.getBalance()) + "\n" +
                                "Nuevo saldo destino: " + target.getBaseCurrency().getSymbol() +
                                uiDataService.formatAmount(target.getBalance());

                JOptionPane.showMessageDialog(null,
                        successMessage,
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "La transferencia falló: " + tx.getNote(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Muestra historial de transacciones
     */
    private void showTransactionHistory() {
        Account account = selectAccount("Seleccione la cuenta para ver el historial:");
        if (account == null) return;

        String history = uiDataService.formatTransactionHistory(account);
        if (history == null) {
            JOptionPane.showMessageDialog(null,
                    "No hay transacciones para esta cuenta",
                    "Historial de Transacciones",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTextArea textArea = new JTextArea(history);
        textArea.setEditable(false);
        textArea.setRows(20);
        textArea.setColumns(60);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(null,
                scrollPane,
                "Historial de Transacciones",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra resumen de patrimonio
     */
    private void showSummary() {
        String summary = uiDataService.formatSummary();
        JOptionPane.showMessageDialog(null,
                summary,
                "Resumen de Patrimonio",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Permite seleccionar una cuenta
     */
    private Account selectAccount(String message) {
        if (!uiDataService.hasAccounts()) {
            JOptionPane.showMessageDialog(null,
                    "No tiene cuentas registradas.\nCree una nueva cuenta primero.",
                    "Sin Cuentas",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        String[] accountOptions = uiDataService.getAccountOptions();

        String selected = (String) JOptionPane.showInputDialog(null,
                message,
                "Seleccionar Cuenta",
                JOptionPane.QUESTION_MESSAGE,
                null,
                accountOptions,
                accountOptions[0]);

        if (selected == null) return null;

        return uiDataService.findAccountByDisplayString(selected);
    }

    /**
     * Muestra el menú de inversiones
     */
    private void showInvestmentMenu() {
        if (!uiDataService.hasAccounts()) {
            JOptionPane.showMessageDialog(null,
                    "No tiene cuentas registradas.",
                    "No tiene cuentas registradas.\nCree una nueva cuenta primero.",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Obtener cuentas de inversión del cliente actual
        List<InvestmentAccount> investmentAccounts = uiDataService.getCurrentClientInvestmentAccounts();

        if (investmentAccounts.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "No tiene cuentas de inversión.\n" +
                    "Cree una cuenta de inversión desde el menú principal.",
                    "Sin Cuentas de Inversión",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Menú interactivo de inversiones
        boolean inInvestmentMenu = true;
        while (inInvestmentMenu) {
            String[] options = {
                    "Ver Estado Actual",
                    "Avanzar 1 Día",
                    "Ver Historial Completo",
                    "Volver al Menú Principal"
            };

            int choice = JOptionPane.showOptionDialog(null,
                    "=== INVERSIONES ===\n\n" +
                    "Día actual: " + SimulatedClock.getCurrentDay() + "\n\n" +
                    "Seleccione una opción:",
                    "Menú de Inversiones",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);

            switch (choice) {
                case 0:
                    showCurrentInvestmentStatus(investmentAccounts);
                    break;
                case 1:
                    advanceOneDayAndShowResult(investmentAccounts);
                    break;
                case 2:
                    showFullInvestmentHistory(investmentAccounts);
                    break;
                case 3:
                case JOptionPane.CLOSED_OPTION:
                    inInvestmentMenu = false;
                    break;
            }
        }
    }

    /**
     * Muestra el estado actual de las inversiones
     */
    private void showCurrentInvestmentStatus(List<InvestmentAccount> investmentAccounts) {
        StringBuilder info = new StringBuilder();
        info.append("=== ESTADO ACTUAL DE INVERSIONES ===\n\n");
        info.append("Día actual: ").append(SimulatedClock.getCurrentDay()).append("\n\n");

        for (InvestmentAccount acc : investmentAccounts) {
            info.append("Cuenta de Inversión - ").append(acc.getBaseCurrency()).append("\n");
            info.append("Saldo actual: ").append(acc.getBaseCurrency().getSymbol())
                .append(uiDataService.formatAmount(acc.getBalance())).append("\n");

            List<InvestmentHistory> history = acc.getHistory();
            if (!history.isEmpty()) {
                InvestmentHistory latest = history.get(history.size() - 1);
                info.append("Última actualización: ").append(latest.getDate()).append("\n");
                info.append("Última tasa: ").append(uiDataService.formatRate(latest.getDailyRate())).append("\n");
                info.append("Último rendimiento: ").append(acc.getBaseCurrency().getSymbol())
                    .append(uiDataService.formatAmount(latest.getProfit())).append("\n");
            } else {
                info.append("Sin movimientos aún\n");
            }
            info.append("\n");
        }

        JOptionPane.showMessageDialog(null,
                info.toString(),
                "Estado Actual",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Avanza un día y muestra los resultados
     */
    private void advanceOneDayAndShowResult(List<InvestmentAccount> investmentAccounts) {
        LocalDate previousDay = SimulatedClock.getCurrentDay();
        SimulatedClock.advanceOneDay();
        LocalDate currentDay = SimulatedClock.getCurrentDay();

        // Generar tasa del día
        BigDecimal dailyRate = investmentService.getMarketSimulator().generateDailyRate();

        // Actualizar TODAS las cuentas de inversión del sistema
        investmentService.updateAllInvestmentAccountsInSystem(currentDay, dailyRate);

        StringBuilder result = new StringBuilder();
        result.append("=== RESULTADOS DEL DÍA ===\n\n");
        result.append("Día anterior: ").append(previousDay).append("\n");
        result.append("Día actual: ").append(currentDay).append("\n\n");
        result.append("Tasa del mercado: ").append(uiDataService.formatRate(dailyRate));

        if (dailyRate.compareTo(BigDecimal.ZERO) > 0) {
            result.append(" 📈 ALCISTA\n\n");
        } else if (dailyRate.compareTo(BigDecimal.ZERO) < 0) {
            result.append(" 📉 BAJISTA\n\n");
        } else {
            result.append(" ➡️ ESTABLE\n\n");
        }

        result.append("-------------------------------------\n");
        result.append("SUS CUENTAS DE INVERSIÓN:\n");
        result.append("-------------------------------------\n\n");

        // Mostrar resultado solo para las cuentas del usuario actual
        for (InvestmentAccount acc : investmentAccounts) {
            // Obtener el historial para ver el último cambio
            List<InvestmentHistory> history = acc.getHistory();
            if (!history.isEmpty()) {
                InvestmentHistory latest = history.get(history.size() - 1);
                BigDecimal profit = latest.getProfit();

                result.append("Cuenta ").append(acc.getBaseCurrency()).append(":\n");
                result.append("  Saldo anterior: ").append(acc.getBaseCurrency().getSymbol())
                      .append(uiDataService.formatAmount(latest.getBalanceBefore())).append("\n");
                result.append("  Saldo nuevo: ").append(acc.getBaseCurrency().getSymbol())
                      .append(uiDataService.formatAmount(latest.getBalanceAfter())).append("\n");
                result.append("  Rendimiento: ").append(acc.getBaseCurrency().getSymbol())
                      .append(uiDataService.formatAmount(profit));

                result.append("\n");
            }
        }

        JOptionPane.showMessageDialog(null,
                result.toString(),
                "Día Simulado",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra el historial completo de todas las simulaciones
     */
    private void showFullInvestmentHistory(List<InvestmentAccount> investmentAccounts) {
        String history = uiDataService.formatInvestmentHistory(investmentAccounts);

        if (history == null || history.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "No hay historial de inversiones",
                    "Historial de Inversiones",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTextArea textArea = new JTextArea(history);
        textArea.setEditable(false);
        textArea.setRows(20);
        textArea.setColumns(60);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(null,
                scrollPane,
                "Historial de Inversiones",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
