package ru.wert.tubus.client.entity.service_interfaces;

import java.io.File;
import java.io.IOException;


public interface IFilesService{

    /**
     * Метод загружает файл из БД в указанную директорию и с указанным именем
     * Если newName = null, то имя скачиваемого файла остается прежним, т.е. id.ext
     * @param path, String - из какой директории выкачиваем файл (apk, drafts, excels, normic, pics)
     * @param fileName, String - обычно ID файла
     * @param ext, String - расширение файла (.exe, .pdf, .apk и т.д.)
     * @param destDir, String -  папка назначения (путь до tempDir, своя папка)
     * @param prefix, String - уточнящий префикс для управления ("chat", "remark")
     */
    boolean download(String path, String fileName, String ext, String destDir, String prefix, String newName);

    boolean upload(String fileNameForSaving, String directoryName, File initialFile) throws IOException;

}
