package test.sdk.miaodou.hzblzx.com.test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_PERMISSIONS_CODE = 1;

    public void showToast(int messageId) {
        showToast(getString(messageId));
    }
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void alert(int messageId) {
        alert(getString(messageId));
    }
    public void alert(String message) {
        alert(message, null);
    }

    public void alert(int messageId, DialogInterface.OnDismissListener onDismissListener) {
        alert(getString(messageId), onDismissListener);
    }
    public void alert(String message, DialogInterface.OnDismissListener onDismissListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name)
                .setMessage(message)
                .setNegativeButton(R.string.close, null);
        if (onDismissListener != null) {
            builder.setOnDismissListener(onDismissListener);
        }
        builder.show();
    }


    PermissionsResultCallback permissionsResultCallback;
    /**
     * 申请权限
     * @param permissions 要申请的权限列表
     */
    public void requestPermissions(String[] permissions, PermissionsResultCallback callback) {
        List<String> list = new ArrayList<>();
        for (String p: permissions) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                list.add(p);
            }
        }

        if (list.size() <= 0) {
            Log.d(TAG, "全部权限已取得");
            callback.onPermissionsResult(true);
            return;
        }

        // 获取
        permissionsResultCallback = callback;
        ActivityCompat.requestPermissions(this, list.toArray(new String[list.size()]), REQUEST_PERMISSIONS_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != REQUEST_PERMISSIONS_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (permissionsResultCallback == null) {
            Log.d(TAG, "未知回调");
            return;
        }

        boolean isSuccess = true;
        for(int r:grantResults) {
            if (r != PackageManager.PERMISSION_GRANTED) {
                isSuccess = false;
                break;
            }
        }

        permissionsResultCallback.onPermissionsResult(isSuccess);
        permissionsResultCallback = null;
    }

    /**
     * 权限检查结果
     */
    public interface PermissionsResultCallback {
        void onPermissionsResult(boolean success);
    }
}
