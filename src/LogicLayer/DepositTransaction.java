package LogicLayer;

import java.math.BigDecimal;

/**
 * Transacción de depósito a una cuenta
 */
public class DepositTransaction extends Transaction {
    private final Account target;

    /**
     * Constructor para nuevo depósito
     */
    public DepositTransaction(TransactionStatus status, BigDecimal amount, Currency currency,
                             String note, Account target) {
        super(TransactionType.DEPOSIT, status, amount, currency, note);
        this.target = target;
    }

    public Account getTarget() {
        return target;
    }

    @Override
    public String summary() {
        return String.format("[%s] %s: +%s%s → %s | %s | %s",
                formatDateTime(),
                type.getDescription(),
                currency.getSymbol(),
                String.format("%,.2f", amount),
                target.type(),
                status.getDescription(),
                note != null ? note : "Sin nota");
    }
}

