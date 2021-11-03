package ru.wert.datapik.utils.images;

import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

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
}
