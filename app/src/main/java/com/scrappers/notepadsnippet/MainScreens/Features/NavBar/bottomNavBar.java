package com.scrappers.notepadsnippet.MainScreens.Features.NavBar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.scrappers.notepadsnippet.MainScreens.Features.TextNotes.saveNote;
import com.scrappers.notepadsnippet.MainScreens.Features.TextNotes.speakNote;
import com.scrappers.notepadsnippet.MainScreens.Features.TodoList.todoListSetup;
import com.scrappers.notepadsnippet.OCR.OCRCamera;
import com.scrappers.notepadsnippet.Paint.Paint;
import com.scrappers.notepadsnippet.R;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.noteBox;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.ocrCameraFragmentLayout;
import static com.scrappers.notepadsnippet.MainScreens.MainActivity.isEditEntry;

public class bottomNavBar {

    private AppCompatActivity context;
    private AlertDialog navBar;

    /**
     * Instantiate a bottom Nav Bar that has more note features
     * @param context
     */
    public bottomNavBar(AppCompatActivity context) {
        this.context=context;
    }

    /**
     * Display the bottom navBar & ist listeners
     */
    public void openNavBar(){
        //Dialog builder & its layout Inflater & its Listener
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context);
        final LayoutInflater inflater = context.getLayoutInflater();
        //side_bar_menu XML layout code inflated
        @SuppressLint("InflateParams")
        final View layout = inflater.inflate(R.layout.side_bar_menu, null);
        //setting the layout to the view of the AlertDialog
        builder.setView(layout);
        //by doing this , you can cancel that dialog by pressing on the blackLit space outside its Design borders
        builder.setCancelable(true);
        //create it
        navBar = builder.create();
        //Dialog background ,by doing this you are making the background as a blacklight space(The Versa is the dimmed background)
        Objects.requireNonNull(navBar.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        //Dialog gravity
        navBar.getWindow().setGravity(Gravity.BOTTOM);
        navBar.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        //dialog animation
        navBar.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation4;
        //show that button w/ an animation
        navBar.show();

        //Define ImageViews Buttons id from that inflated Layout
        ImageView close = layout.findViewById(R.id.closeNav);
        ImageView speakNav = layout.findViewById(R.id.speakNav);
        ImageView todo = layout.findViewById(R.id.todoNav);
        ImageView painter = layout.findViewById(R.id.painterNav);
        ImageView ocr = layout.findViewById(R.id.ocrNav);

//        //buttons click Listeners
        close.setOnClickListener(v -> {
            //closing the dialog on click on the close btn
            navBar.dismiss();
        });

        speakNav.setOnClickListener(new speakNote(context));

        todo.setOnClickListener(v -> {
            if( isEditEntry == 1){
                new todoListSetup(context).openTodoList();
            }else if(isEditEntry == 0){
                Toast.makeText(context, "Please Save the note first via save button !", Toast.LENGTH_LONG).show();
                new saveNote(context).DialogBoxWithoutRecordSave();
            }
        });

        painter.setOnClickListener(v -> {
            if ( isEditEntry == 1 ){
                context.startActivity(new Intent(context, Paint.class));
            } else if ( isEditEntry == 0 ){
                Toast.makeText(context, "Please Save the note first via save button !", Toast.LENGTH_LONG).show();
                new saveNote(context).DialogBoxWithoutRecordSave();
            }
        });


        ocr.setOnClickListener(v ->{
                    navBar.dismiss();
                    FragmentTransaction fragmentTransaction=context.getSupportFragmentManager().beginTransaction();
                    OCRCamera ocrCamera =new OCRCamera(noteBox);
                    fragmentTransaction.replace(R.id.ocrChooser,ocrCamera);
                    fragmentTransaction.commit();
                    ocrCameraFragmentLayout=context.findViewById(R.id.ocrChooser);
                    ocrCameraFragmentLayout.setVisibility(View.VISIBLE);
                }
        );
    }
}
