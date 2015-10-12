package com.a1kesamose.wordclock.Service;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WordClockWallpaperService extends WallpaperService implements SharedPreferences.OnSharedPreferenceChangeListener{
    private SharedPreferences sharedPreferences;
    private int backgroundType;
    private int backgroundColor;
    private int textColor;
    private int clockType;
    private int textAlignment;
    private float textSize;

    @Override
    public Engine onCreateEngine() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        backgroundType = sharedPreferences.getInt("background_type", 0);
        backgroundColor = sharedPreferences.getInt("background_color", Color.CYAN);
        textColor = sharedPreferences.getInt("text_color", Color.WHITE);
        clockType = sharedPreferences.getInt("clock_type", 0);
        textAlignment = sharedPreferences.getInt("text_alignment", 1);
        textSize = sharedPreferences.getFloat("text_size", 50f);

        return new WordClockWallpaperEngine();
    }

    @Override
    public void onDestroy(){
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("background_type")){
            backgroundType = sharedPreferences.getInt("background_type", 0);
        }else if(key.equals("background_color")){
            backgroundColor = sharedPreferences.getInt("background_color", Color.CYAN);
        }else if(key.equals("text_color")){
            textColor = sharedPreferences.getInt("text_color", Color.WHITE);
        }else if(key.equals("clock_type")){
            clockType = sharedPreferences.getInt("clock_type", 0);
        }else if(key.equals("text_alignment")){
            textAlignment = sharedPreferences.getInt("text_alignment", 1);
        }else if(key.equals("text_size")){
            textSize = sharedPreferences.getFloat("text_size", 50f);
        }
    }

    private class WordClockWallpaperEngine extends Engine{
        private SimpleDateFormat sdfTime;
        private Paint paint;
        private int width;
        private int height;
        private boolean isVisible = false;

        private final Handler handler = new Handler();
        private final Runnable drawRunnable = new Runnable(){
            public void run(){
                draw();
            }
        };

        public WordClockWallpaperEngine(){
            sdfTime = new SimpleDateFormat("HH:mm:ss");
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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

        public void onSurfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height){
            super.onSurfaceChanged(surfaceHolder, format, width, height);

            this.width = width;
            this.height = height;
        }

        public void onSurfaceDestroyed(SurfaceHolder surfaceHolder){
            isVisible = false;
            handler.removeCallbacks(drawRunnable);
        }

        private void draw(){
            SurfaceHolder surfaceHolder = getSurfaceHolder();
            Canvas canvas = null;

            try{
                canvas = surfaceHolder.lockCanvas();

                paint.setColor(backgroundColor);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawPaint(paint);

                paint.setTextSize(textSize);
                paint.setColor(textColor);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                switch(textAlignment){
                    case 0:{
                        paint.setTextAlign(Paint.Align.LEFT);
                        canvas.drawText(sdfTime.format(Calendar.getInstance().getTime()), 0, height/2, paint);

                        break;
                    }
                    case 1:{
                        paint.setTextAlign(Paint.Align.CENTER);
                        canvas.drawText(sdfTime.format(Calendar.getInstance().getTime()), width/2, height/2, paint);

                        break;
                    }
                    case 2:{
                        paint.setTextAlign(Paint.Align.RIGHT);
                        canvas.drawText(sdfTime.format(Calendar.getInstance().getTime()), width, height/2, paint);

                        break;
                    }
                }
            }catch(Exception e){

            }finally{
                if(canvas != null){
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }

            if(isVisible){
                handler.postDelayed(drawRunnable, 1000);
            }
        }
    }
}
