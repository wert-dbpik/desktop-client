package ru.wert.tubus.client.entity.serviceQUICK;

import ru.wert.tubus.client.entity.models.Folder;
import ru.wert.tubus.client.entity.models.Passport;
import ru.wert.tubus.client.entity.models.Pic;
import ru.wert.tubus.client.entity.models.Remark;
import ru.wert.tubus.client.entity.serviceREST.FolderService;
import ru.wert.tubus.client.entity.serviceREST.RemarkService;
import ru.wert.tubus.client.entity.service_interfaces.IFolderService;
import ru.wert.tubus.client.entity.service_interfaces.IRemarkService;
import ru.wert.tubus.client.interfaces.CatalogService;
import ru.wert.tubus.client.interfaces.ItemService;

import java.util.ArrayList;
import java.util.List;

public class RemarkQuickService implements IRemarkService, ItemService<Remark> {

    private static RemarkQuickService instance;
    public static List<Remark> LOADED_REMARKS;
    private static final RemarkService service = RemarkService.getInstance();

    private RemarkQuickService() {
        reload();
    }

    public static RemarkQuickService getInstance() {
        if (instance == null)
            instance =  new RemarkQuickService();
        return instance;
    }

    public static void reload(){
        while(true) {
            if(service != null) {
                LOADED_REMARKS = new ArrayList<>(service.findAll());
                break;
            }
        }
    }

    public Remark findByName(String name) {
        Remark foundRemark = null;
        for(Remark remark : LOADED_REMARKS){
            if(remark.getName() != null && remark.getName().equals(name)) {
                foundRemark = remark;
                break;
            }
        }
        return foundRemark;
    }

    @Override
    public List<Remark> findAllByPassport(Passport passport) {
        List<Remark> foundRemarks = new ArrayList<>();
        for(Remark remark : LOADED_REMARKS){
            if(remark.getPassport().equals(passport)) {
                foundRemarks.add(remark);
                break;
            }
        }
        return foundRemarks;
    }

    @Override
    public List<Pic> getPics(Remark remark) {
        return null;
    }


    public Remark findById(Long id) {
        Remark foundRemark = null;
        for(Remark remark : LOADED_REMARKS){
            if(remark.getId().equals(id)) {
                foundRemark = remark;
                break;
            }
        }
        return foundRemark;
    }

    @Override
    public Remark save(Remark remark) {
        Remark savedRemark = service.save(remark);
        reload();
        return savedRemark;
    }

    @Override
    public boolean update(Remark remark) {
        boolean res = service.update(remark);
        reload();
        return res;
    }

    @Override
    public boolean delete(Remark remark){
        boolean res = service.delete(remark);
        reload();
        return res;
    }


    public List<Remark> findAll() {
        return LOADED_REMARKS;
    }

    public List<Remark> findAllByText(String text) {
        List<Remark> foundRemarks = new ArrayList<>();
        for(Remark remark : LOADED_REMARKS){
            String name = remark.getName();

            if(name != null && name.contains(text)) {
                foundRemarks.add(remark);
            }
        }
        return foundRemarks;
    }
}
