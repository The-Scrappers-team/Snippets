package com.scrappers.notepadsnippet.MainScreens.Features.TodoList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.scrappers.notepadsnippet.App;
import com.scrappers.notepadsnippet.R;
import com.scrappers.notepadsnippet.TodoList.TodoNoteAdapter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.scrappers.notepadsnippet.MainScreens.MainActivity.fileName;
import static java.lang.System.out;

public class todoListSetup {
    /**
     * Attributes
     */
    private final AppCompatActivity context;
    private AlertDialog saveDialog;
    private ArrayList<String> dynamicList =new ArrayList<>();
    private TodoNoteAdapter todoNoteAdapter;

    public todoListSetup(AppCompatActivity context) {
        this.context=context;
    }

    /**
     * opens a new todoList for this note
     */
    public void openTodoList(){

        //Dialog builder & its layout Inflater & its Listener
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context);
        //side_bar_menu XML layout code inflated
        @SuppressLint("InflateParams")
        final View layout = context.getLayoutInflater().inflate(R.layout.activity_todo__list_, null);
        //setting the layout to the view of the AlertDialog
        builder.setView(layout);
        //by doing this , you can cancel that dialog by pressing on the back blackLight space outside its Design borders
        builder.setCancelable(true);
        //create it
        saveDialog = builder.create();
        //Dialog background ,by doing this you are making the background as a back blackLight space(The Versa is the dimmed background)
        Objects.requireNonNull(saveDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        //Dialog gravity
        saveDialog.getWindow().setGravity(Gravity.CENTER);
        //dialog animation
        saveDialog.getWindow().getAttributes().windowAnimations = R.style.Scale;
        saveDialog.setCancelable(false);
        //show that button w/ an animation
        saveDialog.show();

        RecyclerView rv=layout.findViewById(R.id.TodoList_Grid);

        readTodoList(context.getFilesDir() + "/SPRecordings/todoLists/" + fileName);
        todoNoteAdapter =new TodoNoteAdapter(dynamicList);
        rv.setAdapter(todoNoteAdapter);
        rv.setLayoutManager(new LinearLayoutManager(context));

        EditText taskName=layout.findViewById(R.id.todoName);

        Button addItem=layout.findViewById(R.id.addtodo);
        //adding new item inside the todoNote
        addItem.setOnClickListener(v -> {
            /*
             * to ensure dummy users won't add an empty item/task
             */
            if(taskName.length()>0){
                addTask(taskName.getText().toString());
                dynamicList.add(taskName.getText().toString());
                todoNoteAdapter.notifyDataSetChanged();
                taskName.setText("");
            }
        });

        ImageButton closeTodoWindow=layout.findViewById(R.id.close_todo);
        closeTodoWindow.setOnClickListener(v -> saveDialog.dismiss());

        ImageButton removeTodo=layout.findViewById(R.id.remove_all);
        removeTodo.setOnClickListener(this::removeTodoList);

    }

    private void removeTodoList(View layoutView) {
        try {
            File file = new File(App.getContext().getFilesDir() + "/SPRecordings/todoLists/" + fileName + "/");
            //delete all files in the path individually
            File[] files = file.listFiles();
            for (File value : files) {
                //remove each file ( resembles todoNote ) individually
                out.println(deleteFile(value));
            }
            //remove data from adapter and clear it
            dynamicList.clear();
            //apply the changes to the adapter
            todoNoteAdapter.notifyDataSetChanged();
            Snackbar.make(layoutView, fileName + " TodoList is deleted Successfully", Snackbar.LENGTH_LONG).show();
        }catch (NullPointerException e){
            e.printStackTrace();
            Snackbar.make(layoutView,"No Todo Tasks to delete", BaseTransientBottomBar.LENGTH_LONG).show();
        }
    }


    private void readTodoList(String file) {
        try {
            dynamicList.clear();
            File[] files = new File(file).listFiles();
            for (File value : files) {
                dynamicList.add(value.getName());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addTask(String note){
        File dir=new File(context.getFilesDir() +  "/SPRecordings/todoLists/"+fileName);
        out.println(makeDirectory(dir));
        try {
            BufferedWriter write=new BufferedWriter(new FileWriter(dir+"/"+note));
            write.write("false");
            write.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean makeDirectory(File currentDir){
        return currentDir.mkdirs();
    }
    private boolean deleteFile(File currentFile){
        return currentFile.delete();
    }
}
