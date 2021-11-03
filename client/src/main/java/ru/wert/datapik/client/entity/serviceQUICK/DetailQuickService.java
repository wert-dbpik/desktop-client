package ru.wert.datapik.client.entity.serviceQUICK;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.wert.datapik.client.entity.models.Detail;
import ru.wert.datapik.client.entity.serviceREST.DetailService;
import ru.wert.datapik.client.entity.service_interfaces.IDetailService;

import java.util.ArrayList;
import java.util.List;

public class DetailQuickService implements IDetailService {

    private static DetailQuickService instance;
    private static List<Detail> details;
    private static final DetailService service = DetailService.getInstance();

    private DetailQuickService() {
        reload();
    }

    public static DetailQuickService getInstance() {
        if (instance == null)
            return new DetailQuickService();
        return instance;
    }

    public static void reload(){
        while(true) {
            if(service != null) {
                details = new ArrayList<>(service.findAll());
                break;
            }
        }
    }

    public Detail findByName(String name) {
        Detail foundDetail = null;
        for(Detail detail : details){
            if(detail.getName() != null && detail.getName().equals(name)) {
                foundDetail = detail;
                break;
            }
        }
        return foundDetail;
    }

    public ObservableList<Detail> findAllByText(String text) {
        ObservableList<Detail> foundDetails = FXCollections.observableArrayList();
        for(Detail detail : details){
            String name = detail.getPassport().getName();
            String decNumber = detail.getPassport().getNumber();
            if((name != null && name.contains(text)) || (decNumber != null && decNumber.contains(text))) {
                foundDetails.add(detail);
            }
        }
        return foundDetails;
    }

    public Detail findById(Long id) {
        Detail foundDetail = null;
        for(Detail detail : details){
            if(detail.getId().equals(id)) {
                foundDetail = detail;
                break;
            }
        }
        return foundDetail;
    }

    @Override
    public Detail save(Detail detail) {
        Detail res = service.save(detail);
        reload();
        return res;
    }

    @Override
    public boolean update(Detail detail) {
        boolean res = service.update(detail);
        reload();
        return res;
    }

    @Override
    public boolean delete(Detail detail){
        boolean res = service.delete(detail);
        reload();
        return res;
    }

    public ObservableList<Detail> findAll() {
        return FXCollections.observableArrayList(details);
    }

    @Override
    public Detail findByPassportId(Long id) {
        Detail foundDetail = null;
        for(Detail detail : details){
            if(detail.getPassport().getId().equals(id)) {
                foundDetail = detail;
                break;
            }
        }
        return foundDetail;
    }

    @Override
    public List<Detail> findAllByDraftId(Long id) {
        List<Detail> foundDetails = new ArrayList<>();
        for(Detail detail : details){
            if(detail.getDraft().getId().equals(id)) {
                foundDetails.add(detail);
            }
        }
        return foundDetails;
    }

    @Override
    public List<Detail> findAllByFolderId(Long id) {
        List<Detail> foundDetails = new ArrayList<>();
        for(Detail detail : details){
            if(detail.getFolder().getId().equals(id)) {
                foundDetails.add(detail);
            }
        }
        return foundDetails;
    }
}
