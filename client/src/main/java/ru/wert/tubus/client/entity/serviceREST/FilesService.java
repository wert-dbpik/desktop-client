package ru.wert.tubus.client.entity.serviceREST;


import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.apache.pdfbox.io.IOUtils;
import retrofit2.Call;
import retrofit2.Response;
import ru.wert.tubus.client.entity.api_interfaces.FilesApiInterface;
import ru.wert.tubus.client.entity.service_interfaces.IFilesService;
import ru.wert.tubus.client.retrofit.RetrofitClient;
import ru.wert.tubus.client.utils.BLlinks;

import java.io.*;
import java.nio.file.Files;

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
            instance = new FilesService();
        return instance;
    }

    /**
     * Метод загружает файл из БД в указанную директорию и с указанным именем
     * Если newName = null, то имя скачиваемого файла остается прежним, т.е. id.ext
     * @param path, String - из какой директории выкачиваем файл (apk, drafts, excels, normic, pics)
     * @param fileName, String - обычно ID файла
     * @param ext, String - расширение файла (.exe, .pdf, .apk и т.д.)
     * @param destDir, String -  папка назначения (путь до tempDir, своя папка)
     * @param prefix, String - уточнящий префикс для управления ("chat", "remark")
     */
    @Override
    public boolean download(String path, String fileName, String ext, String destDir, String prefix, String newName) {
        //ext уже с точкой
        String file = fileName + ext;
        String destFileName;
        if(newName != null)
            destFileName = newName;
        else
            destFileName = file;
        if(prefix != null) destFileName = prefix + "-" + file;
        try {
            Call<ResponseBody> call = api.download(path, file);
            Response<ResponseBody> r = call.execute();
            if (r.isSuccessful()) {

//                if (ext.toLowerCase().equals(".pdf")) {
                    InputStream inputStream = r.body().byteStream();
                    try (OutputStream outputStream = new FileOutputStream(destDir + "/" + destFileName)) {
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

//    public boolean downloadWithNewName(String path, String fileName, String ext, String destDir, String prefix, String newName) {
//        //ext уже с точкой
//        String file = fileName + ext;
//        try {
//            Call<ResponseBody> call = api.download(path, file);
//            Response<ResponseBody> r = call.execute();
//            if (r.isSuccessful()) {
//                InputStream inputStream = r.body().byteStream();
//                try (OutputStream outputStream = new FileOutputStream(destDir + "/" + newName)) {
//                    IOUtils.copy(inputStream, outputStream);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return true;
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        return false;
//    }

    @Override
    public boolean upload(String fileNameForSaving, String directoryName, File initialFile) throws IOException {
        log.debug("upload : Загружаем в БД чертеж {}", fileNameForSaving);
        byte[] draftBytes = Files.readAllBytes(initialFile.toPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/pdf"), draftBytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", fileNameForSaving, requestBody);
        try {
            Call<Void> call = api.upload(directoryName, body);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


}
