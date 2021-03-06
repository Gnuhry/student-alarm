package com.example.studentalarm.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.studentalarm.R;
import com.example.studentalarm.imports.ICS;
import com.example.studentalarm.imports.Import;
import com.example.studentalarm.imports.LectureSchedule;
import com.example.studentalarm.imports.dhbwMannheim.Course;
import com.example.studentalarm.imports.dhbwMannheim.CourseCategory;
import com.example.studentalarm.imports.dhbwMannheim.CourseImport;
import com.example.studentalarm.save.PreferenceKeys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;


public class ImportDialog extends Dialog {

    private static final String LINK_BEGIN = "http://vorlesungsplan.dhbw-mannheim.de/ical.php?uid=";
    private static final String LOG = "ImportDialog";
    public static final int REQUEST_CODE = 2;
    @Nullable
    private final ProgressDialog progress;
    @NonNull
    private final Activity activity;
    private static boolean phone = false;
    private static ICS ics;
    private boolean isValid = false;
    private String lastValidString;

    public ImportDialog(@NonNull Context context, @NonNull Activity activity) {
        super(context);
        progress = new ProgressDialog(context);
        progress.setTitle(context.getString(R.string.loading));
        progress.setMessage(context.getString(R.string.wait_while_loading));
        progress.setCancelable(false);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG, "open");
        setContentView(R.layout.dialog_import);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT)
            findViewById(R.id.rBtnPhone).setVisibility(View.GONE);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        switch (preferences.getInt(PreferenceKeys.MODE, Import.ImportFunction.NONE)) {
            case Import.ImportFunction.NONE:
                ((RadioButton) findViewById(R.id.rBtnNone)).setChecked(true);
                break;
            case Import.ImportFunction.PHONE:
                ((RadioButton) findViewById(R.id.rBtnPhone)).setChecked(true);
                phone = true;
                break;
            case Import.ImportFunction.ICS:
                ((RadioButton) findViewById(R.id.rBtnICS)).setChecked(true);
                findViewById(R.id.LLLink).setVisibility(View.VISIBLE);
                break;
            case Import.ImportFunction.DHBWMA:
                ((RadioButton) findViewById(R.id.rBtnDHBWMa)).setChecked(true);
                findViewById(R.id.LLDHBWMaCourse).setVisibility(View.VISIBLE);
                findViewById(R.id.rBtnDHBWMa).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
                findViewById(R.id.LLRefresh).setVisibility(View.VISIBLE);
                break;
        }
        ((RadioButton) findViewById(R.id.rBtnPhone)).setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                initFilePicker();
        });
        ((RadioButton) findViewById(R.id.rBtnICS)).setOnCheckedChangeListener((compoundButton, b) -> findViewById(R.id.LLLink).setVisibility(b ? View.VISIBLE : View.GONE));
        ((RadioButton) findViewById(R.id.rBtnDHBWMa)).setOnCheckedChangeListener((compoundButton, b) -> {
            findViewById(R.id.rBtnDHBWMa).setLayoutParams(new LinearLayout.LayoutParams(b ? LinearLayout.LayoutParams.WRAP_CONTENT : LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            findViewById(R.id.LLDHBWMaCourse).setVisibility(b ? View.VISIBLE : View.GONE);
            findViewById(R.id.LLRefresh).setVisibility(b ? View.VISIBLE : View.GONE);
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
                preferences.edit().putString(PreferenceKeys.IMPORT_TIME, PreferenceKeys.DEFAULT_IMPORT_TIME).apply();
                preferences.edit().putBoolean(PreferenceKeys.AUTO_IMPORT, false).apply();
                this.cancel();
            } else if (((RadioButton) findViewById(R.id.rBtnPhone)).isChecked()) {
                Log.d(LOG, "phone");
                if (phone && ics != null) {
                    preferences.edit().putInt(PreferenceKeys.MODE, Import.ImportFunction.PHONE).apply();
                    LectureSchedule.load(getContext()).importICS(ics, getContext()).save(getContext());
                    Toast.makeText(getContext(), R.string.it_may_take_a_minute_until_the_change_is_visible_in_the_calendar, Toast.LENGTH_LONG).show();
                    this.cancel();
                } else
                    Toast.makeText(getContext(), R.string.file_is_not_a_ics_file, Toast.LENGTH_SHORT).show();
            } else if (((RadioButton) findViewById(R.id.rBtnDHBWMa)).isChecked()) {
                Log.d(LOG, "DHBW");
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

        findViewById(R.id.btnCancel).setOnClickListener(view1 -> {
            Log.i(LOG, "cancel");
            this.cancel();
        });

        initDHBW(preferences);
        initICS(preferences);
    }

    /**
     * set result Intent
     *
     * @param intent   intent from the result
     * @param activity activity of app
     */
    public static void setResultIntent(@Nullable Intent intent, @NonNull Activity activity) {
        if (intent != null) {
            Uri uri = intent.getData();
            if (uri != null) {
                try {
                    ics = new ICS(readTextFromUri(uri, activity));
                    if (!ics.isSuccessful()) {
                        Toast.makeText(activity, R.string.wrong_file, Toast.LENGTH_SHORT).show();
                        phone = false;
                        return;
                    }
                    phone = true;
                } catch (IOException e) {
                    phone = false;
                    Toast.makeText(activity, R.string.wrong_file, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * init the ICS part
     */
    private void initICS(@NonNull SharedPreferences preferences) {
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

        findViewById(R.id.btnCheckLink).setOnClickListener(view12 -> {
            String text = ((EditText) findViewById(R.id.edTLink)).getText().toString();
            Log.i(LOG, "check link: " + text);
            if (!URLUtil.isValidUrl(text)) {
                Toast.makeText(getContext(), R.string.string_is_not_a_valid_url, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Import.checkConnection(getContext(), true)) return;
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

    /**
     * init the DHBW part
     */
    private void initDHBW(@NonNull SharedPreferences preferences) {
        new Thread(() -> {
            if (progress != null)
                findViewById(R.id.spDHBWMaCourseCategory).post(progress::show);
            Log.i(LOG, "get DHBW course");
            List<CourseCategory> courseCategories = CourseImport.load(getContext());
            if (courseCategories == null) {
                return;
            }
            ArrayAdapter<CourseCategory> categoryAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, courseCategories);
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            findViewById(R.id.spDHBWMaCourseCategory).post(() -> ((Spinner) findViewById(R.id.spDHBWMaCourseCategory)).setAdapter(categoryAdapter));

            String category = preferences.getString(PreferenceKeys.DHBW_MANNHEIM_COURSE_CATEGORY, null);
            if (category != null) {
                for (int i = 0; i < categoryAdapter.getCount(); i++)
                    if (0 == categoryAdapter.getItem(i).compareTo(category)) {
                        int finalI = i;
                        findViewById(R.id.spDHBWMaCourseCategory).post(() -> ((Spinner) findViewById(R.id.spDHBWMaCourseCategory)).setSelection(finalI));
                    }
            }
            ((Spinner) findViewById(R.id.spDHBWMaCourseCategory)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(@NonNull AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d(LOG, "Spinner:" + adapterView.getItemAtPosition(i).toString());
                    ArrayAdapter<Course> courseAdapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, ((CourseCategory) adapterView.getItemAtPosition(i)).getCourses());
                    courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    ((Spinner) findViewById(R.id.spDHBWMaCourse)).setAdapter(courseAdapter);
                    String course = preferences.getString(PreferenceKeys.DHBW_MANNHEIM_COURSE, null);
                    if (course != null)
                        for (int in = 0; in < courseAdapter.getCount(); in++)
                            if (0 == courseAdapter.getItem(in).compareTo(course)) {
                                ((Spinner) findViewById(R.id.spDHBWMaCourse)).setSelection(in);
                                return;
                            }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            if (progress != null)
                findViewById(R.id.spDHBWMaCourseCategory).post(progress::dismiss);
        }).start();

        findViewById(R.id.btnImportDhbwCourses).setOnClickListener(view22 -> {
            findViewById(R.id.btnImportDhbwCourses).setEnabled(false);
            findViewById(R.id.imgStatusDHBW).setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(R.drawable.sandglass).into((ImageView) findViewById(R.id.imgStatusDHBW));
            new Thread(() -> {
                Log.i(LOG, "Start new Import");
                List<CourseCategory> courseCategories = CourseImport.reloadFromInternet(getContext());
                if (courseCategories != null) {
                    ArrayAdapter<CourseCategory> categoryAdapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, courseCategories);
                    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    findViewById(R.id.spDHBWMaCourseCategory).post(() -> ((Spinner) findViewById(R.id.spDHBWMaCourseCategory)).setAdapter(categoryAdapter));
                } else
                    findViewById(R.id.btnImportDhbwCourses).post(() -> Toast.makeText(getContext(), R.string.no_connection, Toast.LENGTH_SHORT).show());//btnImportDhbwCourses is a element of the site
                findViewById(R.id.btnImportDhbwCourses).post(() -> {//one post for performance important if Ids change
                    findViewById(R.id.btnImportDhbwCourses).setEnabled(true);
                    findViewById(R.id.imgStatusDHBW).setVisibility(View.GONE);
                });
            }).start();
        });
    }

    /**
     * init the filePicker
     */
    private void initFilePicker() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            phone = false;
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");

            activity.startActivityForResult(intent, REQUEST_CODE);
        }
        else
            Toast.makeText(getContext(), getContext().getString(R.string.not_supported_in_your_android_version), Toast.LENGTH_LONG).show();
    }

    /**
     * read text from uri file
     *
     * @param uri      uri to file
     * @param activity activity to app
     * @return text
     */
    @NonNull
    private static String readTextFromUri(Uri uri, @NonNull Activity activity) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = activity.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null)
                stringBuilder.append(line).append("\n");
        }
        return stringBuilder.toString();
    }


}
