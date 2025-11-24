package UserLayer;

import LogicLayer.*;

import javax.swing.*;
import java.math.BigDecimal;

/**
 * Clase principal - punto de entrada de la aplicación bancaria
 */
public class Main {
    private static MenuUIService menuService;
    private static AuthService authService;
    private static AccountService accountService;
    private static RegistrationService registrationService;

    public static void main(String[] args) {
        initializeSystem();
        createDemoData();
        startApplication();
    }

    /**
     * Inicializa todos los servicios del sistema
     */
    private static void initializeSystem() {
        // Crear DAO
        ClientDao clientDao = new InMemoryClientDao();
        AccountDao accountDao = new InMemoryAccountDao();
        TransactionDao transactionDao = new InMemoryTransactionDao();
        CredentialDao credentialDao = new InMemoryCredentialDao();
        SessionDao sessionDao = new InMemorySessionDao();
        RateProvider rateProvider = new RateProvider();

        // Crear servicios base
        authService = new AuthService(credentialDao, sessionDao, clientDao);
        ClientService clientService = new ClientService(clientDao);
        accountService = new AccountService(accountDao, transactionDao, authService, rateProvider);
        SummaryService summaryService = new SummaryService(accountDao, rateProvider, authService);
        registrationService = new RegistrationService(clientService, authService);
        TransferService transferService = new TransferService(accountService, clientService, authService);
        UIDataService uiDataService = new UIDataService(clientService, accountService, authService, summaryService);

        // Crear servicios de inversión
        MarketSimulator marketSimulator = new MarketSimulator();
        InvestmentService investmentService = new InvestmentService(accountDao, marketSimulator);

        // Crear servicio de menú
        menuService = new MenuUIService(authService, accountService, registrationService,
                transferService, uiDataService, rateProvider, investmentService);
    }

    /**
     * Crea datos de demostración para testing
     */
    private static void createDemoData() {
        try {
            // Crear clientes demo (PIN ya se registra automáticamente)
            Client juan = registrationService.registerNewClient("Juan Pérez", "juan", new byte[]{1, 2, 3, 4});
            Client maria = registrationService.registerNewClient("María García", "maria", new byte[]{5, 6, 7, 8});
            Client carlos = registrationService.registerNewClient("Carlos López", "carlos", new byte[]{9, 9, 9, 9});

            // Crear cuentas para Juan
            accountService.createSavingsAccount(juan, Currency.ARS, new BigDecimal("50000"));
            accountService.createSavingsAccount(juan, Currency.USD, new BigDecimal("1000"));
            accountService.createCreditAccount(juan, Currency.ARS, BigDecimal.ZERO, new BigDecimal("100000"));
            accountService.createInvestmentAccount(juan, Currency.ARS, new BigDecimal("10000"));

            // Crear cuentas para María
            accountService.createSavingsAccount(maria, Currency.USD, new BigDecimal("5000"));
            accountService.createCreditAccount(maria, Currency.EUR, new BigDecimal("500"), new BigDecimal("2000"));
            accountService.createInvestmentAccount(maria, Currency.USD, new BigDecimal("2000"));

            // Crear cuentas para Carlos
            accountService.createSavingsAccount(carlos, Currency.EUR, new BigDecimal("3000"));
            accountService.createSavingsAccount(carlos, Currency.ARS, new BigDecimal("100000"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al crear datos de demostración: " + e.getMessage(),
                    "Error de Inicialización",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Inicia el loop principal de la aplicación
     */
    private static void startApplication() {
        boolean running = true;

        while (running) {
            Session session = authService.getActiveSession();

            if (session == null) {
                // Sin sesión - mostrar menú de login
                running = menuService.showLoginMenu();
            } else {
                // Con sesión - mostrar menú principal
                menuService.showMainMenu();
            }
        }
    }
}

