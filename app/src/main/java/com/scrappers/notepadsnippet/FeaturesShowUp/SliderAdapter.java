package com.scrappers.notepadsnippet.FeaturesShowUp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scrappers.notepadsnippet.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

public class SliderAdapter extends PagerAdapter {

    private final AppCompatActivity context;

    SliderAdapter(AppCompatActivity context){
        this.context = context;
    }
    //Arrays
    static int[] slide_images = {
            R.drawable.write_paper_ink_icon,
            R.drawable.themes,
            R.drawable.download_monitor,
            R.drawable.todo_icon,
            R.drawable.record_player_icon,
            R.drawable.camera_slider,
            R.drawable.qrcode_slider,
            R.drawable.ic_palette_black_24dp,
            R.drawable.ic_settings_black_24dp,
            R.drawable.ic_fingerprint_black_24dp
    };

    private String[] slide_Headings = {
            "Hello !",
            "Themes",
            "BackUp",
            "To-Do List",
            "Recording",
            "Text Recognition",
            "BarCode Reader",
            "Paint",
            "Customizations",
            "Built-in Security"
    };

    private String [] slide_text = {
            "Welcome to our Snippet App, \nThis is a tour to know the Features of Snippet.",
            "As everyone has his own taste, Snippet has many themes to fit your style and mode.",
            "Backup your data and format your phone and then restore them like nothing had happened.",
            "Snippet is not just a Notebook -with many typography Features- but also a todo list that carries your tasks.",
            "Long Lectures and meetings can be recorded, even if the screen is off, to record whatever you want, save your battery, record efficiently.",
            "Optical Camera Character Feature(OCR) provides Text Recognition onBoard,\nSo you can now extract text from any Image",
            "Optical Camera Character Feature(OCR) provides QR/BarCode Reader onBoard,\nSo you can now extract data from any QR/BarCode",
            "Every Note Holds a paint file,\nNow you can release the artist insides you :-)",
            "A lot of Features depend on quality or styles so Snippet gives you the access to customize them in settings.",
            "No need for extra lock apps, \nSnippet gives you a Security to your precious information with a fingerprint lock, the same one you have on your lock screen."
    };

    @Override
    public int getCount() {
        return slide_Headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//        return super.instantiateItem(container, position);
        LayoutInflater la_inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = la_inflater.inflate(R.layout.the_slides, container, false);

        ImageView slideImageView = view.findViewById(R.id.slide_image);
        TextView slideHead = view.findViewById(R.id.slide_head);
        TextView slideText = view.findViewById(R.id.slide_text);

        //Anim for res
        Animation small_ToBig = AnimationUtils.loadAnimation(context, R.anim.smalltobig);
        Animation head_anim = AnimationUtils.loadAnimation(context, R.anim.text_up);
        Animation desc_anim = AnimationUtils.loadAnimation(context, R.anim.text_up2);

        slideImageView.startAnimation(small_ToBig);
        slideHead.startAnimation(head_anim);
        slideText.startAnimation(desc_anim);


        slideImageView.setImageResource(slide_images[position]);
        slideHead.setText(slide_Headings[position]);
        slideText.setText(slide_text[position]);

        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((RelativeLayout)object);

    }
}
