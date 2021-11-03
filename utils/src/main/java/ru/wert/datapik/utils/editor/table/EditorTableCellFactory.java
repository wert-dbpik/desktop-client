package ru.wert.datapik.utils.editor.table;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import lombok.extern.java.Log;
import ru.wert.datapik.utils.editor.model.EditorRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static ru.wert.datapik.utils.editor.enums.EColName.*;
import static ru.wert.datapik.utils.editor.enums.EColor.*;
import static ru.wert.datapik.utils.editor.table.TableMaster.getAllColumns;

@Log
public class EditorTableCellFactory extends TableCell<EditorRow, String> {

    protected EditorTable editorTable;
    protected TextField textField = new TextField();
    private String previousStyle;
    private static int startIndex, startId;
    private int currentIndex, currentId;
    private final List<String> intCols =
            Arrays.asList(ROW_NUM.getColName(), TOTAL_AMOUNT.getColName(), A.getColName(), B.getColName());

    public EditorTableCellFactory(EditorTable editorTable) {
        this.editorTable = editorTable;
        textField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                commitEdit(textField.getText());
                clearAllSelections();
                getTableView().getSelectionModel().selectBelowCell();
            } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                textField.setText(getItem());
                cancelEdit();
                keyEvent.consume();
            } else if (keyEvent.getCode() == KeyCode.RIGHT) {
                commitEdit(textField.getText());
                clearAllSelections();
                getTableView().getSelectionModel().selectRightCell();
                keyEvent.consume();
            } else if (keyEvent.getCode() == KeyCode.LEFT) {
                commitEdit(textField.getText());
                clearAllSelections();
                getTableView().getSelectionModel().selectLeftCell();
                keyEvent.consume();
            } else if (keyEvent.getCode() == KeyCode.UP) {
                commitEdit(textField.getText());
                clearAllSelections();
                getTableView().getSelectionModel().selectAboveCell();
                keyEvent.consume();
            } else if (keyEvent.getCode() == KeyCode.DOWN) {
                commitEdit(textField.getText());
                clearAllSelections();
                getTableView().getSelectionModel().selectBelowCell();
                keyEvent.consume();
            }

        });

        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (! isNowFocused) {
                commitEdit(textField.getText());
            }
        });

        createBlockSelection();
    }

    @Override
    public void startEdit(){

        super.startEdit();
        if (!isEmpty()) {

            setGraphic(textField);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            textField.setText(getText());
            textField.setStyle(getStyle() + "-fx-background-color: white; -fx-padding: 0px");

            Platform.runLater(()->{
                textField.requestFocus();
                textField.selectEnd();//отменяет выделение текста, ставит каретку в конец строки(иногда!)
            });

        }
    }



    @Override
    public void commitEdit(String newValue)
    {
        super.commitEdit(newValue);

//        if(getTableColumn().getText().equals("(кол)")){
//            int val = Integer.parseInt(newValue.replaceAll("[()]", ""));
//            List<EditorRow> sortedTable = new ArrayList<>(getTableView().getItems());
//            sortedTable.sort(EditorTableUtil.createRowNumberComparator());
//            int currentRow = Integer.parseInt(getTableView().getItems().get(getIndex()).getRowNumber());
//
//
//        }

        TableView<EditorRow> table = getTableView();
        TableColumn<EditorRow, String> column = getTableColumn();
        TableColumn.CellEditEvent<EditorRow, String> event = new TableColumn.CellEditEvent<>(table,
                new TablePosition<>(table, getIndex(), column),
                TableColumn.editCommitEvent(), newValue);

        Event.fireEvent(column, event);

//        super.commitEdit(newValue);

        getTableView().refresh();
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    public void cancelEdit(){
        super.cancelEdit();
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    protected void updateItem(String item, boolean empty){
        setStyle(cellStyle(getTableColumn().getText()));
        setGraphic(getGraphic());

        super.updateItem(item, empty);

        if(item == null || empty){
            setStyle("");
        } else {
            if(getTableColumn().getText().equals("(кол)")){
                if(item.equals("") || !item.matches("^[0-9()]+?$"))
                    item = "";
                else{
                    //Чтобы избежать дублирования скобок Сначала удаляем скобки
                    item = item.replaceAll("[()]", "");
                    //Потом добавляем снова
                    item = "(" + item + ")";
                }

            }

            else if(intCols.contains(getTableColumn().getText()) && !item.matches("^[0-9]+?$")) item = "";


            setText(item);
            TableRow<EditorRow> currentRow = getTableRow();

            if (!isEmpty() && currentRow != null && currentRow.getItem() != null) {

                final String whiteRowStyle = "-fx-background-color:white; -fx-text-background-color: black;";
                final String greenRowStyle = "-fx-background-color:rgb(146, 209, 79); -fx-text-background-color: black; -fx-font-weight: bold;";
                final String blackRowStyle = "-fx-background-color:black; -fx-text-background-color: white; -fx-font-weight: bold;";
                final String orangeRowStyle = "-fx-background-color:orange; -fx-text-background-color: black; -fx-font-weight: bold;";
                final String yellowRowStyle = "-fx-background-color:yellow; -fx-text-background-color: black; -fx-font-weight: bold;";
                final String blueRowStyle = "-fx-background-color:lightblue; -fx-text-background-color: black; -fx-font-weight: bold;";
                final String colNStyle = "-fx-alignment: center; -fx-font-style: oblique; -fx-font-weight: normal;";

                String cell0 = currentRow.getItem().getColor();
                String cell1 = currentRow.getItem().getRowNumber();

                switch (currentRow.getItem().getColor()) {
                    case "WHITE":
                        if (!item.equals(cell0) && !(item + ".0").equals(cell1)) {
                            setStyle(getStyle() + whiteRowStyle);
                        } else
                            setStyle(getStyle());
                        break;
                    case "GREEN":
                        if (!item.equals(cell0) && !(item + ".0").equals(cell1)) {
                            setStyle(getStyle() + greenRowStyle);
                            currentRow.getItem().setKrp("◊");
                        } else
                            setStyle(getStyle());
                        break;
                    case "BLACK":
                        if (!item.equals(cell0) && !(item + ".0").equals(cell1)) {
                            setStyle(getStyle() + blackRowStyle);
                        } else
                            setStyle(getStyle());
                        break;
                    case "ORANGE":
                        if (!item.equals(cell0) && !(item + ".0").equals(cell1)) {
                            setStyle(getStyle() + orangeRowStyle);
                            currentRow.getItem().setKrp("●");
                        } else
                            setStyle(getStyle());
                        break;
                    case "YELLOW":
                        if (!item.equals(cell0) && !(item + ".0").equals(cell1)){
                            setStyle(getStyle() + yellowRowStyle);
                            currentRow.getItem().setKrp("●●");
                        } else
                            setStyle(getStyle());
                        break;
                    case "BLUE":
                        if (!item.equals(cell0) && !(item + ".0").equals(cell1)) {
                            setStyle(getStyle() + blueRowStyle);
                            currentRow.getItem().setKrp("●●●");
                        } else
                            setStyle(getStyle());
                        break;
                    default:
                        setStyle(getStyle());
                }

                if(getId().equals("1")) setStyle(colNStyle);
            }
        }
    }



    /**
     * Функция для каждого столбца устанавливает свой стиль оформления
     * @param name
     * @return
     */
    protected String cellStyle(String name){
        final String commonStyle = "-fx-border-radius: 0 0 0 0;-fx-border-width: 1 1 1 1;-fx-background-color: transparent;";
        final String centeredWideStyle = "-fx-alignment: center;-fx-min-width: 100;";
        final String centeredNarrowStyle = "-fx-alignment: center;-fx-min-width: 40;";
        final String boldStyle = "-fx-font-weight: bold;";

        switch (name){
            case "N" : return centeredNarrowStyle + commonStyle;
            case "КРП" : return boldStyle + centeredNarrowStyle + commonStyle;
            case "Кол" : return centeredNarrowStyle + commonStyle;
            case "(кол)" : return centeredNarrowStyle + commonStyle;

            case "Номер" : return commonStyle;
            case "Наименование" :return commonStyle;
            case "Материал А" :return commonStyle;

            default: return  centeredWideStyle + commonStyle;
        }
    }

    /**
     * Метод добавляет таблице возможность выделять ячейки целым блоком
     */
    protected void createBlockSelection() {
        selectedProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue) {
                previousStyle = getStyle();
                String color = ((EditorRow)getTableRow().getItem()).getColor();

                if(color.equals(GREEN.name()))
                    setStyle(previousStyle + "-fx-background-color: rgb(112, 188, 141); ");
                else if(color.equals(BLACK.name()))
                    setStyle(previousStyle + "-fx-background-color: rgb(17, 53, 89); ");
                else if(color.equals(ORANGE.name()))
                    setStyle(previousStyle + "-fx-background-color: rgb(183, 177, 89); ");
                else if(color.equals(YELLOW.name()))
                    setStyle(previousStyle + "-fx-background-color: rgb(183, 218, 89); ");
                else if(color.equals(BLUE.name()))
                    setStyle(previousStyle + "-fx-background-color: rgb(144, 194, 245); ");
                else
                    setStyle(previousStyle + "-fx-background-color: rgb(183, 219, 255); ");


            } else {
                setStyle(previousStyle);
            }
        });

        setOnDragDetected(event -> {
            startFullDrag();
            clearAllSelections();

            startIndex = getIndex();
            startId = findNumOfTableColumnById(getId());

            getTableColumn()
                    .getTableView().getSelectionModel().select(getIndex(), getTableColumn());
//                getTableView().scrollTo(getTableRow().getIndex());
        });

        setOnMouseDragEntered(event -> {
            currentIndex = getIndex();
            currentId = findNumOfTableColumnById(getId());

            clearAllSelections();

            int a = Math.min(startIndex, currentIndex);
            int b = Math.max(startIndex, currentIndex);
            int c = Math.min(startId, currentId);
            int d = Math.max(startId, currentId);

            for (int i = a; i <= b; i++)
                for (int j = c; j <= d; j++) {
                    getTableView().getSelectionModel().select(i, getAllColumns(getTableView()).get(j));
                }
        });
    }

    /**
     * Метод снимает всякое выделение в таблице
     */
    protected void clearAllSelections(){
        ObservableList<TablePosition> tablePositions =
                FXCollections.observableArrayList(getTableView().getSelectionModel().getSelectedCells());
        TableView.TableViewSelectionModel<EditorRow> sm = getTableView().getSelectionModel();
        for (TablePosition tp : tablePositions) {
            sm.clearSelection(tp.getRow(), tp.getTableColumn());
        }
    }

    /**
     * Метод находит в таблице порядковый номер столбца по его id
     */
    protected int findNumOfTableColumnById(String id){
        ArrayList<TableColumn<EditorRow, ?>> allColumns = getAllColumns(getTableView());
        for(int i = 0; i < allColumns.size(); i++){
            if(allColumns.get(i).getId().equals(id))
                return i;
        }

        throw new NoSuchElementException("There is no column in the table with id = " + id);
    }





}
