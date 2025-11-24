package LogicLayer;

import java.util.*;

/**
 * Implementación en memoria del DAO de cuentas
 */
public class InMemoryAccountDao implements AccountDao {
    private final Map<UUID, Account> accountsById;
    private final Map<UUID, List<Account>> accountsByOwner;

    public InMemoryAccountDao() {
        this.accountsById = new HashMap<>();
        this.accountsByOwner = new HashMap<>();
    }

    @Override
    public void save(Account account) {
        accountsById.put(account.getId(), account);
        UUID ownerId = account.getOwner().getId();
        accountsByOwner.computeIfAbsent(ownerId, k -> new ArrayList<>()).add(account);
    }

    @Override
    public List<Account> listByOwner(UUID ownerId) {
        return new ArrayList<>(accountsByOwner.getOrDefault(ownerId, Collections.emptyList()));
    }

    @Override
    public void update(Account account) {
        accountsById.put(account.getId(), account);
    }

    @Override
    public List<Account> findAll() {
        return new ArrayList<>(accountsById.values());
    }
}