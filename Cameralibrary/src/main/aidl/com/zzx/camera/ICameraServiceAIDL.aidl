// ICameraServiceAIDL.aidl
package com.zzx.camera;
import com.zzx.camera.IRecordStateCallback;
import com.zzx.camera.ICameraStateCallback;
import com.zzx.camera.IFrameRenderCallback;
import com.zzx.camera.IPreviewCallback;
import android.view.Surface;

// Declare any non-default types here with import statements

interface ICameraServiceAIDL {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void startRecord(boolean needResult, boolean oneShot);

    void stopRecord();

    void zoomUp();

    void zoomDown();

    int getZoomMax();

    void zoomLevel(int level);

    boolean flashOn();

    boolean flashOff();

    void takePicture(boolean needResult, boolean oneShot);

    void takeBurstPicture(boolean needResult, int burstCount);

    void setPreviewParams(int width, int height, int format);

    boolean isRecording();

    boolean isUIRecording();

    boolean isPreRecordEnabled();

    long getRecordStartTime();

    long getRecordStopTime();

    void hideControlView();

    void showControlView();

    void showAt(int x, int y, int width, int height);

    void switchCamera();

    int getState();

    boolean isCapturing();

    void registerCameraStateCallback(ICameraStateCallback cameraStateCallback);

    void unregisterCameraStateCallback(ICameraStateCallback cameraStateCallback);


    void registerRecordStateCallback(IRecordStateCallback recordCallback);

    void unregisterRecordStateCallback(IRecordStateCallback recordCallback);


    /*void registerFrameRenderCallback(IFrameRenderCallback renderCallback, in Surface surface);

    void unregisterFrameRenderCallback(IFrameRenderCallback renderCallback);*/

    void registerPreviewCallback(IPreviewCallback renderCallback);

    void unregisterPreviewCallback(IPreviewCallback renderCallback);


    int registerPreviewSurface(in Surface surface, int surfaceWidth, int surfaceHeight, IFrameRenderCallback renderCallback, boolean surfaceNeedRelease);

    int unregisterPreviewSurface(in Surface surface);

    boolean isSurfaceRegistered(in Surface surface);

    void focusOnPoint(int x, int y, int cameraViewWidth, int cameraViewHeight);

//    void setSurfaceSize(int surfaceWidth, int surfaceHeight);

}
