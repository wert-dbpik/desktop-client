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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import ru.wert.datapik.client.entity.models.Passport;
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
import java.util.Comparator;
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

    //Переменные для taRemarksText
    private Text textHolder = new Text();
    private double oldHeight = 0;

    private EOperation operation;
    private Remark changingRemark;
    private ItemCommands<Remark> commands;
    private Passport passport;
    private List<FileImage> picturesInRemark;
    private List<Pic> initialPics; //Картинки, бывшие изначально

    @FXML
    void initialize(){
        vbMain.setVisible(false);
        btnAddPhoto.setText("");
        btnAddPhoto.setGraphic(new ImageView(BTN_ADD_PHOTO_IMG));
        btnAddPhoto.setTooltip(new Tooltip("Добавить изображение"));
        //Применим CSS стили к TextArea
        taRemarksText.setId("blobTextArea");
        //Сделаем из стандартного TextArea раздуваемый
        textHolder.textProperty().bind(taRemarksText.textProperty());
        textHolder.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            if (oldHeight != newValue.getHeight()) {
                oldHeight = newValue.getHeight();
                double newHeight = textHolder.getLayoutBounds().getHeight() + 30;
                taRemarksText.setPrefHeight(newHeight);
                taRemarksText.setMinHeight(newHeight);
            }
        });
    }


    @Override
    public void init(EOperation operation, IFormView<Remark> formView, ItemCommands<Remark> commands) {
        this.commands = commands;
        this.operation = operation;

        passport = ((Remark_TableView)formView).getPassport();

        //Изменим текст на кнопке ОК согласно типу выполняемой операции
        if(operation.equals(EOperation.ADD)) btnOk.setText("ДОБАВИТЬ");
        else btnOk.setText("ИЗМЕНИТЬ");

        //Инициализируем коллекцию с изображениями
        picturesInRemark = new ArrayList<>();

        //Заполним поля формы данными
        if(operation.equals(EOperation.CHANGE)) {
            changingRemark = formView.getAllSelectedItems().get(0);
            Platform.runLater(() -> {
                fillFieldsOnTheForm(changingRemark);
                //Снимаем шторку, когда поля формы заполнены
                waitingBlind.setVisible(false);
                vbMain.setVisible(true);
            });

        }else { //Если создаем новый комментарий
            Platform.runLater(()-> {
                showEmptyForm();
                //Снимаем шторку, когда поля формы заполнены
                waitingBlind.setVisible(false);
                vbMain.setVisible(true);
            });
        }
    }


    @FXML
    void cancel(ActionEvent event) {
        closeWindow(event);
    }

    @FXML
    void ok(ActionEvent event) {
        //Включаем индикатор
        spIndicator.setVisible(true);

        //Создаем новый комментарий с полями
        Remark newRemark = new Remark();
        newRemark.setCreationTime(AppStatic.getCurrentTime());
        newRemark.setUser(CH_CURRENT_USER);
        newRemark.setPassport(passport);
        newRemark.setText(taRemarksText.getText());
        newRemark.setPicsInRemark(collectPicturesInRemark());

        if(operation.equals(EOperation.CHANGE)) {
            newRemark.setId(changingRemark.getId());
            commands.change(event, newRemark);
        } else
            commands.add(event, newRemark);

        //Закрываем окно
        closeWindow(event);
    }

    /**
     * Метод из коллекции picturesInRemark формирует новую коллекцию List<Pic>
     * которая необходима для добавления в поле pics нового экземпляра Remark
     */
    private List<Pic> collectPicturesInRemark(){
        List<Pic> allPics = new ArrayList<>();
        for(FileImage fi : picturesInRemark){
            if(fi.pic != null){
                allPics.add(fi.pic);
            } else {
                Pic pic = createPicFromFileAndSaveItToDB(fi.file);
                allPics.add(pic);
            }
        }

        return allPics;
    }

    /**
     * Метод создает Pic и сохраняет его в БД
     * Затем изображение, соответствующее Pic загружается на сервер
     * Возвращается сохраненный в базе Pic
     */
    private Pic createPicFromFileAndSaveItToDB(File file){
        //Находим в коллекции picturesInRemark необходимый Image соответствующий нашему File
        Image picture = picturesInRemark.stream().filter(i->i.file.equals(file)).findFirst().get().image;
        //Создаем новый экземпляр Pic с полями
        Pic newPic = new Pic();
        newPic.setUser(CH_CURRENT_USER);
        newPic.setTime(AppStatic.getCurrentTime());
        newPic.setWidth((int) picture.getWidth());
        newPic.setHeight((int) picture.getHeight());
        //Готовим файл для добавления картинки в папку "pics" на сервере
        //сжимаем картинку до приемлимых значений
        File compressedFile = ImageUtil.compressImageToFile(file, null);
        //Добавляем расширение уже сжатого файла
        newPic.setExtension(FilenameUtils.getExtension(compressedFile.toString()));

        //Сохраняем Pic в базе данных
        Pic savedPic = CH_PICS.save(newPic);

        //Загружаем картинку на сервер
        String fileName = savedPic.getId() + "." + FilenameUtils.getExtension(compressedFile.toString());
        try {

            CH_FILES.upload(fileName, "pics", compressedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return savedPic;
    }

    /**
     * Кнопка ДОБАВИТЬ ФОТО
     */
    @FXML
    void addPicture(ActionEvent event) {
        // Пользователь выбирает несколько рисунков
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg");
        List<File> chosenFiles = AppStatic.chooseManyFile(event, new File("C:\\"), filter);
        if(chosenFiles == null || chosenFiles.isEmpty()) return;
        for(File file : chosenFiles){
            //Добавляем рисунок на форму
            Image image = addNewImageToTheForm(file);
            //Добавляем рисунок в коллекцию без поля pic, так как он еще не сохранен в БД
            picturesInRemark.add(new FileImage(file, image, null));
        }
    }

    /**
     * Метод добавляет изображение на форму и возвращает Image,
     * необходимый для получения исходных размеров изображения
     */
    private Image addNewImageToTheForm(File file){

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
            ContextMenu contextMenu = createContextMenu(file);
            contextMenu.show(imageView, e.getScreenX(), e.getScreenY());
        });

        vbRemarksEntryContainer.getChildren().add(imageView);

        return image;
    }

    private ContextMenu createContextMenu(File file) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deletePhoto = new MenuItem("Удалить фото");
        deletePhoto.setOnAction(ev -> {
            //Находим в коллекции picturesInRemark изображение, которое нужно удалить
            FileImage deletingPicture = picturesInRemark.stream().filter(i -> i.file.equals(file)).findFirst().get();
            picturesInRemark.remove(deletingPicture);
            //Если удаляемая картинка уже сохранена в базе, то удаляем ее и из базы
            Pic pic = deletingPicture.getPic();
            if (pic != null)
                CH_PICS.delete(pic);

            //Обновляем изображения с учетом удаленного
            vbRemarksEntryContainer.getChildren().clear();
            for(FileImage fi : picturesInRemark){
                addNewImageToTheForm(fi.file);
            }
        });

        contextMenu.getItems().add(deletePhoto);
        return contextMenu;
    }

    @Override
    public ArrayList<String> getNotNullFields() {
        //Не используется
        return null;
    }

    @Override
    public Remark getNewItem() {
        //Не используется
        return null;
    }

    @Override
    public Remark getOldItem() {
        //Не используется
        return null;
    }

    @Override
    public void fillFieldsOnTheForm(Remark oldItem) {
        taRemarksText.setText(oldItem.getText());
        initialPics = CH_REMARKS.getPics(oldItem);
        if(initialPics == null || initialPics.isEmpty()) return;
        //Сортируем список, чтобы последние добавленные(новые) оказались внизу
        initialPics.sort(Comparator.comparing(Pic::getTime));
        for(Pic p : initialPics) {
            String tempFileName = "remark" + "-" + p.getId() + "." + p.getExtension();
            boolean res = CH_FILES.download("pics", //Постоянная папка в каталоге для чертежей
                    String.valueOf(p.getId()), //название скачиваемого файла
                    "." + p.getExtension(), //расширение скачиваемого файла
                    WF_TEMPDIR.toString(),//временная папка, куда необходимо скачать фай
                    "remark"); //префикс

            File file = new File(WF_TEMPDIR.toString() + "\\" + tempFileName);
            //Добавляем файл в общий список
            Image image = addNewImageToTheForm(file);

            picturesInRemark.add(new FileImage(file, image, p));
        }

    }

    @Override
    public void changeOldItemFields(Remark oldItem) {
        //Не используется
    }

    @Override
    public void showEmptyForm() {

    }

    @Override
    public boolean enteredDataCorrect() {
        //Не используется
        return true;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private class FileImage{
        File file;
        Image image;
        Pic pic;
    }
}