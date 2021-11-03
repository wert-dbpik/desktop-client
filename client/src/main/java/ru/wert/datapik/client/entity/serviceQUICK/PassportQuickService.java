package ru.wert.datapik.client.entity.serviceQUICK;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.client.entity.models.Prefix;
import ru.wert.datapik.client.entity.serviceREST.PassportService;
import ru.wert.datapik.client.entity.service_interfaces.IPassportService;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.client.utils.BLConst.RAZLOZHENO;

public class PassportQuickService implements IPassportService {

    private static PassportQuickService instance;
    private static List<Passport> passports;
    private static PassportService service = PassportService.getInstance();
    public static Passport DEFAULT_FOLDER;

    private PassportQuickService() {
        reload();
    }

    public static PassportQuickService getInstance() {
        if (instance == null)
            return new PassportQuickService();
        return instance;
    }

    public static void reload(){
        while(true) {
            if(service != null) {
                passports = new ArrayList<>(service.findAll());
                break;
            }
        }
    }

  //   ОСНОВНЫЕ

    @Override
    public Passport save(Passport passport) {
        Passport res = service.save(passport);
        reload();
        return res;
    }

    @Override
    public boolean update(Passport passport) {
        boolean res = service.update(passport);
        reload();
        return res;
    }

    @Override
    public boolean delete(Passport passport){
        boolean res = service.delete(passport);
        reload();
        return res;
    }

    //   ПОИСКИ

    public Passport findByName(String name) {
        Passport foundPassport = null;
        for(Passport passport : passports){
            if(passport.getName() != null && passport.getName().equals(name)) {
                foundPassport = passport;
                break;
            }
        }
        return foundPassport;
    }

    public Passport findByPrefixIdAndNumber(Prefix prefix, String number) {

        Passport foundPassport = null;
        for(Passport passport : passports){
            if(passport.getNumber() != null && passport.getNumber().equals(number) && passport.getPrefix().getId().equals(prefix.getId())) {
                foundPassport = passport;
                break;
            }
        }
        return foundPassport;
    }

    @Override
    public List<Passport> findAllByName(String name) {
        List<Passport> foundPassports = new ArrayList<>();
        for(Passport passport : passports){
            if(passport.getName().equals(name))
                foundPassports.add(passport);
        }
        return foundPassports;
    }

    public Passport findById(Long id) {
        Passport foundPassport = null;
        for(Passport passport : passports){
            if(passport.getId().equals(id)) {
                foundPassport = passport;
                break;
            }
        }
        return foundPassport;
    }



    public ObservableList<Passport> findAll() {
        return FXCollections.observableArrayList(passports);
    }

    public ObservableList<Passport> findAllByText(String text) {
        ObservableList<Passport> foundPassports = FXCollections.observableArrayList();
        for(Passport passport : passports){
            String name = passport.getName();
            String decNumber = passport.getPrefix().getName() + "." + passport.getNumber();
            if(name != null && name.contains(text) || decNumber.contains(text)) {
                foundPassports.add(passport);
            }
        }
        return foundPassports;
    }
}
