package ru.wert.datapik.chogori.entities.material_groups;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.MaterialGroup;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.chogori.common.commands.ItemCommands;
import ru.wert.datapik.chogori.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.chogori.common.interfaces.IFormView;
import ru.wert.datapik.chogori.common.treeView.Item_TreeView;
import ru.wert.datapik.chogori.statics.AppStatic;
import ru.wert.datapik.winform.enums.EOperation;

import java.util.ArrayList;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_MATERIAL_GROUPS;

@Slf4j
public class MaterialGroup_ACCController extends FormView_ACCController<MaterialGroup> {

    @FXML
    private TextField tfName;

    @FXML
    private StackPane spIndicator;

    @FXML
    private Button btnOk;

    private MaterialGroup focusedItem;
    private MaterialGroup copiedGroup;

    private MaterialGroup_TreeView<MaterialGroup> treeView;

    @FXML
    void initialize(){
        AppStatic.createSpIndicator(spIndicator);
    }

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
        super.okPressed(event, spIndicator, btnOk);
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
    public MaterialGroup getOldItem() {
        return formView.getAllSelectedItems().get(0);
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

    @Override
    public boolean enteredDataCorrect() {
        return true;
    }

}