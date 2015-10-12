package com.a1kesamose.wordclock.BaseObject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class SquareView extends View {
    public SquareView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        if(widthMeasureSpec < heightMeasureSpec){
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
        }else{
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        }
    }
}
