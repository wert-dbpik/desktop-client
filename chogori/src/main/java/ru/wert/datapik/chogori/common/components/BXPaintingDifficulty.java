package ru.wert.datapik.chogori.common.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import ru.wert.datapik.chogori.calculator.part_calculator.EPaintingDifficulty;


public class BXPaintingDifficulty {

    public static EPaintingDifficulty LAST_PAINTING_DIFFICULTY;
    private ComboBox<EPaintingDifficulty> bxPaintingDifficulty;

    public void create(ComboBox<EPaintingDifficulty> bxPaintingDifficulty){
        this.bxPaintingDifficulty = bxPaintingDifficulty;
        ObservableList<EPaintingDifficulty> paintingDifficulties = FXCollections.observableArrayList(EPaintingDifficulty.values());
        bxPaintingDifficulty.setItems(paintingDifficulties);

        createCellFactory();
        //Выделяем префикс по умолчанию
        createConverter();

        if(LAST_PAINTING_DIFFICULTY == null)
            LAST_PAINTING_DIFFICULTY = EPaintingDifficulty.MIDDLE;
        bxPaintingDifficulty.setValue(LAST_PAINTING_DIFFICULTY);

    }

    private void createCellFactory() {
        //CellFactory определяет вид элементов комбобокса - только имя префикса
        bxPaintingDifficulty.setCellFactory(i -> new ListCell<EPaintingDifficulty>() {
            @Override
            protected void updateItem (EPaintingDifficulty item, boolean empty){
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getDifficultyName());
                }
            }

        });
    }

    private void createConverter() {
        bxPaintingDifficulty.setConverter(new StringConverter<EPaintingDifficulty>() {
            @Override
            public String toString(EPaintingDifficulty paintingDifficulty) {
                LAST_PAINTING_DIFFICULTY = paintingDifficulty; //последний выбранный префикс становится префиксом по умолчанию
                return paintingDifficulty.getDifficultyName();
            }

            @Override
            public EPaintingDifficulty fromString(String string) {
                return null;
            }
        });
    }
}
