package ru.wert.datapik.utils.search;

import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import ru.wert.datapik.utils.popups.PastePopup;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;

public class SearchField extends TextField {

    private Searchable searchableTableController;
    private PastePopup paste = new PastePopup(this);
    private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public SearchField() {
        //Слушатель следит за изменением текста. Если текст изменился, то вызывается апдейт таблицы
        textProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue) && searchableTableController != null) {
                searchableTableController.setSearchedText(newValue);
                search();
            }
        });

        //Слушатель следит за кликами по полю TextField.
        //Если TextField пуст, а буфер обмена clipboard - нет, то предлагается сделать вставку
        setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.SECONDARY)) {
                if (getText().equals("") && clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                    paste.showHint();
                }
            }
        });
    }

    public void search() {

        searchableTableController.updateSearchedView();
    }

    public void setSearchableTableController(Searchable searchableTableController) {
        this.searchableTableController = searchableTableController;
    }

//    public void setSearchableTableController(Excel_TableView searchableTableController) {
//        this.searchableTableController = searchableTableController;
//    }

    public void setSearchedText(String searchedText){
        setText(searchedText);
    }


}
