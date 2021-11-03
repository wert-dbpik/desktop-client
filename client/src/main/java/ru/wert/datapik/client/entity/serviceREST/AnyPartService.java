package ru.wert.datapik.client.entity.serviceREST;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import retrofit2.Call;
import ru.wert.datapik.client.entity.api_interfaces.AnyPartApiInterface;
import ru.wert.datapik.client.entity.models.AnyPart;
import ru.wert.datapik.client.entity.service_interfaces.IAnyPartService;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.client.retrofit.RetrofitClient;
import ru.wert.datapik.client.utils.BLlinks;
;

import java.io.IOException;
import java.util.List;

public class AnyPartService implements IAnyPartService, ItemService<AnyPart> {

    private static AnyPartService instance;
    private AnyPartApiInterface api;

    private AnyPartService() {
        BLlinks.partService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(AnyPartApiInterface.class);
    }

    public AnyPartApiInterface getApi() {
        return api;
    }

    public static AnyPartService getInstance() {
        if (instance == null)
            return new AnyPartService();
        return instance;
    }

    @Override
    public AnyPart findById(Long id) {
        try {
            Call<AnyPart> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public AnyPart findByName(String name) {
        try {
            Call<AnyPart> call = api.getByName(name);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ObservableList<AnyPart> findAll() {
        try {
            Call<List<AnyPart>> call = api.getAll();
            return FXCollections.observableArrayList(call.execute().body());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ObservableList<AnyPart> findAllByText(String text) {
        try {
            Call<List<AnyPart>> call = api.getAllByText(text);
            return FXCollections.observableArrayList(call.execute().body());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AnyPart save(AnyPart entity) {

        try {
            Call<AnyPart> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(AnyPart entity) {
        try {
            Call<Void> call = api.update(entity);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(AnyPart entity) {
        Long id = entity.getId();
        try {
            Call<Void> call = api.deleteById(id);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Создаем и сохраняем в БД новый элемент, если он еще не существует
     * @param partGroup
     * @param name
     * @return
     */
    public AnyPart createNewPart(Long partGroup, String name, String secondName){
        AnyPart part = findByName(name);
        if(part == null){
            part = new AnyPart(name, secondName, AnyPartGroupService.getInstance().findById(partGroup));
            save(part);
        }
        return part;
    }
    
    //ЧЕРТЕЖИ 

//    @Override
//    public ObservableSet<Draft> findDrafts(AnyPart part) {
//        try {
//            Call<Set<Draft>> call = api.getDraft(part.getId());
//            Set<Draft> res = call.execute().body();
//            if(res != null)
//                return FXCollections.observableSet(res);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @Override
//    public ObservableSet<Draft> addDrafts(AnyPart part, Draft draft) {
//        try {
//            Call<Set<Draft>> call = api.addDraft(part.getId(), draft.getId());
//            Set<Draft> res = call.execute().body();
//            if(res != null)
//                return FXCollections.observableSet(res);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @Override
//    public ObservableSet<Draft> removeDrafts(AnyPart part, Draft draft) {
//        try {
//            Call<Set<Draft>> call = api.removeDraft(part.getId(), draft.getId());
//            Set<Draft> res = call.execute().body();
//            if(res != null)
//                return FXCollections.observableSet(res);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


}
