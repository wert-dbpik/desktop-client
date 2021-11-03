package ru.wert.datapik.utils.editor.table;

import com.sun.javafx.scene.control.TableColumnSortTypeWrapper;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import lombok.Data;
import ru.wert.datapik.utils.editor.Cursors;
import ru.wert.datapik.utils.editor.context_menu.EditorContextMenu;
import ru.wert.datapik.utils.editor.context_menu.EditorExContextMenu;
import ru.wert.datapik.utils.editor.model.EditorRow;
import ru.wert.datapik.utils.editor.poi.POIReader;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static ru.wert.datapik.utils.editor.enums.EColName.*;
import static ru.wert.datapik.utils.editor.table.TableMaster.*;

@Data
public class EditorTable {

    private ObservableList<EditorRow> data;
    private POIReader poi;
    private HashMap<Integer, Integer> executions;
    private HashMap<Integer, String> executionNames;
    private HashMap<Integer, String> executionDescriptions;
    private HBox hbox;
    private TableView<EditorRow> tableView;
    private ScrollBar verticalBar;
    private ScrollBar horizontalBar;
    double scrW = 20; //ширина бокового скролла

    public EditorTable(POIReader poi, HBox hbox) {
        this.poi = poi;
        this.hbox = hbox;
        this.data = poi.findModelData();
        this.executions = poi.getExecutions(); //ключ - порядковый номер исполнения, значение - индекс столбца

        createTableView(poi, hbox);
        setIdToColumns(tableView);

        for (TableColumn tc : getAllColumns(tableView)) {
            if(tc.getText().equals("(кол)"))
                tc.setCellFactory(param -> new EditorTableExCellFactory(this, tc.getParentColumn().getId()));
            else tc.setCellFactory(param -> new EditorTableCellFactory(this));
            addStretchListenerToColumn(tc);
        }

        //Запрет на реодеринг столбцов
        restrictColumnReodering();

        //Наполняем таблицу данными
        tableView.setItems(data);

        tableView.setEditable(true);
        tableView.getSelectionModel().setCellSelectionEnabled(true);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setCursor(Cursors.cursorWhiteCross);

        createKeyEvents();
        createMouseEvents();
        createWidthPropertyListener(hbox);

        //Устанавливаем первоначальную ширину таблицы и контейнера HBox
        double tW = countPrefTableWidth(getAllColumns(tableView));
        tableView.setPrefWidth(tW);
        tableView.resize(tW, tableView.getHeight());
        hbox.setPrefWidth(tW);

        tableView.setContextMenu(new EditorContextMenu(tableView));
        reArrangeExecutionCols();
//        testShowTableView();
    }


    public void changeTableWidth() {
        Platform.runLater(() -> {
            double newWidth = countPrefTableWidth(getAllColumns(tableView));
            tableView.resize(newWidth, tableView.getHeight());
            tableView.setPrefWidth(newWidth);
        });
    }

    private void restrictColumnReodering() {
        tableView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            final TableHeaderRow header = (TableHeaderRow) tableView.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((o, oldVal, newVal) -> header.setReordering(false));
        });
    }


    /**
     * Метод создает TableView
     * @param poi
     * @param hbox
     */
    private void createTableView(POIReader poi, HBox hbox) {
        tableView = new TableView();

        //Цвет
        TableColumn<EditorRow, String> color = new TableColumn<>(ECOLOR.getColName());
        color.setCellValueFactory(new PropertyValueFactory<>("color"));
        color.setVisible(false);

        //N
        TableColumn<EditorRow, String> rowNumber = new TableColumn<>(ROW_NUM.getColName());
        rowNumber.setCellValueFactory(new PropertyValueFactory<>("rowNumber"));
        rowNumber.setComparator(createIntegerComparator(rowNumber));

        //КРП
        TableColumn<EditorRow, String> krp = new TableColumn<>(KRP.getColName());
        krp.setCellValueFactory(new PropertyValueFactory<>("krp"));
        krp.setOnEditCommit((TableColumn.CellEditEvent<EditorRow, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setKrp(t.getNewValue());
        });

        //Децимальный номер
        TableColumn<EditorRow, String> decNumber = new TableColumn<>(DEC_NUM.getColName());
        decNumber.setCellValueFactory(new PropertyValueFactory<>("decNumber"));
        decNumber.setOnEditCommit((TableColumn.CellEditEvent<EditorRow, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setDecNumber(t.getNewValue());
        });

        //Наименование
        TableColumn<EditorRow, String> name = new TableColumn<>(NAME.getColName());
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        name.setCellFactory(param -> new TextFieldTableCell<>());
        name.setOnEditCommit((TableColumn.CellEditEvent<EditorRow, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setName(t.getNewValue());
        });

        //Лак
        TableColumn<EditorRow, String> lacquer = new TableColumn<>(LACQUER.getColName());
        lacquer.setCellValueFactory(new PropertyValueFactory<>("lacquer"));
        lacquer.setOnEditCommit((TableColumn.CellEditEvent<EditorRow, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setLacquer(t.getNewValue());
        });

        //Покрытие
        TableColumn<EditorRow, String> coat = new TableColumn<>(COAT.getColName());
        coat.setCellValueFactory(new PropertyValueFactory<>("coat"));
        coat.setOnEditCommit((TableColumn.CellEditEvent<EditorRow, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setCoat(t.getNewValue());
        });

        //ЦСГ
        TableColumn<EditorRow, String> zpc = new TableColumn<>(ZCP.getColName());
        zpc.setCellValueFactory(new PropertyValueFactory<>("zpc"));
        zpc.setOnEditCommit((TableColumn.CellEditEvent<EditorRow, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setZcp(t.getNewValue());
        });

        //Источник
        TableColumn<EditorRow, String> folder = new TableColumn<>(FOLDER.getColName());
        folder.setCellValueFactory(new PropertyValueFactory<>("folder"));
        folder.setOnEditCommit((TableColumn.CellEditEvent<EditorRow, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setFolder(t.getNewValue());
        });

        //Материал
        TableColumn<EditorRow, String> material = new TableColumn<>(MATERIAL.getColName());
        material.setCellValueFactory(new PropertyValueFactory<>("material"));
        material.setOnEditCommit((TableColumn.CellEditEvent<EditorRow, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setMaterial(t.getNewValue());
        });

        //Параметр А
        TableColumn<EditorRow, String> paramA = new TableColumn<>(A.getColName());
        paramA.setCellValueFactory(new PropertyValueFactory<>("paramA"));
        paramA.setComparator(createIntegerComparator(paramA));
        paramA.setOnEditCommit((TableColumn.CellEditEvent<EditorRow, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setParamA(t.getNewValue());
        });

        //Параметр В
        TableColumn<EditorRow, String> paramB = new TableColumn<>(B.getColName());
        paramB.setCellValueFactory(new PropertyValueFactory<>("paramB"));
        paramB.setComparator(createIntegerComparator(paramB));
        paramB.setOnEditCommit((TableColumn.CellEditEvent<EditorRow, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setParamB(t.getNewValue());
        });

        //Добавляем в таблицу первые столбцы
        tableView.getColumns().addAll(color, rowNumber, krp, decNumber, name);

        //Столлбец с Лаком
        if (poi.isColLaсquerExist()) tableView.getColumns().add(lacquer);
        //Столлбец с порошковым покрытием
        tableView.getColumns().add(coat);
        //Столлбец с Цинк содержащим грунтом
        if (poi.isColZpcExist()) tableView.getColumns().add(zpc);

        //Создаем исполнения
        for (int ex : executions.keySet()) {
            String exName = poi.getExecutionName(ex);
            ExecutionColumn exCol = new ExecutionColumn(exName, "", ex, this);

            //Добавляем исполнение в таблицу
            tableView.getColumns().add(exCol);
        }

        //Добавляем в таблицу оставшиеся столбцы
        tableView.getColumns().addAll(folder, material, paramA, paramB);


    }

    /**
     * Метод обеспечивает редактирование ячеек по нажатию клавиши
     */
    private void createKeyEvents() {
        tableView.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (!event.isControlDown() && !event.getText().equals("")) {
                if (tableView.getEditingCell() == null) {
                    TablePosition focusedCellPosition = tableView.getFocusModel().getFocusedCell();
                    int row = focusedCellPosition.getRow();
                    TableColumn col = focusedCellPosition.getTableColumn();

                    //Определяем веденный текст - один символ
                    String text = event.getText();
                    if (event.isShiftDown()) text = text.toUpperCase();

                    if (col.getParentColumn() != null)
                        tableView.getItems().get(row).changeItemValue(col.getParentColumn().getId(), col.getText(), text);
                    else
                        tableView.getItems().get(row).changeItemValue(null, col.getText(), text);
                    tableView.edit(row, col);

                    event.consume();
                }
            }

        });
    }

    /**
     * Обработка нажатия мыши
     */
    private void createMouseEvents() {

        Platform.runLater(() -> {
            verticalBar = (ScrollBar) tableView.lookup(".scroll-bar:vertical");
            horizontalBar = (ScrollBar) tableView.lookup(".scroll-bar:horizontal");
        });


        tableView.addEventFilter(ScrollEvent.ANY, e -> {
            Platform.runLater(() -> tableView.refresh());
        });
    }

    /**
     * Метод создает слушатель, который меняет ширину контейнера при изменении ширины таблицы внутри него
     */
    private void createWidthPropertyListener(HBox hbox) {
        //При изменении размеров контейнера меняется ширина таблицы, без пустой колонки справа
        hbox.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() > tableView.getWidth()) {
                tableView.setPrefWidth(countPrefTableWidth(getAllColumns(tableView)));
                tableView.resize(countPrefTableWidth(getAllColumns(tableView)), tableView.getHeight());
            }
        });
    }

    /**
     * Метод изменяет ширину контейнеа при изменении ширины столбца
     */
    private void addStretchListenerToColumn(TableColumn<List<String>, String> col) {
        col.widthProperty().addListener((observable, oldValue, newValue) -> {
            double dW = newValue.doubleValue() - oldValue.doubleValue();
            double tW = tableView.getWidth();
            if (tW > 0 && dW > 0) {
                if (tW + dW + scrW <= hbox.getWidth()) {
                    tableView.setPrefWidth(tableView.getWidth() + dW);
                    tableView.resize(tableView.getWidth() + dW, tableView.getHeight());
                } else {
                    tableView.setPrefWidth(hbox.getWidth() - scrW);
                    tableView.resize(hbox.getWidth() - scrW, tableView.getHeight());
                }
            }

            if (tW > 0 && dW < 0) {
                if (countPrefTableWidth(getAllColumns(tableView)) + scrW < hbox.getWidth()) {
                    tableView.setPrefWidth(tableView.getWidth() + dW);
                    tableView.resize(tableView.getWidth() + dW, tableView.getHeight());
                } else {

                }
            }
        });
    }

    /**
     * Компаратор для строковых значений Id, ParentId и т.д
     */
    public Comparator<String> createIntegerComparator(TableColumn<EditorRow, String> col) {

        return (o1, o2) -> {

            //Для того, чтобы пустые строки были всегда внизу
            if (TableColumnSortTypeWrapper.isAscending(col)) {
                if (o1.isEmpty()) o1 = String.valueOf(Integer.MAX_VALUE);
                if (o2.isEmpty()) o2 = String.valueOf(Integer.MAX_VALUE);
            } else {
                if (o1.isEmpty()) o1 = String.valueOf(Integer.MIN_VALUE);
                if (o2.isEmpty()) o2 = String.valueOf(Integer.MIN_VALUE);
            }

            Integer i1 = Integer.parseInt(o1);
            Integer i2 = Integer.parseInt(o2);

            return i1.compareTo(i2);
        };
    }

    public HashMap<Integer, Integer> reArrangeExecutionCols() {
        HashMap<Integer, Integer> executions = new HashMap<>();
        int ex = 0;
        for (int index = 0; index < tableView.getColumns().size(); index++) {
            TableColumn<EditorRow, ?> tc = tableView.getColumns().get(index);
            if (tc.getId().startsWith("ex")) {
                tc.setId("ex" + ex);
                ((ExecutionColumn)tc).currentEx = ex;
                tc.contextMenuProperty().setValue(new EditorExContextMenu(this, tc, ex));
                executions.put(ex, index);
                ex++;
            }
        }
        this.executions = executions;
        return executions;
    }


    public void reArrangeExecutionRows() {
        for (EditorRow row : tableView.getItems()) {
            int exId = 0;
            for (EditorRow.Execution ex : row.getExecutions()) {
                ex.setId("ex" + (exId++));
            };
        }
    }
}
