// IAudioRecordStateCallback.aidl
package com.zzx.recorder.audio;

// Declare any non-default types here with import statements

interface IAudioRecordStateCallback {

    void onRecordStart();

    void onRecordStop(String filePath);

    void onRecordError(int code);

}