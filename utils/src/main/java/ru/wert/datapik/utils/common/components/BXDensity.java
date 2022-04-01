package ru.wert.datapik.utils.common.components;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import ru.wert.datapik.client.entity.models.Density;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_DENSITIES;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_PREFIXES;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.*;

public class BXDensity {

    private ComboBox<Density> bxDensity;
    public static Density LAST_DENSITY;

    public ComboBox<Density> create(ComboBox<Density> bxDensity){
        this.bxDensity = bxDensity;
        ObservableList<Density> prefixes = CH_DENSITIES.findAll();
        bxDensity.setItems(prefixes);

        createCellFactory();
        //Выделяем префикс по умолчанию
        createConverter();

        if(LAST_DENSITY == null)
            LAST_DENSITY = CH_DENSITIES.findByName(CH_DEFAULT_DENSITY);
        bxDensity.setValue(LAST_DENSITY);

        return bxDensity;
    }

    private void createCellFactory() {
        //CellFactory определяет вид элементов комбобокса - только имя префикса
        bxDensity.setCellFactory(i -> new ListCell<Density>() {
            @Override
            protected void updateItem (Density item,boolean empty){
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
        bxDensity.setConverter(new StringConverter<Density>() {
            @Override
            public String toString(Density density) {
                LAST_DENSITY = density; //последний выбранный префикс становится префиксом по умолчанию
                return density.getName();
            }

            @Override
            public Density fromString(String string) {
                return null;
            }
        });
    }
}
