package LogicLayer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Servicio que gestiona las operaciones de inversión
 * Aplica rendimientos diarios y mantiene el historial
 */
public class InvestmentService {
    private final AccountDao accountDao;
    private final MarketSimulator marketSimulator;

    public InvestmentService(AccountDao accountDao, MarketSimulator marketSimulator) {
        this.accountDao = accountDao;
        this.marketSimulator = marketSimulator;
    }

    /**
     * Obtiene el simulador de mercado
     */
    public MarketSimulator getMarketSimulator() {
        return marketSimulator;
    }

    /**
     * Actualiza TODAS las cuentas de inversión del sistema con la tasa del día
     *
     * @param currentDate fecha actual
     * @param dailyRate tasa del día a aplicar
     */
    public void updateAllInvestmentAccountsInSystem(LocalDate currentDate, BigDecimal dailyRate) {
        // Obtener todas las cuentas del sistema
        List<Account> allAccounts = accountDao.findAll();

        // Aplicar rendimiento a cada cuenta de inversión
        for (Account account : allAccounts) {
            if (account instanceof InvestmentAccount) {
                InvestmentAccount investmentAccount = (InvestmentAccount) account;

                // Solo actualizar si no está actualizada al día actual
                if (investmentAccount.getLastUpdateDate().isBefore(currentDate)) {
                    investmentAccount.applyDailyReturn(dailyRate, currentDate);
                    accountDao.update(investmentAccount);
                }
            }
        }
    }
}

