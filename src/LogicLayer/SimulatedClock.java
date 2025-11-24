package LogicLayer;

import java.time.LocalDate;

/**
 * Reloj simulado para el sistema bancario
 * Permite avanzar el tiempo de forma controlada para simular días
 */
public class SimulatedClock {
    private static LocalDate currentDay = LocalDate.now();

    /**
     * Obtiene el día actual simulado
     */
    public static LocalDate getCurrentDay() {
        return currentDay;
    }

    /**
     * Avanza el reloj un día
     * @return el nuevo día actual
     */
    public static LocalDate advanceOneDay() {
        currentDay = currentDay.plusDays(1);
        return currentDay;
    }
}

