package ru.wert.tubus.client.entity.serviceQUICK;

import ru.wert.tubus.client.entity.models.Prefix;
import ru.wert.tubus.client.entity.serviceREST.PrefixService;
import ru.wert.tubus.client.entity.service_interfaces.IPrefixService;

import java.util.ArrayList;
import java.util.List;

public class PrefixQuickService implements IPrefixService {

    private static PrefixQuickService instance;
    public static List<Prefix> LOADED_PREFIXES;
    private static PrefixService service = PrefixService.getInstance();

    private PrefixQuickService() {
        reload();
    }

    public static PrefixQuickService getInstance() {
        if (instance == null)
            return new PrefixQuickService();
        return instance;
    }

    public static void reload(){
        while(true) {
            if(service != null) {
                LOADED_PREFIXES = new ArrayList<>(service.findAll());
                break;
            }
        }
    }

    public Prefix findByName(String name) {
        Prefix foundPrefix = null;
        for(Prefix prefix : LOADED_PREFIXES){
            if(prefix.getName() != null && prefix.getName().equals(name)) {
                foundPrefix = prefix;
                break;
            }
        }
        return foundPrefix;
    }


    public Prefix findById(Long id) {
        Prefix foundPrefix = null;
        for(Prefix prefix : LOADED_PREFIXES){
            if(prefix.getId().equals(id)) {
                foundPrefix = prefix;
                break;
            }
        }
        return foundPrefix;
    }

    @Override
    public Prefix save(Prefix prefix) {
        Prefix res = service.save(prefix);
        reload();
        return res;
    }

    @Override
    public boolean update(Prefix prefix) {
        boolean res = service.update(prefix);
        reload();
        return res;
    }

    @Override
    public boolean delete(Prefix prefix){
        boolean res = service.delete(prefix);
        reload();
        return res;
    }

    public List<Prefix> findAll() {
        return LOADED_PREFIXES;
    }

    public List<Prefix> findAllByText(String text) {
        List<Prefix> foundPrefixes = new ArrayList<>();
        for(Prefix prefix : LOADED_PREFIXES){
            String name = prefix.getName();
            if((name != null && name.contains(text))) {
                foundPrefixes.add(prefix);
            }
        }
        return foundPrefixes;
    }
}
