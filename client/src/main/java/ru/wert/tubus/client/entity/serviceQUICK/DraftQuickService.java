package ru.wert.tubus.client.entity.serviceQUICK;

import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.client.entity.models.Folder;
import ru.wert.tubus.client.entity.models.Passport;
import ru.wert.tubus.client.entity.models.Product;
import ru.wert.tubus.client.entity.serviceREST.DraftService;
import ru.wert.tubus.client.entity.service_interfaces.IDraftService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DraftQuickService implements IDraftService {

    private static DraftQuickService instance;
    private static List<Draft> drafts;
    private static DraftService service = DraftService.getInstance();

    private DraftQuickService() {
        reload();
    }

    public static DraftQuickService getInstance() {
        if (instance == null)
            return new DraftQuickService();
        return instance;
    }

    public static void reload(){
        while(true) {
            if (service != null) {
                drafts = new ArrayList<>(service.findAll());
                break;
            }
        }
    }


    @Override
    public List<String> findDraftsByMask(String folder, String mask) {
        return service.findDraftsByMask(folder, mask);
    }

//    @Override
//    public boolean download(String path, String fileName, String extension, String tempDir) {
//        return service.download(path, fileName, extension,tempDir);
//    }

    @Override
    public boolean upload(String fileNewName, String folder, File draft) throws IOException {
        boolean res =  service.upload(fileNewName, folder, draft);
        reload();
        return res;
    }

    @Override
    public boolean deleteDraftFile(String folder, String fileName) {
        boolean res =  service.deleteDraftFile(folder, fileName);
        reload();
        return res;
    }


    public List<Draft> findByPassport(Passport passport) {
        Long id = passport.getId();
        List<Draft> foundDrafts = new ArrayList<>();
        Draft foundDraft = null;
        for(Draft draft : drafts){
            if(draft.getPassport().getId().equals(id)) {
                foundDrafts.add(draft);
            }
        }
        return foundDrafts;
    }

    public List<Draft> findAllByFolder(Folder folder) {
        List<Draft> foundDrafts = new ArrayList<>();
        Long folderId = folder.getId();
        for(Draft draft : drafts){
            if(draft.getFolder() != null && draft.getFolder().getId().equals(folderId)) {
                foundDrafts.add(draft);
            }
        }
        return foundDrafts;
    }

    public Draft findById(Long id) {
        Draft foundDraft = null;
        for(Draft draft : drafts){
            if(draft.getId().equals(id)) {
                foundDraft = draft;
                break;
            }
        }
        return foundDraft;
    }

    @Override
    public Draft save(Draft draft) {
        Draft res = service.save(draft);
        reload();
        return res;
    }

    @Override
    public boolean update(Draft draft) {
        boolean res = service.update(draft);
        reload();
        return res;
    }

    @Override
    public boolean delete(Draft draft)  {
        boolean res = service.delete(draft);
        reload();
        return res;
    }

    public List<Draft> findAll() {
        return drafts;
    }
    
    public List<Draft> findAllByText(String text) {
        List<Draft> foundDrafts = new ArrayList<>();
        for(Draft draft : drafts){
            String name = draft.getPassport().getName();
            String decNumber = draft.getPassport().getNumber();
            if((name != null && name.contains(text)) || (decNumber != null && decNumber.contains(text))) {
                foundDrafts.add(draft);
            }
        }
        return foundDrafts;
    }
    
    // ИЗДЕЛИЯ

    @Override
    public Set<Product> findProducts(Draft draft) {
        return service.findProducts(draft);
    }

    @Override
    public Set<Product> addProduct(Draft draft, Product product) {
        Set<Product> res = service.addProduct(draft, product);
        ProductQuickService.reload();
        reload();
        return res;
    }

    @Override
    public Set<Product> removeProduct(Draft draft, Product product) {
        Set<Product> res = service.removeProduct(draft, product);
        ProductQuickService.reload();
        reload();
        return res;
    }
}
