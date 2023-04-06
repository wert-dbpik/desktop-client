package ru.wert.datapik.chogori.search;

import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import ru.wert.datapik.client.interfaces.CatalogGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.chogori.common.tableView.CatalogableTable;
import ru.wert.datapik.chogori.common.tableView.ItemTableView;
import ru.wert.datapik.chogori.popups.PastePopup;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;

public class SearchField extends TextField {


    private String promptText;
    private ItemTableView<? extends Item> searchedTableView = null;
    private PastePopup paste = new PastePopup(this);
    private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public SearchField() {

        //Слушатель следит за изменением текста. Если текст изменился, то вызывается апдейт таблицы
        textProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue) && searchedTableView != null) {
                searchNow(newValue);
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
    /**
     * Метод вызывается также при нажатии кнопки искать на панели MAIN_MENU, насильственный поиск
     */
    public void searchNow(String newValue) {
        searchedTableView.setSearchedText(newValue);
        if (newValue.equals("")) {
            setPromptText(promptText);
            if(searchedTableView instanceof CatalogableTable){
                //При обновлении таблицы - части каталога - нужно учитывать верхний узел видимой
                CatalogableTable<? extends Item> catalogableTable = (CatalogableTable<? extends Item>) searchedTableView;
                CatalogGroup upwardTreeItemRow = catalogableTable.getUpwardRow().getValue();
                ((CatalogableTable) searchedTableView).updateVisibleLeafOfTableView(upwardTreeItemRow);
            } else
                searchedTableView.updateTableView();
        } else
            ((Searchable<? extends Item>) searchedTableView).updateSearchedView();
    }

    public void changeSearchedTableView(ItemTableView<? extends Item> tableView, String promptText) {
        this.searchedTableView = tableView;
        this.promptText = promptText;

        String searchedText = tableView.getSearchedText();
        setText(searchedText);
        if (searchedText.equals(""))
            setPromptText(promptText);
        else
            setText(searchedText);

    }

}
