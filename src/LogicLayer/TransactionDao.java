package LogicLayer;

import java.util.List;
import java.util.UUID;

/**
 * Interfaz DAO para operaciones de persistencia de transacciones
 */
public interface TransactionDao {
    /**
     * Guarda una nueva transacción
     */
    void save(Transaction newTx);

    /**
     * Lista todas las transacciones de una cuenta
     */
    List<Transaction> listByAccountId(UUID accountId);
}
