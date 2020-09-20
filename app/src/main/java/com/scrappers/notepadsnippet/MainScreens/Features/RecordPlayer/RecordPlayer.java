package com.scrappers.notepadsnippet.MainScreens.Features.RecordPlayer;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.scrappers.notepadsnippet.MainScreens.Features.Recorder.recorder;
import com.scrappers.notepadsnippet.R;

import androidx.appcompat.app.AppCompatActivity;

import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.deleted;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.handler;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.handlerForRecordDuration;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.playRecord;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.recordingNow;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.stopSimulate;

public class RecordPlayer {
    private View fabView;
    private final AppCompatActivity context;
    private FloatingActionButton playRecordFab;
    private FloatingActionButton pauseRecord;
    private SeekBar mediaSeekBar;
    private TextView mediaDuration;
    private TextView mediaCurrentProgress;

    public RecordPlayer(AppCompatActivity context, View fabView, FloatingActionButton playRecordFab, FloatingActionButton pauseRecord,
                        SeekBar mediaSeekBar, TextView mediaDuration, TextView mediaCurrentProgress ) {
        this.context=context;
        this.fabView=fabView;
        this.playRecordFab=playRecordFab;
        this.pauseRecord=pauseRecord;
        this.mediaSeekBar=mediaSeekBar;
        this.mediaDuration=mediaDuration;
        this.mediaCurrentProgress=mediaCurrentProgress;
    }
    public void setOnCompletedMediaListener(){
        playRecord.setOnCompletionListener(mp -> {
            playRecordFab.setLabelText("Play Record Again");
            Drawable img = context.getResources().getDrawable(R.drawable.play_ico,null);
            playRecordFab.setImageDrawable(img);
            stopSimulate = false;
        });
    }

    public void play(){
        handlerForRecordDuration.removeCallbacksAndMessages(null);
        if (!deleted){
            if ( !recordingNow ){
                try {
                    if ( !stopSimulate ){
                        playRecord.start();
                        addSeekBarListener();
                        showMediaComponents();
                        getDurationOfPlayMedia();
                        Snackbar.make(fabView, "Playing Record", Snackbar.LENGTH_LONG).show();
                        stopSimulate = true;
                        playRecordFab.setLabelText("Pause");
                        Drawable img = context.getResources().getDrawable(R.drawable.pause_ico,null);
                        playRecordFab.setImageDrawable(img);

                    } else {
                        playRecord.pause();
                        Snackbar.make(fabView, "Pausing Record play", Snackbar.LENGTH_SHORT).show();
                        stopSimulate = false;
                        playRecordFab.setLabelText("Play");
                        Drawable img = context.getResources().getDrawable(R.drawable.play_ico,null);
                        playRecordFab.setImageDrawable(img);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Snackbar.make(fabView, "No Record to declare !", Snackbar.LENGTH_LONG).show();
                    new recorder(context,null,pauseRecord,mediaSeekBar,mediaDuration,mediaCurrentProgress).hideMediaComponents();
                }
            }
        } else {
            new recorder(context,null,pauseRecord,mediaSeekBar,mediaDuration,mediaCurrentProgress).hideMediaComponents();
        }
    }
    private void addSeekBarListener() {
        context.runOnUiThread(new Runnable() {
            public void run() {
                if ( playRecord != null ){
                    try {
                        mediaSeekBar.setMax(playRecord.getDuration() / 1000);
                        int currentPosition = playRecord.getCurrentPosition() / 1000;
                        mediaSeekBar.setProgress(currentPosition);
                        //Add SeekBar Listener
                        mediaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                if ( playRecord != null && fromUser ){
                                    playRecord.seekTo(progress * 1000);
                                    getCurrentProgressOfPlayMedia();
                                }
                            }
                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                            }
                        });
                        getCurrentProgressOfPlayMedia();
                    } catch (Exception e) {
                        new recorder(context,null,pauseRecord,mediaSeekBar,mediaDuration,mediaCurrentProgress).hideMediaComponents();
                        e.printStackTrace();
                    }
                }
                handler.postDelayed(this, 1000);
            }

        });
    }

    @SuppressLint("SetTextI18n")
    private void getCurrentProgressOfPlayMedia() {

        //explanations are in todo getDurationOfPlayMedia() method
        long dur = playRecord.getCurrentPosition()/ 1000; //convert it into seconds
        String seconds = String.valueOf((dur % 60)); // get the number of whats before 60 secs
        String minutes = String.valueOf((dur % 3600) / 60); //get the number of whats before 3600 secs(1 hr) & convert it into hrs
        String hours= String.valueOf(dur/3600);//get the hrs & take off points of precision(decimals)

        if ( seconds.length() == 1 ){
            seconds="0"+seconds;
        }
        if(minutes.length() == 1){
            minutes="0"+minutes;
        }
        if(hours.length() == 1){
            hours="0"+hours;
        }
        mediaCurrentProgress.setText(hours + ":" + minutes + ":" + seconds);
    }


    @SuppressLint("SetTextI18n")
    private void getDurationOfPlayMedia() {
        // get mp3 duration
        long dur = playRecord.getDuration();

        //preRequirements:-
        //Quotient Remainder(The quotient remainder theorem) :
        // R=firstNum-[secondNum*QuotientResult] from the main operation: firstNum= (secondNum * Q )+ R ; where 0 ≤ R < B
        // (NB:QuotientResult must be floored to a whole number , so if Q=0.0005 then it would be 0 , if Q=2.5 then Q=2 not 3)
        //A= B * Q + R  is the breakdown of any multiplication operation in our life , but usually R is zero so we denoted idiocy as A=B*Q w/o R
        // 1 ms= 1/1,000 sec
        // 1 ms = (1/1,000 sec) / 60 = 1/60,000 mins
        // 1 ms = (1/60,000 mins) /60 = 1/3600,000 hrs
        //equations formulas
        //-->  unit(second) = (duration % (its limit converted from millisecond (60,000 as its the beginning of mins))  / Conversion constant(1000 for secs )
        //procedure:-
        //beginning by numbers that would produce zero for the seconds(time below seconds)
        //recall dur = 30 ms -> seconds = (30 % 60,000) = remainder=firstNum-[secondNum*QuotientResult]=30-[60,000*0]= 30 (NB:- QuotientResult=0.0005 floored to 0)
        // -> divide by Conversion constant(1000 in this case) = 30/1000 =0.03 fraction still persist -> variable floored & returns zero
        //these operations will go all the way till we reach the 1 real second

        //when we reach 1 sec
        //recall dur = 1000 ms( 1 real second) -> seconds = (1000 % 60,000)= remainder=firstNum-[secondNum*QuotientResult]=1000-[60,000*0]= 1000 (NB:- QuotientResult=0.016666 floored to 0)
        // -> divide by Conversion constant(1000 in this case) = 1000/1000 = 1 now the value is 1 so 1 will be presented in seconds
        //these operations will go all the way till we reach the 2 real seconds


        //when we reach 2 secs
        //recall dur = 2000 ms( 2 real seconds) -> seconds = (2000 % 60,000)= remainder=firstNum-[secondNum*QuotientResult]=2000-[60,000*0]= 2000 (NB:- QuotientResult=0.0333333 floored to 0)
        // -> divide by Conversion constant(1000 in this case) = 2000/1000 = 2 now the value is 2 so 2 will be presented in seconds
        //these operations will go all the way till we reach the 3 real seconds


        //when we reach 22 secs
        //recall dur = 22,000 ms( 22 real seconds) -> seconds = (22,000 % 60,000)= remainder=firstNum-[secondNum*QuotientResult]=22,000-[60,000*0]=22,000  (NB:- QuotientResult=0.3666666 floored to 0)
        // -> divide by Conversion constant(1000 in this case) = 22,000/1000 = 22 now the value is 22 so 22 will be presented in seconds
        //these operations will go all the way till we reach the 60 real seconds

        //when we reach 60 secs
        //recall dur = 60,000 ms( 60 real seconds) -> seconds = (60,000 % 60,000)= remainder=firstNum-[secondNum*QuotientResult]=60,000-[60,000*1]= 0 (NB:- QuotientResult=60,000/60,000 = 1 )
        // -> divide by Conversion constant(1000 in this case) = 0/1000 = 0 now the value is 0 so 0 will be presented in seconds
        //then mins counter starts counting



        //when we reach above 60 secs(in mins)
        //recall dur = 61,000 ms( 61 real seconds) -> seconds = (61,000 % 60,000)= remainder=firstNum-[secondNum*QuotientResult]=61,000-[60,000*1]= 1,000 (NB:- QuotientResult=1.016666 floored to 1 )
        // -> divide by Conversion constant(1000 in this case) = 1,000/1000 = 1 now the value is 1 so 1 will be presented in seconds  as the time now is 01 mins : 01 secs
        //then mins counter continue counting

        //as for mins 61,000 % 3600,000=remainder=firstNum-[secondNum*QuotientResult]=61,000-[3600,000*0]= 61,000


        //when we reach above 60 secs(in mins)
        //recall dur = 70,000 ms( 60 real seconds) -> seconds = (70,000 % 60,000)= remainder=firstNum-[secondNum*QuotientResult]=70,000-[60,000*1]= 10,000 (NB:- QuotientResult=1.16666 floored to 1 )
        // -> divide by Conversion constant(1000 in this case) = 10,000/1000 = 10 now the value is 10 so 10 will be presented in seconds  as the time now is 01 mins : 10 secs
        //then mins counter continue counting

        String seconds = String.valueOf((dur % 60000) / 1000);
        //-->  unit(minutes) = (duration % (its limit converted from millisecond  (3600,000 as its the beginning of hrs))  / Conversion constant(60,000 for mins )
        String minutes = String.valueOf((dur % 3600000) / 60000);
        //-->  unit(hrs) = (duration / 3600,000) to convert the units to hours if present above the value 3600,000 & if its below then it would be decimal & therefore its a partial hour(immature) not fully mature
        String hours= String.valueOf(dur/3600000);

        //or
//        long dur = playRecord.getDuration()/ 1000; //convert it into seconds
//        String seconds = String.valueOf((dur % 60)); // get the number of whats before 60 secs
//        String minutes = String.valueOf((dur % 3600) / 60); //get the number of whats before 3600 secs(1 hr) & convert it into hrs
//        String hours= String.valueOf(dur/3600);//get the hrs & take off points of precision(decimals)

        //or
//        long dur = playRecord.getCurrentPosition();
        //todo dur/1000 to convert ms to seconds before setting its limit (60 seconds)
//        String seconds = String.valueOf(((dur/1000) % 60));//todo get the number that is below a minute & convert it from millisec to sec
        //todo dur/60*1000 to convert ms to minutes before setting its limit (60 minutes)
//        String minutes = String.valueOf(((dur/ 60000) % 60));//todo get the number that is below an hour & convert it from millisec to minutes
//        String hours= String.valueOf(dur/3600000);//todo get the number that is equal to hours & convert it into hrs


        //these conditions will check for the seconds,minutes,hours Strings to see if
        //their length is 1 in letters size(i.e only one number ...i.e numbers below 9 ) --> it will assign "0" to the expression,so for instance it would be "0"+9= 09 in reality
        if ( seconds.length() == 1 ){
            seconds="0"+seconds;
        }
        if(minutes.length() == 1){
            minutes="0"+minutes;
        }
        if(hours.length() == 1){
            hours="0"+hours;
        }
        //printing the numbers up onto the screen using the regular timer format
        mediaDuration.setText(hours+":"+minutes + ":" + seconds);

    }

    private void showMediaComponents() {
        if(mediaDuration.getVisibility()== View.INVISIBLE){
            mediaSeekBar.setVisibility(View.VISIBLE);
            mediaDuration.setVisibility(View.VISIBLE);
            mediaCurrentProgress.setVisibility(View.VISIBLE);
        }
    }
}
