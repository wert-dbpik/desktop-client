package ru.wert.datapik.utils.remarks;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

import java.io.File;
import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.*;
import static ru.wert.datapik.utils.statics.AppStatic.BASE_URL;
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
        lblRemarksAuthor.setText(remark.getUser().getName());
        lblRemarksDate.setText(remark.getCreationTime());
        lblRemarksText.setText(remark.getText());

        List<Pic> pics = CH_REMARKS.getPics(remark);
        for(Pic p : pics){
            String tempFileName = p.getId() + "." + p.getExtension();
            boolean res = CH_FILES.download("pics", //Постоянная папка в каталоге для чертежей
                    String.valueOf(p.getId()), //название скачиваемого файла
                    ".jpg", //расширение скачиваемого файла
                    WF_TEMPDIR.toString(),//временная папка, куда необходимо скачать фай
                    "remark"); //префикс

            String path = WF_TEMPDIR.toString() + "\\" + tempFileName;

            Image image = new Image(path);
            ImageView imageView = new ImageView(image);

            vbRemarksEntryContainer.getChildren().add(imageView);
        }


    }
}
