package LogicLayer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Cuenta de inversión que genera rendimientos según tasas de interés variables
 * El saldo fluctúa diariamente según el comportamiento del mercado
 */
public class InvestmentAccount extends Account {
    private final List<InvestmentHistory> history;
    private LocalDate lastUpdateDate;

    /**
     * Constructor para crear una nueva cuenta de inversión
     */
    public InvestmentAccount(Client owner, Currency baseCurrency, BigDecimal initialBalance) {
        super(owner, baseCurrency, initialBalance);
        this.history = new ArrayList<>();
        this.lastUpdateDate = SimulatedClock.getCurrentDay();
    }

    @Override
    public boolean hasSufficientFunds(BigDecimal amount) {
        // La cuenta de inversión no puede tener saldo negativo
        return balance.compareTo(amount) >= 0;
    }

    @Override
    public String type() {
        return "Cuenta de Inversión";
    }

    /**
     * Aplica el rendimiento diario a la cuenta según la tasa de interés
     *
     * @param dailyRate tasa de interés del día
     * @param currentDate fecha actual
     */
    public void applyDailyReturn(BigDecimal dailyRate, LocalDate currentDate) {
        if (dailyRate == null) {
            throw new IllegalArgumentException("La tasa no puede ser nula");
        }
        if (currentDate == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }

        // Solo aplicar si hay saldo
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal balanceBefore = balance;

            // Calcular nuevo saldo: balance * (1 + rate)
            BigDecimal multiplier = BigDecimal.ONE.add(dailyRate);
            balance = balance.multiply(multiplier).setScale(2, java.math.RoundingMode.HALF_UP);

            // Registrar en el historial
            InvestmentHistory record = new InvestmentHistory(
                currentDate, dailyRate, balanceBefore, balance
            );
            history.add(record);

            lastUpdateDate = currentDate;
        }
    }

    /**
     * Obtiene el historial de inversiones
     */
    public List<InvestmentHistory> getHistory() {
        return Collections.unmodifiableList(history);
    }

    /**
     * Obtiene la fecha de la última actualización
     */
    public LocalDate getLastUpdateDate() {
        return lastUpdateDate;
    }

    /**
     * Calcula el rendimiento total acumulado
     */
    public BigDecimal getTotalReturn() {
        BigDecimal total = BigDecimal.ZERO;
        for (InvestmentHistory record : history) {
            total = total.add(record.getProfit());
        }
        return total;
    }

    /**
     * Cuenta cuántos días fueron alcistas
     */
    public int getBullishDaysCount() {
        int count = 0;
        for (InvestmentHistory record : history) {
            if (record.isBullish()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Cuenta cuántos días fueron bajistas
     */
    public int getBearishDaysCount() {
        int count = 0;
        for (InvestmentHistory record : history) {
            if (record.isBearish()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void credit(BigDecimal amount) {
        super.credit(amount);
        // Actualizar fecha si es necesario
        if (lastUpdateDate == null) {
            lastUpdateDate = SimulatedClock.getCurrentDay();
        }
    }
}

