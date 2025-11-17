package LogicLayer;

import java.util.List;
import java.util.UUID;

/**
 * Interfaz DAO para operaciones de persistencia de cuentas
 */
public interface AccountDao {
    /**
     * Guarda una nueva cuenta
     */
    void save(Account account);

    /**
     * Lista todas las cuentas de un propietario
     */
    List<Account> listByOwner(UUID ownerId);

    /**
     * Actualiza una cuenta existente
     */
    void update(Account account);
}

