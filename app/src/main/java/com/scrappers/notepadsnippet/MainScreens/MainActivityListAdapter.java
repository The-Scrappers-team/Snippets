package com.scrappers.notepadsnippet.MainScreens;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.scrappers.notepadsnippet.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MainActivityListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> mainTitle;
    private final ArrayList<String> subTitle;

    MainActivityListAdapter(Activity context, ArrayList<String> mainTitle, ArrayList<String> subTitle) {
        super(context, R.layout.custom_list_xml, mainTitle);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.mainTitle = mainTitle;
        this.subTitle = subTitle;
    }

    @NotNull
    @Override
    public View getView(final int position, View view, @NotNull ViewGroup parent) {
        //define the layout inflater & the view to inflate a custom layout from a xml layout file
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"})
        final View rowView = inflater.inflate(R.layout.custom_list_xml, null, true);

        //define your components

        TextView mainTxtView = rowView.findViewById(R.id.mainTitleTxt);
        TextView subTxtView = rowView.findViewById(R.id.subTitleTxt);


        mainTxtView.setText(mainTitle.get(position));

        subTxtView.setText(subTitle.get(position));


        //List View Animation
        Animation animation = AnimationUtils.loadAnimation(context,R.anim.fade_in);
        rowView.startAnimation(animation);

        //finish your function by returning the custom view layout
        return rowView;

    }
}

