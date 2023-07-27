package ru.wert.tubus.chogori.logging;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import ru.wert.tubus.client.entity.models.AppLog;
import ru.wert.tubus.chogori.images.BtnImages;

import static ru.wert.tubus.winform.statics.WinformStatic.parseLDTtoNormalDate;

public class AppLog_Columns {

    /**
     * ID
     */
    public static TableColumn<AppLog, String> createTcId(){
        TableColumn<AppLog, String> tc = new TableColumn<>("ID");
        tc.setCellValueFactory(new PropertyValueFactory<>("id"));
        tc.setStyle("-fx-alignment: CENTER;");
        tc.setSortable(false);
        return tc;
    };

    /**
     * DATE
     */
    public static TableColumn<AppLog, String> createTcDate(){
        TableColumn<AppLog, String> tc = new TableColumn<>("Дата");
        tc.setCellValueFactory(cd->{
            AppLog log = cd.getValue();
            String str = parseLDTtoNormalDate(log.getTime());
            return new ReadOnlyStringWrapper(str);
        });
        tc.setStyle("-fx-alignment: CENTER;");
        tc.setMinWidth(130);
        tc.setMaxWidth(130);
        tc.setSortable(false);
        return tc;
    };

    /**
     * ПОЛЬЗОВАТЕЛЬ
     */
    public static TableColumn<AppLog, String> createTcUser(){
        TableColumn<AppLog, String> tc = new TableColumn<>("Пользователь");
        tc.setCellValueFactory(cd->{
            String str = cd.getValue().getUser().getName();
            return new ReadOnlyStringWrapper(str);
        });
        tc.setStyle("-fx-alignment: CENTER-LEFT;");
        tc.setMinWidth(100);
        tc.setPrefWidth(200);
        tc.setMaxWidth(300);
        tc.setSortable(false);
        return tc;
    };

    /**
     * ТЕКСТ
     */
    public static TableColumn<AppLog, String> createTcText(){
        TableColumn<AppLog, String> tc = new TableColumn<>("Текст записи");
        tc.setCellValueFactory(new PropertyValueFactory<>("text"));
        tc.setStyle("-fx-alignment: CENTER-LEFT;");
        tc.setPrefWidth(400);
        tc.setMaxWidth(5000);
        tc.setSortable(false);
        return tc;
    };

    /**
     * ПРИЛОЖЕНИЕ
     */
    public static TableColumn<AppLog, Label> createTcApplication(){
        TableColumn<AppLog, Label> tc = new TableColumn<>("App");
        tc.setCellValueFactory(cd->{
            int app = cd.getValue().getApplication();
            Label label = new Label();
            switch (app) {
                case 0:
                    label.setGraphic(new ImageView(BtnImages.BTN_DESKTOP_IMG));
                    label.setTooltip(new Tooltip("Desktop"));
                    break;
                case 1:
                    label.setGraphic(new ImageView(BtnImages.BTN_ANDROID_IMG));
                    label.setTooltip(new Tooltip("Android"));
                    break;
                case 2:
                    label.setGraphic(new ImageView(BtnImages.BTN_NORMIC_IMG));
                    label.setTooltip(new Tooltip("Normic"));
                    break;
            }
            return new ReadOnlyObjectWrapper(label);
        });
        tc.setStyle("-fx-alignment: CENTER;");
        tc.setMinWidth(32);
        tc.setMaxWidth(32);
        tc.setSortable(false);
        return tc;
    };

    /**
     * ВЕРСИЯ
     */
    public static TableColumn<AppLog, String> createTcVersion(){
        TableColumn<AppLog, String> tc = new TableColumn<>("Версия");
        tc.setCellValueFactory(new PropertyValueFactory<>("version"));
        tc.setStyle("-fx-alignment: CENTER-LEFT;");
        tc.setMinWidth(100);
        tc.setMaxWidth(100);
        tc.setSortable(false);
        return tc;
    };

}
