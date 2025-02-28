package ru.wert.tubus.chogori.tempfile;

import org.apache.commons.io.FileUtils;
import ru.wert.tubus.chogori.components.FileFwdSlash;

import java.io.File;
import java.io.IOException;

public class TempDir {

    public static FileFwdSlash createTempDirectory(String dirName){
        final File tmp = new File(FileUtils.getTempDirectory().getAbsolutePath()
                + File.separator + dirName );
//                + System.currentTimeMillis());
        tmp.mkdir();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            try {
                FileUtils.deleteDirectory(tmp);
            } catch (IOException e) {
                //Ничего не делаем
            }
        }));
        //меняем бэкслэши на обычные
        return new FileFwdSlash(tmp.toString());
    }
}
