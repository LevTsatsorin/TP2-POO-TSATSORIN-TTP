package UserLayer;

import LogicLayer.*;

import java.math.BigDecimal;

/**
 * Clase independiente para ejecutar pruebas de cálculo de inversiones
 */
public class InvestmentCalculationTests {

    public static void main(String[] args) {
        runTests();
    }

    /**
     * Ejecuta todas las pruebas de cálculo de inversiones
     */
    public static void runTests() {
        printLine("=", 60);
        System.out.println("PRUEBAS DE CÁLCULO DE INVERSIONES");
        printLine("=", 60);
        System.out.println();

        // Crear cliente y cuenta de prueba
        Client testClient = new Client("Test User", "testuser");
        InvestmentAccount testAccount = new InvestmentAccount(testClient, Currency.USD, new BigDecimal("1000.00"));

        System.out.println("Cuenta inicial:");
        System.out.println("  Moneda: " + testAccount.getBaseCurrency());
        System.out.println("  Saldo inicial: $" + testAccount.getBalance());
        System.out.println();

        // Prueba 1: Tasa positiva (alcista)
        System.out.println("PRUEBA 1: Mercado Alcista (+5%)");
        printLine("-", 40);
        BigDecimal rate1 = new BigDecimal("0.05"); // 5%
        BigDecimal expectedBalance1 = testAccount.getBalance().multiply(BigDecimal.ONE.add(rate1))
                .setScale(2, java.math.RoundingMode.HALF_UP);

        testAccount.applyDailyReturn(rate1, SimulatedClock.getCurrentDay());
        BigDecimal actualBalance1 = testAccount.getBalance();

        System.out.println("  Tasa aplicada: +5.000%");
        System.out.println("  Saldo esperado: $" + expectedBalance1);
        System.out.println("  Saldo obtenido: $" + actualBalance1);
        System.out.println("  RESULTADO: " + (expectedBalance1.compareTo(actualBalance1) == 0 ? "CORRECTO" : "ERROR"));
        System.out.println();

        // Prueba 2: Tasa negativa (bajista)
        System.out.println("PRUEBA 2: Mercado Bajista (-3%)");
        printLine("-", 40);
        BigDecimal rate2 = new BigDecimal("-0.03"); // -3%
        BigDecimal balanceBefore2 = testAccount.getBalance();
        BigDecimal expectedBalance2 = balanceBefore2.multiply(BigDecimal.ONE.add(rate2))
                .setScale(2, java.math.RoundingMode.HALF_UP);

        SimulatedClock.advanceOneDay();
        testAccount.applyDailyReturn(rate2, SimulatedClock.getCurrentDay());
        BigDecimal actualBalance2 = testAccount.getBalance();

        System.out.println("  Saldo antes: $" + balanceBefore2);
        System.out.println("  Tasa aplicada: -3.000%");
        System.out.println("  Saldo esperado: $" + expectedBalance2);
        System.out.println("  Saldo obtenido: $" + actualBalance2);
        System.out.println("  RESULTADO: " + (expectedBalance2.compareTo(actualBalance2) == 0 ? "CORRECTO" : "ERROR"));
        System.out.println();

        // Prueba 3: Tasa cero (mercado estable)
        System.out.println("PRUEBA 3: Mercado Estable (0%)");
        printLine("-", 40);
        BigDecimal rate3 = BigDecimal.ZERO;
        BigDecimal balanceBefore3 = testAccount.getBalance();

        SimulatedClock.advanceOneDay();
        testAccount.applyDailyReturn(rate3, SimulatedClock.getCurrentDay());
        BigDecimal actualBalance3 = testAccount.getBalance();

        System.out.println("  Saldo antes: $" + balanceBefore3);
        System.out.println("  Tasa aplicada: 0.000%");
        System.out.println("  Saldo esperado: $" + balanceBefore3 + " (sin cambio)");
        System.out.println("  Saldo obtenido: $" + actualBalance3);
        System.out.println("  RESULTADO: " + (balanceBefore3.compareTo(actualBalance3) == 0 ? "CORRECTO" : "ERROR"));
        System.out.println();

        // Prueba 4: Múltiples días consecutivos
        System.out.println("PRUEBA 4: Simulación de 3 días consecutivos");
        printLine("-", 40);
        InvestmentAccount testAccount2 = new InvestmentAccount(testClient, Currency.ARS, new BigDecimal("10000.00"));

        BigDecimal[] rates = {
            new BigDecimal("0.02"),   // +2%
            new BigDecimal("-0.01"),  // -1%
            new BigDecimal("0.015")   // +1.5%
        };

        BigDecimal expectedFinal = new BigDecimal("10000.00");
        for (BigDecimal rate : rates) {
            expectedFinal = expectedFinal.multiply(BigDecimal.ONE.add(rate))
                    .setScale(2, java.math.RoundingMode.HALF_UP);
        }

        System.out.println("  Saldo inicial: $10,000.00");
        System.out.println("  Día 1: +2.000% → $" + new BigDecimal("10000.00").multiply(new BigDecimal("1.02")).setScale(2, java.math.RoundingMode.HALF_UP));
        System.out.println("  Día 2: -1.000% → $" + new BigDecimal("10000.00").multiply(new BigDecimal("1.02")).multiply(new BigDecimal("0.99")).setScale(2, java.math.RoundingMode.HALF_UP));
        System.out.println("  Día 3: +1.500% → $" + expectedFinal);

        for (BigDecimal rate : rates) {
            SimulatedClock.advanceOneDay();
            testAccount2.applyDailyReturn(rate, SimulatedClock.getCurrentDay());
        }

        System.out.println("  Saldo esperado final: $" + expectedFinal);
        System.out.println("  Saldo obtenido final: $" + testAccount2.getBalance());
        System.out.println("  RESULTADO: " + (expectedFinal.compareTo(testAccount2.getBalance()) == 0 ? "CORRECTO" : "ERROR"));
        System.out.println();

        // Verificar historial
        System.out.println("PRUEBA 5: Verificación del Historial");
        printLine("-", 40);
        System.out.println("  Registros en historial: " + testAccount2.getHistory().size());
        System.out.println("  Días esperados: 3");
        System.out.println("  RESULTADO: " + (testAccount2.getHistory().size() == 3 ? "CORRECTO" : "ERROR"));
        System.out.println();

        printLine("=", 60);
        System.out.println("TODAS LAS PRUEBAS COMPLETADAS");
        System.out.println("Los cálculos de inversión se realizaron según la fórmula:");
        System.out.println("  Nuevo Saldo = Saldo Actual × (1 + Tasa Diaria)");
        printLine("=", 60);
        System.out.println();
    }

    /**
     * Imprime una línea de caracteres repetidos
     */
    private static void printLine(String character, int times) {
        for (int i = 0; i < times; i++) {
            System.out.print(character);
        }
        System.out.println();
    }
}

