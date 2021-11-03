package ru.wert.datapik.utils.entities.material_groups;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.MaterialGroup;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.utils.common.treeView.Item_TreeView;
import ru.wert.datapik.winform.enums.EOperation;

import java.util.ArrayList;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_MATERIAL_GROUPS;

@Slf4j
public class MaterialGroup_ACCController extends FormView_ACCController<MaterialGroup> {

    @FXML
    private TextField tfName;

    private MaterialGroup focusedItem;
    private MaterialGroup copiedGroup;

    private MaterialGroup_TreeView<MaterialGroup> treeView;

    @Override
    public void init(EOperation operation, IFormView<MaterialGroup> formView, ItemCommands<MaterialGroup> commands) {
        super.initSuper(operation, formView, commands, CH_MATERIAL_GROUPS);
        this.treeView = (MaterialGroup_TreeView<MaterialGroup>) formView;

        setInitialValues();

    }


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
    public MaterialGroup getNewItem() {

        Long parentId;
        TreeItem<MaterialGroup> chosenItem = ((Item_TreeView<Product, MaterialGroup>)formView)
                .getSelectionModel().getSelectedItem();

        if(chosenItem == null ) {
            parentId = treeView.getRoot().getValue().getId(); //=1L
        }

        else {
            if (operation.equals(EOperation.COPY)) //При копировании берется родитель родителя
                parentId = ((Item_TreeView<Product, MaterialGroup>) formView)
                        .getSelectionModel().getSelectedItem().getParent().getValue().getId();
            else {//При добавлении и изменении просто родитель
                parentId = ((Item_TreeView<Product, MaterialGroup>) formView)
                        .getSelectionModel().getSelectedItem().getValue().getId();
            }
        }

        MaterialGroup group = new MaterialGroup();
        group.setName(tfName.getText().trim());
        group.setParentId(parentId);

        return group;
    }



    @Override
    public void fillFieldsOnTheForm(MaterialGroup oldItem) {
        tfName.setText(oldItem.getName());
    }

    @Override
    public void changeOldItemFields(MaterialGroup oldItem) {
        oldItem.setName(tfName.getText().trim());
    }

    @Override
    public void showEmptyForm() {

    }

}
