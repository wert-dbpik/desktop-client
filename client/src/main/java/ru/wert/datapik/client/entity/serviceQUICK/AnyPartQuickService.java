package ru.wert.datapik.client.entity.serviceQUICK;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.wert.datapik.client.entity.models.AnyPart;
import ru.wert.datapik.client.entity.serviceREST.AnyPartService;
import ru.wert.datapik.client.entity.service_interfaces.IAnyPartService;

import java.util.ArrayList;
import java.util.List;

public class AnyPartQuickService implements IAnyPartService {

    private static AnyPartQuickService instance;
    private static List<AnyPart> parts;
    private static final AnyPartService service = AnyPartService.getInstance();


    private AnyPartQuickService() {
        reload();
        
    }

    public static AnyPartQuickService getInstance() {
        if (instance == null)
            return new AnyPartQuickService();
        return instance;
    }

    public static void reload(){
        while(true) {
            if(service != null) {
                parts = new ArrayList<>(service.findAll());
                break;
            }
        }
    }


    public AnyPart findByName(String name) {
        AnyPart foundAnyPart = null;
        for(AnyPart part : parts){
            if(part.getName() != null && part.getName().equals(name)) {
                foundAnyPart = part;
                break;
            }
        }
        return foundAnyPart;
    }

    /**
     * Ищет дубликаты записи
     * @param number по сути name
     * @param name по сути secondName
     * @return
     */
    public AnyPart findByNumberAndName(String number, String name) {
        AnyPart foundAnyPart = null;
        for(AnyPart part : parts){
            if((part.getName() != null && part.getName().equals(number)) &&
                    (part.getSecondName() != null && part.getSecondName().equals(name))) {
                foundAnyPart = part;
                break;
            }
        }
        return foundAnyPart;
    }


    public AnyPart findById(Long id) {
        AnyPart foundAnyPart = null;
        for(AnyPart part : parts){
            if(part.getId().equals(id)) {
                foundAnyPart = part;
                break;
            }
        }
        return foundAnyPart;
    }

    @Override
    public AnyPart save(AnyPart part) {
        AnyPart savedPart = service.save(part);
        reload();
        return savedPart;
    }

    @Override
    public boolean update(AnyPart part) {
        boolean res = service.update(part);
        reload();
        return res;
    }

    @Override
    public boolean delete(AnyPart part){
        boolean res = service.delete(part);
        reload();
        return res;
    }

    public ObservableList<AnyPart> findAll() {
        return FXCollections.observableArrayList(parts);
    }

    public ObservableList<AnyPart> findAllByText(String text) {
        ObservableList<AnyPart> foundAnyParts = FXCollections.observableArrayList();
        for(AnyPart part : parts){
            String name = part.getName() + part.getSecondName();
            if(name != null && name.contains(text)) {
                foundAnyParts.add(part);
            }
        }
        return foundAnyParts;
    }
    
    // ЧЕРТЕЖИ

//    @Override
//    public ObservableSet<Draft> findDrafts(AnyPart part) {
//        return service.findDrafts(part);
//    }
//
//    @Override
//    public ObservableSet<Draft> addDrafts(AnyPart part, Draft draft) {
//        ObservableSet<Draft> res = service.addDrafts(part, draft);
//        DraftQuickService.reload();
//        reload();
//        return res;
//
//    }
//
//    @Override
//    public ObservableSet<Draft> removeDrafts(AnyPart part, Draft draft) {
//        ObservableSet<Draft> res = service.removeDrafts(part, draft);
//        DraftQuickService.reload();
//        reload();
//        return res;
//    }
}
