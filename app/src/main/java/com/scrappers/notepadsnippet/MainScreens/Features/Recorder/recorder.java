package com.scrappers.notepadsnippet.MainScreens.Features.Recorder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.scrappers.notepadsnippet.MainScreens.Features.TextNotes.saveNote;
import com.scrappers.notepadsnippet.R;

import java.io.IOException;
import java.util.Objects;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import static android.os.Environment.getExternalStorageDirectory;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.PAUSE_VALUE;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.VALUE_OF_RECORD;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.deleted;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.handleRecordText_Notification;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.handlerForRecordDuration;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.high_quality;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.noteBox;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.path;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.playRecord;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.recordHours;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.recordHoursString;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.recordMinutes;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.recordMinutesString;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.recordSeconds;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.recordSecondsString;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.record_duration_txt;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.recordingNow;
import static com.scrappers.notepadsnippet.MainScreens.MainActivity.fileName;
import static com.scrappers.notepadsnippet.MainScreens.MainActivity.isEditEntry;
import static com.scrappers.notepadsnippet.MainScreens.MainActivity.notesNumber;
import static com.scrappers.notepadsnippet.MainScreens.MainActivity.recordName;

public class recorder {
    /**
     * Attributes
     */
    private final AppCompatActivity context;
    private View fabView;
    private static MediaRecorder mediaRecorder;
    private FloatingActionButton pauseRecord;
    private SeekBar mediaSeekBar;
    private TextView mediaDuration;
    private TextView mediaCurrentProgress;
    private AlertDialog saveDialog;

    /**
     *
     * Records a record Note for this specific Note
     * @param context Holder class context
     * @param fabView Listener Holder Class View
     * @param pauseRecord pause Record fab btn
     * @param mediaSeekBar media seekBar widget
     * @param mediaDuration TextView holding the media duration
     * @param mediaCurrentProgress TextView holding the current duration
     */
    public recorder(AppCompatActivity context, View fabView
            , FloatingActionButton pauseRecord, SeekBar mediaSeekBar, TextView mediaDuration,
                    TextView mediaCurrentProgress) {
        this.context=context;
        this.fabView=fabView;
        this.pauseRecord=pauseRecord;
        this.mediaSeekBar=mediaSeekBar;
        this.mediaDuration=mediaDuration;
        this.mediaCurrentProgress=mediaCurrentProgress;
    }

    /**
     * Pauses the current recording process
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void pause(){
        if(mediaRecorder != null){
            try {
                switch (PAUSE_VALUE) {
                    case 1:
                        mediaRecorder.pause();
                        pauseRecord.setLabelText("Resume");
                        Snackbar.make(fabView, "Pause", Snackbar.LENGTH_LONG).show();
                        handlerForRecordDuration.removeCallbacksAndMessages(null);
                        pauseRecord.setImageDrawable(context.getResources().getDrawable(R.drawable.play_ico, null));
                        PAUSE_VALUE = 2;
                        break;
                    case 2:
                        mediaRecorder.resume();
                        pauseRecord.setLabelText("Pause");
                        Snackbar.make(fabView, "Resuming", Snackbar.LENGTH_LONG).show();
                        Drawable imgPause = context.getResources().getDrawable(R.drawable.pause_ico, null);
                        pauseRecord.setImageDrawable(imgPause);
                        PAUSE_VALUE = 1;
                        record_duration_txt.setText(handleRecordText_Notification);
                        Handle_Record_Duration();
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Snackbar.make(fabView, "No Recordings Running", Snackbar.LENGTH_LONG).show();
            PAUSE_VALUE = 2;
        }
    }

    /**
     * starts recording a note
     */
    public void record(){
        try {
            FloatingActionButton recordStop = context.findViewById(R.id.RecordBtn);
            recordingNow=true;
            deleted=false;
            switch (VALUE_OF_RECORD) {
                case 1:
                    DialogBoxWithRecordSave();
                    hideMediaComponents();
                    VALUE_OF_RECORD = 2;
                    recordStop.setLabelText("Stop");
                    recordStop.setImageDrawable(context.getDrawable(R.drawable.stop_recording));
                    FloatingActionButton pauseRecord = context.findViewById(R.id.PauseRecord);
                    pauseRecord.setEnabled(true);
                    FloatingActionButton DeleteRecord = context.findViewById(R.id.deleteRecord);
                    DeleteRecord.setEnabled(false);
                    FloatingActionButton playRecord = context.findViewById(R.id.playMedia);
                    playRecord.setEnabled(false);
                    break;
                case 2:
                    stopRecording();
                    RefreshRecordingValues();
                    handlerForRecordDuration.removeCallbacksAndMessages(null);
                    hideRecordComponent();
                    VALUE_OF_RECORD = 1;
                    PAUSE_VALUE=1;
                    recordStop.setLabelText("Record");
                    recordStop.setImageDrawable(context.getDrawable(R.drawable.recordbtn));
                    pauseRecord=context.findViewById(R.id.PauseRecord);
                    pauseRecord.setEnabled(false);
                    DeleteRecord=context.findViewById(R.id.deleteRecord);
                    DeleteRecord.setEnabled(true);
                    playRecord=context.findViewById(R.id.playMedia);
                    playRecord.setEnabled(true);
                    break;
            }
        } catch (Exception error) {
            error.printStackTrace();
            Toast.makeText(context, "Error Recording Lecture Check MIC Permissions", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * stops recording & shows up media components preparing for playing a recorded media
     */
    private void stopRecording() {
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            recordingNow=false;
            PAUSE_VALUE = 2;
            DefineExistingRecording(recordName);
            Drawable imgPause = context.getResources().getDrawable(R.drawable.pause_ico,null);
            pauseRecord.setImageDrawable(imgPause);
            if ( playRecord != null ){
                mediaDuration.setVisibility(View.VISIBLE);
                mediaCurrentProgress.setVisibility(View.VISIBLE);
                /*
                 * get Duration of the recorded media
                 */
                String out = "";
                long dur = playRecord.getDuration();
                String seconds = String.valueOf((dur % 60000) / 1000);
                String minutes = String.valueOf(dur / 60000);
                out = minutes + ":" + seconds;
                if ( seconds.length() == 1 ){
                    mediaDuration.setText(String.format("0%s:0%s", minutes, seconds));
                } else {
                    mediaDuration.setText(String.format("0%s:%s", minutes, seconds));
                }
                /*
                 * shows up media components
                 */
                if ( mediaSeekBar.getVisibility() == View.INVISIBLE ){
                    mediaSeekBar.setVisibility(View.VISIBLE);
                    //use animation w/ seekBar
                    Animation anim = AnimationUtils.loadAnimation(context, R.anim.scale);
                    mediaSeekBar.startAnimation(anim);
                }
            }else{
                hideMediaComponents();
            }
        } catch (IllegalStateException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * defines an existing record
     * @param recordName record file directory Fetched from {@link com.scrappers.notepadsnippet.MainScreens.MainActivity}
     */
    private void DefineExistingRecording(String recordName) {
        playRecord = new MediaPlayer();
        try {
            playRecord.setDataSource(recordName);
            playRecord.prepare();
            Toast.makeText(context, "Record Found For this Note", Toast.LENGTH_LONG).show();
            PAUSE_VALUE = 1;
            FloatingActionButton deleteRecordBtn=context.findViewById(R.id.deleteRecord);
            deleteRecordBtn.setEnabled(true);
            FloatingActionButton pauseMediaBtn=context.findViewById(R.id.playMedia);
            pauseMediaBtn.setEnabled(true);
            FloatingActionButton pauseRecordBtn=context.findViewById(R.id.PauseRecord);
            pauseRecordBtn.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "No Recordings for this Note,Yet", Toast.LENGTH_LONG).show();
            FloatingActionButton deleteRecordBtn=context.findViewById(R.id.deleteRecord);
            deleteRecordBtn.setEnabled(false);
            FloatingActionButton pauseMediaBtn=context.findViewById(R.id.playMedia);
            pauseMediaBtn.setEnabled(false);
            FloatingActionButton pauseRecordBtn=context.findViewById(R.id.PauseRecord);
            pauseRecordBtn.setEnabled(false);
        }
    }

    /**
     * creates a dialog to save both the note file & record file by the same name
     */
    private void DialogBoxWithRecordSave() {
        if ( isEditEntry == 0 ){
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(context);
            final LayoutInflater inflater = context.getLayoutInflater();
            @SuppressLint("InflateParams")
            final View layout = inflater.inflate(R.layout.dialogboxlayout, null);
            Button okBtn = layout.findViewById(R.id.okBtn);
            Button cancelBtn = layout.findViewById(R.id.CancelBtn);
            builder.setView(layout);

            builder.setCancelable(false);
            saveDialog = builder.create();
            Objects.requireNonNull(saveDialog.getWindow()).setGravity(Gravity.CENTER);
            saveDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation2;
            saveDialog.show();
            // Dialog will have "Make a selection" as the title

            okBtn.setOnClickListener(v -> {
                EditText saveNoteName = layout.findViewById(R.id.nameofsavefile);
                String NewFileName = saveNoteName.getText().toString();

                if ( !NewFileName.isEmpty() ){
                    path = context.getFilesDir() + "/SPRecordings/Notes/" + NewFileName;
                    new saveNote(context).writeTextFiles(path, noteBox.getHtml()==null?"":noteBox.getHtml());
                    recordName = getExternalStorageDirectory() + "/SPRecordings/records/" + NewFileName + ".mp3";
                    DefineNewRecord(recordName);
                    isEditEntry = 1;
                    saveDialog.dismiss();
                } else {
                    path = context.getFilesDir() + "/SPRecordings/Notes/" +"Note "+(notesNumber);
                    new saveNote(context).writeTextFiles(path, noteBox.getHtml()==null?"":noteBox.getHtml());
                    recordName = getExternalStorageDirectory() + "/SPRecordings/records/" + "Note "+(notesNumber)+".mp3";
                    DefineNewRecord(recordName);
                    isEditEntry =1;
                    saveDialog.dismiss();
                }
            });

            cancelBtn.setOnClickListener(v -> {
                VALUE_OF_RECORD = 1;
                FloatingActionButton recordStop=context.findViewById(R.id.RecordBtn);
                recordStop.setLabelText("Record");
                recordStop.setImageDrawable(context.getDrawable(R.drawable.recordbtn));
                saveDialog.dismiss();
            });
        } else {
            new saveNote(context).writeTextFiles(context.getFilesDir() + "/SPRecordings/Notes/" + fileName, noteBox.getHtml()==null?"":noteBox.getHtml());
            DefineNewRecord(recordName);
        }
    }

    /**
     * defines a new Record with specific file name & dir
     * @param recordLocalName name of file & directory
     */
    private void DefineNewRecord(String recordLocalName) {
        try {
            mediaRecorder = null;
            mediaRecorder = new MediaRecorder();
            mediaRecorder.reset();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //don't use AAC ADTS & AMR_WB & AMR_NB & anything rather than THREE_GPP  w/ any audioEncoder
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mediaRecorder.setOutputFile(recordLocalName);
            //assign the boolean high_quality to the key high_quality & get the stored boolean which is true as default
            high_quality=context.getSharedPreferences("recordstate", Context.MODE_PRIVATE)
                    .getBoolean("high_quality", true);
            //assign the value of the record quality according to the high_quality boolean value HE_ACC is the high Quality while AMR_NB is the low quality
            mediaRecorder.setAudioEncoder(high_quality ? MediaRecorder.AudioEncoder.HE_AAC : MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();
            //to refresh values
            RefreshRecordingValues();
            Handle_Record_Duration();
            Toast.makeText(context, "Start Recording on", Toast.LENGTH_LONG).show();
        } catch (IllegalStateException | IOException error) {
            error.printStackTrace();
            Toast.makeText(context, "Error Mic is Busy", Toast.LENGTH_LONG).show();
            VALUE_OF_RECORD = 1;
        }

    }

    /**
     * Handler thread for record progress text & duration
     */
    private void Handle_Record_Duration(){
        record_duration_txt=context.findViewById(R.id.recordduration);
        showRecordComponent();
        //Thread runs on the UI
        context.runOnUiThread(new Runnable() {
            public void run() {
                if ( mediaRecorder != null ){
                    try {
                        recordSeconds++;
                        recordSecondsString = String.valueOf((recordSeconds % 60));
                        //-->  unit(minutes) = (duration % (its limit converted from seconds  (3600 as its the beginning of hrs))  / Conversion constant(60 for minutes )
                        recordMinutesString = String.valueOf((recordSeconds % 3600) / 60);
                        //-->  unit(hrs) = (duration / 3600) to convert the units(seconds) to hours if present above the value 3600 & if its below then it would be decimal
                        // & therefore its a partial hour(immature) not fully mature so hours counter would be ZERO IN THIS CASE
                        recordHoursString= String.valueOf(recordSeconds/3600);

                        if(recordSecondsString.length() == 1){
                            recordSecondsString="0"+recordSecondsString;
                        }
                        if(recordMinutesString.length() == 1){
                            recordMinutesString="0"+recordMinutesString;
                        }
                        if(recordHoursString.length() == 1){
                            recordHoursString="0"+recordHoursString;
                        }
                        handleRecordText_Notification = recordHoursString + ":" + recordMinutesString + ":" + recordSecondsString;
                        record_duration_txt.setText(handleRecordText_Notification);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                handlerForRecordDuration.postDelayed(this, 1000);
            }

        });
    }

    /**
     * Restore duration & progress text to defaults
     */
    private void RefreshRecordingValues(){
        recordSeconds=0;
        recordHours=0;
        recordMinutes=0;
        handleRecordText_Notification="";
    }


    private void showRecordComponent(){
        if(record_duration_txt.getVisibility()==View.INVISIBLE){
            record_duration_txt.setVisibility(View.VISIBLE);
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
            record_duration_txt.startAnimation(anim);
        }
    }
    public void hideMediaComponents() {
        if(mediaDuration.getVisibility()==View.VISIBLE){
            mediaSeekBar.setVisibility(View.INVISIBLE);
            mediaDuration.setVisibility(View.INVISIBLE);
            mediaCurrentProgress.setVisibility(View.INVISIBLE);
        }
    }
    private void hideRecordComponent(){
        if(record_duration_txt.getVisibility()==View.VISIBLE){
            record_duration_txt.setVisibility(View.INVISIBLE);
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.shake);
            record_duration_txt.startAnimation(anim);
        }
    }
}
