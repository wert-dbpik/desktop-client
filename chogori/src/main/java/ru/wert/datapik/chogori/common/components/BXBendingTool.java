package ru.wert.datapik.chogori.common.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import ru.wert.datapik.chogori.calculator.part_calculator.EBendingTool;


public class BXBendingTool {

    public static EBendingTool LAST_BENDING_TOOL;
    private ComboBox<EBendingTool> bxBendingTool;

    public void create(ComboBox<EBendingTool> bxBendingTool){
        this.bxBendingTool = bxBendingTool;
        ObservableList<EBendingTool> bendingTools = FXCollections.observableArrayList(EBendingTool.values());
        bxBendingTool.setItems(bendingTools);

        createCellFactory();
        //Выделяем префикс по умолчанию
        createConverter();

        if(LAST_BENDING_TOOL == null)
            LAST_BENDING_TOOL = EBendingTool.UNIVERSAL;
        bxBendingTool.setValue(LAST_BENDING_TOOL);

    }

    private void createCellFactory() {
        //CellFactory определяет вид элементов комбобокса - только имя префикса
        bxBendingTool.setCellFactory(i -> new ListCell<EBendingTool>() {
            @Override
            protected void updateItem (EBendingTool item, boolean empty){
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getToolName());
                }
            }

        });
    }

    private void createConverter() {
        bxBendingTool.setConverter(new StringConverter<EBendingTool>() {
            @Override
            public String toString(EBendingTool bendingTool) {
                LAST_BENDING_TOOL = bendingTool; //последний выбранный префикс становится префиксом по умолчанию
                return bendingTool.getToolName();
            }

            @Override
            public EBendingTool fromString(String string) {
                return null;
            }
        });
    }
}
