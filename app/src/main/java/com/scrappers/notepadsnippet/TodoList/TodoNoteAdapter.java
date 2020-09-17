package com.scrappers.notepadsnippet.TodoList;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.scrappers.notepadsnippet.App;
import com.scrappers.notepadsnippet.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import static com.scrappers.notepadsnippet.MainScreens.MainActivity.fileName;

public class TodoNoteAdapter extends Adapter<TodoNoteAdapter.ViewHolderClass> {

    private ArrayList<String> checkboxesTitles;

    /**
     * Create a RV adapter with its ViewHolder for the todoList
     *
     * @param checkboxesTitles parse the checkboxesTitles fetched from the EditPaneActivity.java added by user + read by File Stream representing files with names
     *                                   resembling the todoItems.
     */
    public TodoNoteAdapter(ArrayList<String> checkboxesTitles){
        this.checkboxesTitles=checkboxesTitles;
    }

     /**
      *
      * Called when RecyclerView needs a new {@link RecyclerView.ViewHolder} of the given type to represent
         * an item.
            * <p>
         * This new ViewHolder should be constructed with a new View that can represent the items
         * of the given type. You can either create a new View manually or inflate it from an XML
         * layout file.
        */
    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*
         *Inflate a single view containing data items into the parent view
         */
        return new ViewHolderClass(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_todolist_adapter, parent, false));
    }

    /**
     * Called on the update of each item synchronously
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass holder, int position) {

        holder.checkBox.setText(checkboxesTitles.get(position));
        holder.deleteBtn.setTag(checkboxesTitles.get(position));
        /*read the state & set the checkboxes states according to the position*/
        holder.checkBox.setChecked(read(App.getContext().getFilesDir() +  "/SPRecordings/todoLists/"+fileName+"/"+checkboxesTitles.get(position)));
        holder.checkBox.setPaintFlags( (!read(App.getContext().getFilesDir() + "/SPRecordings/todoLists/" + fileName + "/" + checkboxesTitles.get(position)))?
        (Paint.LINEAR_TEXT_FLAG ): (Paint.STRIKE_THRU_TEXT_FLAG));

        /*set the delete listener class*/
        holder.deleteBtn.setOnClickListener(new DeleteTodoTask(this,position,checkboxesTitles));
        /*set the check & strikeThrough listener class*/
        holder.checkBox.setOnClickListener(new CheckTodoTask(this,position,checkboxesTitles));

    }

    /*
     * a simple function to read the check states inside the todo_Items & convert them into Boolean
     */
    private Boolean read(String file){
        try{
            BufferedReader br=new BufferedReader(new FileReader(file));
            /*return the read string in booleans*/
            return Boolean.parseBoolean(br.readLine());
        }catch (Exception e){
            e.printStackTrace();
            //as a default value
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return checkboxesTitles.size();
    }


    static class ViewHolderClass extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        AppCompatImageButton deleteBtn;

        /**
         * Create a holder class that holds widget items for each single view
         * @param itemView the view of the current positioned item
         */
        ViewHolderClass(@NonNull View itemView) {
            /*call the Constructor ViewHolder(@NonNull View itemView) to get the current view */
            super(itemView);
            /*your widgets(buttons,Checkboxes,RadioButtons,ImageButtons,AppCompatImageButton,etc) goes here by the inflated xml id */
            checkBox=itemView.findViewById(R.id.checkbox_list);
            deleteBtn=itemView.findViewById(R.id.deleteTodoNote);

        }
    }
}


