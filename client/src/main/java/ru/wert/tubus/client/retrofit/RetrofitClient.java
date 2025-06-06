package ru.wert.tubus.client.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import ru.wert.tubus.client.entity.models.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;


@Slf4j
public class RetrofitClient{
    private static final String TAG = "RetrofitClient";

    public static String baseUrl = "http://localhost:8080";

    private static RetrofitClient mInstance;
    private static Retrofit mRetrofit;
    @Getter
    private static Gson gson;

    public static RetrofitClient getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    /**
     * Приватный конструктор
     */
    private RetrofitClient() {

        gson = GsonConfiguration.createGson();

        //Перехватчик для логгирования запросов и ответов
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //борьба с readTimeout
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .callTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
//                .addInterceptor(interceptor)
                ;


//        Interceptor interceptor1 = chain -> {
//            Request.Builder requestBuilder = chain.request().newBuilder();
//            requestBuilder.header("Content-Type", "application/json");
//            requestBuilder.header("Accept", "application/json");
//            return chain.proceed(requestBuilder.build());
//        };
//
//        OkHttpClient.Builder client1 = new OkHttpClient.Builder()
//                .addInterceptor(interceptor1);

        String ip = AppProperties.getInstance().getIpAddress();
        String port = AppProperties.getInstance().getPort();
        baseUrl = "http://"+ip +":"+ port;
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(new NullOnEmptyConverterFactory()) //Исправляет исключение на null, когда приходит пустое тело
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build()) //борьба с readTimeout
//                .client(client.build()) // логгирование ответа
//                .client(client1.build()) // json forever!
                .build();

    }

    public static void restartWithNewUrl() {
        new RetrofitClient();
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    /**
     * Проверка работы ретрофита
     * Если найдется пользователь с id = 1, то ретрофит и соединение настроены
     */
    public static boolean checkUpConnection(){
        log.debug("checkUpConnection : is starting ...");
        CheckUpConnectionInterface api = RetrofitClient.getInstance().getRetrofit().create(CheckUpConnectionInterface.class);
        try {
            Call<User> call = api.getById(1L);
            call.execute().body();
        } catch (IOException e) {
            return false;
        }
        log.info("checkUpConnection : connection to Data Base is OK");
        return true;
    }

    /**
     * Интерфейс для проверки соединения
     */
    interface CheckUpConnectionInterface{
        @GET("users/id/{id}")
        Call<User> getById(@Path("id") Long id);
    }

}



