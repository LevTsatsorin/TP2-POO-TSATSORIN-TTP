package LogicLayer;

import java.util.List;
import java.util.UUID;

/**
 * Servicio para gestión de clientes
 */
public class ClientService {
    private final ClientDao clientDao;

    public ClientService(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

    /**
     * Crea un nuevo cliente
     * @param fullName Nombre completo
     * @param uniqueAlias Alias único
     * @return Cliente creado
     */
    public Client createClient(String fullName, String uniqueAlias) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (uniqueAlias == null || uniqueAlias.trim().isEmpty()) {
            throw new IllegalArgumentException("El alias no puede estar vacío");
        }

        // Verificar que el alias no exista
        if (clientDao.findByAlias(uniqueAlias) != null) {
            throw new IllegalArgumentException("El alias ya está en uso");
        }

        Client client = new Client(fullName, uniqueAlias);
        clientDao.save(client);
        return client;
    }

    /**
     * Obtiene un cliente por ID
     */
    public Client getById(UUID clientId) {
        return clientDao.findById(clientId);
    }

    /**
     * Obtiene un cliente por alias
     */
    public Client getByAlias(String uniqueAlias) {
        return clientDao.findByAlias(uniqueAlias);
    }

    /**
     * Lista todos los clientes
     */
    public List<Client> listClients() {
        return clientDao.listAll();
    }
}

