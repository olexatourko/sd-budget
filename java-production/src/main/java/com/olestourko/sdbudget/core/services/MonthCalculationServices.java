package com.olestourko.sdbudget.core.services;

import com.olestourko.sdbudget.core.models.Month;
import javax.inject.Inject;

/**
 *
 * @author oles
 */
public class MonthCalculationServices {

    private final PeriodCalculationServices periodCalculationServices;

    @Inject
    public MonthCalculationServices(PeriodCalculationServices periodServices) {
        this.periodCalculationServices = periodServices;
    }

    public Month calculateMonthTotals(Month month) {
        EstimateResult result = periodCalculationServices.calculateEstimate(
                month.getTotalRevenues(),
                month.getTotalExpenses(),
                month.getTotalAdjustments(),
                month.getDebtRepayments().getAmount(),
                month.getInvestmentOutflows().getAmount(),
                month.getNetIncomeTarget().getAmount(),
                month.getOpeningBalance().getAmount(),
                month.getOpeningSurplus().getAmount()
        );

        month.getClosingBalanceTarget().setAmount(result.expectedBalance);
        month.getEstimatedClosingBalance().setAmount(result.estimatedBalance.subtract(month.getOpeningSurplus().getAmount())); // Adding the opening surplus doesn't make sense in the context of multiple months
        month.getClosingSurplus().setAmount(result.surplus);

        if (month.getIsClosed()) {
            ClosingResult closingResult = periodCalculationServices.calculateClosing(
                    month.getNetIncomeTarget().getAmount(),
                    month.getOpeningBalance().getAmount(),
                    month.getClosingBalance().getAmount(),
                    month.getOpeningSurplus().getAmount()
            );
            month.getClosingSurplus().setAmount(closingResult.closingSurplus);
        }

        return month;
    }
}
