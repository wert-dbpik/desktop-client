package ru.wert.datapik.chogori.calculator.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import ru.wert.datapik.chogori.calculator.enums.EAssemblingType;
import ru.wert.datapik.chogori.calculator.enums.ESealersWidth;


public class BXSealersWidth {

    public static ESealersWidth LAST_SEALERS_WIDTH;
    private ComboBox<ESealersWidth> bxSealersWidth;

    public void create(ComboBox<ESealersWidth> bxAssemblingType){
        this.bxSealersWidth = bxAssemblingType;
        ObservableList<ESealersWidth> sealersWidths = FXCollections.observableArrayList(ESealersWidth.values());
        bxAssemblingType.setItems(sealersWidths);

        createCellFactory();
        //Выделяем префикс по умолчанию
        createConverter();

        if(LAST_SEALERS_WIDTH == null)
            LAST_SEALERS_WIDTH = ESealersWidth.W10;
        bxAssemblingType.setValue(LAST_SEALERS_WIDTH);

    }

    private void createCellFactory() {
        //CellFactory определяет вид элементов комбобокса - только имя префикса
        bxSealersWidth.setCellFactory(i -> new ListCell<ESealersWidth>() {
            @Override
            protected void updateItem (ESealersWidth item, boolean empty){
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getWidth());
                }
            }

        });
    }

    private void createConverter() {
        bxSealersWidth.setConverter(new StringConverter<ESealersWidth>() {
            @Override
            public String toString(ESealersWidth sealersWidth) {
                LAST_SEALERS_WIDTH = sealersWidth; //последний выбранный префикс становится префиксом по умолчанию
                return sealersWidth.getWidth();
            }

            @Override
            public ESealersWidth fromString(String string) {
                return null;
            }
        });
    }
}
