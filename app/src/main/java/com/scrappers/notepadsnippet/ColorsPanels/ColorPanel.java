package com.scrappers.notepadsnippet.ColorsPanels;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.scrappers.notepadsnippet.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.COLOR_PANEL_FOR;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.noteBox;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.frameLayout;

/**
 * a Fragment class to change text colors & highlight text with different colors
 */
public class ColorPanel extends Fragment {

    /**
     * Instantiated when this view is created
     *
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /*
         * Inflate the XML layout & don't attach it to the root parent design
         */
        View view = inflater.inflate(
                COLOR_PANEL_FOR.equals("textColor")?(R.layout.text_color_chooser):(R.layout.highlight_color_chooser), container, false);
        //assigning IDs for those  buttons from the current inflated View

            switch (COLOR_PANEL_FOR) {
                case "highlightColor":
                    listeners_For_highlight_pane(view);
                    break;
                case "textColor":
                    listeners_For_txtColor_pane(view);
                    break;
            }

        /*
         * Close button makes it invisible
         */
        ImageView imageView=view.findViewById(R.id.close_highL);
        imageView.setOnClickListener(v -> frameLayout.setVisibility(View.INVISIBLE));

        return view;
    }

    /**
     * Creates listeners for color buttons based on this view
     * @param view the view container that holds these buttons/ImageButtons
     */
    private void listeners_For_txtColor_pane(View view) {
        final ImageButton red = view.findViewById(R.id.red_colorChooser);

        red.setOnClickListener(v -> noteBox.setTextColor(getResources().getColor(R.color.text_red_a400,null)));


        final ImageButton green = view.findViewById(R.id.green_colorChooser);

        green.setOnClickListener(v -> noteBox.setTextColor(getResources().getColor(R.color.text_green_a400,null)));

        final ImageButton blue = view.findViewById(R.id.blue_colorChooser);

        blue.setOnClickListener(v -> noteBox.setTextColor(getResources().getColor(R.color.text_blue_a400,null)));

        final ImageButton yellow = view.findViewById(R.id.yellow_colorChooser);

        yellow.setOnClickListener(v -> noteBox.setTextColor(getResources().getColor(R.color.text_yellow_a400,null)));

        final ImageButton black = view.findViewById(R.id.black_colorChooser);

        black.setOnClickListener(v -> noteBox.setTextColor(getResources().getColor(R.color.dark_3,null)));


        final ImageButton white = view.findViewById(R.id.white_colorChooser);

        white.setOnClickListener(v -> noteBox.setTextColor(getResources().getColor(R.color.white_e,null)));

        final ImageButton orange = view.findViewById(R.id.orange_colorChooser);

        orange.setOnClickListener(v -> noteBox.setTextColor(getResources().getColor(R.color.text_highL_orange,null)));
    }







    /**
     * Creates listeners for Highlight color buttons based on this view
     * @param view the view container that holds these buttons/ImageButtons
     */
    private void listeners_For_highlight_pane(View view) {
        final ImageButton yellow = view.findViewById(R.id.highlight_yellow);

        yellow.setOnClickListener(v -> noteBox.setTextBackgroundColor(getResources().getColor(R.color.text_highL_yellow_default,null)));

        final ImageButton red = view.findViewById(R.id.rose_colorChooser);

        red.setOnClickListener(v -> noteBox.setTextBackgroundColor(getResources().getColor(R.color.text_highL_red,null)));


        final ImageButton green = view.findViewById(R.id.green2_colorChooser);

        green.setOnClickListener(v -> noteBox.setTextBackgroundColor(getResources().getColor(R.color.text_highL_green,null)));

        final ImageButton blue = view.findViewById(R.id.lightblue_colorChooser);

        blue.setOnClickListener(v -> noteBox.setTextBackgroundColor(getResources().getColor(R.color.text_highL_blue,null)));
        final ImageButton orange = view.findViewById(R.id.orange2_colorChooser);

        orange.setOnClickListener(v -> noteBox.setTextBackgroundColor(getResources().getColor(R.color.text_highL_orange,null)));
        final ImageButton white = view.findViewById(R.id.white_highL_colorChooser);

        white.setOnClickListener(v -> noteBox.setTextBackgroundColor(getResources().getColor(R.color.fade_white,null)));

        final ImageButton black = view.findViewById(R.id.black2_colorChooser);

        black.setOnClickListener(v -> noteBox.setTextBackgroundColor(getResources().getColor(R.color.colorAccent,null)));

        final ImageButton cornySilk = view.findViewById(R.id.corny_colorChooser);

        cornySilk.setOnClickListener(v -> noteBox.setTextBackgroundColor(getResources().getColor(R.color.cornysilk,null)));

    }

}
