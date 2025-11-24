package LogicLayer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa un registro histórico de una inversión en un día específico
 */
public class InvestmentHistory {
    private final LocalDate date;
    private final BigDecimal dailyRate;
    private final BigDecimal balanceBefore;
    private final BigDecimal balanceAfter;
    private final BigDecimal profit;

    public InvestmentHistory(LocalDate date, BigDecimal dailyRate,
                           BigDecimal balanceBefore, BigDecimal balanceAfter) {
        this.date = Objects.requireNonNull(date, "La fecha no puede ser nula");
        this.dailyRate = Objects.requireNonNull(dailyRate, "La tasa no puede ser nula");
        this.balanceBefore = Objects.requireNonNull(balanceBefore, "El saldo inicial no puede ser nulo");
        this.balanceAfter = Objects.requireNonNull(balanceAfter, "El saldo final no puede ser nulo");
        this.profit = balanceAfter.subtract(balanceBefore);
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public BigDecimal getBalanceBefore() {
        return balanceBefore;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    /**
     * Verifica si fue un día alcista
     */
    public boolean isBullish() {
        return profit.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Verifica si fue un día bajista
     */
    public boolean isBearish() {
        return profit.compareTo(BigDecimal.ZERO) < 0;
    }
}

