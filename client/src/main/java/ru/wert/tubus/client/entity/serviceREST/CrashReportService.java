package ru.wert.tubus.client.entity.serviceREST;

import retrofit2.Call;
import ru.wert.tubus.client.entity.api_interfaces.CrashReportApiInterface;
import ru.wert.tubus.client.entity.models.CrashReport;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.client.entity.service_interfaces.ICrashReportService;
import ru.wert.tubus.client.retrofit.RetrofitClient;
import ru.wert.tubus.client.utils.BLlinks;

import java.io.IOException;
import java.util.List;

public class CrashReportService implements ICrashReportService {

    private static CrashReportService instance;
    private CrashReportApiInterface api;

    private CrashReportService() {
        BLlinks.crashReportService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(CrashReportApiInterface.class);
    }

    public CrashReportApiInterface getApi() {
        return api;
    }

    public static CrashReportService getInstance() {
        if (instance == null)
            return new CrashReportService();
        return instance;
    }

    @Override
    public CrashReport findById(Long id) {
        try {
            Call<CrashReport> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CrashReport> findAll() {
        try {
            Call<List<CrashReport>> call = api.getAll();
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CrashReport> findAllByText(String text) {
        return null;
    }

    @Override
    public List<CrashReport> findAllByUser(User user) {
        try {
            Call<List<CrashReport>> call = api.getAllByUserId(user.getId());
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CrashReport> findAllByDevice(String device) {
        try {
            Call<List<CrashReport>> call = api.getAllByDevice(device);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CrashReport save(CrashReport entity) {
        try {
            Call<CrashReport> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(CrashReport entity) {
        try {
            Call<CrashReport> call = api.update(entity);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(CrashReport entity) {
        try {
            Call<Void> call = api.deleteById(entity.getId());
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}