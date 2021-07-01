package zhaohad.demo.MultiScreen;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class VirtualDisplayActivity extends Activity {
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Surface mSurface;
    private DisplayManager mDisplayManager;

    private VirtualHandler mVirtualHandler;
    private VirtualDisplay mDisplay;

    private int mH;
    private int mW;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSurfaceView = new SurfaceView(getBaseContext());
        setContentView(mSurfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(mSurfaceCallback);

        mVirtualHandler = new VirtualHandler();

        mDisplayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void createVirtual() {
        Log.e("hanwei", "createVirtual");
        try {
            /*int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
                    | DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY;*/
            int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
            mDisplay = mDisplayManager.createVirtualDisplay("hanwei-vd", mW, mH, 392, mSurface, flags, mVirtualCallback, mVirtualHandler);
            Log.e("hanwei", "DisplayId = " + mDisplay.getDisplay().getDisplayId());
            showDeputyActivity();
            // Log.e("hanwei", "createVirtual display = " + mDisplay);
        } catch (Exception e) {
            Log.e("hanwei", "", e);
        }
    }

    private SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            mSurface = mSurfaceHolder.getSurface();
            Log.e("hanwei", "surfaceCreated");
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            Log.e("hanwei", "surfaceChanged");
            mW = width;
            mH = height;
            createVirtual();
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            Log.e("hanwei", "surfaceDestroyed");
        }
    };

    private VirtualDisplay.Callback mVirtualCallback = new VirtualDisplay.Callback() {
        @Override
        public void onPaused() {
            super.onPaused();
            Log.e("hanwei", "VirtualDisplay.Callback onPaused");
        }

        @Override
        public void onResumed() {
            super.onResumed();
            Log.e("hanwei", "VirtualDisplay.Callback onResumed");
        }

        @Override
        public void onStopped() {
            super.onStopped();
            Log.e("hanwei", "VirtualDisplay.Callback onStopped");
        }
    };

    private class VirtualHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.e("hanwei", "VirtualHandler handleMessage: " + msg);
        }
    }

    private void showDeputyActivity() {
        ActivityOptions options = null;
        if (mDisplay != null) {
            options = ActivityOptions.makeBasic();
            options.setLaunchDisplayId(mDisplay.getDisplay().getDisplayId());
            Log.e("hanwei", "setLaunchDisplayId " + options.getLaunchDisplayId());
        }
        Intent i = new Intent();
        // i.setClassName("zhaohad.glpath.multiscreen", "zhaohad.glpath.multiscreen.MainActivity");
        i.setClassName("com.miui.gallery", "com.miui.gallery.activity.HomePageActivity");
        // i.setClass(getBaseContext(), DeputyActivity.class);
        // i.setClassName("com.ss.android.ugc.aweme.lite", "com.ss.android.ugc.aweme.splash.SplashActivity");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        if (options == null) {
            startActivity(i);
        } else {
            Log.e("hanwei", "options = " + options);
            startActivity(i, options.toBundle());
        }
    }
}
