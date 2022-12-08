// IPreviewCallback.aidl
package com.zzx.camera;

// Declare any non-default types here with import statements

interface IPreviewCallback {
    void onFrameCallback(out byte[] data, int format);
}
