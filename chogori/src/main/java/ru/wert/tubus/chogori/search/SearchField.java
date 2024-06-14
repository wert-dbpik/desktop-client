package ru.wert.tubus.chogori.search;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import lombok.Getter;
import ru.wert.tubus.chogori.common.tableView.CatalogableTable;
import ru.wert.tubus.chogori.common.tableView.ItemTableView;
import ru.wert.tubus.chogori.popups.PastePopup;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.client.interfaces.CatalogGroup;
import ru.wert.tubus.client.interfaces.Item;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_DRAFTS;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;


public class SearchField extends ComboBox<String> {

    @Getter private String searchedText;
    @Getter private final List<String> searchHistory = new ArrayList<>();

    @Getter private String enteredText;
    private String promptText;
    @Getter private ItemTableView<? extends Item> searchedTableView = null;
    private PastePopup paste = new PastePopup(this.getEditor());
    private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    public static BooleanProperty searchProProperty = new SimpleBooleanProperty(true);

    public static boolean allTextIsSelected;
    private TextField editor;
    final AtomicBoolean userAction = new AtomicBoolean(true);

    public SearchField() {

        editor = getEditor();

        setEditable(true);
//        ObservableList<String> texts = FXCollections.observableArrayList();
        setItems(FXCollections.observableArrayList());

        //Слушатель следит за изменением текста. Если текст изменился, то вызывается апдейт таблицы
        getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            // Если значение устанавливается не этим слушателем
            if (userAction.compareAndSet(true, false)) {
//                Platform.runLater(() -> {
                    if (!oldValue.equals(newValue) && searchedTableView != null) {
                        enteredText = newValue;
                        searchNow();
                        //Если произошла вставка
                        if (newValue.length() - oldValue.length() > 1)
                            searchedTableView.requestFocus();
                    }
                    // Отмечаем завершение работы слушателя
                    userAction.lazySet(true);
//                });
            }
        });

        //При нажатии на ентер происходит принудительный поиск
        setOnKeyPressed(e->{
            if(e.getCode().equals(KeyCode.ENTER)){
                searchNow();
                searchedTableView.requestFocus();
            }
        });

        //При выборе элемента в выпадающем списке фокус переходит в таблицу с найденным
        getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            searchedTableView.requestFocus();
        });

        //При потере фокуса выделение с поля поиска снимается
        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                editor.deselect();
                allTextIsSelected = false;
            }
        });

        //Управляет выделением строки поиска при клике на поле
        getEditor().setOnMouseClicked(e -> {
            editor.requestFocus();
            if (editor.getSelection().getLength() == 0) {
                if (allTextIsSelected) {
                    editor.deselect();
                    allTextIsSelected = false;
                } else {
                    editor.selectAll();
                    allTextIsSelected = true;
                }
            }
        });

    }

    /**
     * Метод вызывается также при нажатии кнопки искать на панели MAIN_MENU, насильственный поиск
     */
    public void searchNow(){
        if(searchProProperty.get()) {
            searchedText = normalizeSearchedText(enteredText);
        } else {
            searchedText = enteredText;
        }
        search(searchedText);
    }

    private void search(String newValue) {
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

    /**
     * Так как данный SearchField используется для многих таблиц, то в этом методе происходит его настройка на новую таблицу
     * @param tableView
     * @param promptText
     */
    public void changeSearchedTableView(ItemTableView<? extends Item> tableView, String promptText) {
        this.searchedTableView = tableView;
        this.promptText = promptText;

        String searchedText = tableView.getSearchedText();
        getEditor().setText(searchedText);
        if (searchedText.equals(""))
            setPromptText(promptText);
        else
            getEditor().setText(searchedText);

    }

    public void updateSearchDraftHistory(Draft draft){
        if(draft == null) return;
        updateSearchHistory(draft.toUsefulString());
    }

    public void updateSearchHistory(String stringDraft){
        if(stringDraft == null) return;

        userAction.set(false);
        String selectedItem = getSelectionModel().getSelectedItem();

        ObservableList<String> searchHistory = getItems();
        if (searchHistory.contains(stringDraft)) {
            //Если чертеж уже в поле поиска, то ничего делать не надо
            if (stringDraft.equals(CH_SEARCH_FIELD.getSelectionModel().getSelectedItem())){
                getSelectionModel().select(selectedItem);
                userAction.set(true);
                return;
            }

            int index = searchHistory.indexOf(stringDraft);
            searchHistory.add(0, stringDraft);
            searchHistory.remove(index + 1);
        } else {
            searchHistory.add(0, stringDraft);
        }

        getSelectionModel().select(selectedItem);
        userAction.set(true);


    }


    /**
     * Позволяет опускать точку при поиске,
     * вставлять строку с номером с пробелами из 1С
     * Если набранных символов меньше равно 6, то ничего не делает
     */
    private String normalizeSearchedText(String text){

        String newText = text.replaceAll("\\s+", "");
        if(newText.matches("[a-zA-ZА-яа-я-]+"))
            return text;

        if(newText.length() <= 6)
            return newText;

        else if(newText.length() < 10){
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
