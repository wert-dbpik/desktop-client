package ru.wert.datapik.chogori.entities.densities;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.wert.datapik.client.entity.models.Density;

public class Density_Columns {

    /**
     * ID
     */
    public static TableColumn<Density, String> createTcId(){
        TableColumn<Density, String> tc = new TableColumn<>("ID");
        tc.setCellValueFactory(new PropertyValueFactory<>("id"));
        tc.setStyle("-fx-alignment: CENTER;");

        return tc;
    };

    /**
     * МАТЕРИАЛ
     */
    public static TableColumn<Density, String> createTcName(){
        TableColumn<Density, String> tc = new TableColumn<>("Материал");
        tc.setCellValueFactory(new PropertyValueFactory<>("name"));
        tc.setPrefWidth(200);
        tc.setMaxWidth(200);
        tc.setMinWidth(200);

        return tc;
    };

    /**
     * ПЛОТНОСТЬ
     */
    public static TableColumn<Density, String> createTcAmount(){
        TableColumn<Density, String> tc = new TableColumn<>("Плотность");
        tc.setCellValueFactory(new PropertyValueFactory<>("amount"));
        tc.setPrefWidth(200);
        tc.setMaxWidth(200);
        tc.setMinWidth(200);
        tc.setStyle("-fx-alignment: CENTER;");
        tc.setSortable(false);

        return tc;
    };

    /**
     * ПРИМЕЧАНИЕ
     */
    public static TableColumn<Density, String> createTcNote(){
        TableColumn<Density, String> tc = new TableColumn<>("Примечание");
        tc.setCellValueFactory(new PropertyValueFactory<>("note"));
        tc.setSortable(false);

        return tc;
    };

}
