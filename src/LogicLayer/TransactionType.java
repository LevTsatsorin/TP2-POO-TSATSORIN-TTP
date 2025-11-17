package LogicLayer;

/**
 * Enum que representa los tipos de transacciones posibles
 */
public enum TransactionType {
    DEPOSIT("Depósito"),
    WITHDRAW("Retiro"),
    TRANSFER("Transferencia");

    private final String description;

    TransactionType(String description) {
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

