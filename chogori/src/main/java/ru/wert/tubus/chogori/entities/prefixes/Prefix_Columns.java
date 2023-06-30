package ru.wert.tubus.chogori.entities.prefixes;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.wert.tubus.client.entity.models.Prefix;

public class Prefix_Columns {

    /**
     * ID
     */
    public static TableColumn<Prefix, String> createTcId(){
        TableColumn<Prefix, String> tcId = new TableColumn<>("ID");
        tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tcId.setStyle("-fx-alignment: CENTER;");
        return tcId;
    };

    /**
     * ПРЕФИКС
     */
    public static TableColumn<Prefix, String> createTcName(){
        TableColumn<Prefix, String> tcName = new TableColumn<>("Наименование");
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcName.setPrefWidth(200);
        tcName.setMaxWidth(200);
        tcName.setMinWidth(200);

        return tcName;
    };

    /**
     * ПРИМЕЧАНИЕ
     */
    public static TableColumn<Prefix, String> createTcNote(){
        TableColumn<Prefix, String> tcNote = new TableColumn<>("Примечание");
        tcNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        return tcNote;
    };

}
