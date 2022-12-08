// IRecordCallback.aidl
package com.zzx.camera;

// Declare any non-default types here with import statements

interface IRecordStateCallback {

    void onRecordStateChanged(int newState, String value, int extraCode);

}
