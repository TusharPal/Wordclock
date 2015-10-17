package com.a1kesamose.wordclock.BaseObject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class SquareView extends View {
    private Paint paint;
    private String text;

    public SquareView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4f);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(50f);

        text = "";
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        if(widthMeasureSpec < heightMeasureSpec){
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
        }else{
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        }
    }

    public void setTextThickness(float thickness){
        paint.setStrokeWidth(thickness);
    }

    public void setTextSize(float size){
        paint.setTextSize(size);
    }

    public void setText(String text){
        this.text = text;
    }

    @Override
    public void draw(Canvas canvas){
        canvas.drawText(text, getWidth() / 2, getHeight() / 2, paint);

        super.draw(canvas);
    }
}
