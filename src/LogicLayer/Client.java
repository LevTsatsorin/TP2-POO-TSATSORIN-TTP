package LogicLayer;

import java.util.UUID;

/**
 * Entidad que representa un cliente del banco
 */
public class Client {
    private final UUID id;
    private final String name;
    private final String alias;

    /**
     * Constructor para crear un nuevo cliente
     * @param name Nombre completo del cliente
     * @param alias Alias único del cliente (usado para login)
     */
    public Client(String name, String alias) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.alias = alias;
    }

    /**
     * Constructor con ID (usado al cargar desde persistencia)
     */
    public Client(UUID id, String name, String alias) {
        this.id = id;
        this.name = name;
        this.alias = alias;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public String toString() {
        return name + " (@" + alias + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return id.equals(client.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
