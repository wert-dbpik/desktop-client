package ru.wert.datapik.chogori.excel.context_menu;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import ru.wert.datapik.chogori.excel.enums.EColName;
import ru.wert.datapik.chogori.excel.enums.EColor;
import ru.wert.datapik.chogori.excel.model.EditorRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static ru.wert.datapik.chogori.excel.enums.EColor.*;
import static ru.wert.datapik.chogori.excel.table.TableMaster.findTableColumnById;
import static ru.wert.datapik.chogori.excel.table.TableMaster.getAllColumns;
import static ru.wert.datapik.chogori.images.EditorImages.*;

public class Excel_ContextMenu extends ContextMenu {

    private TableView<EditorRow> tv;

    public Excel_ContextMenu(TableView<EditorRow> tv) {
        this.tv = tv;

        MenuItem cutItem = new MenuItem("Вырезать");
        cutItem.setOnAction(this::cutItems);

        MenuItem copyItem = new MenuItem("Копировать");
        copyItem.setOnAction(this::copyItems);

        MenuItem copyRows = new MenuItem("Копировать строки");
        copyRows.setOnAction(this::copyRows);

        MenuItem pasteItem = new MenuItem("Вставить");
        pasteItem.setOnAction(this::paste);

        MenuItem deleteRows = new MenuItem("Удалить строки");
        deleteRows.setOnAction(this::deleteRows);

        MenuItem addEmptyRowsUpper = new MenuItem("Вставить пустые строки");
        addEmptyRowsUpper.setOnAction(e->addEmptyRows(e, "upper"));

        MenuItem addEmptyRowsLower = new MenuItem("Вставить пустые строки ниже");
        addEmptyRowsLower.setOnAction(e->addEmptyRows(e, "lower"));

        MenuItem renumerateRows = new MenuItem("Перенумеровать");
        renumerateRows.setOnAction(this::renumerateRows);

        Menu inAddition = new Menu("Дополнительно:");
        inAddition.getItems().addAll(renumerateRows, addEmptyRowsLower);

        //Цвета

        MenuItem noColor = new MenuItem("Без цвета", new ImageView(WHITE_SQ_IMG));
        noColor.setOnAction(e -> changeColor(WHITE));

        MenuItem greenColor = new MenuItem("Зеленый", new ImageView(GREEN_SQ_IMG));
        greenColor.setOnAction(e -> changeColor(GREEN));

        MenuItem blackColor = new MenuItem("Черный", new ImageView(BLACK_SQ_IMG));
        blackColor.setOnAction(e -> changeColor(BLACK));

        MenuItem orangeColor = new MenuItem("Оранжевый", new ImageView(ORANGE_SQ_IMG));
        orangeColor.setOnAction(e -> changeColor(ORANGE));

        MenuItem yellowColor = new MenuItem("Желтый", new ImageView(YELLOW_SQ_IMG));
        yellowColor.setOnAction(e -> changeColor(YELLOW));

        MenuItem blueColor = new MenuItem("Голубой", new ImageView(BLUE_SQ_IMG));
        blueColor.setOnAction(e -> changeColor(BLUE));

        Menu changeColor = new Menu("Изменить цвет на:");

        changeColor.getItems().addAll(noColor, greenColor, blackColor, orangeColor, yellowColor, blueColor);


        getItems().addAll(cutItem, copyItem, pasteItem); // Операции с ячейками
        getItems().addAll(new SeparatorMenuItem(), copyRows,  deleteRows); //Операции со строками
        getItems().addAll(new SeparatorMenuItem(), changeColor, addEmptyRowsUpper, inAddition);

        createKeyEventHandlers();

    }

    private void testShowTable(){
        System.out.println(tv.getItems().get(0).getExecutions().get(0) == tv.getItems().get(1).getExecutions().get(0));
    }

    /**
     * Метод обрабатывает сочетания клавиш
     */
    private void createKeyEventHandlers(){
        tv.addEventFilter(KeyEvent.KEY_PRESSED, e->{
            if(e.isShiftDown() && e.isControlDown()){
                if(e.getCode().equals(KeyCode.C) || e.getCode().equals(KeyCode.INSERT)) copyRows(e);
                else if(e.getCode().equals(KeyCode.V)) pasteRows(e);
                else if(e.getCode().equals(KeyCode.DELETE)) deleteRows(e);
                else if(e.getCode().equals(KeyCode.N)) renumerateRows(e);
            }
            else if(e.isControlDown()){
                if(e.getCode().equals(KeyCode.C) || e.getCode().equals(KeyCode.INSERT)) copyItems(e);
                else if(e.getCode().equals(KeyCode.V)) paste(e);
                else if(e.getCode().equals(KeyCode.R)) addEmptyRows(e,"upper");
            }
            else if(e.isShiftDown()){
                if(e.getCode().equals(KeyCode.INSERT)) paste(e);
            }

            else if(e.getCode().equals(KeyCode.DELETE)) clearSelectedCells(e);

        });
    }

    /**
     * Метод удаляет данные из выделенных ячеек таблицы
     */
    private void clearSelectedCells(Event event){
        ObservableList<TablePosition> tPoses = FXCollections.observableArrayList(tv.getSelectionModel().getSelectedCells());
        for(TablePosition tp : tPoses){
            EditorRow editedRow = tv.getItems().get(tp.getRow());
            TableColumn exTc = (TableColumn) tp.getTableColumn().getParentColumn();
            if(tp.getTableColumn().getParentColumn() != null) //Если это столбец исполнения
                editedRow.changeItemValue(exTc.getId(), tp.getTableColumn().getText(), "");
            else //Если это другой прочий столбец
                editedRow.changeItemValue(null, tp.getTableColumn().getText(), "");
        }

        tv.refresh();
        event.consume();
    }

    /**
     * Метод меняет цвет строки в зависимости от выбора в контекстном меню
     * @param color
     */
    private void changeColor(EColor color) {
        for(int row : tv.getSelectionModel().getSelectedIndices()) {
            switch (color) {
                case WHITE:
                    tv.getItems().get(row).setColor(WHITE.name());
                    tv.getItems().get(row).setKrp("");
                    break;
                case GREEN:
                    tv.getItems().get(row).setColor(GREEN.name());
                    tv.getItems().get(row).setKrp("◊");
                    break;
                case BLACK:
                    tv.getItems().get(row).setColor(BLACK.name());
                    tv.getItems().get(row).setKrp("");
                    break;
                case ORANGE:
                    tv.getItems().get(row).setColor(ORANGE.name());
                    tv.getItems().get(row).setKrp("●");
                    break;
                case YELLOW:
                    tv.getItems().get(row).setColor(YELLOW.name());
                    tv.getItems().get(row).setKrp("●●");
                    break;
                case BLUE:
                    tv.getItems().get(row).setColor(BLUE.name());
                    tv.getItems().get(row).setKrp("●●●");
                    break;
                default:
                    break;
            }
            tv.refresh();
        }
    }



    /**
     * Метод вырезает выделенные ячейки и копирует их в системный клипборд
     */
    private void cutItems(Event event) {
        testShowTable();
    }

    /**
     * Метод копирует выделенный участок таблицы в системный клипборд
     */
    private void copyItems(final Event event) {
       Platform.runLater(() -> {
            ObservableList<TablePosition> tPoses = FXCollections.observableArrayList(tv.getSelectionModel().getSelectedCells());
            StringBuilder clipboardString = new StringBuilder();
            int currentRow = tPoses.get(0).getRow();
            for(int i = 0; i < tPoses.size(); i++){
                TablePosition<EditorRow, String> tp = tPoses.get(i);
                clipboardString.append(tp.getTableColumn().getCellData(currentRow));
                if(i < tPoses.size() - 1){
                    if(tPoses.get(i  + 1).getRow() != currentRow){
                        clipboardString.append("\n");
                        currentRow = tPoses.get(i + 1).getRow();
                    } else{
                        clipboardString.append("\t");
                    }
                }
            }

            final ClipboardContent content = new ClipboardContent();

            content.putString(clipboardString.toString());
            Clipboard.getSystemClipboard().setContent(content);

            //Нейтрализуем возникшие артефакты
            tv.refresh();
            event.consume();
        });

    }

    /**
     * Копировать строки в буфер обмена
     */
    private void copyRows(Event event){
        List<Integer> rows = tv.getSelectionModel().getSelectedIndices();
        for(int row : rows){
            tv.getSelectionModel().select(row);
            tv.getSelectionModel().select(row, findTableColumnById("0", tv));
        }
        copyItems(event);
    }

    /**
     * Вставить строки из буфера обмена
     */
    private void pasteRows(Event event){
        List<Integer> rows = tv.getSelectionModel().getSelectedIndices();
        for(int row : rows){
            tv.getSelectionModel().select(row);
            tv.getSelectionModel().select(row, findTableColumnById("0", tv));
        }
        pasteItems(event);
    }

    /**
     *  Метод удаляет выделенные строки
     */
    private void deleteRows(Event event) {
        tv.getItems().removeAll(tv.getSelectionModel().getSelectedItems());
        tv.getSelectionModel().clearSelection();
        event.consume();
    }

    /**
     * Метод вставляет данные из системного клипборда в таблицу
     */
    private void paste(Event event) {
        //Проверяем, что в буфере обмена
        List<List<String>> clipBoardArr = divideClipBoardToItems();
        //Если в буфере целые строки, то вставляем строки
        if(clipBoardArr.get(0).size() == getAllColumns(tv).size()) pasteRows(event);
        //Иначе вставляем отдельные элементы
        else pasteItems(event);
    }

    /**
     * Метод вставляет данные из системного клипборда в таблицу
     */
    private void pasteItems(final Event event) {
        Platform.runLater(() -> {
            List<List<String>> clipBoardArr = divideClipBoardToItems();

            ObservableList<TablePosition> tPoses = FXCollections.observableArrayList(tv.getSelectionModel().getSelectedCells());

            int zeroRowIndex = tPoses.get(0).getRow();
            int zeroColumnId = Integer.parseInt(tPoses.get(0).getTableColumn().getId());

            int lastSelectedRow = tv.getSelectionModel().getSelectedIndices().size() + zeroRowIndex;

            int targetRow = zeroRowIndex;
            while(targetRow < lastSelectedRow) {
                for (int i = 0; i < clipBoardArr.size(); i++) {
                    int targetCol = zeroColumnId;
                    //вставка происходит построчно
                    EditorRow editedRow = tv.getItems().get(targetRow);
                    for (int j = 0; j < clipBoardArr.get(i).size(); j++) {
                        TableColumn<EditorRow, ?> currentTc = findTableColumnById(String.valueOf(targetCol), tv);
                        TableColumnBase<EditorRow, ?> exTc = findTableColumnById(String.valueOf(targetCol), tv).getParentColumn();
                        if(exTc != null)
                            editedRow.changeItemValue(exTc.getId(), currentTc.getText(), clipBoardArr.get(i).get(j));
                        else
                            editedRow.changeItemValue(null, currentTc.getText(), clipBoardArr.get(i).get(j));

                        tv.getSelectionModel().select(targetRow, currentTc);
                        targetCol++;
                    }
                    targetRow++;
                }
            }

            tv.refresh();
            event.consume();
        });
    }

    /**
     * Метод разбивает строку из буфера обмена на строки
     * @return
     */
    private List<List<String>> divideClipBoardToItems() {
        String cbString = (String) Clipboard.getSystemClipboard().getContent(DataFormat.PLAIN_TEXT);
        //Разбиваем строку из клипборда на массив строк,
        // -1 Чтобы учитывались и пустые строки
        List<String> cbRows = new ArrayList<>(Arrays.asList(cbString.split("\\n", -1)));

        List<List<String>> clipBoardArr = new ArrayList<>();
        for (String r : cbRows) {
            //-1 Чтобы учитывались и пустые ячейки
            clipBoardArr.add(Arrays.asList(r.split("\\t", -1)));
        }
        return clipBoardArr;
    }

    /**
     * Метод добавляет в таблицу пустые строки
     */
    private void addEmptyRows(final Event event, final String where) {
        Platform.runLater(() -> {
            int firstSelectedRow = tv.getSelectionModel().getSelectedIndices().get(0);
            int lastSelectedRow = tv.getSelectionModel().getSelectedIndices().get(0)
                    + tv.getSelectionModel().getSelectedIndices().size();
            int numberOfRows = tv.getSelectionModel().getSelectedIndices().size();

            if(where.equals("upper")) {
                for (int i = 0; i < numberOfRows; i++)
                    tv.getItems().add(firstSelectedRow, createEmptyRow());
            } else {
                for (int i = 0; i < numberOfRows; i++)
                    tv.getItems().add(lastSelectedRow, createEmptyRow());
            }
            tv.refresh();
            event.consume();
        });
    }

    private EditorRow createEmptyRow(){
        EditorRow row = new EditorRow();
        row.setExecutions(getEmptyExecutions());

        //Не забываем про цвет пустой строки
        row.setColor("WHITE");
        return row;
    }


    /**
     * Метод восстанавливает нумерацию в столбце N
     */
    private void renumerateRows(Event event) {
        int number = 1;
        for(EditorRow row : tv.getItems()){
            row.setRowNumber(String.valueOf(number++));
        }
        tv.refresh();
        event.consume();
    }

    /**
     * Метод возвращает текущие исполнения
     */
    public HashMap<String, String> findExecutions(){
        //Первый String - id колонки, второй String - id всего исполнения
        HashMap<String, String> executions = new HashMap<>();
        for(TableColumn<EditorRow, ?> tc: getAllColumns(tv)){
            if(tc.getText().equals(EColName.TOTAL_AMOUNT.getColName()))
                executions.put(tc.getId(), tc.getParentColumn().getId());
        }
        return executions;
    }

    public ArrayList<EditorRow.Execution> getEmptyExecutions(){
        ArrayList<EditorRow.Execution> emptyExecutions = new ArrayList<>();
        for(EditorRow.Execution ex: tv.getItems().get(0).getExecutions()){
            emptyExecutions.add(new EditorRow.Execution(ex.getId(), "", ""));
        }
        return emptyExecutions;
    }
}
