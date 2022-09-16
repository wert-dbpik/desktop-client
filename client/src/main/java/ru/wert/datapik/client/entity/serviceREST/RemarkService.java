package ru.wert.datapik.client.entity.serviceREST;

import retrofit2.Call;
import ru.wert.datapik.client.entity.api_interfaces.RemarkApiInterface;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.client.entity.models.Pic;
import ru.wert.datapik.client.entity.models.Remark;
import ru.wert.datapik.client.entity.service_interfaces.IRemarkService;
import ru.wert.datapik.client.retrofit.RetrofitClient;
import ru.wert.datapik.client.utils.BLlinks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class RemarkService implements IRemarkService{

    private static RemarkService instance;
    private RemarkApiInterface api;

    private RemarkService() {
        BLlinks.remarkService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(RemarkApiInterface.class);
    }

    public RemarkApiInterface getApi() {
        return api;
    }

    public static RemarkService getInstance() {
        if (instance == null)
            return new RemarkService();
        return instance;
    }

    @Override
    public Remark findById(Long id) {
        try {
            Call<Remark> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Remark> findAll() {
        try {
            Call<List<Remark>> call = api.getAll();
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Remark> findAllByPassport(Passport passport) {
        try {
            Call<List<Remark>> call = api.getAllByPassportId(passport.getId());
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Pic> getPics(Remark remark) {
        try {
            Call<Set<Pic>> call = api.getPics(remark.getId());
            return new ArrayList<>(Objects.requireNonNull(call.execute().body()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Remark> findAllByText(String text) {
        //НЕ ИСПОЛЬЗУЕТСЯ
        return null;
    }

    @Override
    public Remark save(Remark entity) {
        try {
            Call<Remark> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public boolean update(Remark entity) {
        try {
            Call<Remark> call = api.update(entity);
            return call.execute().isSuccessful();
        } catch (IOException e) {

        }
        return false;
    }

    @Override
    public boolean delete(Remark entity) {
        Long id = entity.getId();
        try {
            Call<Void> call = api.deleteById(id);
            if(call.execute().code() == 200)
                return true;
        } catch (IOException e) {

        }
        return false;
    }

    @Override
    public Remark findByName(String name) {
        //НЕ ИСПОЛЬЗУЕТСЯ
        return null;
    }
}
