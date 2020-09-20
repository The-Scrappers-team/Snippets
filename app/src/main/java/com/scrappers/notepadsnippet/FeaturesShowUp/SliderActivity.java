package com.scrappers.notepadsnippet.FeaturesShowUp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scrappers.notepadsnippet.MainScreens.MainActivity;
import com.scrappers.notepadsnippet.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import static com.scrappers.notepadsnippet.FeaturesShowUp.SliderAdapter.slide_images;


public class SliderActivity extends AppCompatActivity {

    private static final String SHAPE_DOTS ="&#8226";
    private LinearLayout DotsLayout;
    private TextView[] DotsOnTxtView;
    private Button finishBtn;
    /**
     * @implNote public is access modifier/specifier means visible to all packages(folders) inside the main App package
     *
     * @implNote static is a memory access keyword means that this var is created in memory on the first runtime of this context only &
     * is visible to all the contexts that shares ths same memory access-
     * ,you can call a static member without using an instance as well because its already in the memory of the package context
     */
    public static boolean firstStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        //to open the activity for the 1st time only
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        firstStart = prefs.getBoolean("firstStart", true);
        
        //StatsBar Coloring for this Activity
        getWindow().setStatusBarColor(getResources().getColor(R.color.morning,null));



        //check if its not really the first start -> finish & start the MAinActivity
        if ( !firstStart ){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            //if its opened for the 1st time
            //slideLayout
            //SliderActivity Adapter
            ViewPager slideViewPager = findViewById(R.id.viewpageslide);
            //linear layout to addView(textViews have html code &#8226 meaning a dot) so we add 3 dots using a for loop check it below
            DotsLayout = findViewById(R.id.dots);
            //custom slider adapter instance/obj
            SliderAdapter slider_adapter = new SliderAdapter(this);
            //setting the adapter for that viewPager
            slideViewPager.setAdapter(slider_adapter);
            //add the dots as textViews to the current LinearLayout that is predefined in activity_slider.xml file
            addDotsIndicator(slide_images.length);
            //add listener
            slideViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    //first of all set all dot colors to Transparent white
                    turn_All_Dots_To_Transparent_White();
                    //then set the current positioned dot to white color
                    DotsOnTxtView[position].setTextColor(getResources().getColor(R.color.white,null));
                    //now if you have reached the end of pages(last index) then set the finish btn to visible
                    //but if you ve returned back then set it to Invisible
                    if(position==DotsOnTxtView.length-1){
                        finishBtn.setVisibility(View.VISIBLE);
                    }else{
                        finishBtn.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            finishBtn = findViewById(R.id.finishbtn);
            finishBtn.setOnClickListener(v -> {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                //changing the SharedPreferences configuration
                SharedPreferences prefs1 = getSharedPreferences("prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs1.edit();
                //after starting the first time --> it will not be a first start ..because firstStart is settled to false
                editor.putBoolean("firstStart", false);
                //then apply the preferences changes
                editor.apply();
            });

        }
    }

    public void addDotsIndicator(int NUMBER_OF_DOTS) {
        //define a textView array given its Length the parameter -->NUMBER_OF_DOTS
        DotsOnTxtView = new TextView[NUMBER_OF_DOTS];
        //add dots from zero to NUMBER_OF_DOTS using their HTML code &#8226 & setting their size to 35
            for (int i = 0; i < NUMBER_OF_DOTS; i++) {
                DotsOnTxtView[i] = new TextView(this);
                DotsOnTxtView[i].setText(Html.fromHtml(SHAPE_DOTS,0));
                DotsOnTxtView[i].setTextSize(35);
                //set the first dot to white at the beginning before reaching the PageChanger Listener
                if(i==0){
                    DotsOnTxtView[i].setTextColor(getResources().getColor(R.color.white,null));
                }else{
                    DotsOnTxtView[i].setTextColor(getResources().getColor(R.color.transparent_white,null));
                }
                //add each dot the LinearLayout DotsLayout predefined in activity_slider.xml file
                DotsLayout.addView(DotsOnTxtView[i]);
            }
    }



    public void turn_All_Dots_To_Transparent_White(){
        //for loop iterates over all dots beginning from zero till NUMBER_OF_DOTS which analogs  DotsOnTxtView.length-1
        for (TextView view : DotsOnTxtView) {
            //& sets their color to transparent_white according to their position
            view.setTextColor(getResources().getColor(R.color.transparent_white, null));
        }

    }

}
