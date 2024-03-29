package ru.wert.tubus.chogori.entities.densities.commands;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.entities.densities.Density_TableView;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.client.entity.models.Density;
import ru.wert.tubus.winform.warnings.Warning1;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_DENSITIES;
import static ru.wert.tubus.winform.warnings.WarningMessages.*;

@Slf4j
public class DensityChangeCommand implements ICommand {

    private Density item;
    private Density_TableView tableView;

    /**
     *
     * @param tableView Density_TableView
     */
    public DensityChangeCommand(Density item, Density_TableView tableView) {
        this.item = item;
        this.tableView = tableView;

    }

    @Override
    public void execute() {

        try {
            CH_DENSITIES.update(item);

            Platform.runLater(()->{
                tableView.easyUpdate(CH_DENSITIES);
                tableView.scrollTo(item);
                tableView.getSelectionModel().select(item);
            });


            log.info("Обновлена плотность материала {}", item.getName());
            AppStatic.createLog(true, String.format("Изменил плотность материала '%s'", item.getName()));
        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_CHANGING_ITEM, $ITEM_IS_NOT_AVAILABLE_MAYBE);
            log.error("При обновлении плотности материала {} произошла ошибка {}", item.getName(), e.getMessage());
}

    }
}
