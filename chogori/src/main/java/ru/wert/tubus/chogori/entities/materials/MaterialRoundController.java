package ru.wert.tubus.chogori.entities.materials;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.AnyPart;
import ru.wert.tubus.client.entity.models.Material;
import ru.wert.tubus.client.entity.models.MaterialGroup;
import ru.wert.tubus.client.interfaces.CatalogGroup;
import ru.wert.tubus.client.utils.BLlinks;
import ru.wert.tubus.chogori.common.commands.ItemCommands;
import ru.wert.tubus.chogori.common.interfaces.IFormView;
import ru.wert.tubus.chogori.orthography.Orthography;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.winform.enums.EOperation;

import java.util.ArrayList;
import java.util.Objects;

@Slf4j
public class MaterialRoundController extends MaterialCommonTypeController {


    @FXML private TextField txtFldDiametre;
    @FXML private TextField txtFldMassMetre;

    private Material_ACCController materialAccController;
    private MaterialGroup materialTreeGroup = null;
    @Setter private CatalogGroup catalogGroup;


    public MaterialRoundController() {
        log.debug("{} создан", this.getClass().getSimpleName());
    }

    @FXML
    void textFieldOnKeyTyped(KeyEvent e){
            super.textFieldOnKeyTyped(e);
    }

    /**
     * Расчет массы
     *
     * @param paramS - не используется
     * @param paramX - масса погонного метра, кг/м
     * @param paramA - длина, мм (L)
     * @param paramB - не используется
     * @return
     */
    @Override
    double calculateMass(double paramS, double paramX, int paramA, int paramB) {

        return paramX * paramA; //толщина * А * B * плотность, кг
    }

    /**
     * Расчет площади
     *
     * @param paramS - диаметр, мм (D)
     * @param paramX - не используется
     * @param paramA - длина, мм (L)
     * @param paramB - не используется
     * @return
     */
    @Override
    double calculateArea(double paramS, double paramX, int paramA, int paramB) {
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

    @Override
    public void initMaterial() {

        //Сброс стилей для текстового поля необходим при исправлении найденных ошибок
        txtFldDiametre.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) txtFldDiametre.setStyle("");
        });

        txtFldMassMetre.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) txtFldMassMetre.setStyle("");
        });
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
            notNullFields.add(txtFldDiametre.getText());
            notNullFields.add(txtFldMassMetre.getText());

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

        Double diameter = getDoubleResult(txtFldDiametre);
        Double massMetre = getDoubleResult(txtFldMassMetre);

        if(diameter != null && massMetre != null)
            return new Material(
                    anyPart,
                    (MaterialGroup)catalogGroup,
                    tfMaterialName.getText().replaceAll("[\\s]{2,}", " ").trim(),
                    bxMatType.getValue(),
                    taMaterialNote.getText(),
                    //------------------- поля этого класса ----------------
                    diameter,
                    massMetre
            );
        //Если ввод неверный, то возвращаем null
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
        materialTreeGroup = oldItem.getCatalogGroup();
        txtFldDiametre.setText(Orthography.onlyDigits(nf.format(oldItem.getParamS())));
        txtFldMassMetre.setText(Orthography.onlyDigits(nf.format(oldItem.getParamX())));

    }

    @Override
    public void changeOldItemFields(Material oldItem) {
        //------------------- поля этого класса ----------------
        oldItem.setParamS(Double.valueOf(Objects.requireNonNull(Orthography.onlyDigits(txtFldDiametre.getText()))));
        oldItem.setParamX(Double.valueOf(Objects.requireNonNull(Orthography.onlyDigits(txtFldMassMetre.getText()))));
    }

    @Override
    public void showEmptyForm() {
        tfMaterialName.setText("");
        //Изменить на последний используемый!!!!
        bxMatType.setValue(BLlinks.matTypeService.findByName("Листовой"));
        taMaterialNote.setText("");
        //------------------- поля этого класса ----------------
        txtFldDiametre.setText("");
        txtFldMassMetre.setText("");
    }

    @Override
    public boolean enteredDataCorrect() {
        return true;
    }

}
