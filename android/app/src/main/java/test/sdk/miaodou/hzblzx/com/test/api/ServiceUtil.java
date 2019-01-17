package test.sdk.miaodou.hzblzx.com.test.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by yanglibing on 2017/6/11.
 */

public class ServiceUtil {
    private static final String TAG = "ServiceUtil";

    static Retrofit apiRetrofit;

    /**
     * 获取API服务
     */
    public static ApiService apiService() {
        if (apiRetrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.readTimeout(45, TimeUnit.SECONDS);

            OkHttpClient client = httpClient.build();

            apiRetrofit = new Retrofit.Builder()
                    .baseUrl("http://121.40.204.191:18080/mdserver/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return apiRetrofit.create(ApiService.class);
    }

}
