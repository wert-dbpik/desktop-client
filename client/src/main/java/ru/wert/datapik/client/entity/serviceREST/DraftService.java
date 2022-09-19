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
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.client.entity.service_interfaces.IDraftService;
import ru.wert.datapik.client.retrofit.RetrofitClient;
import ru.wert.datapik.client.utils.BLlinks;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class DraftService implements IDraftService {

    private static DraftService instance;
    private DraftApiInterface api;

    private DraftService() {
        BLlinks.draftService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(DraftApiInterface.class);
    }

    public DraftApiInterface getApi() {
        return api;
    }

    public static DraftService getInstance() {
        if (instance == null)
            return new DraftService();
        return instance;
    }

    //=====================================================================================
    @Override
    public List<String> findDraftsByMask(String folder, String mask){
        try {
            Call<List<String>> call = api.findDraftsByMask(folder, mask);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<String>();
    }

//    @Override
//    public boolean download(String path, String fileName, String ext, String tempDir) {
//        //ext уже с точкой
//        String file = fileName + ext;
//        try {
//            Call<ResponseBody> call = api.download(path, file);
//            Response<ResponseBody> r = call.execute();
//            if (r.isSuccessful()) {
//
////                if (ext.toLowerCase().equals(".pdf")) {
//                    InputStream inputStream = r.body().byteStream();
//                    try (OutputStream outputStream = new FileOutputStream(tempDir + "/" + fileName  + ext)) {
//                        IOUtils.copy(inputStream, outputStream);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
////                }
//                return true;
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        return false;
//    }

/* Вариант при котором используется библиотека PDFbox, на выходе Image
@Override
    public List<Image> download(String path, String fileName, String extension) {
        String file = fileName + extension;
        List<Image> images = new ArrayList<>();

        try {
            Call<ResponseBody> call = api.download(path, file);
            Response<ResponseBody> r = call.execute();
            if (r.isSuccessful()) {
                if (extension.toLowerCase().equals(".pdf")) {
                    try (final PDDocument document = PDDocument.load((r.body()).byteStream())) {
                        PDFRenderer pdfRenderer = new PDFRenderer(document);
                        int pageIndex = 0;
                        for (PDPage page : document.getPages()) {
                            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageIndex++, 100, ImageType.GRAY);
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            ImageIOUtil.writeImage(bufferedImage, "PNG", bos, 100);
                            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                            images.add(image);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    BufferedImage bufferedImage = ImageIO.read((r.body()).byteStream());
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                    ImageIOUtil.writeImage(bufferedImage, "PNG", bos, 100);
                    Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                    images.add(image);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return images;
    }*/

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

    /**
     * Физически удаляет чертеж из БД
     * @param folder String Папка = "drafts"
     * @param fileName String, имя файла = "25.png"
     * @return boolean
     */
    @Override
    public boolean deleteDraftFile(String folder, String fileName) {
        try {
            Call<Void> call = api.deleteDraftFile(folder, fileName);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }



    @Override
    public List<Draft> findAllByFolder(Folder folder) {
        try {
            Call<List<Draft>> call = api.findAllByFolder(folder.getId());
            List<Draft> res = call.execute().body();
            if (res != null)
                return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //========================================================================

    @Override
    public Draft findById(Long id) {
        try {
            Call<Draft> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Draft> findByPassport(Passport passport) {
        Long id = passport.getId();
        try {
            Call<List<Draft>> call = api.getAllByPassportId(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public List<Draft> findAll() {
        try {
            Call<List<Draft>> call = api.getAll();
            return call.execute().body();
        } catch (IOException e) {
            log.error("getAll : не удалось загрузить чертежи с сервера");
        }
        return null;
    }

    @Override
    public List<Draft> findAllByText(String text) {
        try {
            Call<List<Draft>> call = api.getAllByText(text);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Draft save(Draft entity) {
        try {
            Call<Draft> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(Draft entity) {
        try {
            Call<Void> call = api.update(entity);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Draft entity) {
        Long id = entity.getId();
        try {
            Call<Void> call = api.deleteById(id);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ЧЕРТЕЖИ

    @Override
    public ObservableSet<Product> findProducts(Draft draft) {
        try {
            Call<Set<Product>> call = api.getProduct(draft.getId());
            Set<Product> res = call.execute().body();
            if (res != null)
                return FXCollections.observableSet(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ObservableSet<Product> addProduct(Draft draft, Product product) {
        try {
            Call<Set<Product>> call = api.addProduct(draft.getId(), product.getId());
            Set<Product> res = call.execute().body();
            if (res != null)
                return FXCollections.observableSet(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ObservableSet<Product> removeProduct(Draft draft, Product product) {
        try {
            Call<Set<Product>> call = api.removeProduct(draft.getId(), product.getId());
            Set<Product> res = call.execute().body();
            if (res != null)
                return FXCollections.observableSet(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
