package test.sdk.miaodou.hzblzx.com.test;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hzblzx.miaodou.sdk.MiaodouKeyAgent;
import com.hzblzx.miaodou.sdk.core.model.OpenDoorModel;
import com.hzblzx.miaodou.sdk.core.opendoor.OpenDoorCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import test.sdk.miaodou.hzblzx.com.test.api.ServiceUtil;
import test.sdk.miaodou.hzblzx.com.test.api.model.OpenDoorParameter;
import test.sdk.miaodou.hzblzx.com.test.api.model.Result;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private static final String SP_NAME = "data.sp";
    private static final String SP_AGTNUM_KEY = "agt_num";
    private static final String SP_APPKEY_KEY = "app_key";
    private static final String SP_PID_KEY = "pid";

    @BindView(R.id.agtNumText)
    TextView agtNum;

    @BindView(R.id.appKeyText)
    TextView appKey;

    @BindView(R.id.pidText)
    TextView pid;

    @BindView(R.id.openButton)
    Button openButton;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        openButton.setOnClickListener(this);

        // 还原数据
        restoreData();

        // 申请权限
        requestPermissions(MiaodouKeyAgent.NECESSARY_PERMISSION, new PermissionsResultCallback() {
            @Override
            public void onPermissionsResult(boolean success) {
                if (success) {
                    openButton.setEnabled(true);
                    MiaodouKeyAgent.init(getApplicationContext(), appKey.getText().toString());
                } else {
                    alert(R.string.no_permissions);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.openButton) {
            openDoor();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 保存数据
        saveData();
    }

    private void saveData() {
        SharedPreferences sp = this.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(SP_AGTNUM_KEY, agtNum.getText().toString());
        editor.putString(SP_APPKEY_KEY, appKey.getText().toString());
        editor.putString(SP_PID_KEY, pid.getText().toString());

        editor.commit();
    }

    private void restoreData() {
        SharedPreferences sp = this.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        agtNum.setText(sp.getString(SP_AGTNUM_KEY, ""));
        appKey.setText(sp.getString(SP_APPKEY_KEY, ""));
        pid.setText(sp.getString(SP_PID_KEY, ""));
    }

    private OpenDoorParameter toModel() {
        OpenDoorParameter model = new OpenDoorParameter();

        model.setAgt_num(agtNum.getText().toString());
        model.setApp_key(appKey.getText().toString());
        model.setPid(pid.getText().toString());

        return model;
    }
    private boolean checkInput(OpenDoorParameter data) {
        agtNum.setError(null);
        appKey.setError(null);
        pid.setError(null);

        if (data.getAgt_num() == null || "".equals(data.getAgt_num())) {
            agtNum.setError(getString(R.string.input_empty_error));
            return false;
        }
        if (data.getApp_key() == null || "".equals(data.getApp_key())) {
            appKey.setError(getString(R.string.input_empty_error));
            return false;
        }
        if (data.getPid() == null || "".equals(data.getPid())) {
            pid.setError(getString(R.string.input_empty_error));
            return false;
        }

        return true;
    }
    private void openDoor() {
        final OpenDoorParameter data = toModel();
        if (!checkInput(data)) {
            return;
        }

        updateUI(true);
        ServiceUtil.apiService().getData(data.getAgt_num(), data.getApp_key(), data.getPid()).enqueue(new Callback<Result<OpenDoorModel>>() {
            @Override
            public void onResponse(Call<Result<OpenDoorModel>> call, Response<Result<OpenDoorModel>> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "获取数据失败");
                    alert(response.message());
                    updateUI(false);
                    return;
                }

                if (!"0".equals(response.body().getCode())) {
                    Log.d(TAG, "账号不正确");
                    alert(response.body().getMsg());
                    updateUI(false);
                    return;
                }

                Log.d(TAG, "获取数据成功");
                doOpenDoor(response.body().getData(), data.getApp_key());
            }

            @Override
            public void onFailure(Call<Result<OpenDoorModel>> call, Throwable t) {
                alert(t.getMessage());
                t.printStackTrace();
                updateUI(false);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MiaodouKeyAgent.abortOpenDoor();
    }

    private void doOpenDoor(OpenDoorModel model, String appKey) {
        Log.d(TAG, "            ssid: " + model.getSsid());
        Log.d(TAG, "             mac: " + model.getMac());
        Log.d(TAG, "    service uuid: " + model.getServiceUUID());
        Log.d(TAG, "      write uuid: " + model.getWriteUUID());
        Log.d(TAG, "     notify uuid: " + model.getNotifyUUID());
        Log.d(TAG, "     key content: " + model.getKey_content());
        Log.d(TAG, "callback success: " + model.getCallback_success());

        MiaodouKeyAgent.openDoor(this, model, new OpenDoorCallback() {
            @Override
            public void onSuccess() {
                alert("已开门");
                updateUI(false);
            }

            @Override
            public void onFailure(int error) {
                alert(MiaodouKeyAgent.getErrorMsg(error));
                updateUI(false);
            }
        });
    }

    private void updateUI(boolean opening) {
        if (opening) {
            progressBar.setVisibility(View.VISIBLE);
            openButton.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            openButton.setEnabled(true);
        }
    }
}
