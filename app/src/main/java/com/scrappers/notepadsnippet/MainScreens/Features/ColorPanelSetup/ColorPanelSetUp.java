package com.scrappers.notepadsnippet.MainScreens.Features.ColorPanelSetup;

import android.view.View;
import android.view.animation.AnimationUtils;

import com.scrappers.notepadsnippet.ColorsPanels.ColorPanel;
import com.scrappers.notepadsnippet.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.COLOR_PANEL_FOR;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.frameLayout;

public class ColorPanelSetUp {

    private final AppCompatActivity context;

    public ColorPanelSetUp(AppCompatActivity context){
        this.context=context;
    }

    public void display(String openFor){
        COLOR_PANEL_FOR=openFor;
        //Fragment & FragmentTransaction
        ColorPanel panel = new ColorPanel();
        FragmentTransaction fm = context.getSupportFragmentManager().beginTransaction();
//        fm.setCustomAnimations(R.anim.fab_slide_in_from_left,R.anim.fab_slide_in_from_right);
        //show the current fragment in that frameLayout Container
        fm.replace(R.id.colorPanelChooser, panel);
        //apply Changes
        fm.commit();
        frameLayout.setVisibility(View.INVISIBLE);

        switch (frameLayout.getVisibility()) {
            case View.INVISIBLE:
                //show fragment control buttons on
                frameLayout.setVisibility(View.VISIBLE);
                frameLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_up));
                break;
            case View.VISIBLE:
                frameLayout.setVisibility(View.INVISIBLE);
                frameLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_down));
                break;
            case View.GONE:
                break;
        }
    }
}
