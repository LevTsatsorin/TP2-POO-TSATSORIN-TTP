package LogicLayer;

import java.math.BigDecimal;

/**
 * Transacción de retiro de una cuenta
 */
public class WithdrawTransaction extends Transaction {
    private final Account source;

    /**
     * Constructor para nuevo retiro
     */
    public WithdrawTransaction(TransactionStatus status, BigDecimal amount, Currency currency,
                              String note, Account source) {
        super(TransactionType.WITHDRAW, status, amount, currency, note);
        this.source = source;
    }

    public Account getSource() {
        return source;
    }

    @Override
    public String summary() {
        return String.format("[%s] %s: -%s%s de %s | %s | %s",
                formatDateTime(),
                type.getDescription(),
                currency.getSymbol(),
                String.format("%,.2f", amount),
                source.type(),
                status.getDescription(),
                note != null ? note : "Sin nota");
    }
}

