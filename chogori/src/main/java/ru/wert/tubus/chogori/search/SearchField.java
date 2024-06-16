package ru.wert.tubus.chogori.search;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import lombok.Getter;
import ru.wert.tubus.chogori.common.tableView.CatalogableTable;
import ru.wert.tubus.chogori.common.tableView.ItemTableView;
import ru.wert.tubus.chogori.entities.drafts.Draft_TableView;
import ru.wert.tubus.chogori.popups.PastePopup;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.client.interfaces.CatalogGroup;
import ru.wert.tubus.client.interfaces.Item;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
    final AtomicBoolean switchOnEditorListeners = new AtomicBoolean(true);

    public SearchField() {

        editor = getEditor();

        setEditable(true);
        setItems(FXCollections.observableArrayList());

        //Слушатель следит за изменением текста. Если текст изменился, то вызывается апдейт таблицы
        getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            // Если значение устанавливается не этим слушателем
            if (switchOnEditorListeners.compareAndSet(true, false)) {
                    if (!oldValue.equals(newValue) && searchedTableView != null) {
                        enteredText = newValue;
                        searchNow();
                        //Если произошла вставка
                        if (newValue.length() - oldValue.length() > 1)
                            searchedTableView.requestFocus();
                    }
                    // Отмечаем завершение работы слушателя
                    switchOnEditorListeners.lazySet(true);
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
            if (switchOnEditorListeners.compareAndSet(true, false)) {

                Platform.runLater(()->{
                    moveToTop(newValue);
                    searchedTableView.requestFocus();
                });

                switchOnEditorListeners.lazySet(true);
            }
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
     * Перемещает newValue на самый верх истории поиска
     */
    private void moveToTop(String newValue) {
        switchOnEditorListeners.set(false);

        Iterator<String> it = getItems().iterator();
        while (it.hasNext()) {
            String nextStr = it.next();
            if (nextStr.equals(newValue))
                it.remove();
        }
        getItems().add(0, newValue);
        getSelectionModel().select(newValue);

        switchOnEditorListeners.set(true);

    }

    /**
     * Метод вызывается также при нажатии кнопки искать на панели MAIN_MENU, принудительный поиск
     */
    public void searchNow(){
        if(searchProProperty.get()) {
            searchedText = normalizeSearchedText(enteredText);
        } else {
            searchedText = enteredText;
        }
        search(searchedText);
    }

    /**
     * Собственно поиск по строке newValue
     */
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
        } else{
            ((Searchable<? extends Item>) searchedTableView).updateSearchedView();
            if(searchedTableView instanceof Draft_TableView && !searchedTableView.getItems().isEmpty()){
                AppStatic.openDraftInPreviewer(
                        (Draft) searchedTableView.getItems().get(0),
                        ((Draft_TableView) searchedTableView).getPreviewerController(),
                        false);
            }
        }

    }

    /**
     * Так как данный SearchField используется для многих таблиц,
     * то в этом методе происходит его настройка на новую таблицу
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

    /**
     * Добавляет чертеж в историю поиска
     */
    public void updateSearchDraftHistory(Draft draft){
        if(draft == null) return;
        String stringDraft = draft.toUsefulString();

        //Отключаем слушатели при изменении истории
        switchOnEditorListeners.set(false);
        String selectedItem = getSelectionModel().getSelectedItem();

        ObservableList<String> searchHistory = getItems();
        if (searchHistory.contains(stringDraft)) {
            //Если чертеж уже в поле поиска, то ничего делать не надо
            if (stringDraft.equals(CH_SEARCH_FIELD.getSelectionModel().getSelectedItem())){
                getSelectionModel().select(selectedItem);
                switchOnEditorListeners.set(true);
                return;
            }

            int index = searchHistory.indexOf(stringDraft);
            searchHistory.add(0, stringDraft);
            searchHistory.remove(index + 1);
        } else {
            searchHistory.add(0, stringDraft);
        }

        //Включаем слушатели обратно
        getSelectionModel().select(selectedItem);
        switchOnEditorListeners.set(true);


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
