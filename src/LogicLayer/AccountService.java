package LogicLayer;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Servicio para gestión de cuentas y transacciones
 */
public class AccountService {
    private final AccountDao accountDao;
    private final TransactionDao transactionDao;
    private final AuthService authService;
    private final RateProvider rateProvider;

    public AccountService(AccountDao accountDao, TransactionDao transactionDao,
                         AuthService authService, RateProvider rateProvider) {
        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
        this.authService = authService;
        this.rateProvider = rateProvider;
    }

    /**
     * Crea una cuenta de ahorro
     */
    public SavingsAccount createSavingsAccount(Client owner, Currency baseCurrency, BigDecimal initialBalance) {
        validateAccountCreation(owner, baseCurrency, initialBalance);

        SavingsAccount account = new SavingsAccount(owner, baseCurrency, initialBalance);
        accountDao.save(account);
        return account;
    }

    /**
     * Crea una cuenta de crédito
     */
    public CreditAccount createCreditAccount(Client owner, Currency baseCurrency,
                                            BigDecimal initialBalance, BigDecimal creditLimit) {
        validateAccountCreation(owner, baseCurrency, initialBalance);

        if (creditLimit == null || creditLimit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El límite de crédito debe ser positivo");
        }

        CreditAccount account = new CreditAccount(owner, baseCurrency, initialBalance, creditLimit);
        accountDao.save(account);
        return account;
    }

    /**
     * Crea una cuenta de inversión
     */
    public InvestmentAccount createInvestmentAccount(Client owner, Currency baseCurrency, BigDecimal initialBalance) {
        validateAccountCreation(owner, baseCurrency, initialBalance);

        InvestmentAccount account = new InvestmentAccount(owner, baseCurrency, initialBalance);
        accountDao.save(account);
        return account;
    }

    /**
     * Realiza un depósito en una cuenta
     */
    public Transaction deposit(Account target, BigDecimal amount, String note) {
        if (!authService.hasAccessToAccount(target)) {
            throw new SecurityException("No tiene acceso a esta cuenta");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo");
        }

        Transaction tx;
        try {
            target.credit(amount);
            accountDao.update(target);
            tx = new DepositTransaction(TransactionStatus.SETTLED, amount, target.getBaseCurrency(), note, target);
        } catch (Exception e) {
            tx = new DepositTransaction(TransactionStatus.FAILED, amount, target.getBaseCurrency(),
                                       "Error: " + e.getMessage(), target);
        }

        transactionDao.save(tx);
        return tx;
    }

    /**
     * Realiza un retiro de una cuenta
     */
    public Transaction withdraw(Account source, BigDecimal amount, String note) {
        if (!authService.hasAccessToAccount(source)) {
            throw new SecurityException("No tiene acceso a esta cuenta");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo");
        }

        Transaction tx;
        try {
            source.debit(amount);
            accountDao.update(source);
            tx = new WithdrawTransaction(TransactionStatus.SETTLED, amount, source.getBaseCurrency(), note, source);
        } catch (Exception e) {
            tx = new WithdrawTransaction(TransactionStatus.FAILED, amount, source.getBaseCurrency(),
                                        "Error: " + e.getMessage(), source);
        }

        transactionDao.save(tx);
        return tx;
    }

    /**
     * Realiza una transferencia entre cuentas (con conversión de moneda si es necesario)
     */
    public Transaction transfer(Account source, Account target, BigDecimal amount, String note) {
        if (!authService.hasAccessToAccount(source)) {
            throw new SecurityException("No tiene acceso a la cuenta origen");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo");
        }

        if (source.getId().equals(target.getId())) {
            throw new IllegalArgumentException("No se puede transferir a la misma cuenta");
        }

        Transaction tx;
        try {
            // Debitar de la cuenta origen
            source.debit(amount);
            accountDao.update(source);

            // Calcular el monto a acreditar (con conversión si las monedas son diferentes)
            BigDecimal amountToCredit;
            if (source.getBaseCurrency().equals(target.getBaseCurrency())) {
                // Misma moneda - sin conversión
                amountToCredit = amount;
            } else {
                // Diferente moneda - aplicar conversión
                amountToCredit = rateProvider.convert(amount,
                                                     source.getBaseCurrency(),
                                                     target.getBaseCurrency());
            }

            // Acreditar a la cuenta destino
            target.credit(amountToCredit);
            accountDao.update(target);

            tx = new TransferTransaction(TransactionStatus.SETTLED, amount, source.getBaseCurrency(),
                                        note, source, target);
        } catch (Exception e) {
            tx = new TransferTransaction(TransactionStatus.FAILED, amount, source.getBaseCurrency(),
                                        "Error: " + e.getMessage(), source, target);
        }

        transactionDao.save(tx);
        return tx;
    }

    /**
     * Realiza una transferencia entre cuentas
     * Usada para transferencias a terceros
     */
    public Transaction transferWithoutOwnerCheck(Account source, Account target, BigDecimal amount, String note) {
        if (!authService.hasAccessToAccount(source)) {
            throw new SecurityException("No tiene acceso a la cuenta origen");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo");
        }

        if (source.getId().equals(target.getId())) {
            throw new IllegalArgumentException("No se puede transferir a la misma cuenta");
        }

        if (!source.getBaseCurrency().equals(target.getBaseCurrency())) {
            throw new IllegalArgumentException("Las cuentas deben tener la misma moneda");
        }

        Transaction tx;
        try {
            // Debitar de la cuenta origen
            source.debit(amount);
            accountDao.update(source);

            // Acreditar a la cuenta destino
            target.credit(amount);
            accountDao.update(target);

            tx = new TransferTransaction(TransactionStatus.SETTLED, amount, source.getBaseCurrency(),
                                        note, source, target);
        } catch (Exception e) {
            tx = new TransferTransaction(TransactionStatus.FAILED, amount, source.getBaseCurrency(),
                                        "Error: " + e.getMessage(), source, target);
        }

        transactionDao.save(tx);
        return tx;
    }

    /**
     * Obtiene el historial de transacciones de una cuenta
     */
    public List<Transaction> getHistory(Account account) {
        if (!authService.hasAccessToAccount(account)) {
            throw new SecurityException("No tiene acceso a esta cuenta");
        }
        return transactionDao.listByAccountId(account.getId());
    }

    /**
     * Lista todas las cuentas de un cliente
     */
    public List<Account> listAccountsOfClient(Client owner) {
        if (!authService.hasAccessToClientId(owner.getId())) {
            throw new SecurityException("No tiene acceso a este cliente");
        }
        return accountDao.listByOwner(owner.getId());
    }

    /**
     * Lista todas las cuentas de un cliente sin verificar autenticación
     * Usado para buscar cuentas de terceros en transferencias
     */
    public List<Account> listAccountsOfClientWithoutAuth(UUID ownerId) {
        return accountDao.listByOwner(ownerId);
    }

    /**
     * Valida los parámetros comunes de creación de cuenta
     */
    private void validateAccountCreation(Client owner, Currency baseCurrency, BigDecimal initialBalance) {
        if (owner == null) {
            throw new IllegalArgumentException("El propietario no puede ser nulo");
        }
        if (baseCurrency == null) {
            throw new IllegalArgumentException("La moneda no puede ser nula");
        }
        if (initialBalance != null && initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser negativo");
        }
    }
}
