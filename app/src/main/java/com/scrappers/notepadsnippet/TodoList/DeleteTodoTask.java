package com.scrappers.notepadsnippet.TodoList;

import android.view.View;

import com.scrappers.notepadsnippet.App;

import java.io.File;
import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

import static com.scrappers.notepadsnippet.MainScreens.MainActivity.fileName;
import static java.lang.System.out;

public  class DeleteTodoTask implements View.OnClickListener {

    private RecyclerView.Adapter adapter;
    private int position;
    private ArrayList<String> checkboxesTitles;

    /**
     * Deletes a todoNote Item & update the adapter
     * @param adapter the RV adapter to update
     * @param position the current position of the todoItem
     * @param checkboxesTitles an ArrayList specifies the checkBoxes in that RV adapter
     */
    DeleteTodoTask(RecyclerView.Adapter adapter, int position, ArrayList<String> checkboxesTitles){
        this.adapter=adapter;
        this.position=position;
        this.checkboxesTitles=checkboxesTitles;
    }
    @Override
    public void onClick(View v) {

        out.println(deleteTask(new File(App.getContext().getFilesDir() +  "/SPRecordings/todoLists/"+fileName+"/"+v.getTag())));
        removeCurrentItem(position);
        adapter.notifyDataSetChanged();

    }

    /**
     * Removes the current todoTask or todoItem from the ArrayList of the RV adapter
     * @param position position of the current Item to be deleted
     * @throws IndexOutOfBoundsException if you exceeds over the specified current length or vice versa
     */
    private void removeCurrentItem(int position) throws IndexOutOfBoundsException{
             checkboxesTitles.remove(position);
    }

    /**
     * delete a file task
     * @param task the file to delete
     * @return true if the todoTask is deleted successfully , false otherwise
     */
    private boolean deleteTask(File task){
        return task.delete();
    }
}