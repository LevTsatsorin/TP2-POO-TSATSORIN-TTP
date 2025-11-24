package LogicLayer;

import java.math.BigDecimal;

/**
 * Cuenta de crédito - puede tener saldo negativo hasta el límite de crédito
 */
public class CreditAccount extends Account {
    private final BigDecimal creditLimit;

    /**
     * Constructor para nueva cuenta de crédito
     * @param owner Cliente propietario
     * @param baseCurrency Moneda base
     * @param initialBalance Saldo inicial
     * @param creditLimit Límite de crédito (valor positivo)
     */
    public CreditAccount(Client owner, Currency baseCurrency, BigDecimal initialBalance, BigDecimal creditLimit) {
        super(owner, baseCurrency, initialBalance);
        this.creditLimit = creditLimit != null ? creditLimit : BigDecimal.ZERO;
    }


    @Override
    public boolean hasSufficientFunds(BigDecimal amount) {
        // Puede ir negativo hasta -creditLimit
        BigDecimal newBalance = balance.subtract(amount);
        return newBalance.compareTo(creditLimit.negate()) >= 0;
    }

    @Override
    public String type() {
        return "Cuenta de Crédito (Límite: " + baseCurrency.getSymbol() + creditLimit + ")";
    }
}

