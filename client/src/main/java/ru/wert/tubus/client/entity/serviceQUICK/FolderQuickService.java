package ru.wert.tubus.client.entity.serviceQUICK;

import ru.wert.tubus.client.entity.models.Folder;
import ru.wert.tubus.client.entity.serviceREST.FolderService;
import ru.wert.tubus.client.entity.service_interfaces.IFolderService;
import ru.wert.tubus.client.interfaces.CatalogService;
import ru.wert.tubus.client.interfaces.ItemService;

import java.util.ArrayList;
import java.util.List;

public class FolderQuickService implements IFolderService, CatalogService<Folder>, ItemService<Folder> {

    private static FolderQuickService instance;
    public static List<Folder> LOADED_FOLDERS;
    private static FolderService service = FolderService.getInstance();
    public static Folder DEFAULT_FOLDER;

    private FolderQuickService() {
        reload();

//        DEFAULT_FOLDER = service.findByName(RAZLOZHENO);

    }

    public static FolderQuickService getInstance() {
        if (instance == null)
            instance =  new FolderQuickService();
        return instance;
    }

    public static void reload(){
        while(true) {
            if(service != null) {
                LOADED_FOLDERS = new ArrayList<>(service.findAll());
                break;
            }
        }
    }

    public Folder findByName(String name) {
        Folder foundFolder = null;
        for(Folder folder : LOADED_FOLDERS){
            if(folder.getName() != null && folder.getName().equals(name)) {
                foundFolder = folder;
                break;
            }
        }
        return foundFolder;
    }


    @Override //CatalogService
    public List<Folder> findAllByGroupId(Long id) {
        List<Folder> foundFolders = new ArrayList<>();
        for(Folder folder : LOADED_FOLDERS){
            if(folder.getProductGroup().getId().equals(id)) {
                foundFolders.add(folder);
            }
        }
        return foundFolders;
    }

    public Folder findById(Long id) {
        Folder foundFolder = null;
        for(Folder folder : LOADED_FOLDERS){
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

    public List<Folder> findAll() {
        return LOADED_FOLDERS;
    }

    public List<Folder> findAllByText(String text) {
        List<Folder> foundFolders = new ArrayList<>();
        for(Folder folder : LOADED_FOLDERS){
            String name = folder.getName();

            if(name != null && name.contains(text)) {
                foundFolders.add(folder);
            }
        }
        return foundFolders;
    }
}
