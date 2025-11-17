package LogicLayer;

import java.util.*;

/**
 * Implementación en memoria del DAO de transacciones
 */
public class InMemoryTransactionDao implements TransactionDao {
    private final Map<UUID, List<Transaction>> transactionsByAccount;

    public InMemoryTransactionDao() {
        this.transactionsByAccount = new HashMap<>();
    }

    @Override
    public void save(Transaction newTx) {
        // Determinar las cuentas involucradas según el tipo de transacción
        List<UUID> accountIds = new ArrayList<>();

        if (newTx instanceof DepositTransaction) {
            accountIds.add(((DepositTransaction) newTx).getTarget().getId());
        } else if (newTx instanceof WithdrawTransaction) {
            accountIds.add(((WithdrawTransaction) newTx).getSource().getId());
        } else if (newTx instanceof TransferTransaction) {
            accountIds.add(((TransferTransaction) newTx).getSource().getId());
            accountIds.add(((TransferTransaction) newTx).getTarget().getId());
        }

        // Agregar la transacción a todas las cuentas involucradas
        for (UUID accountId : accountIds) {
            transactionsByAccount.computeIfAbsent(accountId, k -> new ArrayList<>()).add(newTx);
        }
    }

    @Override
    public List<Transaction> listByAccountId(UUID accountId) {
        List<Transaction> transactions = transactionsByAccount.get(accountId);
        if (transactions == null) {
            return Collections.emptyList();
        }
        // Copia ordenada por fecha (más reciente primero)
        List<Transaction> sorted = new ArrayList<>(transactions);
        sorted.sort((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()));
        return sorted;
    }
}

