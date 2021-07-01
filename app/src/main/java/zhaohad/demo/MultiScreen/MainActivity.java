package zhaohad.demo.MultiScreen;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int REQCODE_OVERLAY_PERMISSION = 1;
    private Button mBtnStartactivity;
    private Button mBtnStartVirtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        mBtnStartactivity = findViewById(R.id.btnStartActivity);
        mBtnStartactivity.setOnClickListener(mOnClickListener);
        mBtnStartVirtual = findViewById(R.id.btnStartVirtual);
        mBtnStartVirtual.setOnClickListener(mOnClickListener);

        PackageManager pm = getPackageManager();
        boolean hasFeature = pm.hasSystemFeature(PackageManager.FEATURE_ACTIVITIES_ON_SECONDARY_DISPLAYS);
        Log.e("hanwei", "Has feature activity on secondary display: " + hasFeature);
        //showDeputyActivity();
    }

    private boolean checkPermission() {
        // SYSTEM_ALERT_WINDOW权限申请
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));//不加会显示所有可能的app
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent, REQCODE_OVERLAY_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQCODE_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                }
            }
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnStartActivity:
                    showDeputyActivity();
                    break;
                case R.id.btnStartVirtual: {
                    Intent i = new Intent(getBaseContext(), VirtualDisplayActivity.class);
                    startActivity(i);
                    break;
                }
            }
        }
    };

    private void showDeputyActivity() {
        DisplayManager dm = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = dm.getDisplays();
        Display display = null;
        if (displays != null) {
            for (Display dp : displays) {
                if (dp.getDisplayId() != 0) {
                    display = dp;
                    break;
                }
            }
        }
        ActivityOptions options = null;
        if (display != null) {
            options = ActivityOptions.makeBasic();
            options.setLaunchDisplayId(display.getDisplayId());
            Log.e("hanwei", "setLaunchDisplayId " + options.getLaunchDisplayId());
        }
        Intent i = new Intent();
        // i.setClassName("zhaohad.glpath.multiscreen", "zhaohad.glpath.multiscreen.MainActivity");
        i.setClass(getBaseContext(), DeputyActivity.class);
        i.setClassName("com.miui.gallery", "com.miui.gallery.activity.HomePageActivity");
        // i.setClassName("com.ss.android.ugc.aweme.lite", "com.ss.android.ugc.aweme.splash.SplashActivity");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (options == null) {
            startActivity(i);
        } else {
            Log.e("hanwei", "options = " + options);
            startActivity(i, options.toBundle());
        }
    }
}