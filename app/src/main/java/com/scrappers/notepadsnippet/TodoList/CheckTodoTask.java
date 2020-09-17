package com.scrappers.notepadsnippet.TodoList;

import android.graphics.Paint;
import android.view.View;
import android.widget.CheckBox;

import com.scrappers.notepadsnippet.App;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

import static com.scrappers.notepadsnippet.MainScreens.MainActivity.fileName;

public  class CheckTodoTask implements View.OnClickListener {

    private RecyclerView.Adapter todoNote_adapter;
    private int position;
    private ArrayList<String> checkboxesTitles;

    /**
     * Create an instance that checks & strikesThrough the selected checkbox according to the @param position
     * @param todoNote_adapter the RV adapter instances that holds the view in which this widget stays
     * @param position position of the item inside the view to place action on
     * @param checkboxesTitles the title of the selected checkbox ( used as a filename to save the state )
     */
    CheckTodoTask(RecyclerView.Adapter todoNote_adapter, int position, ArrayList<String> checkboxesTitles) {
        this.todoNote_adapter=todoNote_adapter;
        this.position=position;
        this.checkboxesTitles=checkboxesTitles;
    }


    /**
     * saves the string equivalent value of selected checkbox state in a file
     * @param file /directory/file to save the state in (NB: fileName must be the todoItem name )
     * @param state the state you want to save
     */
    private void save_state(String file, boolean state){
        try{
            BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(new File(file)));
            bufferedWriter.write(String.valueOf(state));
            bufferedWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * gets the current TodoTask position
     * @param position
     * @return the TodoTask at that position
     */
    private String getCurrentItem(int position){
         try {
             return checkboxesTitles.get(position);
         }catch (IndexOutOfBoundsException ex){
             System.err.println(ex.getMessage());
             return "";
         }
    }
    /**
     * Instantiated when the checkbox is clicked by the user action
     * @param buttonView the current view of this position on the ViewHolder of the RV adapter
     */
    @Override
    public void onClick(View buttonView) {
        /*
         * check the selected checkBox if they unchecked & uncheck it if its checked
         */
        if(!((CheckBox)buttonView).isChecked()){
            ((CheckBox)buttonView).setPaintFlags(android.graphics.Paint.LINEAR_TEXT_FLAG);
            ((CheckBox)buttonView).setChecked(false);
            /*update the changed item*/
            todoNote_adapter.notifyItemChanged(position);
            /*
             * save the state in the file(that resembles the todoItem using streams
             */
            save_state(App.getContext().getFilesDir() +  "/SPRecordings/todoLists/"+fileName+"/"+((CheckBox)buttonView).getText(),false);
        }else {
            ((CheckBox)buttonView).setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            ((CheckBox)buttonView).setChecked(true);
            /*update the changed item*/
            todoNote_adapter.notifyItemChanged(position);

            /*
             * save the state in the file(that resembles the todoItem using streams
             */
            save_state(App.getContext().getFilesDir() +  "/SPRecordings/todoLists/"+fileName+"/"+((CheckBox)buttonView).getText(),true);
        }
    }
}