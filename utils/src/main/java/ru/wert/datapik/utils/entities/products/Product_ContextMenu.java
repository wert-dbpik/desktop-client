package ru.wert.datapik.utils.entities.products;

import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.datapik.utils.entities.products.commands._Product_Commands;

import java.util.ArrayList;
import java.util.List;

public class Product_ContextMenu extends FormView_ContextMenu<Product> {

    private final _Product_Commands commands;
    private TableView<Product> tableView;

    private MenuItem lickBoots;
    private MenuItem blueWaser;


    public Product_ContextMenu(Product_TableView tableView, _Product_Commands commands, String productsACCRes) {
        super(tableView, commands, productsACCRes);
        this.commands = commands;
        this.tableView = tableView;

        createOnShowing();

    }

    @Override
    public void createOnShowing() {
        boolean addItem = true;
        boolean copyItem =true;
        boolean changeItem = true;
        boolean deleteItem = true;

        List<Product> selectedProducts = tableView.getSelectionModel().getSelectedItems();

        if(selectedProducts.size() == 0){
            copyItem = false;
            changeItem = false;
            deleteItem = false;
        } else if(selectedProducts.size() > 1){
            copyItem = false;
            changeItem = false;
        }

        createMenu(addItem, copyItem, changeItem, deleteItem);
    }

    @Override
    public List<MenuItem> createExtraItems(){

        List<MenuItem> extraItems = new ArrayList<>();
        lickBoots = new MenuItem("Лизать сапог");

        lickBoots.setOnAction(commands::lick);
        blueWaser = new MenuItem("Блевать в вазу");

        extraItems.add(lickBoots);
        extraItems.add(blueWaser);

        return extraItems;
    }



}
