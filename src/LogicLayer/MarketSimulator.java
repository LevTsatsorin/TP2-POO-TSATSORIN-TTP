package LogicLayer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * Simulador de mercado que genera tasas de interés diarias
 * Utiliza una combinación de fluctuaciones normales y eventos extremos
 */
public class MarketSimulator {
    // Tasas normales
    private static final double NORMAL_MIN_RATE = -0.02;  // -2%
    private static final double NORMAL_MAX_RATE = 0.03;   // +3%

    // Tasas extremas
    private static final double EXTREME_MIN_RATE = -0.08; // -8%
    private static final double EXTREME_MAX_RATE = 0.12;  // +12%

    // Probabilidad de evento extremo (5%)
    private static final double EXTREME_EVENT_PROBABILITY = 0.05;

    private final Random random;

    public MarketSimulator() {
        this.random = new Random();
    }

    /**
     * Genera una tasa de interés diaria aleatoria
     * El 95% del tiempo genera tasas normales (-2% a +3%)
     * El 5% del tiempo genera tasas extremas (-8% a +12%)
     *
     * @return tasa de interés diaria como BigDecimal
     */
    public BigDecimal generateDailyRate() {
        double rate;

        // Determinar si es un evento extremo
        if (random.nextDouble() < EXTREME_EVENT_PROBABILITY) {
            // Evento extremo
            rate = EXTREME_MIN_RATE + (EXTREME_MAX_RATE - EXTREME_MIN_RATE) * random.nextDouble();
        } else {
            // Fluctuación normal
            rate = NORMAL_MIN_RATE + (NORMAL_MAX_RATE - NORMAL_MIN_RATE) * random.nextDouble();
        }

        return BigDecimal.valueOf(rate).setScale(5, RoundingMode.HALF_UP);
    }
}

