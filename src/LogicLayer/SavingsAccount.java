package LogicLayer;

import java.math.BigDecimal;

/**
 * Cuenta de ahorro - no puede tener saldo negativo
 */
public class SavingsAccount extends Account {

    public SavingsAccount(Client owner, Currency baseCurrency, BigDecimal initialBalance) {
        super(owner, baseCurrency, initialBalance);
    }

    @Override
    public boolean hasSufficientFunds(BigDecimal amount) {
        // La cuenta de ahorro no puede ser negativa
        return balance.compareTo(amount) >= 0;
    }

    @Override
    public String type() {
        return "Cuenta de Ahorro";
    }
}

