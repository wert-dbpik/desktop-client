package ru.wert.datapik.chogori.entities.material_groups.commands;

import javafx.event.Event;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.client.entity.models.MaterialGroup;
import ru.wert.datapik.chogori.common.commands.ICommand;
import ru.wert.datapik.chogori.common.commands.ItemCommands;
import ru.wert.datapik.chogori.common.contextMenuACC.FormViewACCWindow;
import ru.wert.datapik.chogori.entities.material_groups.MaterialGroup_TreeView;
import ru.wert.datapik.chogori.entities.products.Product_TableView;
import ru.wert.datapik.chogori.entities.products.commands._Product_Commands;
import ru.wert.datapik.winform.enums.EOperation;

import java.util.List;

public class _MaterialGroup_Commands implements ItemCommands<MaterialGroup> {

    private final MaterialGroup_TreeView treeView;

    public _MaterialGroup_Commands(MaterialGroup_TreeView treeView) {
        this.treeView = treeView;
    }

    @Override
    public void add(Event event, MaterialGroup newItem){
        ICommand command = new MaterialGroup_AddCommand(newItem, treeView);
        command.execute();
    }

    @Override
    public void copy(Event event){
        System.out.println("added with copy");
    }

    @Override
    public void delete(Event event, List<MaterialGroup> items){
        ICommand command = new MaterialGroup_DeleteCommand(items.get(0), treeView);
        command.execute();
    }

    @Override
    public void change(Event event, MaterialGroup item){
        ICommand command = new MaterialGroup_ChangeCommand(item, treeView);
        command.execute();
    }

    public void addProductToFolder(Event event){
        Product_TableView formView = (Product_TableView) treeView.getConnectedForm();
        _Product_Commands commands = (_Product_Commands) formView.getCommands();
        String itemACCRes = formView.getAccWindowRes();

        new FormViewACCWindow<Product>().create(EOperation.ADD, formView, commands, itemACCRes);
    }
}
