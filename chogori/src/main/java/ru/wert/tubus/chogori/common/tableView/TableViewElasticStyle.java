package ru.wert.tubus.chogori.common.tableView;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.chogori.excel.table.TableMaster;

public class TableViewElasticStyle<P extends Item> {

    ItemTableView<P> tableView;
    HBox hbox;
    double scrollW = 20; //ширина бокового скролла

    public TableViewElasticStyle(ItemTableView<P> tableView, HBox hbox) {
        this.tableView = tableView;
        this.hbox = hbox;

    }

    public void mount() {

        hbox.setAlignment(Pos.TOP_CENTER);
        createWidthPropertyListener();
        double width = 0.0;
        for (TableColumn<P, ?> tc : TableMaster.getAllColumns(tableView)) {
            width += tc.getWidth();
            addStretchListenerToColumn(tc);
            tc.setResizable(true);
        }


        //Устанавливаем первоначальную ширину таблицы и контейнера HBox
        double tableW = TableMaster.countPrefTableWidth(TableMaster.getAllColumns(tableView));
        tableView.setPrefWidth(tableW);
        tableView.resize(tableW, tableView.getHeight());
        hbox.setPrefWidth(tableW);

    }

    /**
     * Метод изменяет ширину контейнеа при изменении ширины столбца
     */
    private void addStretchListenerToColumn(TableColumn<P, ?> col) {
        col.widthProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("-----------------------START-------------------------");
            double deltaW = newValue.doubleValue() - oldValue.doubleValue();
            System.out.println("deltaW = " + deltaW);
            double tableW = tableView.getWidth();
            System.out.println("tableW = " + tableW);
            if (tableW > 0 && deltaW > 0) {
                if (tableW + deltaW + scrollW <= hbox.getWidth()) {
                    tableView.setPrefWidth(tableView.getWidth() + deltaW);
                    tableView.resize(tableView.getWidth() + deltaW, tableView.getHeight());
                } else {
                    tableView.setPrefWidth(hbox.getWidth() - scrollW);
                    tableView.resize(hbox.getWidth() - scrollW, tableView.getHeight());
                }
            }

            if (tableW > 0 && deltaW < 0) {
                if (TableMaster.countPrefTableWidth(TableMaster.getAllColumns(tableView)) + scrollW < hbox.getWidth()) {
                    tableView.setPrefWidth(tableView.getWidth() + deltaW);
                    tableView.resize(tableView.getWidth() + deltaW, tableView.getHeight());
                } else {

                }
            }

            System.out.println("tableW later = " + tableW);
        });
    }

    /**
     * Метод создает слушатель, который меняет ширину контейнера при изменении ширины таблицы внутри него
     */
    private void createWidthPropertyListener() {
        //При изменении размеров контейнера меняется ширина таблицы, без пустой колонки справа
        hbox.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() > tableView.getWidth()) {
                tableView.setPrefWidth(TableMaster.countPrefTableWidth(TableMaster.getAllColumns(tableView)));
                tableView.resize(TableMaster.countPrefTableWidth(TableMaster.getAllColumns(tableView)), tableView.getHeight());
            }
        });
    }

    public void changeTableWidth() {
        Platform.runLater(() -> {
            double newWidth = TableMaster.countPrefTableWidth(TableMaster.getAllColumns(tableView));
            tableView.resize(newWidth, tableView.getHeight());
            tableView.setPrefWidth(newWidth);
        });
    }

}
