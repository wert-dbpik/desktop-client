package ru.wert.tubus.chogori.entities.densities.commands;

import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.entities.densities.Density_TableView;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.client.entity.models.Density;
import ru.wert.tubus.winform.warnings.Warning1;

import java.util.List;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_DENSITIES;
import static ru.wert.tubus.winform.warnings.WarningMessages.*;

@Slf4j
public class DensityDeleteCommand implements ICommand {

    private List<Density> items;
    private Density_TableView tableView;

    /**
     *
     * @param tableView Density_TableView
     */
    public DensityDeleteCommand(List<Density> items, Density_TableView tableView) {
        this.items = items;
        this.tableView = tableView;

    }

    @Override
    public void execute() {
        //После удаления таблица "подтянется вверх" и поэтому нужна позиция первого из удаляемых элементов
        int row = tableView.getItems().lastIndexOf(items.get(0));

        for(Density item : items){
            try {
                CH_DENSITIES.delete(item);
                log.info("Удалена плотность материала {}", item.getName());
                AppStatic.createLog(true, String.format("Удалил плотность материала '%s'", item.getName()));
            } catch (Exception e) {
                Warning1.create($ATTENTION, $ERROR_WHILE_DELETING_ITEM, $ITEM_IS_BUSY_MAYBE);
                log.error("При удалении плотности материала {} произошла ошибка {}", item.getName(), e.getMessage());

            }
        }

        tableView.easyUpdate(CH_DENSITIES);

        tableView.getSelectionModel().select(row);

    }
}
