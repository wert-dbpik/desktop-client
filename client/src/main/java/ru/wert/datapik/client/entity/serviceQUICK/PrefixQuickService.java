package ru.wert.datapik.client.entity.serviceQUICK;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.wert.datapik.client.entity.models.Prefix;
import ru.wert.datapik.client.entity.serviceREST.PrefixService;
import ru.wert.datapik.client.entity.service_interfaces.IPrefixService;
import ru.wert.datapik.client.exceptions.ItemIsBusyException;
import ru.wert.datapik.client.utils.BLConst;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.client.utils.BLConst.RAZLOZHENO;

public class PrefixQuickService implements IPrefixService {

    private static PrefixQuickService instance;
    private static List<Prefix> prefixes;
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
                prefixes = new ArrayList<>(service.findAll());
                break;
            }
        }
    }

    public Prefix findByName(String name) {
        Prefix foundPrefix = null;
        for(Prefix prefix : prefixes){
            if(prefix.getName() != null && prefix.getName().equals(name)) {
                foundPrefix = prefix;
                break;
            }
        }
        return foundPrefix;
    }


    public Prefix findById(Long id) {
        Prefix foundPrefix = null;
        for(Prefix prefix : prefixes){
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

    public ObservableList<Prefix> findAll() {
        return FXCollections.observableArrayList(prefixes);
    }

    public ObservableList<Prefix> findAllByText(String text) {
        ObservableList<Prefix> foundPrefixes = FXCollections.observableArrayList();
        for(Prefix prefix : prefixes){
            String name = prefix.getName();
            if((name != null && name.contains(text))) {
                foundPrefixes.add(prefix);
            }
        }
        return foundPrefixes;
    }
}
