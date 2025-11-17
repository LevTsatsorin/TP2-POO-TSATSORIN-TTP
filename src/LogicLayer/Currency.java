package LogicLayer;

/**
 * Enum que representa las monedas soportadas en el sistema bancario
 */
public enum Currency {
    ARS("Peso Argentino", "$"),
    USD("Dólar Estadounidense", "US$"),
    EUR("Euro", "€");

    private final String name;
    private final String symbol;

    Currency(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return name + " (" + symbol + ")";
    }
}
