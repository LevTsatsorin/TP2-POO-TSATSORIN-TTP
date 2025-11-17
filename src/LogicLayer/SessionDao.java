package LogicLayer;

/**
 * Interfaz DAO para operaciones de persistencia de sesiones
 */
public interface SessionDao {
    /**
     * Guarda una sesión activa
     */
    void saveActive(Session session);

    /**
     * Obtiene la sesión activa actual
     */
    Session getActive();

    /**
     * Limpia la sesión activa
     */
    void clearActive();
}

