package ru.wert.datapik.utils.remarks;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import ru.wert.datapik.client.entity.models.Pic;
import ru.wert.datapik.client.entity.models.Remark;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.utils.images.ImageUtil;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.winform.enums.EOperation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.utils.images.BtnImages.BTN_ADD_PHOTO_IMG;
import static ru.wert.datapik.utils.services.ChogoriServices.*;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER;
import static ru.wert.datapik.winform.statics.WinformStatic.WF_TEMPDIR;
import static ru.wert.datapik.winform.statics.WinformStatic.closeWindow;

public class Remark_ACCController extends FormView_ACCController<Remark> {

    @FXML
    private VBox vbMain;

    @FXML
    private HBox waitingBlind;

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
    private Button btnAddPhoto;

    @FXML
    private Button btnOk;

    @FXML
    private Button btnCancel;


    private List<FileImage> picturesInRemark;
    private List<Pic> initialPics; //Картинки, бывшие изначально
    private List<File> allPicFiles;

    @FXML
    void cancel(ActionEvent event) {
        closeWindow(event);
    }

    @FXML
    void ok(ActionEvent event) {
        if(operation.equals(EOperation.ADD)) {
            Remark newRemark = new Remark();

        }
    }

    private List<Pic> addNewRemark(){
        List<Pic> newPics = new ArrayList<>();
        for(File f : allPicFiles){
            Pic pic = createPicFromFileAndSaveItToDB(f);



        }

    }

    private Pic createPicFromFileAndSaveItToDB(File file){
        Image picture = picturesInRemark.stream().filter(i->i.file.equals(file)).findFirst().get().image;
        Pic newPic = new Pic();
        newPic.setUser(CH_CURRENT_USER);
        newPic.setTime(AppStatic.getCurrentTime());
        newPic.setWidth((int) picture.getWidth());
        newPic.setHeight((int) picture.getHeight());
        newPic.setExtension(FilenameUtils.getExtension(file.toString()));
        Pic savedPic = CH_PICS.save(newPic);
        //Загружаем изображение в БД
        File compressedFile = ImageUtil.compressImageToFile(file, null);
        String fileName = String.valueOf(savedPic.getId()) + "." + FilenameUtils.getExtension(compressedFile.toString());
        try {
            CH_FILES.upload(fileName, "pics", compressedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void initialize(){
        vbMain.setVisible(false);
        btnAddPhoto.setText("");
        btnAddPhoto.setGraphic(new ImageView(BTN_ADD_PHOTO_IMG));
    }


    @FXML
    void addPhoto(ActionEvent event) {
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg");
        List<File> chosenFiles = AppStatic.chooseManyFile(event, new File("C:\\"), filter);
        if(chosenFiles == null || chosenFiles.isEmpty()) return;
        for(File f : chosenFiles){
            allPicFiles.add(f);
            addNewImageToTheForm(f);
        }
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

        allPicFiles = new ArrayList<>();

        if(operation.equals(EOperation.CHANGE))
            Platform.runLater(()-> {
                fillFieldsOnTheForm(formView.getAllSelectedItems().get(0));
                waitingBlind.setVisible(false);
                vbMain.setVisible(true);
            });

        else {
            Platform.runLater(()-> {
                showEmptyForm();
                waitingBlind.setVisible(false);
                vbMain.setVisible(true);
            });
        }


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
        imageView.setFitWidth(600 * weight);

        imageView.fitWidthProperty().bind(taRemarksText.widthProperty().multiply(weight));

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

        imageView.setOnContextMenuRequested(e ->{
            Menu contextMenu = new Menu();
            MenuItem deletePhoto = new MenuItem("Удалить фото");
            deletePhoto.setOnAction(ev -> {
                allPicFiles.remove(file);
                vbRemarksEntryContainer.getChildren().clear();
                for(File f : allPicFiles){
                    addNewImageToTheForm(f);
                }
            });

            contextMenu.getItems().add(deletePhoto);

        });

        vbRemarksEntryContainer.getChildren().add(imageView);
    }

//    private void updateListOfPhotos(){
//        vbRemarksEntryContainer.getChildren().clear();
//        for(File f : allPics){
//            addNewImageToTheForm(f);
//        }
//    }

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
        initialPics = CH_REMARKS.getPics(oldItem);
        if(initialPics == null || initialPics.isEmpty()) return;
        for(Pic p : initialPics) {
            String tempFileName = "remark" + "-" + p.getId() + "." + p.getExtension();
            boolean res = CH_FILES.download("pics", //Постоянная папка в каталоге для чертежей
                    String.valueOf(p.getId()), //название скачиваемого файла
                    "." + p.getExtension(), //расширение скачиваемого файла
                    WF_TEMPDIR.toString(),//временная папка, куда необходимо скачать фай
                    "remark"); //префикс

            File file = new File(WF_TEMPDIR.toString() + "\\" + tempFileName);
            //Добавляем файл в общий список
            allPicFiles.add(file);

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

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private class FileImage{
        File file;
        Image image;
    }
}
