package ru.wert.tubus.chogori.entities.products.commands;

import javafx.event.Event;
import ru.wert.tubus.client.entity.models.Product;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.common.commands.ItemCommands;
import ru.wert.tubus.chogori.entities.products.Product_TableView;


import java.util.List;

public class _Product_Commands implements ItemCommands<Product> {

    private final Product_TableView tableView;

    public _Product_Commands(Product_TableView tableView) {
        this.tableView = tableView;
    }

    @Override
    public void add(Event event, Product newItem){
        ICommand command = new Product_AddCommand(newItem, tableView);
        command.execute();
    }

    @Override
    public void copy(Event event){
        System.out.println("added with copy");
    }

    @Override
    public void delete(Event event, List<Product> items){
        ICommand command = new Product_DeleteCommand(items, tableView);
        command.execute();
    }

    @Override
    public void change(Event event, Product item){
        ICommand command = new Product_ChangeCommand(item, tableView);
        command.execute();
    }

    public void lick(Event event){System.out.println("killed");}
}
