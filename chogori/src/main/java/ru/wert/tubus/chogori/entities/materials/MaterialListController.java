package ru.wert.tubus.chogori.entities.materials;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.AnyPart;
import ru.wert.tubus.client.entity.models.Density;
import ru.wert.tubus.client.entity.models.Material;
import ru.wert.tubus.client.entity.models.MaterialGroup;
import ru.wert.tubus.client.interfaces.CatalogGroup;
import ru.wert.tubus.chogori.common.commands.ItemCommands;
import ru.wert.tubus.chogori.common.components.BXDensity;
import ru.wert.tubus.chogori.common.interfaces.IFormView;
import ru.wert.tubus.chogori.orthography.Orthography;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.winform.enums.EOperation;

import java.util.ArrayList;
import java.util.Objects;


@Slf4j
public class MaterialListController extends MaterialCommonTypeController {


    @FXML
    private ComboBox<Density> bxDensity;

    @FXML
    private TextField txtFldThickness;

    private Material_ACCController materialAccController;
    @Setter private CatalogGroup catalogGroup;


    public MaterialListController() {
        log.debug("{} создан", this.getClass().getSimpleName());
    }

    @FXML
    void textFieldOnKeyTyped(KeyEvent e){
            super.textFieldOnKeyTyped(e);
    }

    @Override
    public void initMaterial() {
        //Сброс стилей для текстового поля необходим при исправлении найденных ошибок
        txtFldThickness.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) txtFldThickness.setStyle("");
        });

        //Инициализация комбобокса с плотность материалов
        bxDensity = new BXDensity().create(bxDensity);
    }

    @Override
    public void init(EOperation operation, IFormView<Material> formView, ItemCommands<Material> commands) {

    }

    @Override
    public ArrayList<String> getNotNullFields() {
        ArrayList<String> notNullFields = new ArrayList<>();

        notNullFields.add(tfMaterialName.getText());
        if (bxMatType.getValue() == null) notNullFields.add("");
        else
            notNullFields.add(bxMatType.getValue().getName());
        //------------------- поля этого класса ----------------
            notNullFields.add(txtFldThickness.getText());
            notNullFields.add(bxDensity.getValue().toString());

        return notNullFields;
    }

    @Override
    public Material getNewItem() {

        //Текущий элемент
        AnyPart anyPart = createPart();

        //Группа в которую сохраняется материал
//        if(materialTreeGroup == null) {
//            materialTreeGroup =
//                    CH_MATERIAL_GROUPS.findById(treeView.getSelectionModel().getSelectedItem().getValue().getId());
//        }

        Double thickness = getDoubleResult(txtFldThickness);

        if(thickness != null)
            return new Material(
                    anyPart,
                    (MaterialGroup) catalogGroup,
                    tfMaterialName.getText().replaceAll("[\\s]{2,}", " ").trim(),
                    bxMatType.getValue(),
                    taMaterialNote.getText(),
                    //------------------- поля этого класса ----------------
                    Double.valueOf(txtFldThickness.getText()),
                    ((Density) bxDensity.getValue()).getAmount()
            );
        ChogoriServices.CH_QUICK_ANY_PARTS.delete(anyPart);
        return null;
    }

    @Override
    public Material getOldItem() {
        return formView.getAllSelectedItems().get(0);
    }

    @Override
    public void fillFieldsOnTheForm(Material oldItem) {
        //------------------- поля этого класса ----------------
        catalogGroup = oldItem.getCatalogGroup();
        txtFldThickness.setText(Orthography.onlyDigits(nf.format(oldItem.getParamS())));
        bxDensity.getSelectionModel().select(ChogoriServices.CH_DENSITIES.findByValue(oldItem.getParamX()));


    }

    @Override
    public void changeOldItemFields(Material oldItem) {
        //------------------- поля этого класса ----------------
        oldItem.setParamS(Double.valueOf(Objects.requireNonNull
                (Orthography.onlyDigits(txtFldThickness.getText()))));
        oldItem.setParamX(((Density) bxDensity.getValue()).getAmount());
    }

    @Override
    public void showEmptyForm() {
        //------------------- поля этого класса ----------------
        txtFldThickness.setText("");
        bxDensity.getSelectionModel().select(ChogoriServices.CH_DENSITIES.findByName("сталь"));
    }

    @Override
    public boolean enteredDataCorrect() {
        return true;
    }

    /**
     * Расчет массы
     *
     * @param paramS - толщина (t)
     * @param paramX - плотность
     * @param paramA - габарит (А)
     * @param paramB - габарит (B)
     * @return
     */
    @Override
    double calculateMass(double paramS, double paramX, int paramA, int paramB) {

        return paramS * paramA * paramB * paramX; //толщина * А * B * плотность, кг
    }

    /**
     * Расчет площади
     *
     * @param paramS - не используется
     * @param paramA - габарит (А)
     * @param paramB - габарит (B)
     * @return
     */
    @Override
    double calculateArea(double paramS,  double paramX, int paramA, int paramB) {
        return 2 * paramA * paramB * 0.000001; // 2 стороны * А * В * 0,000001,  м2
    }

    /**
     * Расчет расхода краски
     *
     * @param paramS  - не используется
     * @param paramA  - габарит (А)
     * @param paramB  - габарит (B)
     * @param paramNR - норма расхода краски
     * @return
     */
    @Override
    double calculatePaint(double paramS, int paramA, int paramB, double paramNR) {
        return (2 * paramA * paramB * 0.000001) * paramNR ; //площадь * норму расхода краски
    }

}
