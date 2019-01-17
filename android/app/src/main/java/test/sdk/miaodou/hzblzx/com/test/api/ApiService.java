package test.sdk.miaodou.hzblzx.com.test.api;

import com.hzblzx.miaodou.sdk.core.model.OpenDoorModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import test.sdk.miaodou.hzblzx.com.test.api.model.Result;

/**
 * Created by yanglibing on 2017/6/11.
 */

public interface ApiService {
    @GET("service/getData")
    Call<Result<OpenDoorModel>> getData(@Query("agt_num") String agt_num, @Query("app_key") String app_key, @Query("pid") String pid);
}
