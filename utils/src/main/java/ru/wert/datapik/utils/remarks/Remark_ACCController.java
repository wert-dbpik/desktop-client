package ru.wert.datapik.utils.remarks;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import ru.wert.datapik.client.entity.models.Pic;
import ru.wert.datapik.client.entity.models.Remark;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.winform.enums.EOperation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.*;
import static ru.wert.datapik.winform.statics.WinformStatic.WF_MAIN_STAGE;
import static ru.wert.datapik.winform.statics.WinformStatic.WF_TEMPDIR;

public class Remark_ACCController extends FormView_ACCController<Remark> {

    @FXML
    private TextArea taRemarksText;
    //Переменные для taRemarksText
    private Text textHolder = new Text();
    private double oldHeight = 0;

    @FXML
    private VBox vbRemarksEntryContainer;

    @FXML
    private StackPane spIndicator;

    @FXML
    private Button btnOk;

    @FXML
    private Button btnCancel;


    private List<Pic> initialPics; //Картинки, бывшие изначально

    @FXML
    void cancel(ActionEvent event) {

    }

    @FXML
    void ok(ActionEvent event) {

    }

    @FXML
    void initialize(){
//        stage = vbRemarksEntryContainer.getScene().getWindow();
    }


    @Override
    public void init(EOperation operation, IFormView<Remark> formView, ItemCommands<Remark> commands) {
        taRemarksText.setId("blobTextArea");

        textHolder.textProperty().bind(taRemarksText.textProperty());
        textHolder.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            if (oldHeight != newValue.getHeight()) {
                oldHeight = newValue.getHeight();
                double newHeight = textHolder.getLayoutBounds().getHeight() + 30;
                taRemarksText.setPrefHeight(newHeight);
                taRemarksText.setMinHeight(newHeight);
            }
        });

        if(operation.equals(EOperation.CHANGE))
            fillFieldsOnTheForm(formView.getAllSelectedItems().get(0));


    }

    private void addNewImageToTheForm(File file){

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

        Platform.runLater(()->{
            vbRemarksEntryContainer.setPrefWidth(600);
            imageView.fitWidthProperty().bind(vbRemarksEntryContainer.widthProperty().multiply(weight));
        });


        imageView.setOnMouseEntered(e -> {
            ((Node) e.getSource()).getScene().setCursor(javafx.scene.Cursor.HAND);
        });
        imageView.setOnMouseExited(e -> {
            ((Node) e.getSource()).getScene().setCursor(javafx.scene.Cursor.DEFAULT);
        });

        imageView.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
                new Pic_OutstandingPreviewer().create(null, file);
            }
        });

        vbRemarksEntryContainer.getChildren().add(imageView);
    }

    @Override
    public ArrayList<String> getNotNullFields() {
        return null;
    }

    @Override
    public Remark getNewItem() {
        return null;
    }

    @Override
    public Remark getOldItem() {
        return null;
    }

    @Override
    public void fillFieldsOnTheForm(Remark oldItem) {
        taRemarksText.setText(oldItem.getText());
        List<Pic> pics = CH_REMARKS.getPics(oldItem);
        if(pics == null || pics.isEmpty()) return;
        for(Pic p : pics){
            String tempFileName = "remark" + "-" + p.getId() + "." + p.getExtension();
            boolean res = CH_FILES.download("pics", //Постоянная папка в каталоге для чертежей
                    String.valueOf(p.getId()), //название скачиваемого файла
                    "." + p.getExtension(), //расширение скачиваемого файла
                    WF_TEMPDIR.toString(),//временная папка, куда необходимо скачать фай
                    "remark"); //префикс

            File file = new File( WF_TEMPDIR.toString() + "\\" + tempFileName);

            addNewImageToTheForm(file);
        }
    }

    @Override
    public void changeOldItemFields(Remark oldItem) {

    }

    @Override
    public void showEmptyForm() {

    }

    @Override
    public boolean enteredDataCorrect() {
        return false;
    }
}
