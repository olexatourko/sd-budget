package com.olestourko.sdbudget.desktop.dagger;

import com.olestourko.sdbudget.desktop.models.Budget;
import com.olestourko.sdbudget.desktop.controllers.MainController;
import com.olestourko.sdbudget.core.services.PeriodCalculationServices;
import com.olestourko.sdbudget.desktop.controls.MonthControl;
import dagger.Component;
import javax.inject.Provider;
import com.olestourko.sdbudget.core.dagger.CoreComponent;
import com.olestourko.sdbudget.desktop.Frontend;

/**
 *
 * @author oles
 */
@DesktopApplicationScope
@Component(modules = {BudgetModule.class}, dependencies = {CoreComponent.class})
public interface BudgetComponent {

    Provider<Frontend> frontend();

    Provider<Budget> budget();

    Provider<MainController> mainController();

    PeriodCalculationServices periodServices();

    void inject(MonthControl monthControl);
}
