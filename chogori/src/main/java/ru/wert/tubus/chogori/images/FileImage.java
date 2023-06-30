package ru.wert.tubus.chogori.images;

import javafx.scene.image.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.tubus.client.entity.models.Pic;

import java.io.File;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileImage{
    File file;
    Image image;
    Pic pic;
}
