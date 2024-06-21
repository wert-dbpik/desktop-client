package ru.wert.tubus.chogori.search;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import lombok.Getter;
import lombok.Setter;
import ru.wert.tubus.chogori.common.tableView.CatalogableTable;
import ru.wert.tubus.chogori.common.tableView.ItemTableView;
import ru.wert.tubus.chogori.entities.drafts.Draft_TableView;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.client.entity.models.Folder;
import ru.wert.tubus.client.entity.models.Passport;
import ru.wert.tubus.client.entity.serviceQUICK.FolderQuickService;
import ru.wert.tubus.client.interfaces.CatalogGroup;
import ru.wert.tubus.client.interfaces.Item;

import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static ru.wert.tubus.chogori.statics.AppStatic.KOMPLEKT;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;
import static ru.wert.tubus.winform.statics.WinformStatic.clearCash;


public class SearchField extends TextField {

    private boolean blockTextPropertyListener;

    @Getter private String searchedText;
    @Getter private final List<String> searchHistory = new ArrayList<>();

    @Getter@Setter
    private String enteredText;
    private String promptText;
    @Getter private ItemTableView<? extends Item> searchedTableView = null;

    public static BooleanProperty searchProProperty = new SimpleBooleanProperty(true);

    public static boolean allTextIsSelected;

    public SearchField() {

        setEditable(true);

        //Слушатель следит за изменением текста. Если текст изменился, то вызывается апдейт таблицы
        textProperty().addListener((observable, oldText, newText) -> {
            if(blockTextPropertyListener) return;
            if (searchedTableView == null) return;
            enteredText = newText;
            if (newText.startsWith(KOMPLEKT)) {
                searchedTableView.setSearchedText(newText);
                searchNow(true);
                requestFocusOnTableView();
            } else {
                //Разница между новым и предыдущим текстом по модулю
                int dif = Math.abs(newText.length() - oldText.length());
                if (dif == 1 || newText.length() == 1) //Если происходит редактирование вручную
                    searchNow(false);
                else {//Если произошла вставка
                    searchNow(true);
                    requestFocusOnTableView();
                }
            }

        });

        //При нажатии на ентер происходит принудительный поиск
        setOnKeyPressed(e->{
            if(e.getCode().equals(KeyCode.ENTER)){
                searchNow(true);
                requestFocusOnTableView();
            }
        });

        //При потере фокуса выделение с поля поиска снимается
        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                deselect();
                allTextIsSelected = false;
            }
        });

        //Управляет выделением строки поиска при клике на поле
        setOnMouseClicked(e -> {
            requestFocus();
            if (getSelection().getLength() == 0) {
                if (allTextIsSelected) {
                    deselect();
                    allTextIsSelected = false;
                } else {
                    selectAll();
                    allTextIsSelected = true;
                }
            }
        });

    }

    /**
     * Блокирует перестройку таблицы еще раз
     */
    public void requestFocusOnTableView() {
        blockTextPropertyListener = true;
        searchedTableView.requestFocus();
        blockTextPropertyListener = false;
    }

    /**
     * Метод вызывается также при нажатии кнопки искать на панели MAIN_MENU, принудительный поиск
     */
    public void searchNow(boolean usePreview) {
        if(enteredText.contains(KOMPLEKT)) {
            openFolder(enteredText);
        } else {
            if (searchProProperty.get()) {
                searchedText = normalizeSearchedText(enteredText);
            } else {
                searchedText = enteredText;
            }
            search(searchedText, usePreview);
        }

    }

    /**
     * Собственно поиск по строке searchedText
     */
    private void search(String searchedText, boolean usePreview) {
        searchedTableView.setSearchedText(enteredText);
        if (searchedText.equals("")) {
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
            if(searchedText.startsWith(KOMPLEKT)){
                return;
            }
            if(searchedTableView instanceof Draft_TableView && !searchedTableView.getItems().isEmpty() && usePreview){
                Platform.runLater(()->{
                    Draft topDraft = (Draft) searchedTableView.getItems().get(0);
                    AppStatic.openDraftInPreviewer(
                            topDraft,
                            ((Draft_TableView) searchedTableView).getPreviewerController(),
                            false);
                });
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
        setText(searchedText);
        if (searchedText.equals(""))
            setPromptText(promptText);
        else
            setText(searchedText);

    }

    /**
     * Добавляет пасспорт в историю поиска
     */
    public void updateSearchHistory(Passport passport) {
        if(passport == null) return;
        SearchHistoryListView.getInstance().updateSearchHistory(passport.toUsefulString());
    }

    /**
     * Добавляем строку в историю поиска
     */
    public void updateSearchHistory(String string) {
        if(string == null) return;

        SearchHistoryListView.getInstance().updateSearchHistory(string);

    }


    /**
     * Позволяет опускать точку при поиске,
     * вставлять строку с номером с пробелами из 1С
     * Если набранных символов меньше равно 6, то ничего не делает
     */
    private String normalizeSearchedText(String text){
        if(text.startsWith(KOMPLEKT)) return text;

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

    /**
     * Открывает комплект чертежей, сохраненный в истории
     * @param folderName - String, содержит KOMPLEKT
     */
    public void openFolder(String folderName){
        clearCash();
        Platform.runLater(() -> {
            Folder folder = FolderQuickService.getInstance().findByName(folderName.substring(KOMPLEKT.length()));
            Draft_TableView draftsTable = (Draft_TableView) CH_SEARCH_FIELD.getSearchedTableView();

            draftsTable.setTempSelectedFolders(Collections.singletonList(folder));
            draftsTable.setModifyingItem(folder);
            draftsTable.updateView();
            draftsTable.getDraftPatchController().showSourceOfPassports(folder);
        });
    }

}
