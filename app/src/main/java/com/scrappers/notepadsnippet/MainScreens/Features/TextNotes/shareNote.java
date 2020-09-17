package com.scrappers.notepadsnippet.MainScreens.Features.TextNotes;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.noteBox;
import static com.scrappers.notepadsnippet.MainScreens.MainActivity.recordName;

public class shareNote {
    private AppCompatActivity context;
    public shareNote(AppCompatActivity context) {
        this.context=context;
    }

    public void share(){
        //Alert dialog to choose what to search
        AlertDialog.Builder shareDialogBuilder = new AlertDialog.Builder(context);
        //setting its message
        shareDialogBuilder.setMessage("Choose what to share :")
                //1st btn
                .setPositiveButton(" TXT Note", (dialog, id) -> {
                    //share text note method
                    try{
                        //SHaring the Note Text>>>>
                        Intent shareIntentNote = new Intent(Intent.ACTION_SEND);
                        shareIntentNote.setType("text/plain");
                        shareIntentNote.putExtra(Intent.EXTRA_SUBJECT, "Share Note");
                        shareIntentNote.putExtra(Intent.EXTRA_TEXT, String.valueOf(Html.fromHtml(noteBox.getHtml())));
                        context.startActivity(Intent.createChooser(shareIntentNote, "Share File"));
                    }catch (NullPointerException error){
                        error.printStackTrace();
                        Toast.makeText(context,"Cannot Share Empty message !",Toast.LENGTH_LONG).show();
                    }
                })
                //second btn
                .setNegativeButton("Recorded Note", (dialog, which) -> {
                    try {
                        //Sharing the recorded Note
                        Intent shareIntentRecord = new Intent(Intent.ACTION_SEND);
                        shareIntentRecord.setType("audio/mp3");
                        shareIntentRecord.putExtra(Intent.EXTRA_STREAM, Uri.parse(recordName));
                        shareIntentRecord.putExtra(Intent.EXTRA_SUBJECT, "Share Record");
                        context.startActivity(Intent.createChooser(shareIntentRecord, "Share Record"));
                    } catch (NullPointerException error) {
                        Toast.makeText(context, "No Record To Declare", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    } catch (IllegalStateException error2) {
                        Toast.makeText(context, "Unknown Error", Toast.LENGTH_SHORT).show();
                        error2.printStackTrace();
                    }
                })
                //you can cancel this dialog by pressing back btn or outside on the Screen
                .setCancelable(true);
        //create that dialog
        AlertDialog shareDialog = shareDialogBuilder.create();
        //display it
        shareDialog.show();
    }
}
