package ru.wert.tubus.chogori.entities.folders;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.common.commands.ItemCommands;
import ru.wert.tubus.chogori.components.BXProductGroup;
import ru.wert.tubus.chogori.common.contextMenuACC.FormView_ACCController;
import ru.wert.tubus.chogori.common.interfaces.IFormView;
import ru.wert.tubus.chogori.common.tableView.ItemTableView;
import ru.wert.tubus.chogori.entities.product_groups.ProductGroup_Chooser;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.client.entity.models.Folder;
import ru.wert.tubus.client.entity.models.ProductGroup;
import ru.wert.tubus.winform.enums.EOperation;

import java.util.ArrayList;

@Slf4j
public class Folder_ACCController extends FormView_ACCController<Folder> {

    @FXML
    private TextField tfName;

    @FXML
    private Button btnFindPGroup;

    @FXML
    private ComboBox<ProductGroup> bxGroup;

    @FXML
    private TextArea taNote;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnOk;

    @FXML
    private StackPane spIndicator;

    private ProductGroup group;

    @Setter
    private ProductGroup selectedGroup = null;


    @Override
    public void init(EOperation operation, IFormView<Folder> formView, ItemCommands<Folder> commands) {
        super.initSuper(operation, formView, commands, ChogoriServices.CH_QUICK_FOLDERS);

        //Создаем комбобоксы
        new BXProductGroup().create(bxGroup,
                selectedGroup); //ГРУППА ИЗДЕЛИЙ

        //Устанавливаем начальные значения полей в зависимости от operation
        setInitialValues();

    }

    @FXML
    void initialize() {
        AppStatic.createSpIndicator(spIndicator);

        Platform.runLater(()->tfName.requestFocus());
    }

    @FXML
    void findProductGroup(ActionEvent event) {
        group = bxGroup.getValue();

        ProductGroup productGroup = ProductGroup_Chooser.create(((Node) event.getSource()).getScene().getWindow());
        if (productGroup != null) {
            this.group = productGroup;
            bxGroup.getSelectionModel().select(group);
        }
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
    public Folder getNewItem() {
        return new Folder(
                bxGroup.getValue(),
                tfName.getText().trim(),
                taNote.getText().trim()
        );
    }

    @Override
    public Folder getOldItem() {
        return ((ItemTableView<Folder>)formView).getSelectionModel().getSelectedItems().get(0);
    }

    @Override
    public void fillFieldsOnTheForm(Folder oldItem) {
        tfName.setText(oldItem.getName());
        bxGroup.setValue(oldItem.getProductGroup());
        taNote.setText(oldItem.getNote());
    }

    @Override
    public void changeOldItemFields(Folder oldItem) {
        oldItem.setProductGroup(bxGroup.getValue());
        oldItem.setName(tfName.getText().trim());
        oldItem.setNote(taNote.getText().trim());
    }

    @Override
    public void showEmptyForm() {

        if(selectedGroup == null)
            setComboboxProductGroupValue(bxGroup);

    }

    @Override
    public boolean enteredDataCorrect() {
        return true;
    }

}
