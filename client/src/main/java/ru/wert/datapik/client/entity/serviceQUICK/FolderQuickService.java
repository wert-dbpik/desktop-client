package ru.wert.datapik.client.entity.serviceQUICK;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.client.entity.serviceREST.FolderService;
import ru.wert.datapik.client.entity.service_interfaces.IFolderService;
import ru.wert.datapik.client.exceptions.ItemIsBusyException;
import ru.wert.datapik.client.interfaces.CatalogService;
import ru.wert.datapik.client.interfaces.ItemService;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.client.utils.BLConst.RAZLOZHENO;

public class FolderQuickService implements IFolderService, CatalogService<Folder>, ItemService<Folder> {

    private static FolderQuickService instance;
    private static List<Folder> folders;
    private static FolderService service = FolderService.getInstance();
    public static Folder DEFAULT_FOLDER;

    private FolderQuickService() {
        reload();

//        DEFAULT_FOLDER = service.findByName(RAZLOZHENO);

    }

    public static FolderQuickService getInstance() {
        if (instance == null)
            return new FolderQuickService();
        return instance;
    }

    public static void reload(){
        while(true) {
            if(service != null) {
                folders = new ArrayList<>(service.findAll());
                break;
            }
        }
    }

    public Folder findByName(String name) {
        Folder foundFolder = null;
        for(Folder folder : folders){
            if(folder.getName() != null && folder.getName().equals(name)) {
                foundFolder = folder;
                break;
            }
        }
        return foundFolder;
    }


    @Override //CatalogService
    public List<Folder> findAllByGroupId(Long id) {
        ObservableList<Folder> foundFolders = FXCollections.observableArrayList();
        for(Folder folder : folders){
            if(folder.getProductGroup().getId().equals(id)) {
                foundFolders.add(folder);
            }
        }
        return foundFolders;
    }

    public Folder findById(Long id) {
        Folder foundFolder = null;
        for(Folder folder : folders){
            if(folder.getId().equals(id)) {
                foundFolder = folder;
                break;
            }
        }
        return foundFolder;
    }

    @Override
    public Folder save(Folder folder) {
        Folder savedFolder = service.save(folder);
        reload();
        return savedFolder;
    }

    @Override
    public boolean update(Folder folder) {
        boolean res = service.update(folder);
        reload();
        return res;
    }

    @Override
    public boolean delete(Folder folder){
        boolean res = service.delete(folder);
        reload();
        return res;
    }

    public ObservableList<Folder> findAll() {
        return FXCollections.observableArrayList(folders);
    }

    public ObservableList<Folder> findAllByText(String text) {
        ObservableList<Folder> foundFolders = FXCollections.observableArrayList();
        for(Folder folder : folders){
            String name = folder.getName();

            if(name != null && name.contains(text)) {
                foundFolders.add(folder);
            }
        }
        return foundFolders;
    }
}
