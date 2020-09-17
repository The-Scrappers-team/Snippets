package com.scrappers.notepadsnippet.MainScreens.Features.ToolTipTutorial;

import android.view.Gravity;
import android.view.View;

import com.scrappers.notepadsnippet.R;

import androidx.appcompat.app.AppCompatActivity;
import io.github.douglasjunior.androidSimpleTooltip.ArrowDrawable;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

import static com.scrappers.notepadsnippet.MainScreens.MainActivity.NoMoreTutorials;

public class ToolTip {

    private final AppCompatActivity context;
    private int shift=0;

    public ToolTip(AppCompatActivity context){
        this.context=context;
    }

    public void showToolTipWindow(View view, int ArrowDirection, String message) {
        if ( NoMoreTutorials ){
            /*
             *Define tooltip & initialize it
             */
            SimpleTooltip.Builder tooltip = new SimpleTooltip.Builder(context);
                        /*
                         * set ToolTip Properties
                         */
                        tooltip.anchorView(view)
                        .text(message)
                        .gravity(Gravity.END)
                        .arrowDirection(ArrowDirection)
                        .dismissOnOutsideTouch(true)
                        .animated(true)
                        .arrowColor(context.getResources().getColor(R.color.fab_red_color,null))
                        .transparentOverlay(false)
                        .textColor(context.getColor(R.color.white))
                        .backgroundColor(context.getResources().getColor(R.color.fab_red_color,null))
                        /*
                         *add dismiss listener to show it in another view
                         */
                        .onDismissListener(thisToolTip -> {
                            thisToolTip.dismiss();
                            ++shift;
                            switch (shift) {
                                case 1:
                                    thisToolTip.dismiss();
                                    showToolTipWindow(context.findViewById(R.id.menu), ArrowDrawable.RIGHT, "More Options");
                                    break;
                                case 2:
                                    thisToolTip.dismiss();
                                    showToolTipWindow(context.findViewById(R.id.openNavBarBtn), ArrowDrawable.RIGHT, "Hold & Swipe Right \n" +
                                            "Speak note,TodoList,Paint,OCR\n");

                                    break;
                                case 3:
                                    thisToolTip.dismiss();
                                    showToolTipWindow(context.findViewById(R.id.bottomBarLayout), ArrowDrawable.LEFT, "Customize Your Text");
                                    NoMoreTutorials = false;
                                    break;
                                default:
                                    thisToolTip.dismiss();
                                    break;
                            }
                        })
                        .build()
                        .show();
        }
    }
}
