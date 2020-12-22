package com.example.studentalarm.regular;

import android.content.Context;
import android.util.Log;

import com.example.studentalarm.save.SaveHour;
import com.example.studentalarm.save.SaveKeys;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Hours {
    private final int id;
    private String from, until;

    public Hours(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getFrom() {
        return from;
    }

    public String getUntil() {
        return until;
    }

    @Nullable
    public Date getFromAsDate() {
        try {
            return new SimpleDateFormat("HH:mm", Locale.getDefault()).parse(from);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public Date getUntilAsDate() {
        try {
            return new SimpleDateFormat("HH:mm", Locale.getDefault()).parse(until);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NonNull
    public Hours setFrom(String from) {
        this.from = from;
        return this;
    }

    @NonNull
    public Hours setUntil(String until) {
        this.until = until;
        return this;
    }

    /**
     * Save the Hour in the internal storage of the application
     *
     * @param context context of the application
     */
    public static void save(@NonNull Context context, @NonNull List<Hours> hours) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(SaveKeys.HOURS, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(saveHours(hours));
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * create a save object
     *
     * @param list list to convert
     * @return save object
     */
    @NonNull
    private static SaveHour saveHours(@NonNull List<Hours> list) {
        SaveHour saveHour = new SaveHour();
        SaveHour.Save[] saves = new SaveHour.Save[list.size()];
        for (int i = 0; i < list.size(); i++) {
            saves[i] = new SaveHour.Save();
            saves[i].id = list.get(i).id;
            saves[i].from = list.get(i).from;
            saves[i].until = list.get(i).until;
        }
        saveHour.save = saves;
        return saveHour;
    }

    /**
     * Load the Hour from the internal storage of the application
     *
     * @param context context of the application
     * @return list of hour objects
     */
    @NonNull
    public static List<Hours> load(@NonNull Context context) {
        try {
            FileInputStream fis = context.openFileInput(SaveKeys.HOURS);
            ObjectInputStream ois = new ObjectInputStream(fis);
            List<Hours> erg = convertSave((SaveHour) ois.readObject());
            fis.close();
            ois.close();
            return erg;
        } catch (IOException | ClassNotFoundException e) {
            Log.d("Hours", "can't load");
        }
        return new ArrayList<>();
    }

    /**
     * convert save object to list of hours objects
     *
     * @param readObject object to convert
     * @return list of hour object
     */
    @NonNull
    private static List<Hours> convertSave(@NonNull SaveHour readObject) {
        List<Hours> hours = new ArrayList<>();
        for (SaveHour.Save save : readObject.save)
            hours.add(new Hours(save.id).setFrom(save.from).setUntil(save.until));
        return hours;
    }

}
