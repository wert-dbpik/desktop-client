package ru.wert.datapik.client.entity.serviceREST;


import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.apache.pdfbox.io.IOUtils;
import retrofit2.Call;
import retrofit2.Response;
import ru.wert.datapik.client.entity.api_interfaces.DraftApiInterface;
import ru.wert.datapik.client.entity.api_interfaces.FilesApiInterface;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.client.entity.service_interfaces.IDraftService;
import ru.wert.datapik.client.entity.service_interfaces.IFilesService;
import ru.wert.datapik.client.retrofit.RetrofitClient;
import ru.wert.datapik.client.utils.BLlinks;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class FilesService implements IFilesService {

    private static FilesService instance;
    private FilesApiInterface api;

    private FilesService() {
        BLlinks.filesService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(FilesApiInterface.class);
    }

    public FilesApiInterface getApi() {
        return api;
    }

    public static FilesService getInstance() {
        if (instance == null)
            return new FilesService();
        return instance;
    }


    @Override
    public boolean download(String path, String fileName, String ext, String tempDir) {
        //ext уже с точкой
        String file = fileName + ext;
        try {
            Call<ResponseBody> call = api.download(path, file);
            Response<ResponseBody> r = call.execute();
            if (r.isSuccessful()) {

//                if (ext.toLowerCase().equals(".pdf")) {
                    InputStream inputStream = r.body().byteStream();
                    try (OutputStream outputStream = new FileOutputStream(tempDir + "/" + fileName  + ext)) {
                        IOUtils.copy(inputStream, outputStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                }
                return true;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean upload(String fileNewName, String folder, File draft) throws IOException {
        log.debug("upload : Загружаем в БД чертеж {}", fileNewName);
        byte[] draftBytes = Files.readAllBytes(draft.toPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/pdf"), draftBytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", fileNewName, requestBody);
        try {
            Call<Void> call = api.upload(folder, body);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


}
