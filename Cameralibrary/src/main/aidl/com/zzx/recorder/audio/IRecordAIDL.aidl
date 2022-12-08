// IRecordAIDL.aidl
package com.zzx.recorder.audio;

// Declare any non-default types here with import statements

interface IRecordAIDL {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    boolean startRecord();

    boolean stopRecord();

    boolean isRecording();

    void pauseRecord();

    void resumeRecord();

    void cancelRecord();

    int getState();

    void toggleRecord();

    long getDuration();

    long getStartTime();

    long getStopTime();

    String getFilePath();

    void recordImpVideo();

}
