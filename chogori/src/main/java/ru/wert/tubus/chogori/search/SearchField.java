package ru.wert.tubus.chogori.search;

import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import ru.wert.tubus.client.interfaces.CatalogGroup;
import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.chogori.common.tableView.CatalogableTable;
import ru.wert.tubus.chogori.common.tableView.ItemTableView;
import ru.wert.tubus.chogori.popups.PastePopup;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;

public class SearchField extends TextField {

    @Getter private String seachedext;
    private String promptText;
    @Getter private ItemTableView<? extends Item> searchedTableView = null;
    private PastePopup paste = new PastePopup(this);
    private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public SearchField() {

        //Слушатель следит за изменением текста. Если текст изменился, то вызывается апдейт таблицы
        textProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue) && searchedTableView != null) {
                seachedext = normalizeSearchedStr(newValue);
                searchNow(seachedext);
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

    /**
     * Позволяет опускать точку при поиске,
     * вставлять строку с номером с пробелами из 1С
     * Если набранных символов меньше равно 6, то ничего не делает
     */
    private String normalizeSearchedStr(String text){
        if(text.length() <= 6) return text;

        else if(text.length() < 9){
            StringBuilder newText = new StringBuilder(text);
            return newText.insert(6, ".").toString();
        }

        StringBuilder newText = new StringBuilder(text.replaceAll("[^\\d]", ""));
        if(newText.length() >= 9){
            newText.delete(9, newText.length());
            newText.insert(6, ".");
            return newText.toString();
        } else
            return text;
    }

}
