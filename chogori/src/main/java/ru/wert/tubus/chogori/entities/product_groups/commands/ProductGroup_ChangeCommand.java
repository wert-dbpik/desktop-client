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
public class ProductGroup_ChangeCommand<P extends Item> implements ICommand {

    private final _ProductGroup_Commands<P> commands;
    private ProductGroup item;


    /**
     *
     */
    public ProductGroup_ChangeCommand(_ProductGroup_Commands<P> commands, ProductGroup item) {
        this.commands = commands;
        this.item = item;
    }

    @Override
    public void execute() {

        try {
            boolean res = ChogoriServices.CH_PRODUCT_GROUPS.update(item);
            if(res){
                log.info("Изменена группа изделий {}", item.getName());
                AppStatic.createLog(false, String.format("Изменил группу изделий '%s'", item.getName()));
                List<Item> items = Collections.singletonList(item);
                Catalogs.updateFormsWhenAddedOrChanged(commands.getTreeView(), commands.getTableView(), items);

            }
        } catch (Exception e){
            Warning1.create($ATTENTION, $ERROR_WHILE_ADDING_ITEM, $SERVER_IS_NOT_AVAILABLE_MAYBE);
            log.error("При добавлении группы изделий {} произошла ошибка {} по причине {}",
                    item.getName(), e.getMessage(), e.getCause());
        }

    }
    }
