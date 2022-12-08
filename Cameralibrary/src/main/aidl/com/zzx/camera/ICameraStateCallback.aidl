// ICameraCallback.aidl
package com.zzx.camera;

// Declare any non-default types here with import statements

interface ICameraStateCallback {

    void onCameraStateChanged(int state, int extraCode);

    void onCurrentState(int state, long time);

    void onCaptureResult(int state, String filePath);

}
