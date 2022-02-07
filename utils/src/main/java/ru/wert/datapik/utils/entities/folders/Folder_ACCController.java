package ru.wert.datapik.utils.entities.folders;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.*;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.components.BXPrefix;
import ru.wert.datapik.utils.common.components.BXProductGroup;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_Chooser;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.winform.enums.EOperation;

import java.util.ArrayList;

import static ru.wert.datapik.utils.services.ChogoriServices.*;
import static ru.wert.datapik.utils.statics.AppStatic.*;

@Slf4j
public class Folder_ACCController extends FormView_ACCController<Folder> {


    @FXML
    private ComboBox<Prefix> bxPrefix;

    @FXML
    private TextField tfNumber;

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


    @Override
    public void init(EOperation operation, IFormView<Folder> formView, ItemCommands<Folder> commands) {
        super.initSuper(operation, formView, commands, CH_QUICK_FOLDERS);

        //Создаем комбобоксы
        new BXPrefix().create(bxPrefix); //ПРЕФИКСЫ
        new BXProductGroup().create(bxGroup); //ГРУППА ИЗДЕЛИЙ

        //Устанавливаем начальные значения полей в зависимости от operation
        setInitialValues();

    }

    @FXML
    void initialize() {
        AppStatic.createSpIndicator(spIndicator);
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
        notNullFields.add(tfNumber.getText());
        notNullFields.add(tfName.getText());
        return notNullFields;
    }

    @Override
    public Folder getNewItem() {
        return new Folder(
                bxGroup.getValue(),
                combineDecNumber(),
                tfName.getText().trim(),
                taNote.getText().trim()
        );
    }

    @Override
    public Folder getOldItem() {
//        return tableView.getSelectionModel().getSelectedItems().get(0);
        return null;
    }

    @Override
    public void fillFieldsOnTheForm(Folder oldItem) {
        bxPrefix.setValue(getPrefixInDecNumber(oldItem.getDecNumber()));
        tfNumber.setText(getShortDecNumber(oldItem.getDecNumber()));
        tfName.setText(oldItem.getName());
        bxGroup.setValue(oldItem.getProductGroup());
        taNote.setText(oldItem.getNote());
    }

    @Override
    public void changeOldItemFields(Folder oldItem) {
        oldItem.setProductGroup(bxGroup.getValue());
        oldItem.setDecNumber(combineDecNumber());
        oldItem.setName(tfName.getText().trim());
        oldItem.setNote(taNote.getText().trim());
    }

    @Override
    public void showEmptyForm() {

        setComboboxPrefixValue(bxPrefix);
        setComboboxProductGroupValue(bxGroup);

    }


    private String combineDecNumber(){
        String decNumber;
        Prefix prefix =  bxPrefix.getValue();
        if(prefix == null || prefix.getName().equals("-")){
            decNumber = tfNumber.getText().trim();
        } else {
            decNumber = bxPrefix.getValue().getName() + "." + tfNumber.getText().trim();
        }
        return decNumber;
    }
}
