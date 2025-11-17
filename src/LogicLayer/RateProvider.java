package LogicLayer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Proveedor de tasas de cambio entre monedas
 */
public class RateProvider {
    // Tasas de cambio fijas
    private final Map<String, BigDecimal> rates;

    public RateProvider() {
        this.rates = new HashMap<>();
        initializeRates();
    }

    /**
     * Inicializa las tasas de cambio (todas respecto a USD como base)
     */
    private void initializeRates() {
        // USD como base = 1.0
        rates.put("USD_USD", BigDecimal.ONE);
        rates.put("USD_EUR", new BigDecimal("0.86"));
        rates.put("USD_ARS", new BigDecimal("1410.00"));

        rates.put("EUR_USD", new BigDecimal("1.16"));
        rates.put("EUR_EUR", BigDecimal.ONE);
        rates.put("EUR_ARS", new BigDecimal("1630.00"));

        rates.put("ARS_USD", new BigDecimal("0.00071"));
        rates.put("ARS_EUR", new BigDecimal("0.00061"));
        rates.put("ARS_ARS", BigDecimal.ONE);
    }

    /**
     * Obtiene la tasa de cambio entre dos monedas
     * @param from Moneda origen
     * @param to Moneda destino
     * @return Tasa de conversión
     */
    public BigDecimal getRate(Currency from, Currency to) {
        if (from == to) {
            return BigDecimal.ONE;
        }

        String key = from.name() + "_" + to.name();
        BigDecimal rate = rates.get(key);

        if (rate == null) {
            throw new IllegalArgumentException("No hay tasa de cambio disponible para " + from + " → " + to);
        }

        return rate;
    }

    /**
     * Convierte un monto de una moneda a otra
     * @param amount Monto a convertir
     * @param from Moneda origen
     * @param to Moneda destino
     * @return Monto convertido
     */
    public BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
        if (from == to) {
            return amount;
        }

        BigDecimal rate = getRate(from, to);
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}
