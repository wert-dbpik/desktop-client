package ru.wert.tubus.chogori.entities.product_groups.commands;

import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.ProductGroup;
import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.chogori.common.commands.Catalogs;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.winform.warnings.Warning1;

import java.util.Collections;
import java.util.List;

import static ru.wert.tubus.winform.warnings.WarningMessages.*;

@Slf4j
public class ProductGroup_AddCommand<P extends Item> implements ICommand {

    private final _ProductGroup_Commands<P> commands;
    private final ProductGroup newItem;

    public ProductGroup_AddCommand(_ProductGroup_Commands<P> commands, ProductGroup newItem) {
        this.commands = commands;
        this.newItem = newItem;
    }

    @Override
    public void execute() {

        try {
            ProductGroup newGroup = ChogoriServices.CH_PRODUCT_GROUPS.save(newItem);
            if(newGroup != null){
                log.info("Добавлена группа изделий {}", newGroup.getName());
                AppStatic.createLog(false, String.format("Добавил группу изделий '%s'", newGroup.getName()));
                List<Item> items = Collections.singletonList(newGroup);
                Catalogs.updateFormsWhenAddedOrChanged(commands.getTreeView(), commands.getTableView(), items);
            }
        } catch (Exception e){
            Warning1.create($ATTENTION, $ERROR_WHILE_ADDING_ITEM, $SERVER_IS_NOT_AVAILABLE_MAYBE);
            log.error("При добавлении группы изделий {} произошла ошибка {} по причине {}",
                    newItem.getName(), e.getMessage(), e.getCause());
        }

    }


}
