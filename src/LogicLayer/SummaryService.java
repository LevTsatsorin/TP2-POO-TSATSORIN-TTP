package LogicLayer;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Servicio para cálculos de resumen y totales
 */
public class SummaryService {
    private final AccountDao accountDao;
    private final RateProvider rateProvider;
    private final AuthService authService;

    public SummaryService(AccountDao accountDao, RateProvider rateProvider, AuthService authService) {
        this.accountDao = accountDao;
        this.rateProvider = rateProvider;
        this.authService = authService;
    }

    /**
     * Calcula activos y deudas por separado en una moneda específica
     * @param clientId ID del cliente
     * @param targetCurrency Moneda objetivo
     * @return Array [activos, deudas] en la moneda objetivo
     */
    public BigDecimal[] calculateAssetsAndDebtsIn(UUID clientId, Currency targetCurrency) {
        if (!authService.hasAccessToClientId(clientId)) {
            throw new SecurityException("No tiene acceso a este cliente");
        }

        List<Account> accounts = accountDao.listByOwner(clientId);
        BigDecimal totalAssets = BigDecimal.ZERO;
        BigDecimal totalDebts = BigDecimal.ZERO;

        for (Account account : accounts) {
            BigDecimal balance = account.getBalance();
            Currency accountCurrency = account.getBaseCurrency();

            // Convertir el saldo a la moneda objetivo
            BigDecimal convertedBalance = rateProvider.convert(balance, accountCurrency, targetCurrency);

            // Si el balance es positivo, es un activo; si es negativo, es una deuda
            if (convertedBalance.compareTo(BigDecimal.ZERO) >= 0) {
                totalAssets = totalAssets.add(convertedBalance);
            } else {
                totalDebts = totalDebts.add(convertedBalance.abs());
            }
        }

        return new BigDecimal[]{totalAssets, totalDebts};
    }

    /**
     * Calcula activos y deudas por separado en Pesos Argentinos (ARS)
     * @param clientId ID del cliente
     * @return Array [activos, deudas] en ARS
     */
    public BigDecimal[] calculateAssetsAndDebtsInARS(UUID clientId) {
        return calculateAssetsAndDebtsIn(clientId, Currency.ARS);
    }
}

