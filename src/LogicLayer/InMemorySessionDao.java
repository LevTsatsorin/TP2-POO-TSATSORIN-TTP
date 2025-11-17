package LogicLayer;

/**
 * Implementación en memoria del DAO de sesiones
 */
public class InMemorySessionDao implements SessionDao {
    private Session activeSession;

    @Override
    public void saveActive(Session session) {
        this.activeSession = session;
    }

    @Override
    public Session getActive() {
        return activeSession;
    }

    @Override
    public void clearActive() {
        this.activeSession = null;
    }
}

