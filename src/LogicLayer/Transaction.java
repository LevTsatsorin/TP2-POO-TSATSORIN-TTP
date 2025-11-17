package LogicLayer;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Clase abstracta base para todas las transacciones
 */
public abstract class Transaction {
    protected final UUID id;
    protected final TransactionType type;
    protected final TransactionStatus status;
    protected final BigDecimal amount;
    protected final Currency currency;
    protected final Instant createdAt;
    protected final String note;

    /**
     * Constructor para crear una nueva transacción
     */
    public Transaction(TransactionType type, TransactionStatus status, BigDecimal amount,
                      Currency currency, String note) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.status = status;
        this.amount = amount;
        this.currency = currency;
        this.createdAt = Instant.now();
        this.note = note;
    }

    public UUID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getNote() {
        return note;
    }

    public boolean isSuccessful() {
        return status == TransactionStatus.SETTLED;
    }

    /**
     * Genera un resumen legible de la transacción
     */
    public abstract String summary();

    /**
     * Formatea la fecha/hora de la transacción
     */
    protected String formatDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        return formatter.format(createdAt);
    }

    @Override
    public String toString() {
        return summary();
    }
}

