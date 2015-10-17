package com.a1kesamose.wordclock.Service;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WordClockWallpaperService extends WallpaperService{
    @Override
    public Engine onCreateEngine() {
        return new WordClockWallpaperEngine();
    }

    private class WordClockWallpaperEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener{
        private SharedPreferences sharedPreferences;
        private int backgroundType;
        private int backgroundColor;
        private int textColor;
        private int clockType;
        private int textAlignment;
        private Paint.Align paintTextAlignment;
        private float textSize;
        private int textThickness;

        private Bitmap bitmap;
        private Rect sourceRect;
        private Rect destinationRect;
        private Paint paint;

        private SimpleDateFormat sdfTime;
        private Calendar calendar;

        private boolean isVisible = false;

        private final String minute[] = {"Twelve", "One", "Two", "Three", "Four",
                                         "Five", "Six", "Seven", "Eight", "Nine",
                                         "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen",
                                         "Quarter", "Sixteen", "Seventeen", "Eighteen", "Nineteen",
                                         "Twenty", "Twenty one", "Twenty two", "Twenty three", "Twenty four",
                                         "Twenty five", "Twenty six", "Twenty seven", "Twenty eight", "Twenty nine",
                                         "Half", "Thirty one", "Thirty two", "Thirty three", "Thirty four",
                                         "Thirty five", "Thirty six", "Thirty seven", "Thirty eight", "Thirty nine",
                                         "Forty", "Forty one", "Forty two", "Forty three", "Forty four",
                                         "Ouarter", "Forty six", "Forty seven", "Forty eight", "Forty nine",
                                         "Fifty", "Fifty one", "Fifty two", "Fifty three", "Fifty four",
                                         "Fifty five", "Fifty six", "Fifty seven", "Fifty eight", "Fifty nine"};
        private String timeStamp[] = {"", "", "", ""};
        private int x[];
        private int y[];
        private int width;
        private int height;

        private final Handler handler = new Handler();
        private final Runnable drawRunnable = new Runnable(){
            public void run(){
                if(clockType == 0){
                    drawDigitalClock();
                }else if(clockType == 1){
                    drawWordClock();
                }
            }
        };

        public WordClockWallpaperEngine(){
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);

            backgroundType = sharedPreferences.getInt("background_type", 0);
            if(backgroundType == 1){
                bitmap = null;
                try {
                    FileInputStream fis = getApplicationContext().openFileInput("background_image.png");
                    bitmap = BitmapFactory.decodeStream(fis);
                    fis.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
            backgroundColor = sharedPreferences.getInt("background_color", Color.CYAN);
            textColor = sharedPreferences.getInt("text_color", Color.WHITE);
            clockType = sharedPreferences.getInt("clock_type", 0);
            textAlignment = sharedPreferences.getInt("text_alignment", 1);
            switch(textAlignment){
                case 0:{
                    paintTextAlignment = Paint.Align.LEFT;

                    break;
                }
                case 1:{
                    paintTextAlignment = Paint.Align.CENTER;
                    break;
                }
                case 2:{
                    paintTextAlignment = Paint.Align.RIGHT;

                    break;
                }
            }
            textSize = sharedPreferences.getFloat("text_size", 50f);

            sdfTime = new SimpleDateFormat("HH:mm:ss");

            x = new int[3];
            y = new int[4];

            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(4f);
            paint.setStrokeJoin(Paint.Join.ROUND);

            handler.post(drawRunnable);
        }

        public void onVisibilityChanged(boolean isVisible){
            this.isVisible = isVisible;

            if(isVisible){
                handler.post(drawRunnable);
            }else{
                handler.removeCallbacks(drawRunnable);
            }
        }

        public void onSurfaceChanged(SurfaceHolder surfaceHolder, int format, int w, int h){
            super.onSurfaceChanged(surfaceHolder, format, w, h);
        }

        public void onSurfaceDestroyed(SurfaceHolder surfaceHolder){
            isVisible = false;
            handler.removeCallbacks(drawRunnable);
        }

        private void drawDigitalClock(){
            SurfaceHolder surfaceHolder = getSurfaceHolder();
            Canvas canvas = null;

            try{
                canvas = surfaceHolder.lockCanvas();
                width = canvas.getWidth();
                height = canvas.getHeight();

                x[0] = (int)(textSize / 4);
                x[1] = width / 2;
                x[2] = width - (int)(textSize / 4);
                y[0] = (height / 2) - (int)textSize;
                y[1] = (height / 2);
                y[2] = (height / 2) + (int)textSize;
                y[3] = (height / 2) + (int)(2 * textSize);

                switch(backgroundType){
                    case 0:{
                        paint.setColor(backgroundColor);
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawPaint(paint);

                        break;
                    }
                    case 1:{
                        if(width < height){
                            sourceRect = new Rect(((bitmap.getWidth() - ((width * bitmap.getHeight()) / height)) / 2), 0, (((width * bitmap.getHeight()) / height) + ((bitmap.getWidth() - ((width * bitmap.getHeight()) / height)) / 2)), bitmap.getHeight());
                        }else{
                            sourceRect = new Rect(0, ((bitmap.getHeight() - ((height * bitmap.getWidth()) / width)) / 2), bitmap.getWidth(), (((height * bitmap.getWidth()) / width) + ((bitmap.getHeight() - ((height * bitmap.getWidth()) / width)) / 2)));
                        }
                        destinationRect = new Rect(0, 0, width, height);
                        canvas.drawBitmap(bitmap, sourceRect, destinationRect, null);

                        break;
                    }
                }

                paint.setTextSize(textSize);
                paint.setColor(textColor);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setTextAlign(paintTextAlignment);

                canvas.drawText(sdfTime.format(Calendar.getInstance().getTime()), x[textAlignment], y[1], paint);
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                if(canvas != null){
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }

            if(isVisible){
                handler.postDelayed(drawRunnable, 1000);
            }
        }

        private void drawWordClock(){
            SurfaceHolder surfaceHolder = getSurfaceHolder();
            Canvas canvas = null;
            calendar = Calendar.getInstance();

            if(calendar.get(Calendar.MINUTE) == 0){
                timeStamp[0] = minute[calendar.get(Calendar.HOUR)];
                timeStamp[1] = "\'o\' clock";
            }else if(calendar.get(Calendar.MINUTE) == 1){
                timeStamp[0] = minute[calendar.get(Calendar.MINUTE)];
                timeStamp[1] = "minute";
                timeStamp[2] = "past";
                timeStamp[3] = minute[calendar.get(Calendar.HOUR)].toLowerCase();
            }else if(calendar.get(Calendar.MINUTE) > 1 && calendar.get(Calendar.MINUTE) < 15){
                timeStamp[0] = minute[calendar.get(Calendar.MINUTE)];
                timeStamp[1] = "minutes";
                timeStamp[2] = "past";
                timeStamp[3] = minute[calendar.get(Calendar.HOUR)].toLowerCase();
            }else if(calendar.get(Calendar.MINUTE) == 15){
                timeStamp[0] = minute[calendar.get(Calendar.MINUTE)];
                timeStamp[1] = "past";
                timeStamp[2] = minute[calendar.get(Calendar.HOUR)].toLowerCase();
            }else if(calendar.get(Calendar.MINUTE) > 15 && calendar.get(Calendar.MINUTE) < 30){
                timeStamp[0] = minute[calendar.get(Calendar.MINUTE)];
                timeStamp[1] = "minutes";
                timeStamp[2] = "past";
                timeStamp[3] = minute[calendar.get(Calendar.HOUR)].toLowerCase();
            }else if(calendar.get(Calendar.MINUTE) == 30){
                timeStamp[0] = minute[calendar.get(Calendar.MINUTE)];
                timeStamp[1] = "past";
                timeStamp[2] = minute[calendar.get(Calendar.HOUR)].toLowerCase();
            }else if(calendar.get(Calendar.MINUTE) > 30 && calendar.get(Calendar.MINUTE) < 45){
                timeStamp[0] = minute[calendar.get(Calendar.MINUTE)];
                timeStamp[1] = "minutes";
                timeStamp[2] = "to";
                timeStamp[3] = minute[(calendar.get(Calendar.HOUR) + 1) % 12].toLowerCase();
            }else if(calendar.get(Calendar.MINUTE) == 45){
                timeStamp[0] = minute[calendar.get(Calendar.MINUTE)];
                timeStamp[1] = "to";
                timeStamp[2] = minute[(calendar.get(Calendar.HOUR) + 1) % 12].toLowerCase();
            }else{
                timeStamp[0] = minute[calendar.get(Calendar.MINUTE)];
                timeStamp[1] = "minutes";
                timeStamp[2] = "to";
                timeStamp[3] = minute[(calendar.get(Calendar.HOUR) + 1) % 12].toLowerCase();
            }

            try{
                canvas = surfaceHolder.lockCanvas();
                width = canvas.getWidth();
                height = canvas.getHeight();

                x[0] = (int)(textSize / 4);
                x[1] = width / 2;
                x[2] = width - (int)(textSize / 4);
                y[0] = (height / 2) - (int)textSize;
                y[1] = (height / 2);
                y[2] = (height / 2) + (int)textSize;
                y[3] = (height / 2) + (int)(2 * textSize);

                switch(backgroundType){
                    case 0:{
                        paint.setColor(backgroundColor);
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawPaint(paint);

                        break;
                    }
                    case 1:{
                        if(width < height){
                            sourceRect = new Rect(((bitmap.getWidth() - ((width * bitmap.getHeight()) / height)) / 2), 0, (((width * bitmap.getHeight()) / height) + ((bitmap.getWidth() - ((width * bitmap.getHeight()) / height)) / 2)), bitmap.getHeight());
                        }else{
                            sourceRect = new Rect(0, ((bitmap.getHeight() - ((height * bitmap.getWidth()) / width)) / 2), bitmap.getWidth(), (((height * bitmap.getWidth()) / width) + ((bitmap.getHeight() - ((height * bitmap.getWidth()) / width)) / 2)));
                        }
                        destinationRect = new Rect(0, 0, width, height);

                        canvas.drawBitmap(bitmap, sourceRect, destinationRect, null);

                        break;
                    }
                }

                paint.setTextSize(textSize);
                paint.setColor(textColor);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setTextAlign(paintTextAlignment);

                canvas.drawText(timeStamp[0], x[textAlignment], y[0], paint);
                canvas.drawText(timeStamp[1], x[textAlignment], y[1], paint);
                canvas.drawText(timeStamp[2], x[textAlignment], y[2], paint);
                canvas.drawText(timeStamp[3], x[textAlignment], y[3], paint);
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                if(canvas != null){
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }

            if(isVisible){
                handler.postDelayed(drawRunnable, 1000);
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals("background_type")){
                backgroundType = sharedPreferences.getInt("background_type", 0);
                if(backgroundType == 1){
                    bitmap = null;
                    try {
                        FileInputStream fis = getApplicationContext().openFileInput("background_image.png");
                        bitmap = BitmapFactory.decodeStream(fis);
                        fis.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }else if(key.equals("background_color")){
                backgroundColor = sharedPreferences.getInt("background_color", Color.CYAN);
            }else if(key.equals("text_color")){
                textColor = sharedPreferences.getInt("text_color", Color.WHITE);
            }else if(key.equals("clock_type")){
                clockType = sharedPreferences.getInt("clock_type", 0);
            }else if(key.equals("text_alignment")){
                textAlignment = sharedPreferences.getInt("text_alignment", 1);
                switch(textAlignment){
                    case 0:{
                        paintTextAlignment = Paint.Align.LEFT;

                        break;
                    }
                    case 1:{
                        paintTextAlignment = Paint.Align.CENTER;

                        break;
                    }
                    case 2:{
                        paintTextAlignment = Paint.Align.RIGHT;

                        break;
                    }
                }
            }else if(key.equals("text_size")){
                textSize = sharedPreferences.getFloat("text_size", 50f);
            }
        }
    }
}
