package ru.wert.datapik.chogori.common.tableView;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import org.icepdf.ri.common.views.Controller;

import java.util.List;

import static javafx.scene.control.TableView.UNCONSTRAINED_RESIZE_POLICY;

public class TableViewWithResizableColumns {

    private TableView table;
    private List<TableColumn> columns;
    private StackPane container;

    public TableViewWithResizableColumns(TableView table, StackPane container) {
        this.table = table;
        this.container = container;
        this.columns = table.getColumns();

        table.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);
        table.setPrefWidth(countPreferredTableWidth());


    }


    private double countPreferredTableWidth(){
        double tableWidth = 0.0;

        for(TableColumn col : columns){
            if(col.isVisible())
                tableWidth += col.getPrefWidth();
        }

        return tableWidth;

    }
}
