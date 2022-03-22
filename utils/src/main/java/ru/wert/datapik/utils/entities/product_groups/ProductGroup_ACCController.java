package ru.wert.datapik.utils.entities.product_groups;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.*;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.treeView.Item_TreeView;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.winform.enums.EOperation;

import java.util.ArrayList;

import static ru.wert.datapik.utils.services.ChogoriServices.*;

@Slf4j
public class ProductGroup_ACCController extends FormView_ACCController<ProductGroup> {

    @FXML
    private TextField tfName;

    @FXML
    private StackPane spIndicator;

    @FXML
    private Button btnOk;

    private TableView<Item> tableView = null;

    private boolean changesInTableView = false;

    private ProductGroup_TreeView<ProductGroup> treeView;

    @Override
    public void init(EOperation operation, IFormView<ProductGroup> formView, ItemCommands<ProductGroup> commands) {
        super.initSuper(operation, formView, commands, CH_PRODUCT_GROUPS);
        this.treeView = (ProductGroup_TreeView<ProductGroup>) formView;

        setInitialValues();

    }

    @FXML
    void initialize(){
        AppStatic.createSpIndicator(spIndicator);
    }

    @FXML
    void cancel(ActionEvent event) {
        super.cancelPressed(event);
    }

    @FXML
    void ok(ActionEvent event) {
        super.okPressed(event, spIndicator, btnOk);
    }

    @Override
    public ArrayList<String> getNotNullFields() {
        ArrayList<String> notNullFields = new ArrayList<>();
        notNullFields.add(tfName.getText());
        return notNullFields;
    }

    @Override
    public ProductGroup getNewItem() {

        Long parentId;
        TreeItem<ProductGroup> chosenItem = null;
        if (!changesInTableView) //Если мы имеем дело только с деревом каталогом
            chosenItem = ((Item_TreeView<Product, ProductGroup>) formView).getSelectionModel().getSelectedItem();
        else {//Если имеем дело с таблицей и деревом каталога
            Item selectedItem = tableView.getSelectionModel().getSelectedItem();
            if(selectedItem == null) //Если клик по пустому полю
                chosenItem = treeView.findTreeItemById(tableView.getItems().get(0).getId());
            else //Если клик по элементу с папкой
                chosenItem = treeView.findTreeItemById(((ProductGroup) selectedItem).getId());
        }

        //Если добавление проимсходит в корень каталога
        if (chosenItem == null) {
            parentId = treeView.getRoot().getValue().getId(); //=1L
        }

        else {
            if (operation.equals(EOperation.COPY)) //При копировании берется родитель родителя
                parentId = chosenItem.getParent().getValue().getId();
            else {//При добавлении и изменении просто родитель
                parentId = chosenItem.getValue().getId();
            }
        }

        ProductGroup group = new ProductGroup();
        group.setName(tfName.getText().trim());
        group.setParentId(parentId);

        return group;
    }

    @Override
    public ProductGroup getOldItem() {
        if(tableView == null)
            return treeView.getSelectionModel().getSelectedItems().get(0).getValue();
        else
            return (ProductGroup) tableView.getSelectionModel().getSelectedItems().get(0);
    }

    public void setTableView(TableView<Item> tableView) {
        this.tableView = tableView;
    }

    public void setChangesInTableView(boolean changesInTableView){
        this.changesInTableView = changesInTableView;
    }

    @Override
    public void fillFieldsOnTheForm(ProductGroup oldItem) {
        if(tableView != null) {
            oldItem = ((ProductGroup)tableView.getSelectionModel().getSelectedItem());
        }
        tfName.setText(oldItem.getName());

    }

    @Override
    public void changeOldItemFields(ProductGroup oldItem) {
        if(tableView != null) {
            oldItem = ((ProductGroup)tableView.getSelectionModel().getSelectedItem());
        }
        oldItem.setName(tfName.getText().trim());
    }

    @Override
    public void showEmptyForm() {

    }

    @Override
    public boolean enteredDataCorrect() {
        return true;
    }
}
