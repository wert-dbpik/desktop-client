package ru.wert.tubus.client.entity.serviceQUICK;

import ru.wert.tubus.client.entity.models.AnyPart;
import ru.wert.tubus.client.entity.serviceREST.AnyPartService;
import ru.wert.tubus.client.entity.service_interfaces.IAnyPartService;

import java.util.ArrayList;
import java.util.List;

public class AnyPartQuickService implements IAnyPartService {

    private static AnyPartQuickService instance;
    public static List<AnyPart> LOADED_ANY_PARTS;
    private static final AnyPartService service = AnyPartService.getInstance();


    private AnyPartQuickService() {
        reload();
        
    }

    public static AnyPartQuickService getInstance() {
        if (instance == null)
            instance = new AnyPartQuickService();
        return instance;
    }

    public static void reload(){
        while(true) {
            if(service != null) {
                LOADED_ANY_PARTS = new ArrayList<>(service.findAll());
                break;
            }
        }
    }


    public AnyPart findByName(String name) {
        AnyPart foundAnyPart = null;
        for(AnyPart part : LOADED_ANY_PARTS){
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
        for(AnyPart part : LOADED_ANY_PARTS){
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
        for(AnyPart part : LOADED_ANY_PARTS){
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

    public List<AnyPart> findAll() {
        return LOADED_ANY_PARTS;
    }

    public List<AnyPart> findAllByText(String text) {
        List<AnyPart> foundAnyParts = new ArrayList<>();
        for(AnyPart part : LOADED_ANY_PARTS){
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
