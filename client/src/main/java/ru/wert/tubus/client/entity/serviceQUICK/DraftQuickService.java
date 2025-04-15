package ru.wert.tubus.client.entity.serviceQUICK;

import ru.wert.tubus.client.entity.models.*;
import ru.wert.tubus.client.entity.serviceREST.DraftService;
import ru.wert.tubus.client.entity.service_interfaces.IDraftService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ru.wert.tubus.client.entity.serviceQUICK.RemarkQuickService.LOADED_REMARKS;

public class DraftQuickService implements IDraftService {

    private static DraftQuickService instance;
    public static List<Draft> LOADED_DRAFTS;
    private static DraftService service = DraftService.getInstance();

    private DraftQuickService() {
        reload();
    }

    public static DraftQuickService getInstance() {
        if (instance == null)
            instance = new DraftQuickService();
        return instance;
    }

    public static void reload(){
        while(true) {
            if (service != null) {
                LOADED_DRAFTS = new ArrayList<>(service.findAll());
                break;
            }
        }
    }

    public boolean hasRemarks(Draft draft){
        Passport p = draft.getPassport();
        for(Remark r : LOADED_REMARKS){
            if(r.getPassport().equals(p))
                return true;
        }
        return false;
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
        for(Draft draft : LOADED_DRAFTS){
            if(draft.getPassport().getId().equals(id)) {
                foundDrafts.add(draft);
            }
        }
        return foundDrafts;
    }

    public List<Draft> findAllByFolder(Folder folder) {
        List<Draft> foundDrafts = new ArrayList<>();
        Long folderId = folder.getId();
        for(Draft draft : LOADED_DRAFTS){
            if(draft.getFolder() != null && draft.getFolder().getId().equals(folderId)) {
                foundDrafts.add(draft);
            }
        }
        return foundDrafts;
    }

    public Draft findById(Long id) {
        Draft foundDraft = null;
        for(Draft draft : LOADED_DRAFTS){
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
        return LOADED_DRAFTS;
    }
    
    public List<Draft> findAllByText(String text) {
        List<Draft> foundDrafts = new ArrayList<>();
        for(Draft draft : LOADED_DRAFTS){
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
