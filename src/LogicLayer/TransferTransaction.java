package LogicLayer;

import java.math.BigDecimal;

/**
 * Transacción de transferencia entre cuentas
 */
public class TransferTransaction extends Transaction {
    private final Account source;
    private final Account target;

    /**
     * Constructor para nueva transferencia
     */
    public TransferTransaction(TransactionStatus status, BigDecimal amount, Currency currency,
                              String note, Account source, Account target) {
        super(TransactionType.TRANSFER, status, amount, currency, note);
        this.source = source;
        this.target = target;
    }

    public Account getSource() {
        return source;
    }

    public Account getTarget() {
        return target;
    }

    @Override
    public String summary() {
        return String.format("[%s] %s: %s%s de %s → %s | %s | %s",
                formatDateTime(),
                type.getDescription(),
                currency.getSymbol(),
                String.format("%,.2f", amount),
                source.type(),
                target.type(),
                status.getDescription(),
                note != null ? note : "Sin nota");
    }
}

