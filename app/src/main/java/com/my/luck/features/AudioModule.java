package com.my.luck.features;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AudioModule {
    private Context context;
    private MediaRecorder recorder;
    private File audioFile;

    public AudioModule(Context context) {
        this.context = context;
    }

    public void startRecording() {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File music = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            audioFile = new File(music, "Audio_" + timestamp + ".3gp");
            
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(audioFile.getAbsolutePath());
            recorder.prepare();
            recorder.start();
            Log.d("AudioModule", "Recording started: " + audioFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e("AudioModule", "Error recording", e);
        }
    }

    public void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
            Log.d("AudioModule", "Recording stopped: " + audioFile.getAbsolutePath());
        }
    }
}