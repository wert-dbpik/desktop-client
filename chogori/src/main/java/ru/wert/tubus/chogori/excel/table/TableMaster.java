package ru.wert.tubus.chogori.excel.table;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import ru.wert.tubus.chogori.excel.Cursors;

import java.util.ArrayList;

public class TableMaster {
    private static double scrollWidth = 20.0; //Ширина правого скролла
    private static int startRow, currentRow; //Переменные для выделения строк

    /**
     * Метод заменяет метод getColumns(), т.к последний не выдает подстолбцов.
     * Здесь и столбцы execution и все остальные
     * @param tv - TableView
     * @param <T> - тип данных в таблице
     * @return ArrayList<TableColumn<T, ?>>
     */
    static public <T> ArrayList<TableColumn<T, ?>> getAllColumns(TableView<T> tv){
        ArrayList<TableColumn<T, ?>> allColumns = new ArrayList<>();
        for(TableColumn<T, ?> tc : tv.getColumns()){
            if(!tc.getColumns().isEmpty()){ //Для столбцов с подстолбцами
                for(Object exTc : tc.getColumns()){ //вычисляем ширину подстолбцов
                    if(exTc instanceof TableColumn)
                        allColumns.add((TableColumn<T, ?>) exTc);
                }
            } else
                allColumns.add(tc);
        }
        return allColumns;
    }


    /**
     * Функция вычисляет ширину таблицы по сумме всех ее столбцов и ширины правого скролла
     * @param allColumns - список всех значимых столбцов таблицы
     * @param <T> - тип данных таблицы
     * @return double
     */
    static public <T>double countPrefTableWidth(ArrayList<TableColumn<T, ?>> allColumns){
        double sum = 0.0;
        for(int i = 0; i < allColumns.size(); i++) {
            if (allColumns.get(i).isVisible()) {
                sum += allColumns.get(i).widthProperty().get();
            }
        }
        return sum + scrollWidth;
    }

    /**
     * Метод добавляет служебный столбец в начало таблицы
     */
    public static <T>void createZeroColumn(TableView<T> tv) {
        //Столбец для выделения строк
        TableColumn<T, String> zeroCol = new TableColumn<>("");
        zeroCol.setCellValueFactory(param-> new ReadOnlyStringWrapper(""));
        zeroCol.setResizable(false);
        zeroCol.setPrefWidth(30);
        zeroCol.setCellFactory(param -> {
            return new TableCell<T, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    setStyle("-fx-background-color: white");
                    addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, event -> {
                        setCursor(Cursors.cursorBlackArrow);
                    });

                    setEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                        startRow = getIndex();
                        setStyle("");
                        tv.getSelectionModel().select(getIndex());
                        event.consume();
                    });

                    setOnDragDetected(event -> {
                        startFullDrag();
                        startRow = getIndex();
                        tv.getSelectionModel().select(getIndex());

                    });

                    setOnMouseDragEntered(event -> {
                        tv.getSelectionModel().clearSelection();
                        currentRow = getIndex();

                        int a = Math.min(startRow, currentRow);
                        int b = Math.max(startRow, currentRow);

                        for (int r = a; r <= b; r++) {
                            tv.getSelectionModel().select(r);
                        }
                    });
                }
            };
        });

        zeroCol.setEditable(false);
        zeroCol.setId("0");

        tv.getColumns().add(0, zeroCol);
    }

    /**
     * Метод присваивает id всем значимым столбцам таблицы
     * @param tv TableView
     * @param <T> тип данных в таблице
     */
    public static <T>void setIdToColumns(TableView<T> tv){
        int id = 0;
        for(TableColumn<T, ?> tc : getAllColumns(tv)){
            tc.setId(String.valueOf(id++));
        }
    }

    /**
     * Метод находит в таблице столбец по его id
     */
    public static <T>TableColumn<T, ?> findTableColumnById(String id, TableView<T> tv){
        TableColumn<T, ?> tableColumn = new TableColumn<>();
        for(TableColumn<T, ?> tc : getAllColumns(tv)){
            if(tc.getId().equals(id))
                tableColumn = tc;
        }
        return tableColumn;
    }

}
