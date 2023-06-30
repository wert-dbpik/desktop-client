package ru.wert.tubus.chogori.common.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import ru.wert.tubus.client.entity.models.Product;
import ru.wert.tubus.chogori.popups.HintPopup;

import java.util.List;

import static ru.wert.tubus.client.entity.serviceQUICK.ProductQuickService.DEFAULT_PRODUCT;
import static ru.wert.tubus.client.utils.BLConst.RAZNOE;
import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_QUICK_PRODUCTS;

public class BXProduct {

    private ComboBox<Product> bxProduct;
    private HintPopup productHint;
    private TextField editor;
    private Product newProduct;

    public void create(ComboBox<Product> bxProduct){
        this.bxProduct = bxProduct;
        this.editor = bxProduct.getEditor();

        bxProduct.setEditable(true);

        ObservableList<Product> products = FXCollections.observableArrayList(CH_QUICK_PRODUCTS.findAll());
        bxProduct.setItems(products);

        createCellFactory();
        //Выделяем префикс по умолчанию
        createConverter();

        createTextListener();

        createDecNumberPrompt();

        bxProduct.setOnAction(event -> {
            if(bxProduct.getValue() != null)
                editor.positionCaret(bxProduct.getValue().getName().length());
        });

        bxProduct.setValue(DEFAULT_PRODUCT);

    }

    private void createCellFactory() {
        //CellFactory определяет вид элементов комбобокса - только имя префикса
        bxProduct.setCellFactory(i -> new ListCell<Product>() {
            @Override
            protected void updateItem (Product item,boolean empty){
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    Label name = new Label(item.getPassport().getName());
                    name.setStyle("-fx-text-fill: darkgreen; -fx-font-weight: bold");
                    Label number = new Label((item.getPassport().getNumber()));
                    setGraphic(new VBox(name, number));
                }
            }

        });
    }

    /**
     * Конвертер вызывается первым
     */
    private void createConverter() {
        bxProduct.setConverter(new StringConverter<Product>() {
            @Override
            public String toString(Product product) {
                String productName = null;
                if(product != null) {
                    productName = product.getPassport().getName();
                    newProduct = product;
                }
                return productName;
            }

            @Override
            public Product fromString(String string) {
                if(newProduct == null) return DEFAULT_PRODUCT;
                return newProduct;
            }
        });
    }

    private void createTextListener() {

        editor.textProperty().addListener((observable, oldVal, newVal) -> {

            if (editor.isFocused()) {
                if (!newVal.equals(newProduct.getPassport().getName())) {

                    //Находим подходящие изделия
                    List<Product> products = CH_QUICK_PRODUCTS.findAllByText(newVal);


                    bxProduct.getItems().clear();
                    bxProduct.getSelectionModel().clearSelection();
                    bxProduct.hide();
                    if (!products.isEmpty()) {
                        bxProduct.getItems().setAll(products);
                        if (!bxProduct.isShowing())
                            bxProduct.show();
                    }

                }
            }
        });
    }

    /**
     * Выводит ниже комбобокса подсказку - децимальный номер
     */
    private void createDecNumberPrompt() {
        bxProduct.setOnMouseEntered(event ->{
            String name = "";
            String decNumber = "";
            Product product = bxProduct.getValue();
            if(product != null) {
                name = product.getPassport().getName();
                decNumber = product.getPassport().getNumber();
                if(decNumber == null || decNumber.equals("") || name.equals(RAZNOE)) return;
                productHint = new HintPopup(bxProduct, decNumber, 0.0);
                productHint.showHint();
            }
        });
        bxProduct.setOnMouseExited(event ->{
            if(productHint != null)
                productHint.closeHint();
        });
        bxProduct.setOnMousePressed(event->{
            if(productHint != null)
                productHint.closeHint();
        });
    }
}
