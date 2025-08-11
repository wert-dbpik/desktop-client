package ru.wert.tubus.chogori.application.numJournal;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.wert.tubus.client.entity.models.AppLog;
import ru.wert.tubus.client.entity.models.Passport;

import static ru.wert.tubus.chogori.statics.Comparators.createLabelComparator;

public class NumJournal_Columns {

    /**
     * ID
     */
    public static TableColumn<Passport, String> createTcId() {
        TableColumn<Passport, String> tc = new TableColumn<>("ID");
        tc.setCellValueFactory(new PropertyValueFactory<>("id"));
        tc.setStyle("-fx-alignment: CENTER;");
        tc.setSortable(false);
        return tc;
    }

    /**
     * ДЕЦИМАЛЬНЫЙ НОМЕР
     */
    public static TableColumn<Passport, Label> createTcPassportNumber() {
        TableColumn<Passport, Label> tcPassportNumber = new TableColumn<>("Дец.номер");
        tcPassportNumber.setCellValueFactory(new PropertyValueFactory<>("id"));

        tcPassportNumber.setMinWidth(50);
        tcPassportNumber.setPrefWidth(80);
        tcPassportNumber.setMaxWidth(5000);
        tcPassportNumber.setResizable(true);
        return tcPassportNumber;
    }

    /**
     * НАИМЕНОВАНИЕ
     */
    public static TableColumn<Passport, Label> createTcPassportName() {
        TableColumn<Passport, Label> tcPassportName = new TableColumn<>("Наименование");

        tcPassportName.setCellValueFactory(cd -> {
            Passport passport = cd.getValue();
            String name = passport.getName();

            Label lblName = new Label(name);

            return new ReadOnlyObjectWrapper<>(lblName);

        });
        tcPassportName.setComparator(createLabelComparator(tcPassportName));
        tcPassportName.setMinWidth(150);
        return tcPassportName;
    }


}


