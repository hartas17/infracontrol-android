package info.androidhive.materialdesign.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.inputmethodservice.ExtractEditText;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Hashtable;

import info.androidhive.materialdesign.R;

/**
 * Created by Hartas on 28/10/2015.
 */


public class Buttons extends LinearLayout {

    private TextView Iniciales;
    private TextView textView;
    private ImageView edit;
    private TextView cont_edit;
    Context cont;
    public Buttons(Context context) {
        super(context);
        Iniciales = new TextView(context);
        textView = new TextView(context);
        edit = new ImageView(context);
        cont_edit = new TextView(context);

        textView.setTextColor(getResources().getColor(android.R.color.white));
        Iniciales.setTextColor(getResources().getColor(android.R.color.white));

        Iniciales.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

        this.setGravity(Gravity.CENTER);
        this.setOrientation(LinearLayout.VERTICAL);
        this.addView(edit);
        this.addView(Iniciales);
        this.addView(textView);
    }


    public void setBackgroud(int resid){
        switch (resid){
            case 1:
                this.setBackgroundResource(R.drawable.color_selector);
                break;
            case 2:
                this.setBackgroundResource(R.drawable.color_selector_light_blue);
                break;
            case 3:
                this.setBackgroundResource(R.drawable.color_selector_blue);
                break;
            case 4:
                this.setBackgroundResource(R.drawable.color_selector_orange);
                break;
            case 5:
                this.setBackgroundResource(R.drawable.color_selector_purple);
                break;
            case 6:
                this.setBackgroundResource(R.color.feedback);
                break;
            case 7:
                this.setBackgroundResource(R.drawable.placeholder);
                break;
            case 8:
                this.setBackgroundResource(R.drawable.placeholder_button);
                break;
        }

    }

    public void setText(CharSequence text) {
        textView.setGravity(Gravity.BOTTOM);
        textView.setGravity(Gravity.LEFT);

        textView.setPadding(10,0,0,0);
        textView.setText(text);
    }

    public void setText2(CharSequence text,int heigh) {
        textView.setGravity(Gravity.BOTTOM);
        textView.setGravity(Gravity.LEFT);

        textView.setPadding(10,0,0,0);
        textView.setText(text);
    }


    public void setIniciales(CharSequence text,int heigh){
        Iniciales.setGravity(Gravity.CENTER_HORIZONTAL);

        Iniciales.setPadding(0,heigh/2,0,0);
        Iniciales.setText(text);
    }

    public void setIcon(int heigh){
        edit.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_edit));
        //cont_edit.setGravity(Gravity.CENTER_HORIZONTAL);

        //cont_edit.setPadding(0,heigh/2,0,0);
        //cont_edit.setBackgroundResource(R.drawable.ic_action_edit_icon);
        //Iniciales.setBackground(R.drawable.ic_action_edit_icon);
        //edit.setImageResource(R.drawable.ic_action_edit);
        //edit.setPadding(0, heigh / 2, 0, 0);

        //this.setIcon(R.drawable.ic_action_edit);

    }



    public TextView getTextView() {
        return textView;
    }
    public TextView getIniciales(){
        return Iniciales;
    }
}