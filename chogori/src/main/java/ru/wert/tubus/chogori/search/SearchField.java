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

import static ru.wert.tubus.chogori.statics.AppStatic.SEARCH_PRO;

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
                if(SEARCH_PRO) {
                    seachedext = normalizeSearchedStr(newValue);
                    searchNow(seachedext);
                } else {
                    seachedext = newValue;
                    searchNow(seachedext);
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

    /**
     * Позволяет опускать точку при поиске,
     * вставлять строку с номером с пробелами из 1С
     * Если набранных символов меньше равно 6, то ничего не делает
     */
    private String normalizeSearchedStr(String text){
        String newText = text.replaceAll("\\s+", "");
        if(!newText.matches("[0-9]+"))
            return text;

        if(newText.length() <= 6)
            return newText;

        else if(newText.length() < 9){
            if(newText.charAt(6) == '/' && newText.substring(0, 5).matches("[0-9]+")) {
                newText = newText.replaceAll("/", ".");
            }
            if(newText.charAt(6) == '.'){
                return newText;
            }
            StringBuilder sbText = new StringBuilder(newText);
            return
                    sbText.insert(6, ".").toString();
        }

        StringBuilder sbText = new StringBuilder(newText.replaceAll("[^\\d]", ""));
        if(sbText.length() >= 9){
            sbText.delete(9, sbText.length());
            sbText.insert(6, ".");
            return sbText.toString();
        } else
            return newText;
    }

}
