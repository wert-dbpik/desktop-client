package ru.wert.tubus.chogori.crashReport;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import ru.wert.tubus.chogori.images.BtnImages;
import ru.wert.tubus.client.entity.models.CrashReport;

import static ru.wert.tubus.winform.statics.WinformStatic.parseLDTtoNormalDate;

public class CrashReport_Columns {

    /**
     * ID
     */
    public static TableColumn<CrashReport, String> createTcId(){
        TableColumn<CrashReport, String> tc = new TableColumn<>("ID");
        tc.setCellValueFactory(new PropertyValueFactory<>("id"));
        tc.setStyle("-fx-alignment: CENTER;");

        tc.setMinWidth(50);
        tc.setPrefWidth(80);
        tc.setMaxWidth(80);
        tc.setResizable(false);
        tc.setSortable(false);
        return tc;
    };

    /**
     * DATE
     */
    public static TableColumn<CrashReport, String> createTcDate(){
        TableColumn<CrashReport, String> tc = new TableColumn<>("Дата");
        tc.setCellValueFactory(cd->{
            CrashReport report = cd.getValue();
            String str = parseLDTtoNormalDate(report.getDate().toString());
            return new ReadOnlyStringWrapper(str);
        });
        tc.setStyle("-fx-alignment: top-left;");
        tc.setMinWidth(130);
        tc.setMaxWidth(130);
        tc.setSortable(false);
        return tc;
    };

    /**
     * USER
     */
    public static TableColumn<CrashReport, String> createTcUser(){
        TableColumn<CrashReport, String> tc = new TableColumn<>("Пользователь");
        tc.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getUser().getName()));
        tc.setStyle("-fx-alignment: top-left; -fx-wrap-text: true;");
        tc.setPrefWidth(120);
        tc.setMaxWidth(120);
        tc.setMinWidth(120);
        tc.setSortable(false);

        return tc;
    };

    /**
     * ПРИЛОЖЕНИЕ
     */
    public static TableColumn<CrashReport, Label> createTcGadget(){
        TableColumn<CrashReport, Label> tc = new TableColumn<>("Гаджет");
        tc.setCellValueFactory(cd->{
            String device = cd.getValue().getDevice();
            Label label = new Label();
            switch (device) {
                case "DESKTOP":
                    label.setGraphic(new ImageView(BtnImages.BTN_DESKTOP_IMG));
                    label.setTooltip(new Tooltip("Desktop"));
                    break;
                case "ANDROID":
                    label.setGraphic(new ImageView(BtnImages.BTN_ANDROID_IMG));
                    label.setTooltip(new Tooltip("Android"));
                    break;
            }
            return new ReadOnlyObjectWrapper(label);
        });
        tc.setStyle("-fx-alignment: TOP-CENTER;");
        tc.setMinWidth(60);
        tc.setMaxWidth(60);
        tc.setResizable(false);
        tc.setSortable(false);
        return tc;
    };

    /**
     * ВЕРСИЯ ПРИЛОЖЕНИЯ
     */
    public static TableColumn<CrashReport, String> createTcVersion(){
        TableColumn<CrashReport, String> tc = new TableColumn<>("Версия");
        tc.setCellValueFactory(new PropertyValueFactory<>("version"));
        tc.setStyle("-fx-alignment: TOP-CENTER; ");
        tc.setMinWidth(60);
        tc.setPrefWidth(60);
        tc.setMaxWidth(60);
        tc.setResizable(false);
        tc.setSortable(false);
        return tc;
    };

    /**
     * СТЭКТРЕЙС
     */
    public static TableColumn<CrashReport, TextArea> createTcStackTrace(){
        TableColumn<CrashReport, TextArea> tc = new TableColumn<>("Stack-trace ошибки");
        tc.setCellValueFactory(cd -> {
            String text = cd.getValue().getStackTrace();
            TextArea ta = new TextArea(text);
            ta.setEditable(false);
            ta.setPrefRowCount(7);
            ta.setStyle("-fx-background-color: transparent;-fx-border-color: transparent;");
            return new ReadOnlyObjectWrapper<>(ta);

        });
        tc.setSortable(false);

        return tc;
    }

}
