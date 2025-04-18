package ru.wert.tubus.client.entity.serviceREST;

import retrofit2.Call;
import ru.wert.tubus.client.entity.api_interfaces.DetailApiInterface;
import ru.wert.tubus.client.entity.models.Detail;
import ru.wert.tubus.client.entity.models.Passport;
import ru.wert.tubus.client.entity.service_interfaces.IDetailService;
import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.client.interfaces.ItemService;
import ru.wert.tubus.client.interfaces.PartItem;
import ru.wert.tubus.client.retrofit.RetrofitClient;
import ru.wert.tubus.client.utils.BLlinks;

import java.io.IOException;
import java.util.List;

public class DetailService implements IDetailService, ItemService<Detail>, PartItem {

    private static DetailService instance;
    private DetailApiInterface api;

    private DetailService() {
        BLlinks.detailService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(DetailApiInterface.class);
    }

    public DetailApiInterface getApi() {
        return api;
    }

    public static DetailService getInstance() {
        if (instance == null)
            instance = new DetailService();
        return instance;
    }

    @Override
    public Detail findById(Long id) {
        try {
            Call<Detail> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Detail findByPassportId(Long id) {
        try {
            Call<Detail> call = api.getByPassportId(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public List<Detail> findAllByFolderId(Long id) {
        try {
            Call<List<Detail>> call = api.getAllByFolderId(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Detail> findAll() {
        try {
            Call<List<Detail>> call = api.getAll();
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Detail> findAllByText(String text) {
        try {
            Call<List<Detail>> call = api.getAllByText(text);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Detail> findAllByDraftId(Long id) {
        try {
            Call<List<Detail>> call = api.getAllByDraftId(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Detail save(Detail entity) {
        System.out.println("сохраняем" );
        try {
            Call<Detail> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(Detail entity) {
        try {
            Call<Void> call = api.update(entity);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Detail entity) {
        Long id = entity.getId();
        try {
            Call<Void> call = api.deleteById(id);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getPartName(Item detail) {
        Passport p = ((Detail)detail).getPassport();
        return p.getPrefix().getName() + "." + p.getNumber() + "-" + ((Detail)detail).getVariant(); //ПИК.745222.123-02
    }
}