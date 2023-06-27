package ru.wert.datapik.chogori.entities.densities.commands;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.chogori.common.commands.ICommand;
import ru.wert.datapik.chogori.entities.densities.Density_TableView;
import ru.wert.datapik.chogori.statics.AppStatic;
import ru.wert.datapik.client.entity.models.Density;
import ru.wert.datapik.winform.warnings.Warning1;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_DENSITIES;
import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_PREFIXES;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class DensityAddCommand implements ICommand {

    private Density newItem;
    private Density_TableView tableView;

    /**
     *
     * @param tableView Density_TableView
     */
    public DensityAddCommand(Density newItem, Density_TableView tableView) {
        this.newItem = newItem;
        this.tableView = tableView;

    }

    @Override
    public void execute() {

        try {
            Density savedDensity = CH_DENSITIES.save(newItem);

            Platform.runLater(()->{
                tableView.easyUpdate(CH_DENSITIES);
                tableView.scrollTo(newItem);
                tableView.getSelectionModel().select(newItem);
            });


            log.info("Добавлена плотность материала {}", newItem.getName());
            AppStatic.createLog(true, String.format("Добавил плотность материала '%s'", newItem.getName()));
        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_ADDING_ITEM, $SERVER_IS_NOT_AVAILABLE_MAYBE);
            log.error("При добавлении плотности материала {} произошла ошибка {}", newItem.getName(), e.getMessage());
        }

    }
}
