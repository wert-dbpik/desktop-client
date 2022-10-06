package ru.wert.datapik.chogori.common.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import ru.wert.datapik.client.entity.models.Prefix;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_QUICK_PREFIXES;
import static ru.wert.datapik.chogori.setteings.ChogoriSettings.CH_DEFAULT_PREFIX;

public class BXPrefix {

    public static Prefix LAST_PREFIX;
    private ComboBox<Prefix> bxPrefix;

    public void create(ComboBox<Prefix> bxPrefix){
        this.bxPrefix = bxPrefix;
        ObservableList<Prefix> prefixes = FXCollections.observableArrayList(CH_QUICK_PREFIXES.findAll());
        bxPrefix.setItems(prefixes);

        createCellFactory();
        //Выделяем префикс по умолчанию
        createConverter();

        if(LAST_PREFIX == null)
            LAST_PREFIX = CH_DEFAULT_PREFIX;
        bxPrefix.setValue(LAST_PREFIX);

    }

    private void createCellFactory() {
        //CellFactory определяет вид элементов комбобокса - только имя префикса
        bxPrefix.setCellFactory(i -> new ListCell<Prefix>() {
            @Override
            protected void updateItem (Prefix item,boolean empty){
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }

        });
    }

    private void createConverter() {
        bxPrefix.setConverter(new StringConverter<Prefix>() {
            @Override
            public String toString(Prefix prefix) {
                LAST_PREFIX = prefix; //последний выбранный префикс становится префиксом по умолчанию
                return prefix.getName();
            }

            @Override
            public Prefix fromString(String string) {
                return null;
            }
        });
    }
}
