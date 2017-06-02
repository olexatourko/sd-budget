package com.olestourko.sdbudget;

import com.olestourko.sdbudget.core.models.Month;
import javafx.application.Application;
import javafx.stage.Stage;
import com.olestourko.sdbudget.core.repositories.MonthRepository;
import com.olestourko.sdbudget.desktop.models.Budget;
import com.olestourko.sdbudget.core.dagger.CoreComponent;
import com.olestourko.sdbudget.core.dagger.DaggerCoreComponent;
import com.olestourko.sdbudget.core.models.factories.MonthFactory;
import com.olestourko.sdbudget.desktop.Frontend;
import com.olestourko.sdbudget.desktop.dagger.BudgetComponent;
import com.olestourko.sdbudget.desktop.dagger.DaggerBudgetComponent;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.List;
import org.flywaydb.core.Flyway;
import javafx.application.Application.Parameters;

public class Sdbudget extends Application {

    private boolean ALWAYS_MIGRATE = true;

    @Override
    public void start(Stage stage) throws Exception {

        // Migrate DB if the migrate flag is set
        Parameters parameters = getParameters();
        List<String> unnamedParamers = parameters.getUnnamed();
        if (unnamedParamers.contains("migrate") || ALWAYS_MIGRATE) {
            Flyway flyway = new Flyway();
            flyway.setDataSource("jdbc:h2:~/sdbudget", "sdbudget", "");
            flyway.migrate();
        }

        // Create configuration file if it doesn't exist
        File configFile = new File("./configuration.yaml");
        if (!configFile.exists()) {
            InputStream link = (getClass().getResourceAsStream("/configuration/configuration.yaml"));
            Files.copy(link, configFile.getAbsoluteFile().toPath());
        };

        final CoreComponent coreComponent = DaggerCoreComponent.builder().build();
        final BudgetComponent budgetComponent = DaggerBudgetComponent.builder().coreComponent(coreComponent).build();
        final Budget budget = budgetComponent.budget().get();
        final Frontend frontend = budgetComponent.frontend().get();

//        System.out.println(budgetComponent.configurationProvider().getProperty("currency", String.class));
        // Populate the month repository
        MonthRepository monthRepository = coreComponent.monthRepository();
        MonthFactory monthFactory = coreComponent.monthFactory();

        monthRepository.fetchMonths();
        Calendar calendar = Calendar.getInstance();

        Month month = monthRepository.getMonth((short) calendar.get(Calendar.MONTH), (short) calendar.get(Calendar.YEAR));
        if (month == null) {
            month = monthFactory.createCurrentMonth();
            monthRepository.putMonth(month);
        }

        for (int i = 0; i < 2; i++) {
            if (monthRepository.getNext(month) == null) {
                monthRepository.putMonth(monthFactory.createNextMonth(month));
            }
            month = monthRepository.getNext(month);
        }

        coreComponent.monthServices().recalculateMonths(monthRepository.getFirst());
        budget.setCurrentMonth(monthRepository.getMonth((short) calendar.get(Calendar.MONTH), (short) calendar.get(Calendar.YEAR)));

        frontend.load(stage);

    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
