package LogicLayer;

import java.util.UUID;

/**
 * Interfaz DAO para operaciones de persistencia de credenciales
 */
public interface CredentialDao {
    /**
     * Guarda el hash del PIN de un cliente
     */
    void save(UUID clientId, byte[] pinHash);

    /**
     * Obtiene el hash del PIN de un cliente
     */
    byte[] getByClientId(UUID clientId);
}
