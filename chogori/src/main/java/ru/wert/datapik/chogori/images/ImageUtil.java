package ru.wert.datapik.chogori.images;

import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import org.apache.commons.io.FilenameUtils;
import ru.wert.datapik.client.entity.models.Pic;
import ru.wert.datapik.chogori.remarks.Pic_OutstandingPreviewer;
import ru.wert.datapik.chogori.statics.AppStatic;
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

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_FILES;
import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_PICS;
import static ru.wert.datapik.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

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
     * Метод возвращает ImageView, доступный для добавления на форму
     * @param file File, - собственно файл
     * @param node Control, nullable - элемент, относительно которого происходит вычисление ширины
     * @param constWidth, int - постоянная длина, от которой считается ширина изображения, исп-ся при node = null
     * @param portrait, float - вертикадьное изображение
     * @param landscape, float - горизонтальное изображение
     * @param square, float - квадратное
     * @return ImageView
     */
    public static ImageView createImageViewFromFile(File file, Control node, Integer constWidth, float portrait, float landscape, float square){

        Image image = new Image(file.toURI().toString());

        double w = image.getWidth();
        double h = image.getHeight();

        float weight;
        if (w - h < w * 0.1f)
            weight = portrait;
        else if (w - h > w * 0.1f)
            weight = landscape;
        else
            weight = square;

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

        //Привязываем ширину imageView к ширине узла node
        if(node != null)
            imageView.fitWidthProperty().bind(node.widthProperty().multiply(weight));
        else if(constWidth != null)
            imageView.setFitWidth(constWidth * weight);

        return imageView;
    }

    /**
     * Метод создает Pic и сохраняет его в БД
     * Затем изображение, соответствующее Pic загружается на сервер
     * Возвращается сохраненный в базе Pic
     */
    public static Pic createPicFromFileAndSaveItToDB(Image image, File file){
        //Создаем новый экземпляр Pic с полями
        Pic newPic = new Pic();
        newPic.setUser(CH_CURRENT_USER);
        newPic.setTime(AppStatic.getCurrentTime());
        newPic.setWidth((int) image.getWidth());
        newPic.setHeight((int) image.getHeight());
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
}
