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
import lombok.Getter;
import ru.wert.datapik.utils.editor.Cursors;
import ru.wert.datapik.utils.editor.context_menu.Excel_ContextMenu;
import ru.wert.datapik.utils.editor.context_menu.Excel_ExContextMenu;
import ru.wert.datapik.utils.editor.model.EditorRow;
import ru.wert.datapik.utils.editor.poi.POIReader;
import ru.wert.datapik.utils.search.Searchable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static ru.wert.datapik.utils.editor.enums.EColName.*;
import static ru.wert.datapik.utils.editor.table.TableMaster.*;


public class Excel_TableView extends TableView<EditorRow> {

    private ObservableList<EditorRow> data;
    @Getter private final POIReader poi;
    @Getter private boolean useContextMenu;
    private HashMap<Integer, Integer> executions;
    private HashMap<Integer, String> executionNames;
    private HashMap<Integer, String> executionDescriptions;
    private HBox hbox;
//    @Getter private TableView<EditorRow> tableView;
    private ScrollBar verticalBar;
    private ScrollBar horizontalBar;
    double scrW = 20; //ширина бокового скролла
    
    public Excel_TableView getTableView(){
        return this;
    }

    //Столбцы
    @Getter private TableColumn<EditorRow, String> color;
    @Getter private TableColumn<EditorRow, String> rowNumber;
    @Getter private TableColumn<EditorRow, String> krp;
    @Getter private TableColumn<EditorRow, String> decNumber;
    @Getter private TableColumn<EditorRow, String> name;
    @Getter private TableColumn<EditorRow, String> coat;
    @Getter private TableColumn<EditorRow, String> lacquer;
    @Getter private TableColumn<EditorRow, String> zpc;
    @Getter private TableColumn<EditorRow, String> folder;
    @Getter private TableColumn<EditorRow, String> material;
    @Getter private TableColumn<EditorRow, String> paramA;
    @Getter private TableColumn<EditorRow, String> paramB;

    public Excel_TableView(POIReader poi, HBox hbox, boolean useContextMenu) {
        this.poi = poi;
        this.hbox = hbox;
        this.useContextMenu = useContextMenu;

        this.data = poi.findModelData();
        this.executions = poi.getExecutions(); //ключ - порядковый номер исполнения, значение - индекс столбца

        createTableView(poi, hbox);
        setIdToColumns(this);

        for (TableColumn tc : getAllColumns(this)) {
            if(tc.getText().equals("(кол)"))
                tc.setCellFactory(param -> new Excel_TableExCellFactory(this, tc.getParentColumn().getId()));
            else tc.setCellFactory(param -> new Excel_TableCellFactory(this));
            addStretchListenerToColumn(tc);
        }

        //Запрет на реодеринг столбцов
        restrictColumnReodering();

        //Наполняем таблицу данными
        setItems(data);

        setEditable(true);
        getSelectionModel().setCellSelectionEnabled(true);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setCursor(Cursors.cursorWhiteCross);

        createKeyEvents();
        createMouseEvents();
        createWidthPropertyListener(hbox);

        //Устанавливаем первоначальную ширину таблицы и контейнера HBox
        double tW = countPrefTableWidth(getAllColumns(this));
        setPrefWidth(tW);
        resize(tW, getHeight());
        hbox.setPrefWidth(tW);

        if (useContextMenu)
            setContextMenu(new Excel_ContextMenu(this));
        reArrangeExecutionCols();
//        testShowTableView();
    }


    public void changeTableWidth() {
        Platform.runLater(() -> {
            double newWidth = countPrefTableWidth(getAllColumns(this));
            resize(newWidth, getHeight());
            setPrefWidth(newWidth);
        });
    }

    private void restrictColumnReodering() {
        skinProperty().addListener((obs, oldSkin, newSkin) -> {
            final TableHeaderRow header = (TableHeaderRow) lookup("TableHeaderRow");
            header.reorderingProperty().addListener((o, oldVal, newVal) -> header.setReordering(false));
        });
    }


    /**
     * Метод создает TableView
     * @param poi
     * @param hbox
     */
    private void createTableView(POIReader poi, HBox hbox) {

        //Цвет
        color = new TableColumn<>(ECOLOR.getColName());
        color.setCellValueFactory(new PropertyValueFactory<>("color"));
        color.setVisible(false);

        //N
        rowNumber = new TableColumn<>(ROW_NUM.getColName());
        rowNumber.setCellValueFactory(new PropertyValueFactory<>("rowNumber"));
        rowNumber.setComparator(createIntegerComparator(rowNumber));

        //КРП
        krp = new TableColumn<>(KRP.getColName());
        krp.setCellValueFactory(new PropertyValueFactory<>("krp"));
        krp.setOnEditCommit((TableColumn.CellEditEvent<EditorRow, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setKrp(t.getNewValue());
        });

        //Децимальный номер
        decNumber = new TableColumn<>(DEC_NUM.getColName());
        decNumber.setCellValueFactory(new PropertyValueFactory<>("decNumber"));
        decNumber.setOnEditCommit((TableColumn.CellEditEvent<EditorRow, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setDecNumber(t.getNewValue());
        });

        //Наименование
        name = new TableColumn<>(NAME.getColName());
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
        coat = new TableColumn<>(COAT.getColName());
        coat.setCellValueFactory(new PropertyValueFactory<>("coat"));
        coat.setOnEditCommit((TableColumn.CellEditEvent<EditorRow, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setCoat(t.getNewValue());
        });

        //ЦСГ
        zpc = new TableColumn<>(ZCP.getColName());
        zpc.setCellValueFactory(new PropertyValueFactory<>("zpc"));
        zpc.setOnEditCommit((TableColumn.CellEditEvent<EditorRow, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setZcp(t.getNewValue());
        });

        //Источник
        folder = new TableColumn<>(FOLDER.getColName());
        folder.setCellValueFactory(new PropertyValueFactory<>("folder"));
        folder.setOnEditCommit((TableColumn.CellEditEvent<EditorRow, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setFolder(t.getNewValue());
        });

        //Материал
        material = new TableColumn<>(MATERIAL.getColName());
        material.setCellValueFactory(new PropertyValueFactory<>("material"));
        material.setOnEditCommit((TableColumn.CellEditEvent<EditorRow, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setMaterial(t.getNewValue());
        });

        //Параметр А
        paramA = new TableColumn<>(A.getColName());
        paramA.setCellValueFactory(new PropertyValueFactory<>("paramA"));
        paramA.setComparator(createIntegerComparator(paramA));
        paramA.setOnEditCommit((TableColumn.CellEditEvent<EditorRow, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setParamA(t.getNewValue());
        });

        //Параметр В
        paramB = new TableColumn<>(B.getColName());
        paramB.setCellValueFactory(new PropertyValueFactory<>("paramB"));
        paramB.setComparator(createIntegerComparator(paramB));
        paramB.setOnEditCommit((TableColumn.CellEditEvent<EditorRow, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setParamB(t.getNewValue());
        });

        //Добавляем в таблицу первые столбцы
        getColumns().addAll(color, rowNumber, krp, decNumber, name);

        //Столлбец с Лаком
        if (poi.isColLaсquerExist()) getColumns().add(lacquer);
        //Столлбец с порошковым покрытием
        getColumns().add(coat);
        //Столлбец с Цинк содержащим грунтом
        if (poi.isColZpcExist()) getColumns().add(zpc);

        //Создаем исполнения
        for (int ex : executions.keySet()) {
            String exName = poi.getExecutionName(ex);
            Excel_ExecutionColumn exCol = new Excel_ExecutionColumn(exName, "", ex, this);

            //Добавляем исполнение в таблицу
            getColumns().add(exCol);
        }

        //Добавляем в таблицу оставшиеся столбцы
        getColumns().addAll(folder, material, paramA, paramB);


    }

    /**
     * Метод обеспечивает редактирование ячеек по нажатию клавиши
     */
    private void createKeyEvents() {
        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (!event.isControlDown() && !event.getText().equals("")) {
                if (getEditingCell() == null) {
                    TablePosition focusedCellPosition = getFocusModel().getFocusedCell();
                    int row = focusedCellPosition.getRow();
                    TableColumn col = focusedCellPosition.getTableColumn();

                    //Определяем веденный текст - один символ
                    String text = event.getText();
                    if (event.isShiftDown()) text = text.toUpperCase();

                    if (col.getParentColumn() != null)
                        getItems().get(row).changeItemValue(col.getParentColumn().getId(), col.getText(), text);
                    else
                        getItems().get(row).changeItemValue(null, col.getText(), text);
                    edit(row, col);

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
            verticalBar = (ScrollBar) lookup(".scroll-bar:vertical");
            horizontalBar = (ScrollBar) lookup(".scroll-bar:horizontal");
        });


        addEventFilter(ScrollEvent.ANY, e -> {
            Platform.runLater(() -> refresh());
        });
    }

    /**
     * Метод создает слушатель, который меняет ширину контейнера при изменении ширины таблицы внутри него
     */
    private void createWidthPropertyListener(HBox hbox) {
        //При изменении размеров контейнера меняется ширина таблицы, без пустой колонки справа
        hbox.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() > getWidth()) {
                setPrefWidth(countPrefTableWidth(getAllColumns(this)));
                resize(countPrefTableWidth(getAllColumns(this)), getHeight());
            }
        });
    }

    /**
     * Метод изменяет ширину контейнеа при изменении ширины столбца
     */
    private void addStretchListenerToColumn(TableColumn<List<String>, String> col) {
        col.widthProperty().addListener((observable, oldValue, newValue) -> {
            double dW = newValue.doubleValue() - oldValue.doubleValue();
            double tW = getWidth();
            if (tW > 0 && dW > 0) {
                if (tW + dW + scrW <= hbox.getWidth()) {
                    setPrefWidth(getWidth() + dW);
                    resize(getWidth() + dW, getHeight());
                } else {
                    setPrefWidth(hbox.getWidth() - scrW);
                    resize(hbox.getWidth() - scrW, getHeight());
                }
            }

            if (tW > 0 && dW < 0) {
                if (countPrefTableWidth(getAllColumns(this)) + scrW < hbox.getWidth()) {
                    setPrefWidth(getWidth() + dW);
                    resize(getWidth() + dW, getHeight());
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
        for (int index = 0; index < getColumns().size(); index++) {
            TableColumn<EditorRow, ?> tc = getColumns().get(index);
            if (tc.getId().startsWith("ex")) {
                tc.setId("ex" + ex);
                ((Excel_ExecutionColumn)tc).currentEx = ex;
                if(useContextMenu)
                    tc.contextMenuProperty().setValue(new Excel_ExContextMenu(this, tc, ex));
                executions.put(ex, index);
                ex++;
            }
        }
        this.executions = executions;
        return executions;
    }


    public void reArrangeExecutionRows() {
        for (EditorRow row : getItems()) {
            int exId = 0;
            for (EditorRow.Execution ex : row.getExecutions()) {
                ex.setId("ex" + (exId++));
            };
        }
    }
}
