package ru.wert.tubus.chogori.common.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import ru.wert.tubus.client.entity.models.Material;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_QUICK_MATERIALS;


public class BXMaterial {

    private static Material LAST_MATERIAL;
    private ComboBox<Material> bxMaterial;

    public void create(ComboBox<Material> bxMaterial){
        this.bxMaterial = bxMaterial;
        ObservableList<Material> materials = FXCollections.observableArrayList(CH_QUICK_MATERIALS.findAll());
        bxMaterial.setItems(materials);

        createCellFactory();
        //Выделяем префикс по умолчанию
        createConverter();

        if(LAST_MATERIAL == null)
            LAST_MATERIAL = CH_QUICK_MATERIALS.findByName("лист 1");
        bxMaterial.setValue(LAST_MATERIAL);

    }

    private void createCellFactory() {
        //CellFactory определяет вид элементов комбобокса - только имя префикса
        bxMaterial.setCellFactory(i -> new ListCell<Material>() {
            @Override
            protected void updateItem (Material item,boolean empty){
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
        bxMaterial.setConverter(new StringConverter<Material>() {
            @Override
            public String toString(Material material) {
                LAST_MATERIAL = material; //последний выбранный префикс становится префиксом по умолчанию
                return material.getName();
            }

            @Override
            public Material fromString(String string) {
                return null;
            }
        });
    }
}
