package ru.wert.datapik.utils.common.components;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import lombok.Getter;
import ru.wert.datapik.client.entity.models.Prefix;
import ru.wert.datapik.client.entity.models.ProductGroup;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_PRODUCT_GROUPS;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_DEFAULT_PRODUCT_GROUP;

@Getter
public class BXProductGroup {

    private ComboBox<ProductGroup> bxProductGroup;

    public void create(ComboBox<ProductGroup> bxProductGroup){
        this.bxProductGroup = bxProductGroup;
        ObservableList<ProductGroup> groups = CH_PRODUCT_GROUPS.findAll();
        bxProductGroup.setItems(groups);

        createCellFactory();
        //Выделяем префикс по умолчанию

        createConverter();

//        bxProductGroup.getEditor().focusedProperty().addListener((obs, old, isFocused) -> {
//            if (!isFocused) {
//                bxProductGroup.setValue(bxProductGroup.getConverter().fromString(bxProductGroup.getEditor().getText()));
//                bxProductGroup.hide();
//            }
//        });

    }

    private void createCellFactory() {
        //CellFactory определяет вид элементов комбобокса - имя группы
        bxProductGroup.setCellFactory(i -> new ListCell<ProductGroup>() {
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
        bxProductGroup.setConverter(new StringConverter<ProductGroup>() {
            @Override
            public String toString(ProductGroup group) {
                if(group == null) return "system bug";
                return group.getName();
            }

            @Override
            public ProductGroup fromString(String string) {
                ProductGroup group = CH_PRODUCT_GROUPS.findByName(string);
                if(group == null) {
                    return CH_PRODUCT_GROUPS.findByName(CH_DEFAULT_PRODUCT_GROUP);
                }
                return group;
            }
        });
    }




}
