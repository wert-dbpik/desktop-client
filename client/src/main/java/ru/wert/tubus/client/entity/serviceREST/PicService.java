package ru.wert.tubus.client.entity.serviceREST;

import retrofit2.Call;
import ru.wert.tubus.client.entity.api_interfaces.PicApiInterface;
import ru.wert.tubus.client.entity.models.Pic;
import ru.wert.tubus.client.entity.service_interfaces.IPicService;
import ru.wert.tubus.client.retrofit.RetrofitClient;
import ru.wert.tubus.client.utils.BLlinks;

import java.io.IOException;
import java.util.List;

public class PicService implements IPicService {

    private static PicService instance;
    private PicApiInterface api;

    private PicService() {
        BLlinks.picService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(PicApiInterface.class);
    }

    public PicApiInterface getApi() {
        return api;
    }

    public static PicService getInstance() {
        if (instance == null)
            return new PicService();
        return instance;
    }

    @Override
    public Pic findById(Long id) {
        try {
            Call<Pic> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Pic findByName(String name) {
        //НЕ ИСПОЛЬЗУЕТСЯ
        return null;
    }

    @Override
    public List<Pic> findAll() {
        try {
            Call<List<Pic>> call = api.getAll();
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public List<Pic> findAllByText(String text) {
        //НЕ ИСПОЛЬЗУЕТСЯ
        return null;
    }

    @Override
    public Pic save(Pic entity) {
        try {
            Call<Pic> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public boolean update(Pic entity) {
        //НЕ ИСПОЛЬЗУЕТСЯ
        return false;
    }

    @Override
    public boolean delete(Pic entity) {
        Long id = entity.getId();
        try {
            Call<Void> call = api.deleteById(id);
            if(call.execute().code() == 200)
                return true;
        } catch (IOException e) {

        }
        return false;
    }

}
