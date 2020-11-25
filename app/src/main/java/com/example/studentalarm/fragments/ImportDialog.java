package com.example.studentalarm.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.studentalarm.import_.ICS;
import com.example.studentalarm.import_.Import;
import com.example.studentalarm.PreferenceKeys;
import com.example.studentalarm.R;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;


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

        switch (preferences.getInt(PreferenceKeys.MODE, Import.ImportFunction.NONE)) {
            case Import.ImportFunction.NONE:
                ((RadioButton) findViewById(R.id.rBtnNone)).setChecked(true);
                break;
            case Import.ImportFunction.ICS:
                ((RadioButton) findViewById(R.id.rBtnICS)).setChecked(true);
                findViewById(R.id.LLLink).setVisibility(View.VISIBLE);
                break;
        }
        ((RadioButton) findViewById(R.id.rBtnICS)).setOnCheckedChangeListener((compoundButton, b) -> findViewById(R.id.LLLink).setVisibility(b ? View.VISIBLE : View.GONE));
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
            public void afterTextChanged(Editable editable) {
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
            }
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