package com.scrappers.notepadsnippet.Paint;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.scrappers.notepadsnippet.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import static com.github.clans.fab.FloatingActionButton.SIZE_MINI;
import static com.github.clans.fab.FloatingActionButton.SIZE_NORMAL;
import static com.scrappers.notepadsnippet.MainScreens.MainActivity.Theme;
import static com.scrappers.notepadsnippet.MainScreens.MainActivity.fileForTheme;
import static com.scrappers.notepadsnippet.MainScreens.MainActivity.fileName;
import static com.scrappers.notepadsnippet.Paint.PaintView.DEFAULT_BG_COLOR;

public class Paint extends AppCompatActivity {

      public static PaintView paintView;
      @SuppressLint("StaticFieldLeak")
      public static  FrameLayout controlFrame;
      public static  FloatingActionButton paintCursor;
      @SuppressLint("StaticFieldLeak")
      public static ImageView brushTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        readTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        /*Assign an instance fo the paint Canvas*/
        paintView=findViewById(R.id.canvas);
        /*create a layout properties instance(containing :display, such as its
         * size, density, and font scaling.)
         * */
        DisplayMetrics metrics = new DisplayMetrics();
        /*getting the custom metrics(height,width) appearance of this current screen & assigning them to the instance metrics*/
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        /*Customize the appearance window(height & width metrics) according to the display metrics of the device - inserting the dir of the previous painting if exist */
        paintView.init(metrics,Environment.getExternalStorageDirectory() + "/" + "SPRecordings" + "/paints/" + fileName + ".png");

        show_Paint_controllers();

        paintCursor=findViewById(R.id.paintCursor);
        brushTrack=findViewById(R.id.brushTrack);

    }


   private void show_Paint_controllers() {
        FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
//        fm.setCustomAnimations(R.anim.fab_slide_in_from_left,R.anim.fab_slide_in_from_right);
        PaintControls paintControls=new PaintControls();
        //show the current fragment in that frameLayout Container
        fm.replace(R.id.PaintControllers, paintControls);
        //apply Changes
        fm.commit();
       controlFrame = findViewById(R.id.PaintControllers);
       controlFrame.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*
         * return the frameLayout back in place
         */
        controlFrame.setVisibility(View.INVISIBLE);
        controlFrame.setLayoutParams(new LinearLayout.LayoutParams(0, 0));


        /*
         * Time taken to save the file after dismissing the controller fragment is  1000 ms means 1 sec
         */
        CountDownTimer ctd=new CountDownTimer(500,200) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                //save the paint to a file
                paintView.save(new File(Environment.getExternalStorageDirectory() + "/" + "SPRecordings" + "/paints/" + fileName + ".png"));
            }
        };
        ctd.start();

    }

    public void openController(View view) {
        if(controlFrame.getVisibility()==View.INVISIBLE) {
            /*
             *
             */
            controlFrame.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM;
            controlFrame.setLayoutParams(params);

            ImageButton dismissBtn=findViewById(R.id.dismiss);
            dismissBtn.setImageDrawable(getDrawable(R.drawable.downfrag));
        }else{
            controlFrame.setVisibility(View.INVISIBLE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);
            controlFrame.setLayoutParams(params);

            ImageButton dismissBtn=findViewById(R.id.dismiss);
            dismissBtn.setImageDrawable(getDrawable(R.drawable.upfrag));
        }

    }

    public void rubber(View view) {
            paintView.setCurrentColor(DEFAULT_BG_COLOR);
            paintCursor.setColorNormal(DEFAULT_BG_COLOR);
            brushTrack.setBackgroundTintList(ColorStateList.valueOf(DEFAULT_BG_COLOR));
    }

    public void quit(View view) {
        /*
         * return the frameLayout back in place
         */
        controlFrame.setVisibility(View.INVISIBLE);
        controlFrame.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

        /*
         * Time taken to save the file after dismissing the controller fragment is  1000 ms means 1 sec
         */
        CountDownTimer ctd=new CountDownTimer(600,500) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                /*Quit without saving*/
                finish();

            }
        };
        ctd.start();

    }

    public void readTheme() {
        try {
            //read themes in that file
            BufferedReader br = new BufferedReader(new FileReader(fileForTheme));
            if ( br.ready() ){
                //reading first line of that db file
                Theme = br.readLine();
                //applying themes according to the content
                if(Theme.contains("GreenTheme")){
                    setTheme(R.style.GreenTheme);
                }else if(Theme.contains("AppTheme")){
                    setTheme(R.style.AppTheme);
                }else if(Theme.contains("Darky")){
                    setTheme(R.style.Darky);
                }else if(Theme.contains("orangeLover")){
                    setTheme(R.style.orangeLover);
                }else if(Theme.contains("BlueDark")){
                    setTheme(R.style.BlueDark);
                }
                //close the BR
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class PaintControls extends Fragment {



        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view=inflater.inflate(R.layout.fragment_paint_controls, container, false);

            //text color
            add_Listener(R.id.black,Color.BLACK,view);
            add_Listener(R.id.blue,Color.BLUE,view);
            add_Listener(R.id.red,Color.RED,view);
            add_Listener(R.id.yellow,Color.YELLOW,view);
            add_Listener(R.id.green,Color.GREEN,view);
            add_Listener(R.id.grey,Color.GRAY,view);
            add_Listener(R.id.pink,Color.MAGENTA,view);




            //text style
            Button emboss=view.findViewById(R.id.emboss);
            emboss.setOnClickListener(v ->paintView.emboss());
            Button normal=view.findViewById(R.id.normal);
            normal.setOnClickListener(v -> paintView.setDefaultStroke());
            Button blur=view.findViewById(R.id.blur);
            blur.setOnClickListener(v -> paintView.blur());

            //clear All
            FloatingActionButton clearAll=view.findViewById(R.id.deletePaint);
            clearAll.setOnClickListener(v -> paintView.destroy());

            //save progress
            Button savePaint=view.findViewById(R.id.save);
            savePaint.setOnClickListener(v -> {
                paintView.save(new File(Environment.getExternalStorageDirectory() + "/" + "SPRecordings" + "/paints/" + fileName + ".png"));
                Toast.makeText(getContext(),"Saved Successfully",Toast.LENGTH_LONG).show();
            });


            //stroke controller
            SeekBar strokeControl=view.findViewById(R.id.strokeControl);
            strokeControl.setProgress(paintView.getStrokeWidth());
            strokeControl.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser){
                        if(progress==seekBar.getMax()){
                            paintCursor.setButtonSize(SIZE_NORMAL);
                        }else {
                            paintCursor.setButtonSize(SIZE_MINI);
                        }

                        paintView.setStrokeWidth(progress);
                        brushTrack.setScaleX((progress==0) ? 1 : (float)progress/20);
                        brushTrack.setScaleY((progress==0) ? 1 : (float)progress/20);
                    }
                }


                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            FloatingActionButton shareBtn=view.findViewById(R.id.sharePainting);
            shareBtn.setOnClickListener(v -> {
                Intent shareIntent=new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/png");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory() + "/" + "SPRecordings" + "/paints/" + fileName + ".png"));
                startActivity(shareIntent);
            });


            FloatingActionButton undoBtn=view.findViewById(R.id.undo);
            undoBtn.setOnClickListener(v -> paintView.undo());

            FloatingActionButton redoBtn=view.findViewById(R.id.redo);
            redoBtn.setOnClickListener(v -> paintView.redo());
            return view;
        }

        private void add_Listener(int id, int color, View view) {
            ImageButton btn=view.findViewById(id);
            btn.setOnClickListener(v -> {
                paintView.setCurrentColor(color);
                paintCursor.setColorNormal(color);
                brushTrack.setBackgroundTintList(ColorStateList.valueOf(color));

            });
        }

    }


}
