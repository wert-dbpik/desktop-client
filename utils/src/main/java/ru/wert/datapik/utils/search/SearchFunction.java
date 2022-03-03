package ru.wert.datapik.utils.search;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.popups.PastePopup;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;

import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public class SearchFunction<P extends Item> {

    ItemTableView<P> tableView;
    String hint;
    private PastePopup paste = new PastePopup(CH_SEARCH_FIELD);
    private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    StringProperty stringProperty;

    public SearchFunction(ItemTableView<P> tableView, String hint) {
        this.tableView = tableView;
        this.hint = hint;
        stringProperty = CH_SEARCH_FIELD.textProperty();
    }

    public void mount(){

        tableView.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue && tableView instanceof Searchable){
                Searchable<P> table = (Searchable<P>)tableView;
                String searchedText = table.getSearchedText();
                CH_SEARCH_FIELD.setText(searchedText);
                if (searchedText.equals(""))
                    CH_SEARCH_FIELD.setPromptText(hint);
                else
                    CH_SEARCH_FIELD.setText(table.getSearchedText());
            }
        });

        //Слушатель следит за изменением текста. Если текст изменился, то вызывается апдейт таблицы
        stringProperty.addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue) && tableView != null && tableView instanceof Searchable) {
                tableView.setSearchedText(newValue);
                ((Searchable) tableView).updateSearchedView();
            }
        });

        //Слушатель следит за кликами по полю TextField.
        //Если TextField пуст, а буфер обмена clipboard - нет, то предлагается сделать вставку
        CH_SEARCH_FIELD.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.SECONDARY)) {
                if (CH_SEARCH_FIELD.getText().equals("") && clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                    paste.showHint();
                }
            }
        });


    }
}
