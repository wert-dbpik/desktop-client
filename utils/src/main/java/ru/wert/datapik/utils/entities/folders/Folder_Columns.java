package ru.wert.datapik.utils.entities.folders;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.jetbrains.annotations.NotNull;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.Prefix;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_PREFIXES;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_DEFAULT_PREFIX;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_SHOW_PREFIX;

public class Folder_Columns {

    /**
     * ID
     */
    public static TableColumn<Folder, String> createTcId(){
        TableColumn<Folder, String> tcId = new TableColumn<>("ID");
        tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tcId.setStyle("-fx-alignment: CENTER;");
        return tcId;
    };


    /**
     * ПОЛНОЕ НАИМЕНОВАНИЕ НОМЕР
     */
    public static TableColumn<Folder, String> createTcFullName(){
        TableColumn<Folder, String> tcFullName = new TableColumn<>("Полное\nНаименование");
        tcFullName.setCellValueFactory(cd->{
            Folder folder = cd.getValue();
            String fullName = folder.getName();
            return new ReadOnlyStringWrapper(fullName);
        });
        tcFullName.setMinWidth(120);
        return tcFullName;
    };



    /**
     * НАИМЕНОВАНИЕ
     */
    public static TableColumn<Folder, String> createTcName(){
        TableColumn<Folder, String> tcName = new TableColumn<>("Наименование");
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
        return tcName;
    };


    /**
     * ПРИМЕЧАНИЕ
     */
    public static TableColumn<Folder, String> createTcNote(){
        TableColumn<Folder, String> tcNote = new TableColumn<>("Примечание");
        tcNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        tcNote.setMinWidth(120);
        return tcNote;
    };

}
