package ru.wert.tubus.chogori.connectionProperties;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.wert.tubus.client.retrofit.AppProperties;
import ru.wert.tubus.client.retrofit.RetrofitClient;
import ru.wert.tubus.chogori.images.AppImages;
import ru.wert.tubus.winform.modal.ModalWindow;
import ru.wert.tubus.winform.window_decoration.WindowDecoration;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class NoConnection extends ModalWindow{

    private TextField tfAddressIP;
    private TextField tfPort;
    private Stage stage;

    public NoConnection(Stage stage) {
        this.stage = stage;
    }

    public boolean create(){
        AtomicBoolean answer = new AtomicBoolean(false);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/connectionProperties/noConnection.fxml"));
            Parent parent = loader.load();
            stage.setScene(new Scene(parent));
            stage.setResizable(false);

            tfAddressIP = ((TextField) parent.lookup("#tfAddressIP"));
            tfAddressIP.setText(AppProperties.getInstance().getIpAddress());
            Platform.runLater(()->tfAddressIP.requestFocus());

            tfPort = ((TextField) parent.lookup("#tfPort"));
            tfPort.setText(AppProperties.getInstance().getPort());

            Button btnCancel = ((Button) parent.lookup("#btnCancel"));
            btnCancel.setOnAction((e)->{
                answer.set(false);
                ((Node)e.getSource()).getScene().getWindow().hide();
            });

            Button btnOK = ((Button) parent.lookup("#btnOK"));
            btnOK.setOnAction((e)->{
                AppProperties.getInstance().setIpAddress(tfAddressIP.getText().trim());

                AppProperties.getInstance().setPort(tfPort.getText().trim());
                answer.set(true);
                RetrofitClient.getInstance();
                RetrofitClient.restartWithNewUrl();
                ((Node)e.getSource()).getScene().getWindow().hide();
            });

            stage.getIcons().add(AppImages.LOGO_ICON);
            stage.setTitle("Нет соединения");

            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/pik-dark.css").toString());;
            new WindowDecoration("Внимание!", parent, false, null, true );
                        
        } catch (IOException e) {
            e.printStackTrace();
        }

        return answer.get();
    }
}
