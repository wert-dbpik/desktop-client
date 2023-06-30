package ru.wert.tubus.chogori.entities.materials;

import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import ru.wert.tubus.client.entity.models.AnyPart;
import ru.wert.tubus.client.entity.models.MatType;
import ru.wert.tubus.client.entity.models.Material;
import ru.wert.tubus.client.interfaces.CatalogGroup;
import ru.wert.tubus.chogori.common.contextMenuACC.FormView_ACCController;
import ru.wert.tubus.chogori.orthography.Orthography;
import ru.wert.tubus.chogori.application.services.ChogoriServices;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;

abstract class MaterialCommonTypeController extends FormView_ACCController<Material> {

    protected Material_ACCController materialAccController;
    protected TextField tfMaterialName;
    protected ComboBox<MatType> bxMatType;
    protected TextArea taMaterialNote;
    protected NumberFormat nf;

    public abstract void initMaterial();
    public abstract void setCatalogGroup(CatalogGroup treeView);

    void init(Material_ACCController materialAccController,
              TextField tfMaterialName,
              ComboBox<MatType> bxMatType,
              TextArea taMaterialNote){

        this.materialAccController = materialAccController;
        this.tfMaterialName = tfMaterialName;
        this.bxMatType = bxMatType;
        this.taMaterialNote = taMaterialNote;

        nf = new DecimalFormat("###.########");

        initMaterial();

    }



    /**
     * Возвращает новую запись из таблицы AnyPart
     */
    protected AnyPart createPart(){
        //Текущий элемент
        String newName = tfMaterialName.getText();
        AnyPart anyPart = new AnyPart(newName, "", ChogoriServices.CH_ANY_PART_GROUPS.findById(1L));
        return anyPart;
    }

    void textFieldOnKeyTyped(KeyEvent e) {
        Orthography.typeOnlyDigits(e);
    }

    /**
     * Проверяем текстовое поле на конвертацию в Double
     * @param textInputControl - текстовое поле
     * @return
     */
    Double getDoubleResult(TextInputControl textInputControl){
        Double res = null;
        try {
            res = Double.valueOf(Objects.requireNonNull(Orthography.onlyDigits(textInputControl.getText())));
        } catch (NumberFormatException e) {
            textInputControl.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            res = null;
        }
        return res;
    }



    /**
     * Расчет массы
     * @param paramS - толщина (t), диаметр (D), периметр P
     * @param paramX - плотность, масса пог. м. (Mпог.м)
     * @param paramA - габарит (А)
     * @param paramB - габарит (B)
     * @return
     */
    abstract double calculateMass(double paramS, double paramX, int paramA, int paramB);

    /**
     * Расчет площади
     * @param paramS - периметр P
     * @param paramX
     * @param paramA - габарит (А)
     * @param paramB - габарит (B)
     *
     * @return
     */

    abstract double calculateArea(double paramS, double paramX, int paramA, int paramB);
    /**
     * Расчет расхода краски
     * @param paramS - диаметр (D), периметр P
     * @param paramA - габарит (А)
     * @param paramB - габарит (B)
     * @param paramNR - норма расхода краски
     * @return
     */
    abstract double calculatePaint(double paramS, int paramA, int paramB, double paramNR);
}
