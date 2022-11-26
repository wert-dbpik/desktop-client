package ru.wert.datapik.chogori.calculator;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.chogori.calculator.components.BXTimeMeasurement;
import ru.wert.datapik.chogori.calculator.entities.OpData;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.chogori.calculator.AbstractOpPlate.*;

public class FormAssmController implements IFormMenu, IForm {

    @FXML
    private TextField tfAssmName;

    @FXML @Getter
    private ComboBox<ETimeMeasurement> cmbxTimeMeasurement;

    @FXML @Getter
    private ListView<VBox> listViewTechOperations;

    @FXML
    private ImageView ivAddOperation;

    @FXML
    private ImageView ivErase;

    @FXML
    private ImageView ivHelpOnTechnologicalProcessing;

    @FXML
    private TextField tfMechanicalTime;

    @FXML
    private TextField tfPaintingTime;

    @FXML
    private TextField tfAssemblingTime;

    @FXML
    private Label lblTimeMeasure;

    @FXML @Getter
    private TextField tfTotalTime;


    @Getter private ObservableList<AbstractOpPlate> addedPlates;
    @Getter private List<OpData> addedOperations;


    @Override
    public void init(TextField tfName) {
        tfAssmName.setText(tfName.getText());
        tfName.textProperty().bindBidirectional(tfAssmName.textProperty());

    }


    @FXML
    void initialize(){

        addedPlates = FXCollections.observableArrayList();
        addedOperations = new ArrayList<>();

        new BXTimeMeasurement().create(cmbxTimeMeasurement);

        MenuCalculator menu = new MenuCalculator(this, addedPlates, listViewTechOperations, addedOperations);

        menu.getItems().add(menu.getAddDetail());
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().addAll(menu.getAddWeldLongSeam(), menu.getAddWeldingDotted());
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().addAll(menu.getAddPaintingAssembling());
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().addAll(menu.getAddAssemblingNuts(), menu.getAddAssemblingCuttings(), menu.getAddAssemblingNodes());
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(menu.getAddLevelingSealer());

        ivAddOperation.setOnMouseClicked(e->{
            menu.show(ivAddOperation, Side.LEFT, -15.0, 30.0);
        });

        cmbxTimeMeasurement.valueProperty().addListener((observable, oldValue, newValue) -> {
            for(AbstractOpPlate nc : addedPlates){
                nc.setTimeMeasurement(newValue);
            }

            countSumNormTimeByShops();

            lblTimeMeasure.setText(newValue.getTimeName());
        });


    }


    /**
     * Метод расчитывает суммарное время по участкам
     */
    public void countSumNormTimeByShops(){
        double mechanicalTime = 0.0;
        double paintingTime = 0.0;
        double assemblingTime = 0.0;
        double packingTime = 0.0;
        for(AbstractOpPlate cn: addedPlates){
            if(cn.getNormType().equals(ENormType.NORM_MECHANICAL))
                mechanicalTime += cn.getCurrentNormTime();
            else if(cn.getNormType().equals(ENormType.NORM_PAINTING))
                paintingTime += cn.getCurrentNormTime();
            else if(cn.getNormType().equals(ENormType.NORM_ASSEMBLING))
                assemblingTime += cn.getCurrentNormTime();
            else if(cn.getNormType().equals(ENormType.NORM_PACKING))
                packingTime += cn.getCurrentNormTime();
        }

        if(cmbxTimeMeasurement.getValue().equals(ETimeMeasurement.SEC)){
            mechanicalTime = mechanicalTime * MIN_TO_SEC;
            paintingTime = paintingTime * MIN_TO_SEC;
            assemblingTime = assemblingTime * MIN_TO_SEC;
        }

        String format = doubleFormat;
        if(cmbxTimeMeasurement.getValue().equals(ETimeMeasurement.SEC)) format = integerFormat;

        tfMechanicalTime.setText(String.format(format, mechanicalTime));
        tfPaintingTime.setText(String.format(format, paintingTime));
        tfAssemblingTime.setText(String.format(format, assemblingTime));

        tfTotalTime.setText(String.format(format, mechanicalTime + paintingTime + assemblingTime + packingTime));

    }

}
