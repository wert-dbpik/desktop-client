package ru.wert.datapik.utils.images;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import ru.wert.datapik.utils.remarks.Pic_OutstandingPreviewer;
import ru.wert.datapik.winform.statics.WinformStatic;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.Iterator;

public class ImageUtil {

    public byte[] getImageBytes(String path){
        DataBufferByte data = null;
        try {
            File imagePath = new File(path);
            BufferedImage bufferedImage = ImageIO.read(imagePath);
            WritableRaster raster = bufferedImage.getRaster();
            data = (DataBufferByte)raster.getDataBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert data != null;
        return data.getData();
    }

    public Image getImageFromBytes(byte[] imageBytes){
        return new Image(new ByteArrayInputStream(imageBytes));

    }

    /**
     *
     * @param input, File - Изначальное несжатое изображение
     * @param compressedImageFile, File - Окончательное сжатое изображение
     * @return File
     */
    public static File compressImageToFile(File input, File compressedImageFile){
        try {
            BufferedImage image = ImageIO.read(input);

            if(compressedImageFile == null)
                compressedImageFile =
                        new File(WinformStatic.WF_TEMPDIR + "/" + "compressed_image.jpg");
            OutputStream os = new FileOutputStream(compressedImageFile);

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            ImageWriter writer = (ImageWriter) writers.next();

            ImageOutputStream ios = ImageIO.createImageOutputStream(os);
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();

            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(0.5f);  // Change the quality value you prefer
            writer.write(null, new IIOImage(image, null, null), param);

            os.close();
            ios.close();
            writer.dispose();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return compressedImageFile;
    }

    /**
     * Метод добавляет изображение на форму и возвращает Image,
     * необходимый для получения исходных размеров изображения
     */
    public static ImageView addNewImageToTheForm(File file, Control node){

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

        imageView.fitWidthProperty().bind(node.widthProperty().multiply(weight));

        return imageView;
    }
}
