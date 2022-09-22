package ru.wert.datapik.utils.images;

import javafx.scene.image.Image;
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
}
