package com.scrappers.notepadsnippet.MainScreens;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filterable;
import android.widget.TextView;

import com.scrappers.notepadsnippet.R;

import java.util.ArrayList;

public class MainActivityListAdapter extends ArrayAdapter<String> {

    private Activity context;
    private ArrayList<String> mainTitle;
    private ArrayList<String> subTitle;

    @SuppressLint("StaticFieldLeak")
    static  TextView Subtxtview;

    @SuppressLint("StaticFieldLeak")
    static TextView Maintxtview;
    @SuppressLint("StaticFieldLeak")
    static CheckBox checkbox;

    /// wooohooo warning critical fatal warning >>>this is the constructor should be the same name as the class name
    public MainActivityListAdapter(Activity context, ArrayList<String> maintitle, ArrayList<String> subtitle) {
        super(context, R.layout.custom_list_xml, maintitle);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.mainTitle = maintitle;
        this.subTitle = subtitle;



    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        //define the layout inflater & the view to inflate a custom layout from a xml layout file
        LayoutInflater inflater = context.getLayoutInflater();
        final View rowView = inflater.inflate(R.layout.custom_list_xml, null, true);

        //define your components

         Maintxtview=rowView.findViewById(R.id.mainTitleTxt);
         Subtxtview=rowView.findViewById(R.id.subTitleTxt);


        Maintxtview.setText(mainTitle.get(position));

        Subtxtview.setText(subTitle.get(position));


        //List View Animation
        Animation animation = AnimationUtils.loadAnimation(context,R.anim.fade_in);
        rowView.startAnimation(animation);


        //finish your function by returning the custom view layout
        return rowView;

    }
}

