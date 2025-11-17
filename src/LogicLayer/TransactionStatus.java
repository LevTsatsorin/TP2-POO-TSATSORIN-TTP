package LogicLayer;

/**
 * Enum que representa el estado de una transacción
 */
public enum TransactionStatus {
    SETTLED("Completada"),
    FAILED("Fallida");

    private final String description;

    TransactionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}

