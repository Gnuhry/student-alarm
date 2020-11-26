package com.example.studentalarm.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.studentalarm.dhbw_mannheim.Course;
import com.example.studentalarm.dhbw_mannheim.CourseCategory;
import com.example.studentalarm.dhbw_mannheim.CourseImport;
import com.example.studentalarm.import_.ICS;
import com.example.studentalarm.import_.Import;
import com.example.studentalarm.PreferenceKeys;
import com.example.studentalarm.R;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import static com.example.studentalarm.import_.Import.ImportFunction.DHBWMa;


public class ImportDialog extends Dialog {

    private boolean isValid = false;
    private String lastValidString;
    private final Activity activity;

    public ImportDialog(@NonNull Context context, Activity activity) {
        super(context);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_import);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        new Thread(() -> {
            ArrayAdapter<CourseCategory> categoryadapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, new CourseImport().getDHBWCourses());
            categoryadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            findViewById(R.id.spDHBWMaCourseCategory).post(() -> ((Spinner)findViewById(R.id.spDHBWMaCourseCategory)).setAdapter(categoryadapter));
            ((Spinner)findViewById(R.id.spDHBWMaCourseCategory)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//Funktioniert aus nicht erkennbaren gründen nicht wenn direkt View by Id verwendet wird
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d("Spinner Course", "adview:" + adapterView.getItemAtPosition(1) + " view :" + view + " i " + i + " l " + l+"  Coursecat: "+adapterView.getItemAtPosition(i));
                    ArrayAdapter<Course> courseadapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, ((CourseCategory)adapterView.getItemAtPosition(i)).getDHBWCourses());
                    courseadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    for (Course course:((CourseCategory)adapterView.getItemAtPosition(i)).getDHBWCourses()){
                        Log.d("Spinnerelement Course","Kurs:: "+ course);
                    }
                    findViewById(R.id.spDHBWMaCourse).post(() -> ((Spinner) findViewById(R.id.spDHBWMaCourse)).setAdapter(courseadapter));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        }).start();

        switch (preferences.getInt(PreferenceKeys.MODE, Import.ImportFunction.NONE)) {
            case Import.ImportFunction.NONE:
                ((RadioButton) findViewById(R.id.rBtnNone)).setChecked(true);
                break;
            case Import.ImportFunction.ICS:
                ((RadioButton) findViewById(R.id.rBtnICS)).setChecked(true);
                findViewById(R.id.LLLink).setVisibility(View.VISIBLE);
                break;
            case DHBWMa:
                ((RadioButton) findViewById(R.id.rBtnDHBWMa)).setChecked(true);
                findViewById(R.id.LLDHBWMaCourse).setVisibility(View.VISIBLE);
                break;
        }
        ((RadioButton) findViewById(R.id.rBtnICS)).setOnCheckedChangeListener((compoundButton, b) -> findViewById(R.id.LLLink).setVisibility(b ? View.VISIBLE : View.GONE));
        ((RadioButton) findViewById(R.id.rBtnDHBWMa)).setOnCheckedChangeListener((compoundButton, b) -> findViewById(R.id.LLDHBWMaCourse).setVisibility(b ? View.VISIBLE : View.GONE));
        String s_import = preferences.getString(PreferenceKeys.LINK, null);
        if (s_import != null) {
            lastValidString = s_import;
            isValid = true;
            ((EditText) findViewById(R.id.edTLink)).setText(s_import);
            ((ImageView) findViewById(R.id.imgStatus)).setImageResource(R.drawable.right);
        }
        ((EditText) findViewById(R.id.edTLink)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(@NonNull Editable editable) {
                isValid = editable.toString().equals(lastValidString);
                ((ImageView) findViewById(R.id.imgStatus)).setImageResource(isValid ? R.drawable.right : R.drawable.question_mark);
            }
        });

        findViewById(R.id.btnSave).setOnClickListener(view1 -> {
            if (((RadioButton) findViewById(R.id.rBtnICS)).isChecked()) {
                if (isValid) {
                    preferences.edit().putInt(PreferenceKeys.MODE, Import.ImportFunction.ICS).apply();
                    preferences.edit().putString(PreferenceKeys.LINK, ((EditText) findViewById(R.id.edTLink)).getText().toString()).apply();
                    this.cancel();
                } else
                    Toast.makeText(getContext(), R.string.missing_checked_valid_url, Toast.LENGTH_SHORT).show();
            } else if (((RadioButton) findViewById(R.id.rBtnNone)).isChecked()) {
                preferences.edit().putInt(PreferenceKeys.MODE, Import.ImportFunction.NONE).apply();
                this.cancel();
            }else if (((RadioButton) findViewById(R.id.rBtnDHBWMa)).isChecked()) {
                preferences.edit().putInt("Mode", DHBWMa).apply();
                preferences.edit().putString("Link", "http://vorlesungsplan.dhbw-mannheim.de/ical.php?uid="+((Course)((Spinner) findViewById(R.id.spDHBWMaCourse)).getSelectedItem()).getCourseID()).apply();
                this.cancel();}
        });

        findViewById(R.id.btnCancel).setOnClickListener(view1 -> this.cancel());

        findViewById(R.id.btnCheckLink).setOnClickListener(view12 -> {
            String text = ((EditText) findViewById(R.id.edTLink)).getText().toString();
            if (!URLUtil.isValidUrl(text)) {
                Toast.makeText(getContext(), R.string.string_is_not_a_valid_url, Toast.LENGTH_SHORT).show();
                return;
            }
            ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() == null || !cm.getActiveNetworkInfo().isConnected()) {
                Toast.makeText(getContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
                return;
            }
            findViewById(R.id.btnCheckLink).setEnabled(false);
            ImageView imageView = findViewById(R.id.imgStatus);
            Glide.with(getContext()).load(R.drawable.sandglass).into(imageView);
            new Thread(() -> {
                isValid = new ICS(text, true).isSuccessful();
                findViewById(R.id.btnCheckLink).post(() -> findViewById(R.id.btnCheckLink).setEnabled(true));
                ((ImageView) findViewById(R.id.imgStatus)).setImageResource(isValid ? R.drawable.right : R.drawable.cross);
                if (isValid) lastValidString = text;
            }).start();
        });
    }
}