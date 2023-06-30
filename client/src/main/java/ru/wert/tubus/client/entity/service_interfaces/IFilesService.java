package ru.wert.tubus.client.entity.service_interfaces;

import java.io.File;
import java.io.IOException;


public interface IFilesService{

    boolean download(String path, String fileName, String extension, String tempDir, String prefix);

    boolean upload(String fileNameForSaving, String directoryName, File initialFile) throws IOException;

}
