package com.a1kesamose.wordclock.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.a1kesamose.wordclock.BaseObject.SquareView;
import com.a1ksamose.wordclock.R;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ActivitySettings extends AppCompatActivity implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener{
    private SquareView squareViewBackgroundColor;
    private SquareView squareViewTextColor;
    private TextView textViewBackgroundTypeIcon;
    private TextView textViewClockTypeIcon;
    private TextView textViewBackgroundImageIcon;
    private TextView textViewTextAlignment;
    private TextView textViewTextSize;
    private TextView textViewTextThickness;
    private SharedPreferences sharedPreferences;
    private Typeface typefaceFontAwesome;
    private Context context;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        typefaceFontAwesome = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        context = this;

        squareViewBackgroundColor = (SquareView)findViewById(R.id.activity_settings_squareView_background_color_icon);
        squareViewTextColor = (SquareView)findViewById(R.id.activity_settings_squareView_text_color_icon);
        textViewBackgroundTypeIcon = (TextView)findViewById(R.id.activity_settings_textView_background_type_icon);
        textViewClockTypeIcon = (TextView)findViewById(R.id.activity_settings_textView_clock_type_icon);
        textViewBackgroundImageIcon = (TextView)findViewById(R.id.activity_settings_textView_background_image_icon);
        textViewTextAlignment = (TextView)findViewById(R.id.activity_settings_textView_text_alignment_icon);
        textViewTextSize = (TextView)findViewById(R.id.activity_settings_textView_text_size_icon);
        textViewTextThickness = (TextView)findViewById(R.id.activity_settings_textView_text_thickness_icon);

        squareViewBackgroundColor.setBackgroundColor(sharedPreferences.getInt("background_color", Color.CYAN));
        squareViewTextColor.setBackgroundColor(sharedPreferences.getInt("text_color", Color.WHITE));

        textViewClockTypeIcon.setTypeface(typefaceFontAwesome);
        textViewBackgroundTypeIcon.setTypeface(typefaceFontAwesome);
        textViewBackgroundImageIcon.setTypeface(typefaceFontAwesome);
        textViewTextAlignment.setTypeface(typefaceFontAwesome);

        if(sharedPreferences.getInt("background_type", 0) == 0){
            textViewBackgroundTypeIcon.setText(getResources().getString(R.string.fa_paint_brush));
        }else if(sharedPreferences.getInt("background_type", 0) == 1){
            textViewBackgroundTypeIcon.setText(getResources().getString(R.string.fa_picture_o));
        }
        if(sharedPreferences.getInt("clock_type", 0) == 0){
            textViewClockTypeIcon.setText(getResources().getString(R.string.fa_clock_o));
        }else{
            textViewClockTypeIcon.setText(getResources().getString(R.string.fa_font));
        }
        if(sharedPreferences.getInt("text_alignment", 1) == 0){
            textViewTextAlignment.setText(getResources().getString(R.string.fa_align_left));
        }else if(sharedPreferences.getInt("text_alignment", 1) == 1){
            textViewTextAlignment.setText(getResources().getString(R.string.fa_align_center));
        }else if(sharedPreferences.getInt("text_alignment", 1) == 2){
            textViewTextAlignment.setText(getResources().getString(R.string.fa_align_right));
        }
        textViewTextSize.setText(String.format("%.0f", sharedPreferences.getFloat("text_size", 4f)));
        textViewTextThickness.setText(String.format("%.0f", sharedPreferences.getFloat("text_thickness", 50f)));

        textViewClockTypeIcon.setOnClickListener(this);
        textViewBackgroundTypeIcon.setOnClickListener(this);
        squareViewBackgroundColor.setOnClickListener(this);
        squareViewTextColor.setOnClickListener(this);
        textViewBackgroundImageIcon.setOnClickListener(this);
        textViewTextAlignment.setOnClickListener(this);
        textViewTextSize.setOnClickListener(this);
        textViewTextThickness.setOnClickListener(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK){
            Crop.of(intent.getData(), Uri.fromFile(new File(getCacheDir(), "cropped"))).asSquare().start(this);
        }else if(requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK){
            alertDialogBackgroundImagePicker(Crop.getOutput(intent));
        }else {
            Toast.makeText(this, Crop.getError(intent).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.activity_settings_textView_clock_type_icon:{
                alertDialogClockType();

                break;
            }
            case R.id.activity_settings_textView_background_type_icon:{
                if(sharedPreferences.getBoolean("background_image_uri_set", false)){
                    alertDialogBackgroundTypePicker();
                }else {
                    Toast.makeText(getApplicationContext(), "Background image not selected!", Toast.LENGTH_LONG).show();
                }

                break;
            }
            case R.id.activity_settings_squareView_background_color_icon:{
                alertDialogColorPicker(0);

                break;
            }
            case R.id.activity_settings_squareView_text_color_icon:{
                alertDialogColorPicker(1);

                break;
            }
            case R.id.activity_settings_textView_background_image_icon:{
                if(sharedPreferences.getBoolean("background_image_uri_set", false)){
                    alertDialogBackgroundImagePicker(Uri.parse(sharedPreferences.getString("background_image_uri_string", "")));
                }else{
                    Crop.pickImage(this);
                }

                break;
            }
            case R.id.activity_settings_textView_text_alignment_icon:{
                alertDialogTextAlignmentPicker();

                break;
            }
            case R.id.activity_settings_textView_text_size_icon:{
                alertDialogTextSizePicker();

                break;
            }
            case R.id.activity_settings_textView_text_thickness_icon:{
                alertDialogTextThicknessPicker();

                break;
            }
        }
    }

    private void alertDialogBackgroundTypePicker(){
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.alert_dialog_background_type, null, false);
        final RadioButton radioButtonColor = (RadioButton)view.findViewById(R.id.alert_dialog_background_type_radioButton_color);
        final RadioButton radioButtonImage = (RadioButton)view.findViewById(R.id.alert_dialog_background_type_radioButton_image);
        switch(sharedPreferences.getInt("background_type", 0)){
            case 0:{
                radioButtonColor.setChecked(true);
                radioButtonImage.setChecked(false);

                break;
            }
            case 1:{
                radioButtonColor.setChecked(false);
                radioButtonImage.setChecked(true);

                break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select background type");
        builder.setView(view);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(radioButtonColor.isChecked()){
                    sharedPreferences.edit().putInt("background_type", 0).apply();
                }else if(radioButtonImage.isChecked()){
                    sharedPreferences.edit().putInt("background_type", 1).apply();
                }
            }
        });
        builder.create().show();
    }

    private void alertDialogColorPicker(int t){
        final int target = t;

        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.alert_dialog_color_picker, null, false);
        final ColorPicker colorPicker = (ColorPicker)view.findViewById(R.id.dialog_preference_color_picker_colorPicker);
        final SaturationBar saturationBar = (SaturationBar)view.findViewById(R.id.dialog_preference_color_picker_saturationBar);
        colorPicker.addSaturationBar(saturationBar);
        colorPicker.setShowOldCenterColor(false);
        switch(target){
            case 0:{
                colorPicker.setColor(sharedPreferences.getInt("background_color", Color.CYAN));

                break;
            }
            case 1:{
                colorPicker.setColor(sharedPreferences.getInt("text_color", Color.WHITE));

                break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select color");
        builder.setView(view);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch(target){
                    case 0:{
                        sharedPreferences.edit().putInt("background_color", colorPicker.getColor()).apply();

                        break;
                    }
                    case 1:{
                        sharedPreferences.edit().putInt("text_color", colorPicker.getColor()).apply();

                        break;
                    }
                }
            }
        });
        builder.create().show();
    }

    private void alertDialogClockType(){
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.alert_dialog_clock_type, null, false);
        final RadioButton radioButtonDigital = (RadioButton)view.findViewById(R.id.alert_dialog_clock_type_radioButton_digital);
        final RadioButton radioButtonWords = (RadioButton)view.findViewById(R.id.alert_dialog_clock_type_radioButton_words);
        switch(sharedPreferences.getInt("clock_type", 0)){
            case 0:{
                radioButtonDigital.setChecked(true);
                radioButtonWords.setChecked(false);

                break;
            }
            case 1:{
                radioButtonDigital.setChecked(false);
                radioButtonWords.setChecked(true);

                break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select clock type");
        builder.setView(view);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(radioButtonDigital.isChecked()) {
                    sharedPreferences.edit().putInt("clock_type", 0).apply();
                } else if(radioButtonWords.isChecked()) {
                    sharedPreferences.edit().putInt("clock_type", 1).apply();
                }
            }
        });
        builder.create().show();
    }

    private void alertDialogBackgroundImagePicker(final Uri uriImage){
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.alert_dialog_background_image_picker, null, false);
        ImageView imageView = (ImageView)view.findViewById(R.id.alert_dialog_background_image_picker_imageView);
        imageView.setImageURI(uriImage);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set background image");
        builder.setView(view);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uriImage);
                    FileOutputStream fos = context.openFileOutput("background_image.png", Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                    sharedPreferences.edit().putString("background_image_uri_string", Uri.fromFile(getFileStreamPath("background_image.png")).toString()).apply();
                    sharedPreferences.edit().putBoolean("background_image_uri_set", true).apply();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNeutralButton("Select image", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Crop.pickImage((Activity) context);
            }
        });
        builder.create().show();
    }

    private void alertDialogTextAlignmentPicker(){
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.alert_dialog_text_alignment, null, false);
        final RadioButton radioButtonLeft = (RadioButton)view.findViewById(R.id.alert_dialog_text_alignment_radioButton_left);
        final RadioButton radioButtonCenter = (RadioButton)view.findViewById(R.id.alert_dialog_text_alignment_radioButton_center);
        final RadioButton radioButtonRight = (RadioButton)view.findViewById(R.id.alert_dialog_text_alignment_radioButton_right);
        switch(sharedPreferences.getInt("text_alignment", 1)){
            case 0:{
                radioButtonLeft.setChecked(true);
                radioButtonCenter.setChecked(false);
                radioButtonRight.setChecked(false);

                break;
            }
            case 1:{
                radioButtonLeft.setChecked(false);
                radioButtonCenter.setChecked(true);
                radioButtonRight.setChecked(false);

                break;
            }
            case 2:{
                radioButtonLeft.setChecked(false);
                radioButtonCenter.setChecked(false);
                radioButtonRight.setChecked(true);

                break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set text alignment");
        builder.setView(view);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(radioButtonLeft.isChecked()) {
                    sharedPreferences.edit().putInt("text_alignment", 0).apply();
                } else if(radioButtonCenter.isChecked()) {
                    sharedPreferences.edit().putInt("text_alignment", 1).apply();
                } else {
                    sharedPreferences.edit().putInt("text_alignment", 2).apply();
                }
            }
        });
        builder.create().show();
    }

    private void alertDialogTextSizePicker(){
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.alert_dialog_text_size, null, false);
        final SquareView squareViewDemo = (SquareView)view.findViewById(R.id.alert_dialog_text_size_squareTextView_size_demo);
        final EditText editTextInput = (EditText)view.findViewById(R.id.alert_dialog_text_size_editText_size_input);
        final Canvas canvas = new Canvas();

        squareViewDemo.setTextThickness(sharedPreferences.getFloat("text_thickness", 4f));
        squareViewDemo.setTextSize(sharedPreferences.getFloat("text_size", 50f));
        squareViewDemo.setText(String.format("%.0f", sharedPreferences.getFloat("text_size", 50f)));
        squareViewDemo.draw(canvas);

        editTextInput.setText(String.format("%.0f", sharedPreferences.getFloat("text_size", 50f)));

        editTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(editTextInput.getText().toString().length() > 0){
                    squareViewDemo.setTextSize(Float.parseFloat(editTextInput.getText().toString()));
                    squareViewDemo.setText(String.format("%.0f", Float.parseFloat(editTextInput.getText().toString())));
                    squareViewDemo.draw(canvas);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(editTextInput.getText().toString().length() > 0){
                    squareViewDemo.setTextSize(Float.parseFloat(editTextInput.getText().toString()));
                    squareViewDemo.setText(String.format("%.0f", Float.parseFloat(editTextInput.getText().toString())));
                    squareViewDemo.draw(canvas);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editTextInput.getText().toString().length() > 0){
                    squareViewDemo.setTextSize(Float.parseFloat(editTextInput.getText().toString()));
                    squareViewDemo.setText(String.format("%.0f", Float.parseFloat(editTextInput.getText().toString())));
                    squareViewDemo.draw(canvas);
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set text size");
        builder.setView(view);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sharedPreferences.edit().putFloat("text_size", Float.parseFloat(editTextInput.getText().toString())).apply();
            }
        });
        builder.create().show();
    }

    private void alertDialogTextThicknessPicker(){
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.alert_dialog_text_thickness, null, false);
        final SquareView squareViewDemo = (SquareView)view.findViewById(R.id.alert_dialog_text_size_squareTextView_thickness_demo);
        final EditText editTextInput = (EditText)view.findViewById(R.id.alert_dialog_text_size_editText_thickness_input);

        final Canvas canvas = new Canvas();
        squareViewDemo.setTextThickness(sharedPreferences.getFloat("text_thickness", 4f));
        squareViewDemo.setTextSize(sharedPreferences.getFloat("text_size", 50f));
        squareViewDemo.setText(String.format("%.0f", sharedPreferences.getFloat("text_thickness", 4f)));
        squareViewDemo.draw(canvas);

        editTextInput.setText(String.format("%.0f", sharedPreferences.getFloat("text_thickness", 4f)));

        editTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(editTextInput.getText().toString().length() > 0) {
                    squareViewDemo.setTextThickness(Float.parseFloat(editTextInput.getText().toString()));
                    squareViewDemo.setText(String.format("%.0f", Float.parseFloat(editTextInput.getText().toString())));
                    squareViewDemo.draw(canvas);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(editTextInput.getText().toString().length() > 0) {
                    squareViewDemo.setTextThickness(Float.parseFloat(editTextInput.getText().toString()));
                    squareViewDemo.setText(String.format("%.0f", Float.parseFloat(editTextInput.getText().toString())));
                    squareViewDemo.draw(canvas);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editTextInput.getText().toString().length() > 0) {
                    squareViewDemo.setTextThickness(Float.parseFloat(editTextInput.getText().toString()));
                    squareViewDemo.setText(String.format("%.0f", Float.parseFloat(editTextInput.getText().toString())));
                    squareViewDemo.draw(canvas);
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set text size");
        builder.setView(view);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sharedPreferences.edit().putFloat("text_thickness", Float.parseFloat(editTextInput.getText().toString())).apply();
            }
        });
        builder.create().show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("background_type")){
            if(sharedPreferences.getInt("background_type", 0) == 0){
                textViewBackgroundTypeIcon.setText(getResources().getString(R.string.fa_paint_brush));
            }else if(sharedPreferences.getInt("background_type", 0) == 1){
                textViewBackgroundTypeIcon.setText(getResources().getString(R.string.fa_picture_o));
            }
        }else if(key.equals("background_color")){
            squareViewBackgroundColor.setBackgroundColor(sharedPreferences.getInt("background_color", Color.CYAN));
        }else if(key.equals("text_color")){
            squareViewTextColor.setBackgroundColor(sharedPreferences.getInt("text_color", Color.WHITE));
        }else if(key.equals("clock_type")){
            if(sharedPreferences.getInt("clock_type", 0) == 0){
                textViewClockTypeIcon.setText(getResources().getString(R.string.fa_clock_o));
            }else if(sharedPreferences.getInt("clock_type", 0) == 1){
                textViewClockTypeIcon.setText(getResources().getString(R.string.fa_font));
            }
        }else if(key.equals("text_alignment")){
            if(sharedPreferences.getInt("text_alignment", 1) == 0){
                textViewTextAlignment.setText(getResources().getString(R.string.fa_align_left));
            }else if(sharedPreferences.getInt("text_alignment", 1) == 1){
                textViewTextAlignment.setText(getResources().getString(R.string.fa_align_center));
            }else{
                textViewTextAlignment.setText(getResources().getString(R.string.fa_align_right));
            }
        }else if(key.equals("text_size")){
            textViewTextSize.setText(String.format("%.0f", sharedPreferences.getFloat("text_size", 50f)));
        }else if(key.equals("text_thickness")){
            textViewTextThickness.setText(String.format("%.0f", sharedPreferences.getFloat("text_thickness", 4f)));
        }
    }
}
