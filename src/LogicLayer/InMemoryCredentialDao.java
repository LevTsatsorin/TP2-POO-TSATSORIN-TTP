package LogicLayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Implementación en memoria del DAO de credenciales
 */
public class InMemoryCredentialDao implements CredentialDao {
    private final Map<UUID, byte[]> pinHashByClientId;

    public InMemoryCredentialDao() {
        this.pinHashByClientId = new HashMap<>();
    }

    @Override
    public void save(UUID clientId, byte[] pinHash) {
        pinHashByClientId.put(clientId, pinHash);
    }

    @Override
    public byte[] getByClientId(UUID clientId) {
        return pinHashByClientId.get(clientId);
    }
}
