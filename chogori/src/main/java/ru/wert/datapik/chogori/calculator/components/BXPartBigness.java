package ru.wert.datapik.chogori.calculator.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import ru.wert.datapik.chogori.calculator.enums.EBendingTool;
import ru.wert.datapik.chogori.calculator.enums.EPartBigness;


public class BXPartBigness {

    public static EPartBigness LAST_PART_BIGNESS;
    private ComboBox<EPartBigness> bxPartBigness;

    public void create(ComboBox<EPartBigness> bxPartBigness){
        this.bxPartBigness = bxPartBigness;
        ObservableList<EPartBigness> partBignesses = FXCollections.observableArrayList(EPartBigness.values());
        bxPartBigness.setItems(partBignesses);

        createCellFactory();
        //Выделяем префикс по умолчанию
        createConverter();

        if(LAST_PART_BIGNESS == null)
            LAST_PART_BIGNESS = EPartBigness.SMALL;
        bxPartBigness.setValue(LAST_PART_BIGNESS);

    }

    private void createCellFactory() {
        //CellFactory определяет вид элементов комбобокса - только имя префикса
        bxPartBigness.setCellFactory(i -> new ListCell<EPartBigness>() {
            @Override
            protected void updateItem (EPartBigness item, boolean empty){
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
        bxPartBigness.setConverter(new StringConverter<EPartBigness>() {
            @Override
            public String toString(EPartBigness partBigness) {
                LAST_PART_BIGNESS = partBigness; //последний выбранный префикс становится префиксом по умолчанию
                return partBigness.getToolName();
            }

            @Override
            public EPartBigness fromString(String string) {
                return null;
            }
        });
    }
}
