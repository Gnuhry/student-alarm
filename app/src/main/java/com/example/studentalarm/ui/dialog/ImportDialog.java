package com.example.studentalarm.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
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

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.example.studentalarm.R;
import com.example.studentalarm.imports.ICS;
import com.example.studentalarm.imports.Import;
import com.example.studentalarm.imports.dhbwMannheim.Course;
import com.example.studentalarm.imports.dhbwMannheim.CourseCategory;
import com.example.studentalarm.imports.dhbwMannheim.CourseImport;
import com.example.studentalarm.imports.dhbwMannheim.DhbwCourses;
import com.example.studentalarm.save.PreferenceKeys;

import java.util.Arrays;
import java.util.List;


public class ImportDialog extends Dialog {

    private static final String LINK_BEGIN = "http://vorlesungsplan.dhbw-mannheim.de/ical.php?uid=";
    private static final String LOG = "ImportDialog";
    private boolean isValid = false;
    private String lastValidString;
    private DhbwCourses dhbwCourses;


    public ImportDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG, "open");
        setContentView(R.layout.dialog_import);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        new Thread(() -> {
            Log.i(LOG, "get DHBW course");

            String[] files = getContext().fileList();
            List<String> list = Arrays.asList(files);
            Log.d("Check", "Is DHBWCOURSES in: "+list);
            if (!list.contains("DHBWCOURSES")){
                Log.i(LOG, "Import from WEB");
                dhbwCourseImport();
            }else{
                Log.i(LOG, "Import from DHBWCOURSES");
                dhbwCourses.load(getContext());
                if (dhbwCourses==null||dhbwCourses.getCoursecategorys()==null){
                    dhbwCourseImport();
                }
            }
            if (dhbwCourses==null||dhbwCourses.getCoursecategorys()==null)
                return;

            ArrayAdapter<CourseCategory> categoryAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, dhbwCourses.getCoursecategorys());
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            findViewById(R.id.spDHBWMaCourseCategory).post(() -> ((Spinner) findViewById(R.id.spDHBWMaCourseCategory)).setAdapter(categoryAdapter));
            ((Spinner) findViewById(R.id.spDHBWMaCourseCategory)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(@NonNull AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d(LOG, "Spinner:" + adapterView.getItemAtPosition(i).toString());
                    ArrayAdapter<Course> courseAdapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, ((CourseCategory) adapterView.getItemAtPosition(i)).getCourses());
                    courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    findViewById(R.id.spDHBWMaCourse).post(() -> ((Spinner) findViewById(R.id.spDHBWMaCourse)).setAdapter(courseAdapter));
                    String course = preferences.getString(PreferenceKeys.DHBW_MANNHEIM_COURSE, null);
                    if (course != null)
                        for (int in = 0; in < courseAdapter.getCount(); in++)
                            if (0 == courseAdapter.getItem(in).compareTo(course)) {
                                int finalIn = in;
                                findViewById(R.id.spDHBWMaCourse).post(() -> ((Spinner) findViewById(R.id.spDHBWMaCourse)).setSelection(finalIn));
                            }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            String category = preferences.getString(PreferenceKeys.DHBW_MANNHEIM_COURSE_CATEGORY, null);
            if (category != null) {
                for (int i = 0; i < categoryAdapter.getCount(); i++)
                    if (0 == categoryAdapter.getItem(i).compareTo(category)) {
                        int finalI = i;
                        findViewById(R.id.spDHBWMaCourseCategory).post(() -> ((Spinner) findViewById(R.id.spDHBWMaCourseCategory)).setSelection(finalI));
                    }
            }
        }).start();

        switch (preferences.getInt(PreferenceKeys.MODE, Import.ImportFunction.NONE)) {
            case Import.ImportFunction.NONE:
                ((RadioButton) findViewById(R.id.rBtnNone)).setChecked(true);
                break;
            case Import.ImportFunction.ICS:
                ((RadioButton) findViewById(R.id.rBtnICS)).setChecked(true);
                findViewById(R.id.LLLink).setVisibility(View.VISIBLE);
                break;
            case Import.ImportFunction.DHBWMA:
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
            Log.i(LOG, "save");
            if (((RadioButton) findViewById(R.id.rBtnICS)).isChecked()) {
                Log.d(LOG, "ICS");
                if (isValid) {
                    preferences.edit().putInt(PreferenceKeys.MODE, Import.ImportFunction.ICS).apply();
                    preferences.edit().putString(PreferenceKeys.LINK, ((EditText) findViewById(R.id.edTLink)).getText().toString()).apply();
                    new Thread(() -> Import.importLecture(this.getContext())).start();
                    Toast.makeText(getContext(), R.string.it_may_take_a_minute_until_the_change_is_visible_in_the_calendar, Toast.LENGTH_LONG).show();
                    this.cancel();
                } else
                    Toast.makeText(getContext(), R.string.missing_checked_valid_url, Toast.LENGTH_SHORT).show();
            } else if (((RadioButton) findViewById(R.id.rBtnNone)).isChecked()) {
                Log.d(LOG, "none");
                preferences.edit().putInt(PreferenceKeys.MODE, Import.ImportFunction.NONE).apply();
                this.cancel();
            } else if (((RadioButton) findViewById(R.id.rBtnDHBWMa)).isChecked()) {
                Log.d(LOG, "dhbw");
                Course course = ((Course) ((Spinner) findViewById(R.id.spDHBWMaCourse)).getSelectedItem());
                CourseCategory category = ((CourseCategory) ((Spinner) findViewById(R.id.spDHBWMaCourseCategory)).getSelectedItem());
                if (course == null || category == null)
                    Toast.makeText(getContext(), R.string.missing_selected_course, Toast.LENGTH_SHORT).show();
                else {
                    preferences.edit()
                            .putInt(PreferenceKeys.MODE, Import.ImportFunction.DHBWMA)
                            .putString(PreferenceKeys.LINK, LINK_BEGIN + course.getCourseID())
                            .putString(PreferenceKeys.DHBW_MANNHEIM_COURSE, course.getCourseName())
                            .putString(PreferenceKeys.DHBW_MANNHEIM_COURSE_CATEGORY, category.getCourseCategory())
                            .apply();
                    new Thread(() -> Import.importLecture(this.getContext())).start();
                    Toast.makeText(getContext(), R.string.it_may_take_a_minute_until_the_change_is_visible_in_the_calendar, Toast.LENGTH_LONG).show();
                    this.cancel();
                }
            }
        });

        findViewById(R.id.btnImportDhbwCourses).setOnClickListener(view22 -> {
            Log.i(LOG, "Start new Import");
                dhbwCourseImport();
        });

        findViewById(R.id.btnCancel).setOnClickListener(view1 -> {
            Log.i(LOG, "cancel");
            this.cancel();
        });

        findViewById(R.id.btnCheckLink).setOnClickListener(view12 -> {
            String text = ((EditText) findViewById(R.id.edTLink)).getText().toString();
            Log.i(LOG, "check link: " + text);
            if (!URLUtil.isValidUrl(text)) {
                Toast.makeText(getContext(), R.string.string_is_not_a_valid_url, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Import.checkConnection(getContext())) return;
            findViewById(R.id.btnCheckLink).setEnabled(false);
            ImageView imageView = findViewById(R.id.imgStatus);
            Glide.with(getContext()).load(R.drawable.sandglass).into(imageView);
            new Thread(() -> {
                Log.d(LOG, "start check thread");
                String icsFile = Import.runSynchronous(text);
                if (icsFile == null) return;
                isValid = new ICS(icsFile).isSuccessful();
                findViewById(R.id.btnCheckLink).post(() -> findViewById(R.id.btnCheckLink).setEnabled(true));
                findViewById(R.id.imgStatus).post(() -> ((ImageView) findViewById(R.id.imgStatus)).setImageResource(isValid ? R.drawable.right : R.drawable.cross));
                if (isValid) lastValidString = text;
            }).start();
        });
    }

    private void dhbwCourseImport() {
        if (Import.checkConnection(getContext())) {
            Log.i(LOG, "Import startet");
            new Thread(() -> {
            findViewById(R.id.btnImportDhbwCourses).post(() -> findViewById(R.id.btnImportDhbwCourses).setEnabled(false));
            findViewById(R.id.imgStatusDHBW).post(() ->findViewById(R.id.imgStatusDHBW).setVisibility(View.VISIBLE));
            findViewById(R.id.imgStatusDHBW).post(() ->Glide.with(getContext()).load(R.drawable.sandglass).into((ImageView)findViewById(R.id.imgStatusDHBW)));
            dhbwCourses = new CourseImport(getContext()).getDHBWCourses();
            findViewById(R.id.btnImportDhbwCourses).post(() -> findViewById(R.id.btnImportDhbwCourses).setEnabled(true));
            findViewById(R.id.imgStatusDHBW).post(() -> findViewById(R.id.imgStatusDHBW).setVisibility(View.GONE));
            }).start();
        }
    }
}
