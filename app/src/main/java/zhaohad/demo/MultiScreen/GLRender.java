package zhaohad.demo.MultiScreen;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import java.util.Arrays;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRender implements GLSurfaceView.Renderer {
    private Context mContext;
    private float[] mProjectionMat = new float[4 * 4];
    private float[] mModelMat = new float[4 * 4];

    private ActivityPanel mPanel;
    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;

    private DisplayManager mDisplayManager;
    private Handler mHandler = new Handler();

    public GLRender(GLSurfaceView glSurfaceView, Context context) {
        mContext = context;
        mDisplayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        mPanel = new ActivityPanel(mContext);
        mSurfaceTexture = new SurfaceTexture(mPanel.mTableProgram.getTextureId());
        mSurface = new Surface(mSurfaceTexture);

        createVirtual();
        showDeputyActivity();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {

        GLES30.glViewport(0, 0, width, height);
        mWidth = width;
        mHeight = height;

        mInitTs = System.currentTimeMillis();

    }
    private int mWidth;
    private int mHeight;
    private long mInitTs;

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glClearColor(1, 1, 1, 1);

        MatrixHelper.perspectiveM(mProjectionMat, 45, (float) mWidth / (float) mHeight, 1, 10);

        Matrix.setIdentityM(mModelMat, 0);
        Matrix.translateM(mModelMat, 0, 0, 0, -5f);
        // Matrix.rotateM(mModelMat, 0, -60, 1, 0, 0);
        Matrix.rotateM(mModelMat, 0, (System.currentTimeMillis() - mInitTs) / 10, 0, 1, 0);

        float[] tmpMat = new float[4 * 4];
        Matrix.multiplyMM(tmpMat, 0, mProjectionMat, 0, mModelMat, 0);
        System.arraycopy(tmpMat, 0, mProjectionMat, 0, tmpMat.length);

        mPanel.mTableProgram.setUMatrix(mProjectionMat);

        try {
            mSurfaceTexture.updateTexImage();
        } catch (Exception e) {
            Log.e("hanwei", e.toString(), e);
        }
        mPanel.draw();
    }

    private VirtualDisplay mDisplay;

    private void createVirtual() {
        Log.e("hanwei", "createVirtual");
        try {
            /*int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
                    | DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY;*/
            int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
            DisplayMetrics screen = mContext.getResources().getDisplayMetrics();
            Log.e("hanwei", "screen.densityDpi = " + screen.densityDpi + " screen.widthPixels = " + screen.widthPixels + " screen.heightPixels = " + screen.heightPixels);
            mDisplay = mDisplayManager.createVirtualDisplay("activity-vd", screen.widthPixels, screen.heightPixels, screen.densityDpi, mSurface, flags, null, null);
            mSurfaceTexture.setDefaultBufferSize(screen.widthPixels, screen.heightPixels);
            Log.e("hanwei", "DisplayId = " + mDisplay.getDisplay().getDisplayId());
            // showDeputyActivity();
            // Log.e("hanwei", "createVirtual display = " + mDisplay);
        } catch (Exception e) {
            Log.e("hanwei", "", e);
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
        // i.setClassName("com.miui.gallery", "com.miui.gallery.activity.HomePageActivity");
        i.setClassName("com.ss.android.ugc.aweme", "com.ss.android.ugc.aweme.splash.SplashActivity");
        // i.setClass(getBaseContext(), DeputyActivity.class);
        // i.setClassName("com.ss.android.ugc.aweme.lite", "com.ss.android.ugc.aweme.splash.SplashActivity");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        if (options == null) {
            mContext.startActivity(i);
        } else {
            Log.e("hanwei", "options = " + options);
            mContext.startActivity(i, options.toBundle());
        }
    }
}
