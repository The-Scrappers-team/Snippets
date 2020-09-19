package com.scrappers.notepadsnippet.AboutActivity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.scrappers.notepadsnippet.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import static com.scrappers.notepadsnippet.MainScreens.MainActivity.Theme;
import static com.scrappers.notepadsnippet.MainScreens.MainActivity.fileForTheme;

public class aboutActivity extends AppCompatActivity{

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        readTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        //Call request Portrait Orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        animateLogoAndBTns(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open));
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

    }

    /**
     * Read the current theme database & apply it to the activity
     */
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

    /**
     * Animate about cards layout
     * @param animation custom animation ot set
     */
    public void animateLogoAndBTns( Animation animation ){
        CardView btnShare=findViewById(R.id.cardView);
        CardView goToWeb=findViewById(R.id.pav);

        btnShare.startAnimation(animation);
        goToWeb.startAnimation(animation);

    }

    /**
     * Opens a link in browser
     * @param socialLink the link to open
     */
    public void runSocials(String socialLink){
        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(socialLink)));
    }
    public void G_mail(View view){
        runSocials("https://mail.google.com/mail/mu/mp/840/#tl/priority/%5Esmartlabel_personal");
    }
    public void facebook(View view){
        runSocials(view.getTag().equals("thomas")?"https://www.facebook.com/2Math.toto":"https://www.facebook.com/profile.php?id=100010116038073");
    }

    /**
     * Shares the app to other fellows
     * @param view the widget view that do this action
     */
    public void shareMyApp(View view){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "https://play.google.com/store/apps/details?id=com.Scrappers.cairobus";
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Please Support US ! By Scrappers >>>>"));
    }


}
