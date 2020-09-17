package com.scrappers.notepadsnippet.MainScreens;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.scrappers.notepadsnippet.MainScreens.Features.ColorPanelSetup.ColorPanelSetUp;
import com.scrappers.notepadsnippet.MainScreens.Features.NavBar.bottomNavBar;
import com.scrappers.notepadsnippet.MainScreens.Features.RecordPlayer.RecordPlayer;
import com.scrappers.notepadsnippet.MainScreens.Features.Recorder.deleteNoteRecord;
import com.scrappers.notepadsnippet.MainScreens.Features.Recorder.recorder;
import com.scrappers.notepadsnippet.MainScreens.Features.TextNotes.saveNote;
import com.scrappers.notepadsnippet.MainScreens.Features.TextNotes.shareNote;
import com.scrappers.notepadsnippet.MainScreens.Features.ToolTipTutorial.ToolTip;
import com.scrappers.notepadsnippet.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import io.github.douglasjunior.androidSimpleTooltip.ArrowDrawable;
import jp.wasabeef.richeditor.RichEditor;

import static android.os.Environment.getExternalStorageDirectory;
import static android.view.View.TEXT_DIRECTION_LOCALE;
import static com.scrappers.notepadsnippet.FeaturesShowUp.SliderActivity.firstStart;
import static com.scrappers.notepadsnippet.MainScreens.MainActivity.DISABLE_FINGERPRINT;
import static com.scrappers.notepadsnippet.MainScreens.MainActivity.Theme;
import static com.scrappers.notepadsnippet.MainScreens.MainActivity.finalOutText;
import static com.scrappers.notepadsnippet.MainScreens.MainActivity.isEditEntry;
import static com.scrappers.notepadsnippet.MainScreens.MainActivity.recordName;




public  class EditPaneActivity extends AppCompatActivity {


    private static final int ACCESS_MIC_PERMISSION = 11;
    private static final int INSERT_IMAGE_ACTION =2020 ;
    private static final int TEXT_FILE_ACTION =3030 ;
    private static final int PLAY_RECORD = 4040;
    //Instances(Components Objects)
    public static RichEditor noteBox;
    private TextView mediaDuration;
    private TextView mediaCurrentProgress;
    private FloatingActionButton playRecordFab;
    private FloatingActionButton pauseRecordingFab;
    private SeekBar mediaSeekBar;

    //tts Engine attributes
    private static MediaRecorder mediaRecorder;
    public static MediaPlayer playRecord;

    //Reference Points
    public static int SPEAK_STATE = 0;
    public static int VALUE_OF_RECORD = 1;
    public static int PAUSE_VALUE = 1;

    public static boolean stopSimulate = false;

    public static String NewFileName;
    public static String path;
    public static boolean recordingNow = false;

    //..............................................................................................
    public static String handleRecordText_Notification = null;
    public static int recordSeconds = 0;
    public static int recordMinutes = 0;
    public static int recordHours = 0;
    public static Handler handlerForRecordDuration = new Handler();
    @SuppressLint("StaticFieldLeak")
    public static TextView record_duration_txt;
    public static String recordHoursString;
    public static String recordMinutesString;
    public static String recordSecondsString;
    //Handle the Progression of the SeekBar in a runOnUiThread >>>
    public static final Handler handler = new Handler();
    public static boolean btnBackPressed = false;


    public static boolean deleted=false;

    private int fontSize =4;


    public static String COLOR_PANEL_FOR="";



    @SuppressLint("StaticFieldLeak")
    public static FrameLayout frameLayout;
    public static boolean high_quality;
    @SuppressLint("StaticFieldLeak")
    public static FrameLayout ocrCameraFragmentLayout;
    private View playRecordView;




    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //read theme & Apply it
        ReadTheme();
        super.onCreate(savedInstanceState);
        //set layout view
        setContentView(R.layout.activity_edit_pane);
        AppCompatActivity appCompatActivity=this;
        //Disable fingerprint assigned to true to disable it on this activity exit
        DISABLE_FINGERPRINT=true;

        frameLayout = findViewById(R.id.colorPanelChooser);

        playRecordFab = findViewById(R.id.playMedia);
        mediaSeekBar = findViewById(R.id.MediaSeekBar);
        mediaDuration = findViewById(R.id.Duration);
        mediaCurrentProgress = findViewById(R.id.CurrentProgress);

        FloatingActionButton openNaveBarBtn=findViewById(R.id.openNavBarBtn);
        openNaveBarBtn.setOnClickListener(v -> new bottomNavBar(this).openNavBar());

        FloatingActionButton saveFab=findViewById(R.id.saveBtn);
        saveFab.setOnClickListener(click->new saveNote(this).DialogBoxWithoutRecordSave());

        FloatingActionButton shareFab=findViewById(R.id.shareBtn);
        shareFab.setOnClickListener(click->new shareNote(this).share());

        FloatingActionButton deleteRecordFab=findViewById(R.id.deleteRecord);
        deleteRecordFab.setOnClickListener(click->new deleteNoteRecord(this,playRecord, mediaSeekBar, mediaDuration, mediaCurrentProgress).deleteRecord());

        playRecordFab.setOnClickListener(click->{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PLAY_RECORD);
            this.playRecordView=click;
        });

        pauseRecordingFab=findViewById(R.id.PauseRecord);
        pauseRecordingFab.setOnClickListener(click->new recorder(this,click, pauseRecordingFab, mediaSeekBar, mediaDuration, mediaCurrentProgress).pause());

        FloatingActionButton recordFab=findViewById(R.id.RecordBtn);
        recordFab.setOnClickListener(click->
                ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                ACCESS_MIC_PERMISSION));


        //progress Dialog to wait for loading components in layout activity_edit_pane
        final ProgressDialog progress= ProgressDialog.show(this,"Wait...","Loading Note Workspace");
        //countDown Timer to Dismiss that dialog and load the components after 1200 ms w/ a countDown=100ms each time
        new CountDownTimer(650, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                //prepare for the options available
                prepare_For_the_note_options();
                //setting up the RichEditor preferences -> B/I/U/Undo/Redo/etc
                prepareNoteBox();
                //themes the noteBox synchronously according to the user's theme
                themeNoteBox();
                //listening for buttons
                prepareToolBarListener();
                //progress dialog dismiss after components load up
                progress.dismiss();
                if ( firstStart ){
                    new ToolTip(appCompatActivity).showToolTipWindow(findViewById(R.id.edText), ArrowDrawable.LEFT, "Write Notes here");
                }
            }
        }.start();

    }

    private void prepareNoteBox(){
        //adjust the webSettings for the richEditor
        final WebSettings webJS= noteBox.getSettings();
        webJS.setAllowFileAccess(true);
        webJS.setDisplayZoomControls(false);
        webJS.setAllowContentAccess(true);
        webJS.setBuiltInZoomControls(true);
        webJS.setLoadsImagesAutomatically(true);
        noteBox.setTextDirection(TEXT_DIRECTION_LOCALE);
        noteBox.setEditorFontSize(20);
        noteBox.setFontSize(7);
        noteBox.setTextBackgroundColor(getResources().getColor(R.color.transparent,null));
        noteBox.setTextColor(getResources().getColor(R.color.text_black,null));
        noteBox.setPlaceholder("Type Here...");
    }


    private void themeNoteBox(){
        //check for themes & apply different preferences
        if ( Theme.contains("GrayScaleTheme") || Theme.contains("CyanTheme") ){
            noteBox.setEditorFontColor(Color.WHITE);
            noteBox.setEditorWidth(22);
            noteBox.setBackgroundColor(getResources().getColor(R.color.colorAccent,null));
        } else if ( Theme.contains("AppTheme") ){
            noteBox.setBackgroundColor(getResources().getColor(R.color.cornysilk,null));
        } else if ( Theme.contains("GreenTheme") ){
            noteBox.setBackgroundColor(getResources().getColor(R.color.fade_white,null));
        }else if(Theme.length()<=0){
            noteBox.setBackgroundColor(getResources().getColor(R.color.cornysilk,null));
        }
    }
    //Method to config the RichEditor preferences + action Listeners of their buttons
    private void prepareToolBarListener(){
        //Button Listeners -->>
        findViewById(R.id.action_undo).setOnClickListener(v -> noteBox.undo());

        findViewById(R.id.action_redo).setOnClickListener(v -> noteBox.redo());

        findViewById(R.id.action_bold).setOnClickListener(v -> noteBox.setBold());

        findViewById(R.id.action_italic).setOnClickListener(v -> noteBox.setItalic());

        findViewById(R.id.action_subscript).setOnClickListener(v -> noteBox.setSubscript());

        findViewById(R.id.action_superscript).setOnClickListener(v -> noteBox.setSuperscript());

        findViewById(R.id.action_strikethrough).setOnClickListener(v -> noteBox.setStrikeThrough());

        findViewById(R.id.action_underline).setOnClickListener(v -> noteBox.setUnderline());


        findViewById(R.id.size_up).setOnClickListener(v -> noteBox.setFontSize((fontSize >=7)? fontSize =7:(fontSize +=1)));

        findViewById(R.id.size_down).setOnClickListener(v -> noteBox.setFontSize((fontSize <=1)? fontSize =1:(fontSize -=1)));

        findViewById(R.id.action_txt_color).setOnClickListener(v -> new ColorPanelSetUp(this).display( "textColor"));

        findViewById(R.id.action_bg_color).setOnClickListener(v -> new ColorPanelSetUp(this).display( "highlightColor"));

        findViewById(R.id.action_indent).setOnClickListener(v -> noteBox.setIndent());

        findViewById(R.id.action_outdent).setOnClickListener(v -> noteBox.setOutdent());

        findViewById(R.id.action_align_left).setOnClickListener(v -> noteBox.setAlignLeft());

        findViewById(R.id.action_align_center).setOnClickListener(v -> noteBox.setAlignCenter());

        findViewById(R.id.action_align_right).setOnClickListener(v -> noteBox.setAlignRight());

        findViewById(R.id.action_blockquote).setOnClickListener(v -> noteBox.setBlockquote());

        findViewById(R.id.action_insert_bullets).setOnClickListener(v -> noteBox.setBullets());

        findViewById(R.id.action_insert_numbers).setOnClickListener(v -> noteBox.setNumbers());

        findViewById(R.id.action_insert_image).setOnClickListener(v -> {
            //focus editor before fetching image from file directory
            noteBox.focusEditor();
            //new Intent instance of the ACTION_OPEN_DOCUMENT parameter
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            //setting type of file displayed there
            intent.setType("image/*");
            //setting intent category Type -> Openable Means it opens the file manager & its as default Applied
            //in case of the ACTION_OPEN_DOCUMENT intent
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            //starting the activity w/ a result listener & an id"Request Code" = 2020
            startActivityForResult(Intent.createChooser(intent, "Choose the file to Upload.."),
                    INSERT_IMAGE_ACTION);
        });

        findViewById(R.id.insert_fromStorage).setOnClickListener(v -> {
            Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/*");
            startActivityForResult(intent,
                    TEXT_FILE_ACTION);
        });
    }



    private void prepare_For_the_note_options(){
        if ( isEditEntry == 1 ){
            noteBox = findViewById(R.id.edText);
            noteBox.setHtml(finalOutText);
            DefineExistingRecording(recordName);

        } else {
            noteBox = findViewById(R.id.edText);
            FloatingActionButton deleteRecordFab=findViewById(R.id.deleteRecord);
            deleteRecordFab.setEnabled(false);
            FloatingActionButton playMediaFab=findViewById(R.id.playMedia);
            playMediaFab.setEnabled(false);
            FloatingActionButton pauseRecordFab=findViewById(R.id.PauseRecord);
            pauseRecordFab.setEnabled(false);
        }
    }


    private void ReadTheme(){
        //applying themes according to the content
        if(Theme.contains("GreenTheme")){
            setTheme(R.style.GreenTheme);
        }else if(Theme.contains("AppTheme")){
            setTheme(R.style.AppTheme);
        }else if(Theme.contains("GrayScaleTheme")){
            setTheme(R.style.Darky);
        }else if(Theme.contains("TitanTheme")){
            setTheme(R.style.orangeLover);
        }else if(Theme.contains("CyanTheme")){
            setTheme(R.style.BlueDark);
        }
    }


    private void stopRecording() {
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            recordingNow=false;
            PAUSE_VALUE = 2;
            DefineExistingRecording(recordName);
            Drawable imgPause = getApplicationContext().getResources().getDrawable(R.drawable.pause_ico,null);
            pauseRecordingFab.setImageDrawable(imgPause);
            showMediaComponents();
        } catch (IllegalStateException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void showMediaComponents() {
        if ( mediaSeekBar.getVisibility() == View.INVISIBLE ){
            mediaSeekBar.setVisibility(View.VISIBLE);
            //use animation w/ seekBar
            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
            mediaSeekBar.startAnimation(anim);
        }
    }

    private void DefineExistingRecording(String recordName) {
        playRecord = new MediaPlayer();
        try {
            playRecord.setDataSource(recordName);
            playRecord.prepare();
            Toast.makeText(getApplicationContext(), "Record Found For this Note", Toast.LENGTH_LONG).show();
            PAUSE_VALUE = 1;
            FloatingActionButton deleteRecordBtn=findViewById(R.id.deleteRecord);
            deleteRecordBtn.setEnabled(true);

            FloatingActionButton pauseMediaBtn=findViewById(R.id.playMedia);
            pauseMediaBtn.setEnabled(true);

            FloatingActionButton pauseRecordBtn=findViewById(R.id.PauseRecord);
            pauseRecordBtn.setEnabled(false);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "No Recordings for this Note,Yet", Toast.LENGTH_LONG).show();
            FloatingActionButton deleteRecordBtn=findViewById(R.id.deleteRecord);
            deleteRecordBtn.setEnabled(false);
            FloatingActionButton pauseMediaBtn=findViewById(R.id.playMedia);
            pauseMediaBtn.setEnabled(false);
            FloatingActionButton pauseRecordBtn=findViewById(R.id.PauseRecord);
            pauseRecordBtn.setEnabled(false);
        }
    }

    private String readString(String file) {
        StringBuilder data= new StringBuilder();
        try{
            BufferedReader reader=new BufferedReader(new FileReader(new File(getExternalStorageDirectory(),file)));
            data = new StringBuilder(reader.readLine() + "\n");
            while(reader.ready()){
                data.append(reader.readLine()).append("\n");
            }
        }catch (IOException e){
            e.printStackTrace();

        }
        return data.toString();
    }

    //Activity Request Listener for handling the open of ACTION_OPEN_DOCUMENT Intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == INSERT_IMAGE_ACTION ){
            //Image Scaling related ->>>>
            assert data.getDataString()!=null;
            //Inset img as <img> html tag vi the RichEditor
            noteBox.insertImage(data.getDataString(),
                    "");
        }else if(requestCode == TEXT_FILE_ACTION){
            assert Objects.requireNonNull(data.getData()).getPath()!=null;
            String path=data.getData().getPath();
            assert path != null;
            path=path.substring(path.indexOf(":")+1);
            noteBox.setHtml((noteBox.getHtml()==null?"": noteBox.getHtml())+"\n"+"  "+ readString(path)+"\n");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==ACCESS_MIC_PERMISSION){
           new recorder(this,null, pauseRecordingFab, mediaSeekBar, mediaDuration, mediaCurrentProgress).record();
        }else if(requestCode==PLAY_RECORD){
            RecordPlayer recordPlayer = new RecordPlayer(this,playRecordView,playRecordFab,pauseRecordingFab, mediaSeekBar, mediaDuration, mediaCurrentProgress);
            recordPlayer.setOnCompletedMediaListener();
            recordPlayer.play();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            stopRecording();
            playRecord.stop();
            playRecord.release();
            //dismiss this callback UI Event Handler
            handler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        btnBackPressed = true;
        new saveNote(this).DialogBoxWithoutRecordSave();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            playRecord.stop();
            playRecord.release();
        } catch (NullPointerException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

}