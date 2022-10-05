package ru.wert.datapik.chogori.excel;

import javafx.scene.ImageCursor;
import javafx.scene.image.Image;

public class Cursors {

    public static ImageCursor cursorWhiteCross;
    public static ImageCursor cursorBlackArrow;

    public Cursors() {

        Image image = new Image(getClass().getResourceAsStream("/pics/editor/cursor-white-cross(16x16).png"));
        cursorWhiteCross = new ImageCursor(image,  image.getWidth() / 2,image.getHeight() /2);

        Image image2 = new Image(getClass().getResourceAsStream("/pics/editor/cursor-black-arrow(16x16).png"));
        cursorBlackArrow = new ImageCursor(image2,  image2.getWidth() / 2,image2.getHeight() /2);
    }
}
