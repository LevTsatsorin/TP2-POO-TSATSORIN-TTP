package LogicLayer;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Clase abstracta que representa una cuenta bancaria
 */
public abstract class Account {
    protected final UUID id;
    protected final Client owner;
    protected final Currency baseCurrency;
    protected BigDecimal balance;

    /**
     * Constructor para crear una nueva cuenta
     * @param owner Cliente propietario
     * @param baseCurrency Moneda base de la cuenta
     * @param initialBalance Saldo inicial
     */
    public Account(Client owner, Currency baseCurrency, BigDecimal initialBalance) {
        this.id = UUID.randomUUID();
        this.owner = owner;
        this.baseCurrency = baseCurrency;
        this.balance = initialBalance != null ? initialBalance : BigDecimal.ZERO;
    }


    public UUID getId() {
        return id;
    }

    public Client getOwner() {
        return owner;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Acredita un monto a la cuenta
     * @param amount Monto a acreditar
     */
    public void credit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo");
        }
        this.balance = this.balance.add(amount);
    }

    /**
     * Debita un monto de la cuenta
     * @param amount Monto a debitar
     * @throws IllegalStateException si no hay fondos suficientes
     */
    public void debit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo");
        }
        if (!hasSufficientFunds(amount)) {
            throw new IllegalStateException("Fondos insuficientes");
        }
        this.balance = this.balance.subtract(amount);
    }

    /**
     * Verifica si hay fondos suficientes para un débito
     * @param amount Monto a verificar
     * @return true si hay fondos suficientes
     */
    public abstract boolean hasSufficientFunds(BigDecimal amount);

    /**
     * Retorna el tipo de cuenta
     */
    public abstract String type();

    @Override
    public String toString() {
        return type() + " - " + baseCurrency.getSymbol() + balance + " [" + owner.getName() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id.equals(account.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

