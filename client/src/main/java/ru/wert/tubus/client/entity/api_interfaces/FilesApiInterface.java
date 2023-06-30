package ru.wert.tubus.client.entity.api_interfaces;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface FilesApiInterface {

    @Streaming
    @GET("files/download/{path}/{fileName}")
    Call<ResponseBody> download(@Path("path") String path, @Path("fileName") String fileName);

    @Multipart
    @POST("drafts/upload/{path}")
    Call<Void> upload(@Path("path") String path, @Part MultipartBody.Part file);


}
