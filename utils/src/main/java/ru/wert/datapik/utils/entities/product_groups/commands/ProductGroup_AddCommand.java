package ru.wert.datapik.utils.entities.product_groups.commands;

import javafx.event.Event;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.commands.Catalogs;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.winform.warnings.Warning1;

import java.util.Collections;
import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_PRODUCT_GROUPS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

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
            ProductGroup newGroup = CH_PRODUCT_GROUPS.save(newItem);
            if(newGroup != null){
                log.info("Добавлена группа изделий {}", newGroup.getName());
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
