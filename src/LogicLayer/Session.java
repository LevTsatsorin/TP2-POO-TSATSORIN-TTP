package LogicLayer;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidad que representa una sesión de usuario autenticado
 */
public class Session {
    private final UUID id;
    private final UUID clientId;
    private final Instant expiresAt;

    /**
     * Crea una nueva sesión con duración de 30 minutos
     * @param clientId ID del cliente autenticado
     */
    public Session(UUID clientId) {
        this.id = UUID.randomUUID();
        this.clientId = clientId;
        this.expiresAt = Instant.now().plusSeconds(1800); // 30 minutos
    }

    public UUID getId() {
        return id;
    }

    public UUID getClientId() {
        return clientId;
    }

    /**
     * Verifica si la sesión está activa actualmente
     */
    public boolean isActive() {
        return Instant.now().isBefore(expiresAt);
    }
}

