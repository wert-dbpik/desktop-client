package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.client.interfaces.ItemService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface IFilesService{

    boolean download(String path, String fileName, String extension, String tempDir);

    boolean upload(String fileNameForSaving, String directoryName, File initialFile) throws IOException;

}
