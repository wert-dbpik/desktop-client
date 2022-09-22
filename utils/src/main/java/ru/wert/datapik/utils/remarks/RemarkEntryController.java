package ru.wert.datapik.utils.remarks;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.commons.io.FileUtils;
import ru.wert.datapik.client.entity.api_interfaces.FilesApiInterface;
import ru.wert.datapik.client.entity.models.Pic;
import ru.wert.datapik.client.entity.models.Remark;
import ru.wert.datapik.client.retrofit.RetrofitClient;
import ru.wert.datapik.utils.common.components.TextAreaBlobbing;
import ru.wert.datapik.utils.services.ChogoriServices;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.utils.tempfile.TempDir;
import ru.wert.datapik.winform.statics.WinformStatic;

import java.awt.*;
import java.io.File;
import java.util.Comparator;
import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.*;
import static ru.wert.datapik.utils.statics.AppStatic.BASE_URL;
import static ru.wert.datapik.winform.statics.WinformStatic.WF_MAIN_STAGE;
import static ru.wert.datapik.winform.statics.WinformStatic.WF_TEMPDIR;

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
                    "remark"); //префикс

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
