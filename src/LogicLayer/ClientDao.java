package LogicLayer;

import java.util.List;
import java.util.UUID;

/**
 * Interfaz DAO para operaciones de persistencia de clientes
 */
public interface ClientDao {
    /**
     * Guarda un nuevo cliente
     */
    void save(Client newClient);

    /**
     * Busca un cliente por su ID
     */
    Client findById(UUID clientId);

    /**
     * Busca un cliente por su alias único
     */
    Client findByAlias(String uniqueAlias);

    /**
     * Lista todos los clientes
     */
    List<Client> listAll();
}
