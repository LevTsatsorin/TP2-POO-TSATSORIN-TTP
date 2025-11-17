package LogicLayer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

/**
 * Servicio de autenticación y gestión de sesiones
 */
public class AuthService {
    private final CredentialDao credentialDao;
    private final SessionDao sessionDao;
    private final ClientDao clientDao;

    public AuthService(CredentialDao credentialDao, SessionDao sessionDao, ClientDao clientDao) {
        this.credentialDao = credentialDao;
        this.sessionDao = sessionDao;
        this.clientDao = clientDao;
    }

    /**
     * Registra un PIN de 4 dígitos para un cliente
     * @param client Cliente
     * @param pinDigits Array de 4 bytes con los dígitos del PIN
     */
    public void registerPin(Client client, byte[] pinDigits) {
        if (pinDigits == null || pinDigits.length != 4) {
            throw new IllegalArgumentException("El PIN debe tener exactamente 4 dígitos");
        }

        byte[] hash = hashPin(pinDigits);
        credentialDao.save(client.getId(), hash);
    }

    /**
     * Inicia sesión con alias y PIN
     * @param alias Alias del cliente
     * @param pinDigits Array de 4 bytes con los dígitos del PIN
     * @return Sesión creada
     * @throws SecurityException si las credenciales son inválidas
     */
    public Session loginWithPin(String alias, byte[] pinDigits) {
        Client client = clientDao.findByAlias(alias);
        if (client == null) {
            throw new SecurityException("Alias o PIN incorrectos");
        }

        byte[] storedHash = credentialDao.getByClientId(client.getId());
        if (storedHash == null) {
            throw new SecurityException("Cliente sin PIN registrado");
        }

        byte[] inputHash = hashPin(pinDigits);
        if (!Arrays.equals(storedHash, inputHash)) {
            throw new SecurityException("Alias o PIN incorrectos");
        }

        // Crear nueva sesión
        Session session = new Session(client.getId());
        sessionDao.saveActive(session);
        return session;
    }

    /**
     * Cierra la sesión activa
     */
    public void logout() {
        sessionDao.clearActive();
    }

    /**
     * Obtiene la sesión activa (si existe y es válida)
     * @return Sesión activa o null
     */
    public Session getActiveSession() {
        Session session = sessionDao.getActive();
        if (session != null && !session.isActive()) {
            sessionDao.clearActive();
            return null;
        }
        return session;
    }

    /**
     * Obtiene el ID del cliente de la sesión activa
     * @return ID del cliente o null si no hay sesión
     */
    public UUID getActiveClientId() {
        Session session = getActiveSession();
        return session != null ? session.getClientId() : null;
    }

    /**
     * Verifica si el usuario actual tiene acceso a una cuenta
     */
    public boolean hasAccessToAccount(Account account) {
        UUID activeClientId = getActiveClientId();
        if (activeClientId == null) {
            return false;
        }
        return account.getOwner().getId().equals(activeClientId);
    }

    /**
     * Verifica si el usuario actual tiene acceso a un cliente por ID
     */
    public boolean hasAccessToClientId(UUID clientId) {
        UUID activeClientId = getActiveClientId();
        if (activeClientId == null) {
            return false;
        }
        return activeClientId.equals(clientId);
    }

    /**
     * Genera hash SHA-256 del PIN
     */
    private byte[] hashPin(byte[] pinDigits) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(pinDigits);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear PIN", e);
        }
    }
}

