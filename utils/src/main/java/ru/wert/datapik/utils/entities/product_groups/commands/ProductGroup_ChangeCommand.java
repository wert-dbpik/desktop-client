package ru.wert.datapik.utils.entities.product_groups.commands;

import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.winform.warnings.Warning1;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_PRODUCT_GROUPS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class ProductGroup_ChangeCommand<P extends Item> implements ICommand {

    private final _ProductGroup_Commands<P> commands;
    private ProductGroup item;
    private ProductGroup_TreeView<P> treeView;
    private IFormView<P> tableView;

    /**
     *
     * @param treeView ProductGroup_TreeView
     */
    public ProductGroup_ChangeCommand(_ProductGroup_Commands<P> commands, ProductGroup item, ProductGroup_TreeView<P> treeView, IFormView<P> tableView) {
        this.commands = commands;
        this.item = item;
        this.treeView = treeView;
        this.tableView = tableView;

    }

    @Override
    public void execute() {

        try {
            boolean res = CH_PRODUCT_GROUPS.update(item);
            if(res){
                log.info("Изменена группа изделий {}", item.getName());
                commands.updateFormsWhenAddedOrChanged(item);

            }
        } catch (Exception e){
            Warning1.create($ATTENTION, $ERROR_WHILE_ADDING_ITEM, $SERVER_IS_NOT_AVAILABLE_MAYBE);
            log.error("При добавлении группы изделий {} произошла ошибка {} по причине {}",
                    item.getName(), e.getMessage(), e.getCause());
        }

    }
    }
