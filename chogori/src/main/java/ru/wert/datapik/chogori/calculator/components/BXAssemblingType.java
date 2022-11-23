package ru.wert.datapik.chogori.calculator.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import ru.wert.datapik.chogori.calculator.enums.EAssemblingType;
import ru.wert.datapik.chogori.calculator.enums.EBendingTool;


public class BXAssemblingType {

    public static EAssemblingType LAST_ASSEMBLING_TYPE;
    private ComboBox<EAssemblingType> bxAssemblingType;

    public void create(ComboBox<EAssemblingType> bxAssemblingType){
        this.bxAssemblingType = bxAssemblingType;
        ObservableList<EAssemblingType> assemblingTypes = FXCollections.observableArrayList(EAssemblingType.values());
        bxAssemblingType.setItems(assemblingTypes);

        createCellFactory();
        //Выделяем префикс по умолчанию
        createConverter();

        if(LAST_ASSEMBLING_TYPE == null)
            LAST_ASSEMBLING_TYPE = EAssemblingType.SOLID;
        bxAssemblingType.setValue(LAST_ASSEMBLING_TYPE);

    }

    private void createCellFactory() {
        //CellFactory определяет вид элементов комбобокса - только имя префикса
        bxAssemblingType.setCellFactory(i -> new ListCell<EAssemblingType>() {
            @Override
            protected void updateItem (EAssemblingType item, boolean empty){
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getType());
                }
            }

        });
    }

    private void createConverter() {
        bxAssemblingType.setConverter(new StringConverter<EAssemblingType>() {
            @Override
            public String toString(EAssemblingType bendingTool) {
                LAST_ASSEMBLING_TYPE = bendingTool; //последний выбранный префикс становится префиксом по умолчанию
                return bendingTool.getType();
            }

            @Override
            public EAssemblingType fromString(String string) {
                return null;
            }
        });
    }
}
