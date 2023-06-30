package ru.wert.tubus.chogori.images;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import ru.wert.tubus.client.entity.models.Pic;
import ru.wert.tubus.chogori.remarks.Pic_OutstandingPreviewer;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.chogori.application.services.ChogoriServices;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MessageContainer {

    TextArea taText;
    VBox vbPicsContainer;
    Button addPicture;

    List<FileImage> picturesInMessage;

    //Переменные для taText
    private Text textHolder = new Text();
    private double oldHeight = 0;


    public MessageContainer(TextArea taText, VBox vbPicsContainer, Button addPicture) {
        this.taText = taText;
        this.vbPicsContainer = vbPicsContainer;
        this.addPicture = addPicture;

        picturesInMessage = new ArrayList<>();

        //Применим CSS стили к TextArea
        taText.setId("blobTextArea");
        //Сделаем из стандартного TextArea раздуваемый
        textHolder.textProperty().bind(taText.textProperty());
        textHolder.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            if (oldHeight != newValue.getHeight()) {
                oldHeight = newValue.getHeight();
                double newHeight = textHolder.getLayoutBounds().getHeight() + 30;
                taText.setPrefHeight(newHeight);
                taText.setMinHeight(newHeight);
            }
        });

        addPicture.setOnAction(this::addPicture);


    }

    private ContextMenu createContextMenu(File file) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deletePhoto = new MenuItem("Удалить фото");
        deletePhoto.setOnAction(ev -> {
            //Находим в коллекции picturesInRemark изображение, которое нужно удалить
            FileImage deletingPicture = picturesInMessage.stream().filter(i -> i.getFile().equals(file)).findFirst().get();
            picturesInMessage.remove(deletingPicture);
            //Если удаляемая картинка уже сохранена в базе, то удаляем ее и из базы
            Pic pic = deletingPicture.getPic();
            if (pic != null)
                ChogoriServices.CH_PICS.delete(pic);

            //Обновляем изображения с учетом удаленного
            vbPicsContainer.getChildren().clear();
            for(FileImage fi : picturesInMessage){
                addNewImageToTheForm(fi.getFile());
            }
        });

        contextMenu.getItems().add(deletePhoto);
        return contextMenu;
    }

    /**
     * Метод добавляет изображение на форму и возвращает Image,
     * необходимый для получения исходных размеров изображения
     */
    public Image addNewImageToTheForm(File file){

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

        imageView.fitWidthProperty().bind(taText.widthProperty().multiply(weight));

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

//        imageView.setOnContextMenuRequested(e ->{
//            ContextMenu contextMenu = createContextMenu(file);
//            contextMenu.show(imageView, e.getScreenX(), e.getScreenY());
//        });

        imageView.setOnContextMenuRequested(e ->{
            ContextMenu contextMenu = createContextMenu(file);
            contextMenu.show(imageView, e.getScreenX(), e.getScreenY());
        });

        vbPicsContainer.getChildren().add(imageView);

        return image;
    }

    void addPicture(ActionEvent event) {
        // Пользователь выбирает несколько рисунков
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg");
        List<File> chosenFiles = AppStatic.chooseManyFile(event, new File("C:\\"), filter);
        if(chosenFiles == null || chosenFiles.isEmpty()) return;
        for(File file : chosenFiles){
            //Добавляем рисунок на форму
            Image image = addNewImageToTheForm(file);
            //Добавляем рисунок в коллекцию без поля pic, так как он еще не сохранен в БД
            picturesInMessage.add(new FileImage(file, image, null));
        }
    }
}
