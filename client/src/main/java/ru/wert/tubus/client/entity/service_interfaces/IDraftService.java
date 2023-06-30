package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.interfaces.ItemService;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.client.entity.models.Folder;
import ru.wert.tubus.client.entity.models.Passport;
import ru.wert.tubus.client.entity.models.Product;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface IDraftService extends ItemService<Draft> {

    List<String> findDraftsByMask(String folder, String mask);

//    boolean download(String path, String fileName, String extension, String tempDir);

    boolean upload(String fileName, String path, File draft) throws IOException;

    boolean deleteDraftFile(String folder, String fileName);

    /***
     * Искать все чертежи входящие в папку
     */
    List<Draft> findAllByFolder(Folder folder);


    List<Draft> findByPassport(Passport passport);

    // ИЗДЕЛИЯ

    Set<Product> findProducts(Draft draft);

    /**
     * Добавить изделие к чертежу
     */
    Set<Product> addProduct(Draft draft, Product product);

    /**
     * Удалить изделие, относящееся к чертежу
     */
    Set<Product> removeProduct(Draft draft, Product product);


}
