// IAudioRecordAIDL.aidl
package com.zzx.recorder.audio;
import com.zzx.recorder.audio.IAudioRecordStateCallback;
// Declare any non-default types here with import statements

interface IAudioRecordAIDL {

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

    void recordImpAudio();

    void registerRecordStateCallback(IAudioRecordStateCallback callback);

    void unregisterRecordStateCallback(IAudioRecordStateCallback callback);

    void releaseRecordStateCallback();
}
