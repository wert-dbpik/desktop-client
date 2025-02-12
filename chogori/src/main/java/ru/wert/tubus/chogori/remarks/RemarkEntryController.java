package ru.wert.tubus.chogori.remarks;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import ru.wert.tubus.client.entity.models.Pic;
import ru.wert.tubus.client.entity.models.Remark;
import ru.wert.tubus.chogori.statics.AppStatic;

import java.io.File;
import java.util.Comparator;
import java.util.List;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.*;
import static ru.wert.tubus.winform.statics.WinformStatic.WF_MAIN_STAGE;
import static ru.wert.tubus.winform.statics.WinformStatic.WF_TEMPDIR;

public class RemarkEntryController {


    @FXML
    private Label lblRemarksText;
//    private TextAreaBlobbing taBlobRemarksText;

    @FXML
    private Label lblRemarksDate;

    @FXML
    private Label lblRemarksAuthor;

    @FXML
    private VBox vbRemarksEntryContainer;

    void init(Remark remark){
        //Автор
        lblRemarksAuthor.setText(remark.getUser().getName());
        lblRemarksAuthor.setStyle("-fx-text-fill: saddlebrown");
        //Дата
        String date = AppStatic.parseStringToDate(remark.getCreationTime());
        lblRemarksDate.setText(date);
        lblRemarksDate.setStyle("-fx-text-fill: saddlebrown; -fx-font-style: italic");
        //Текст
        lblRemarksText.setText(remark.getText());
        //Изображения
        List<Pic> pics = CH_REMARKS.getPics(remark);
        //Сортируем список, чтобы последние добавленные(новые) оказались внизу
        pics.sort(Comparator.comparing(Pic::getTime));
        for(Pic p : pics){
            String tempFileName = "remark" + "-" + p.getId() + "." + p.getExtension();
            boolean res = CH_FILES.download("pics", //Постоянная папка в каталоге для чертежей
                    String.valueOf(p.getId()), //название скачиваемого файла
                    "." + p.getExtension(), //расширение скачиваемого файла
                    WF_TEMPDIR.toString(),//временная папка, куда необходимо скачать фай
                    "remark",
                    null); //префикс

            File file = new File( WF_TEMPDIR.toString() + "\\" + tempFileName);

            Image image = new Image(file.toURI().toString());

            double w = image.getWidth();
            double h = image.getHeight();

            float weight;
            if (w - h < w * 0.1f)
                weight = 0.3f;
            else if (w - h > w * 0.1f)
                weight = 0.6f;
            else
                weight = 0.4f;

            ImageView imageView = new ImageView(image);
            imageView.setStyle("-fx-start-margin: 5");
            imageView.setStyle("-fx-end-margin: 5");
            imageView.setPreserveRatio(true);
            imageView.fitWidthProperty().bind(WF_MAIN_STAGE.widthProperty().multiply(weight));

            imageView.setOnMouseEntered(e->{
                ((Node)e.getSource()).getScene().setCursor(javafx.scene.Cursor.HAND);
            });
            imageView.setOnMouseExited(e->{
                ((Node)e.getSource()).getScene().setCursor(javafx.scene.Cursor.DEFAULT);
            });

            imageView.setOnMouseClicked(e->{
                if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2){
                    new Pic_OutstandingPreviewer().create(null, file);
                }
            });

            vbRemarksEntryContainer.getChildren().add(imageView);
        }


    }
}
