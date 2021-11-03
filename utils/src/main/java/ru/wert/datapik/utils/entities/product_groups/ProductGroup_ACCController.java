package ru.wert.datapik.utils.entities.product_groups;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.*;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.treeView.Item_TreeView;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.winform.enums.EOperation;

import java.util.ArrayList;

import static ru.wert.datapik.utils.services.ChogoriServices.*;

@Slf4j
public class ProductGroup_ACCController extends FormView_ACCController<ProductGroup> {

    @FXML
    private TextField tfName;

    private ProductGroup focusedItem;
    private ProductGroup copiedGroup;

    private ProductGroup_TreeView<ProductGroup> treeView;

    @Override
    public void init(EOperation operation, IFormView<ProductGroup> formView, ItemCommands<ProductGroup> commands) {
        super.initSuper(operation, formView, commands, CH_PRODUCT_GROUPS);
        this.treeView = (ProductGroup_TreeView<ProductGroup>) formView;

        setInitialValues();

    }

    @FXML
    void initialize(){    }


    @FXML
    void cancel(ActionEvent event) {
        super.cancelPressed(event);
    }

    @FXML
    void ok(ActionEvent event) {
        super.okPressed(event);
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
        TreeItem<ProductGroup> chosenItem = ((Item_TreeView<Product, ProductGroup>)formView)
                .getSelectionModel().getSelectedItem();

        if(chosenItem == null ) {
            parentId = treeView.getRoot().getValue().getId(); //=1L
        }

        else {
            if (operation.equals(EOperation.COPY)) //При копировании берется родитель родителя
                parentId = ((Item_TreeView<Product, ProductGroup>) formView)
                        .getSelectionModel().getSelectedItem().getParent().getValue().getId();
            else {//При добавлении и изменении просто родитель
                parentId = ((Item_TreeView<Product, ProductGroup>) formView)
                        .getSelectionModel().getSelectedItem().getValue().getId();
            }
        }

        ProductGroup group = new ProductGroup();
        group.setName(tfName.getText().trim());
        group.setParentId(parentId);

        return group;
    }



    @Override
    public void fillFieldsOnTheForm(ProductGroup oldItem) {
        tfName.setText(oldItem.getName());
    }

    @Override
    public void changeOldItemFields(ProductGroup oldItem) {
        oldItem.setName(tfName.getText().trim());
    }

    @Override
    public void showEmptyForm() {

    }

}
