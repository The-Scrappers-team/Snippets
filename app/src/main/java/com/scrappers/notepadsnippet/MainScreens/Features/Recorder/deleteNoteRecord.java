package com.scrappers.notepadsnippet.MainScreens.Features.Recorder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.scrappers.notepadsnippet.R;

import java.io.File;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.deleted;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.record_duration_txt;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.stopSimulate;
import static com.scrappers.notepadsnippet.MainScreens.MainActivity.recordName;

public class deleteNoteRecord {
    /**
     * attributes
     */
    private final AppCompatActivity context;
    private MediaPlayer currentPlayingRecord;
    private SeekBar mediaSeekBar;
    private TextView duration;
    private TextView currentProgress;

    /**
     * Deletes a Note Record
     * @param context the editPaneActivity context
     * @param currentPlayingRecord the RecordPlayer that's assigned to play the record
     * @param mediaSeekBar the RecordPlayer seekBar
     * @param duration the duration of the seekBar
     * @param currentProgress the currentProgress of the seekBar
     * @apiNote these parameters are only used when the user was playing his/her record then he/she deletes it,otherwise nothing applies except the record being deleted
     */
    public deleteNoteRecord(AppCompatActivity context, MediaPlayer currentPlayingRecord,
                            SeekBar mediaSeekBar, TextView duration,TextView currentProgress) {
        this.context=context;
        this.currentPlayingRecord=currentPlayingRecord;
        this.mediaSeekBar=mediaSeekBar;
        this.duration=duration;
        this.currentProgress=currentProgress;
    }

    /**
     * deletes a current Note Record & dismiss the media Components ( mediaSeekBar & duration )
     */
    public void deleteRecord(){
        try {
            AlertDialog.Builder AB=new AlertDialog.Builder(context);
            @SuppressLint("InflateParams")
            View view=context.getLayoutInflater().inflate(R.layout.custom_delete_record,null);
            AB.setView(view);

            final AlertDialog dialogBox=AB.create();
            final Button delete=view.findViewById(R.id.deleteBtn);
            final Button cancel=view.findViewById(R.id.cancelBtn);
            Objects.requireNonNull( dialogBox.getWindow() ).getAttributes().windowAnimations=R.style.DialogAnimation3;
            dialogBox.show();

            delete.setOnClickListener(v -> {
                try {
                    File recordFile = new File(recordName);
                    if(recordFile.exists()){
                        deleted=recordFile.delete();
                        stopSimulate=!deleted;
                        //because there might be a malicious fail to hide those components if they aren't really present ,so we can catch those errors seperatly to prevent failure of deletion
                        currentPlayingRecord.stop();
                        currentPlayingRecord.release();
                        hideMediaComponents();
                        hideRecordComponent();
                        Snackbar.make(v,  " Record is deleted Successfully", Snackbar.LENGTH_SHORT).show();

                        FloatingActionButton pauseRecord=context.findViewById(R.id.PauseRecord);
                        pauseRecord.setEnabled(false);

                        FloatingActionButton DeleteRecord=context.findViewById(R.id.deleteRecord);
                        DeleteRecord.setEnabled(false);

                        FloatingActionButton playRecord=context.findViewById(R.id.playMedia);
                        playRecord.setEnabled(false);

                    }
                    Dismiss_Dialog_Box(dialogBox);
                }catch (NullPointerException | IllegalStateException e){
                    Snackbar.make(v,"No Record found", Snackbar.LENGTH_SHORT).show();
                    Dismiss_Dialog_Box(dialogBox);
                }
            });
            cancel.setOnClickListener(v -> dialogBox.dismiss());
        }catch (Exception error){
            error.printStackTrace();
        }
    }

    /**
     * dismisses an AlertDialog after 1000 ms or 1 sec
     * @param dialog the AlertDialog to dismiss
     */
    private void Dismiss_Dialog_Box(final AlertDialog dialog){
        new CountDownTimer(1000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
            }
        }.start();
    }

    /**
     * Hides media components
     */
    private void hideMediaComponents() {
        if(mediaSeekBar.getVisibility()==View.VISIBLE){
            mediaSeekBar.setVisibility(View.INVISIBLE);
            duration.setVisibility(View.INVISIBLE);
            currentProgress.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * hides recording components
     */
    private void hideRecordComponent(){
        if(record_duration_txt.getVisibility()==View.VISIBLE){
            record_duration_txt.setVisibility(View.INVISIBLE);
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.shake);
            record_duration_txt.startAnimation(anim);
        }
    }
}
