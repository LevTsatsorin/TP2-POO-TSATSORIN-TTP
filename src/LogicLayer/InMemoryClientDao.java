package LogicLayer;

import java.util.*;

/**
 * Implementación en memoria del DAO de clientes
 */
public class InMemoryClientDao implements ClientDao {
    private final Map<UUID, Client> clientsById;
    private final Map<String, Client> clientsByAlias;

    public InMemoryClientDao() {
        this.clientsById = new HashMap<>();
        this.clientsByAlias = new HashMap<>();
    }

    @Override
    public void save(Client newClient) {
        if (clientsByAlias.containsKey(newClient.getAlias())) {
            throw new IllegalArgumentException("Ya existe un cliente con el alias: " + newClient.getAlias());
        }
        clientsById.put(newClient.getId(), newClient);
        clientsByAlias.put(newClient.getAlias(), newClient);
    }

    @Override
    public Client findById(UUID clientId) {
        return clientsById.get(clientId);
    }

    @Override
    public Client findByAlias(String uniqueAlias) {
        return clientsByAlias.get(uniqueAlias);
    }

    @Override
    public List<Client> listAll() {
        return new ArrayList<>(clientsById.values());
    }
}

