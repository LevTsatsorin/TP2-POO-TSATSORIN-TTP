package LogicLayer;

/**
 * Servicio para operaciones de registro de nuevos clientes
 */
public class RegistrationService {
    private final ClientService clientService;
    private final AuthService authService;

    public RegistrationService(ClientService clientService, AuthService authService) {
        this.clientService = clientService;
        this.authService = authService;
    }

    /**
     * Registra un nuevo cliente con su PIN
     * @param fullName Nombre completo
     * @param uniqueAlias Alias único
     * @param pinDigits PIN de 4 dígitos
     * @return Cliente creado
     * @throws IllegalArgumentException si los datos son inválidos
     */
    public Client registerNewClient(String fullName, String uniqueAlias, byte[] pinDigits) {
        // Validar nombre
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        // Validar alias
        if (uniqueAlias == null || uniqueAlias.trim().isEmpty()) {
            throw new IllegalArgumentException("El alias no puede estar vacío");
        }

        // Validar que el alias sea alfanumérico
        if (!uniqueAlias.matches("^[a-zA-Z0-9_]{3,20}$")) {
            throw new IllegalArgumentException("El alias debe tener entre 3 y 20 caracteres alfanuméricos");
        }

        // Validar PIN
        if (pinDigits == null || pinDigits.length != 4) {
            throw new IllegalArgumentException("El PIN debe tener exactamente 4 dígitos");
        }

        for (byte digit : pinDigits) {
            if (digit < 0 || digit > 9) {
                throw new IllegalArgumentException("El PIN solo puede contener dígitos del 0 al 9");
            }
        }

        // Crear cliente
        Client client = clientService.createClient(fullName, uniqueAlias);

        // Registrar PIN
        authService.registerPin(client, pinDigits);

        return client;
    }

    /**
     * Valida el formato de un PIN como String
     * @param pinStr String del PIN
     * @return true si es válido
     */
    public boolean isValidPinFormat(String pinStr) {
        return pinStr != null && pinStr.matches("\\d{4}");
    }

    /**
     * Convierte un String de PIN a array de bytes
     * @param pinStr String del PIN (4 dígitos)
     * @return Array de bytes
     */
    public byte[] pinStringToBytes(String pinStr) {
        if (!isValidPinFormat(pinStr)) {
            throw new IllegalArgumentException("Formato de PIN inválido");
        }

        byte[] pinDigits = new byte[4];
        for (int i = 0; i < 4; i++) {
            pinDigits[i] = (byte) Character.getNumericValue(pinStr.charAt(i));
        }
        return pinDigits;
    }
}
