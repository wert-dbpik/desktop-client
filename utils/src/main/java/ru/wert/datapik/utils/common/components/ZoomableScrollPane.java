package ru.wert.datapik.utils.common.components;

import com.sun.deploy.security.SelectableSecurityManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.Set;

public class ZoomableScrollPane extends ScrollPane {

    double imgWidth;
    double imgHeight;
    double paneWidth;
    double paneHeight;
    private ImageView image;
    private StackPane stPane;
    double ratio;

    double mouseX, mouseY, mX, mY;


    public ZoomableScrollPane(ImageView image, StackPane stPane) {
        super(image);
        this.image = image;
        this.stPane = stPane;

        setFitToHeight(true);
        setFitToWidth(true);
        setPannable(true);


        paneWidth = stPane.getWidth();
        paneHeight = stPane.getHeight();
        imgWidth = image.getFitWidth();
        imgHeight = image.getFitHeight();

        ratio = imgHeight/imgWidth;

        fitHeight();

        stPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            double delta = newValue.doubleValue() - oldValue.doubleValue();
//            if(ratio >= 1) {
                image.setFitHeight(image.getFitHeight() + delta);
                image.setFitWidth(image.getFitHeight() / ratio);
//            image.setTranslateX((paneWidth - image.getFitWidth())/2);
                image.setTranslateX(image.getTranslateX() - delta / ratio / 2);
//            } else
//
//                image.setTranslateY(image.getTranslateY() + delta/2);

        });

        stPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            double delta = newValue.doubleValue() - oldValue.doubleValue();
//            if(ratio < 1) {
//                image.setFitWidth(image.getFitWidth() + delta);
//                image.setFitHeight(image.getFitWidth() * ratio);
//                image.setTranslateY(image.getTranslateY() - delta*ratio/2);
//            } else
                image.setTranslateX(image.getTranslateX() + delta/2);

        });

        image.setOnScroll(event -> {
            if(event.isControlDown()){
                double zoom = 1.05;
                if(event.getDeltaY() < 0)
                    zoom = 0.95;

                double h = image.getFitHeight(); //начальная высота
                double w = image.getFitWidth(); //начальная ширина

                image.setFitHeight(image.getFitHeight() * zoom);
                image.setFitWidth(image.getFitWidth() * zoom);

                double deltaX = image.getTranslateX() - (image.getFitWidth() - w)/2; //c одной стороны
                if (deltaX > 0)
                    image.setTranslateX(deltaX);
                else {
                    if(getHvalue() != 0.0)
                        setHvalue(0.5);
                }

                double deltaY = image.getTranslateY() - (image.getFitHeight() - h)/2; //c одной стороны
                if (deltaY > 0)
                    image.setTranslateY(deltaY);
                else {
                    if(getVvalue() != 0.0)
                        setVvalue(0.5);
                }

                event.consume();
            }
        });

        image.setOnMousePressed(event -> {
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
        });



        image.setOnMouseDragOver(event -> {
            mX = event.getSceneX();
            mY = event.getSceneY();
            double deltaX = mX - mouseX;
            double deltaY = mY - mouseY;

            image.setTranslateX(image.getTranslateX() + deltaX);
            image.setTranslateY(image.getTranslateY() + deltaY);
        });


    }

    private void fitHeight(){
        image.setFitHeight(paneHeight);
        image.setFitWidth(imgWidth * paneHeight / imgHeight);

//        image.translateXProperty().bind(widthProperty().subtract(image.fitWidthProperty()).divide(2));

        image.setTranslateX((paneWidth - image.getFitWidth())/2);

    }

    private void fitWidth(){
        image.setFitWidth(paneWidth);
        image.setFitHeight(imgHeight * paneWidth / imgWidth);

        image.setTranslateY((paneHeight - image.getFitHeight())/2);
    }
}
