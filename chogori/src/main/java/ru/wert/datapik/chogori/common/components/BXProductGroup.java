package ru.wert.datapik.chogori.common.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import lombok.Getter;
import ru.wert.datapik.client.entity.models.ProductGroup;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_PRODUCT_GROUPS;
import static ru.wert.datapik.chogori.setteings.ChogoriSettings.CH_DEFAULT_PRODUCT_GROUP;

@Getter
public class BXProductGroup {

    private ComboBox<ProductGroup> cmbx;
    private ProductGroup productGroup;

    public void create(ComboBox<ProductGroup> cmbx, ProductGroup productGroup){
        this.cmbx = cmbx;
        ObservableList<ProductGroup> groups = FXCollections.observableArrayList(CH_PRODUCT_GROUPS.findAll());
        cmbx.setItems(groups);

        createCellFactory();
        //Выделяем префикс по умолчанию

        createConverter();

        if (productGroup == null)
            cmbx.getSelectionModel().select(CH_PRODUCT_GROUPS.findByName(CH_DEFAULT_PRODUCT_GROUP));
        else
            cmbx.getSelectionModel().select(productGroup);

    }

    private void createCellFactory() {
        //CellFactory определяет вид элементов комбобокса - только имя префикса
        cmbx.setCellFactory(i -> new ListCell<ProductGroup>() {
            @Override
            protected void updateItem (ProductGroup item,boolean empty){
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
        cmbx.setConverter(new StringConverter<ProductGroup>() {
            @Override
            public String toString(ProductGroup productGroup) {
//                LAST_VAL = productGroup; //последний выбранный префикс становится префиксом по умолчанию
                return productGroup.getName();
            }

            @Override
            public ProductGroup fromString(String string) {
                return null;
            }
        });

    }




}
