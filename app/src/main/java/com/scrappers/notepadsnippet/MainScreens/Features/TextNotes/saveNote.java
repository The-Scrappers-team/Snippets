package com.scrappers.notepadsnippet.MainScreens.Features.TextNotes;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.scrappers.notepadsnippet.MainScreens.MainActivity;
import com.scrappers.notepadsnippet.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.NewFileName;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.btnBackPressed;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.noteBox;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.path;
import static com.scrappers.notepadsnippet.MainScreens.MainActivity.fileName;
import static com.scrappers.notepadsnippet.MainScreens.MainActivity.isEditEntry;
import static com.scrappers.notepadsnippet.MainScreens.MainActivity.notesNumber;
import static java.lang.System.out;

public class saveNote {

    private final AppCompatActivity context;
    private AlertDialog saveDialog;

    /**
     * Save a Basic Note
     * @param context the activity context
     */
    public saveNote(AppCompatActivity context){
        this.context=context;
    }

    /**
     * Save a Basic Note through a custom dialog box
     */
    public void DialogBoxWithoutRecordSave() {
        //check if its a new note -> then show a dialog box w/ save name procedure ;_>>
        switch (isEditEntry){
            case 0:
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(context);
                @SuppressLint("InflateParams")
                final View layout = context.getLayoutInflater().inflate(R.layout.dialogboxlayout, null);
                Button okBtn = layout.findViewById(R.id.okBtn);
                Button cancelBtn = layout.findViewById(R.id.CancelBtn);
                builder.setView(layout);
                // Dialog will have "Make a selection" as the title
                okBtn.setOnClickListener(v -> {
                    EditText saveNoteName = layout.findViewById(R.id.nameofsavefile);
                    NewFileName = saveNoteName.getText().toString();
                    if ( !NewFileName.isEmpty() ){
                        path = context.getFilesDir() + "/SPRecordings/Notes/" + NewFileName;
                        writeTextFiles(path, (noteBox.getHtml()==null)?"":noteBox.getHtml());
                        isEditEntry = 1;
                        saveDialog.dismiss();
                        context.finish();
                        //restart to refresh
                        context.startActivity(new Intent(context, MainActivity.class));
                        context.overridePendingTransition(R.anim.slide_in_left_noalpha,R.anim.slide_out_right_noalpha);
                    } else {
                        path = context.getFilesDir() + "/SPRecordings/Notes/" + "Note "+(notesNumber);
                        writeTextFiles(path, (noteBox.getHtml()==null)?"":noteBox.getHtml());
                        isEditEntry =1;
                        saveDialog.dismiss();
                        //restart to refresh
                        context.startActivity(new Intent(context, MainActivity.class));
                        context.overridePendingTransition(R.anim.slide_in_left_noalpha,R.anim.slide_out_right_noalpha);
                        context.finish();
                    }
                });


                cancelBtn.setOnClickListener(v -> {
                    if ( btnBackPressed ){
                        saveDialog.dismiss();
                        btnBackPressed = false;
                        //Back to the MainActivity class
                        context.startActivity(new Intent(context, MainActivity.class));
                        context.overridePendingTransition(R.anim.slide_in_left_noalpha,R.anim.slide_out_right_noalpha);
                        context.finish();
                    } else {
                        saveDialog.dismiss();
                    }
                });

                builder.setCancelable(true);
                saveDialog = builder.create();
                Objects.requireNonNull(saveDialog.getWindow()).setGravity(Gravity.CENTER);
                saveDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation2;
                saveDialog.show();

                break;

            case 1 :

                writeTextFiles(context.getFilesDir() + "/SPRecordings/Notes/" + fileName, (noteBox.getHtml()==null)?"":noteBox.getHtml());
                context.finish();
                //restart to refresh
                context.startActivity(new Intent(context, MainActivity.class));
                context.overridePendingTransition(R.anim.slide_in_left_noalpha,R.anim.slide_out_right_noalpha);

                break;
        }

        //to delete the excess null file created by the  DialogBoxWithoutRecordSave(); after pressing back after recording something
        //why it saves an excess file as a null file ..?
        //Because  DialogBoxWithoutRecordSave(); method takes the name written in a dialog box displayed when there is
        //susceptible behaviour of user to press the save Note fab btn but in case he didn't as in this case
        //an excess file will be saved as null w/o a record due to using DialogBoxWithoutRecordSave();
        //& w/ the same the text that the last file is saved w/ (which the file w/ the record in this case
        try {
            out.println(removeFile(context.getFilesDir() + "/SPRecordings/Notes/" + "null"));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //write Text Files to External Storage

    public void writeTextFiles(String path, String data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean removeFile(String file){
        return new File(file).delete();
    }

}
