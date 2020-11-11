package com.example.studentalarm.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.studentalarm.DhbwMannheimCourse;
import com.example.studentalarm.Import.DhbwMannheimCourseImport;
import com.example.studentalarm.Import.ICS;
import com.example.studentalarm.Import.Import;
import com.example.studentalarm.R;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;


public class ImportDialog extends Dialog {

    private boolean isValid = false;
    private SharedPreferences preferences;
    private String lastValidString;

    public ImportDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_import);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        new Thread(() -> {
            ArrayAdapter<DhbwMannheimCourse> adapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, new DhbwMannheimCourseImport().getDHBWCourses());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            findViewById(R.id.spDHBWMaCourse).post(() -> ((Spinner)findViewById(R.id.spDHBWMaCourse)).setAdapter(adapter));
        }).start();

        switch (preferences.getInt("Mode", Import.ImportFunction.NONE)) {
            case Import.ImportFunction.NONE:
                ((RadioButton) findViewById(R.id.rBtnNone)).setChecked(true);
                break;
            case Import.ImportFunction.ICS:
                ((RadioButton) findViewById(R.id.rBtnICS)).setChecked(true);
                findViewById(R.id.LLLink).setVisibility(View.VISIBLE);
                break;
            case Import.ImportFunction.DHBWMa:
                ((RadioButton) findViewById(R.id.rBtnDHBWMa)).setChecked(true);
                findViewById(R.id.spDHBWMaCourse).setVisibility(View.VISIBLE);
                break;
        }
        ((RadioButton) findViewById(R.id.rBtnICS)).setOnCheckedChangeListener((compoundButton, b) -> findViewById(R.id.LLLink).setVisibility(b ? View.VISIBLE : View.GONE));
        ((RadioButton) findViewById(R.id.rBtnDHBWMa)).setOnCheckedChangeListener((compoundButton, b) -> findViewById(R.id.spDHBWMaCourse).setVisibility(b ? View.VISIBLE : View.GONE));
        String s_import = preferences.getString("Link", null);
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
            public void afterTextChanged(Editable editable) {
                isValid = editable.toString().equals(lastValidString);
                ((ImageView) findViewById(R.id.imgStatus)).setImageResource(isValid ? R.drawable.right : R.drawable.question_mark);
            }
        });

        findViewById(R.id.btnSave).setOnClickListener(view1 -> {
            if (((RadioButton) findViewById(R.id.rBtnICS)).isChecked()) {
                if (isValid) {
                    preferences.edit().putInt("Mode", Import.ImportFunction.ICS).apply();
                    preferences.edit().putString("Link", ((EditText) findViewById(R.id.edTLink)).getText().toString()).apply();
                    this.cancel();
                } else
                    Toast.makeText(getContext(), R.string.missing_checked_valid_url, Toast.LENGTH_SHORT).show();
            } else if (((RadioButton) findViewById(R.id.rBtnNone)).isChecked()) {
                preferences.edit().putInt("Mode", Import.ImportFunction.NONE).apply();
                this.cancel();
            }else if (((RadioButton) findViewById(R.id.rBtnDHBWMa)).isChecked()) {
                preferences.edit().putInt("Mode", Import.ImportFunction.DHBWMa).apply();
                preferences.edit().putString("Link", "http://vorlesungsplan.dhbw-mannheim.de/ical.php?uid="+((DhbwMannheimCourse)((Spinner) findViewById(R.id.spDHBWMaCourse)).getSelectedItem()).getCourseID()).apply();
                this.cancel();
            }
        });

        findViewById(R.id.btnCancel).setOnClickListener(view1 -> this.cancel());

        findViewById(R.id.btnCheckLink).setOnClickListener(view12 -> {
            String text = ((EditText) findViewById(R.id.edTLink)).getText().toString();
            if (!URLUtil.isValidUrl(text)) {
                Toast.makeText(getContext(), R.string.string_is_not_a_valid_url, Toast.LENGTH_SHORT).show();
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