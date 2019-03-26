package com.nemge.ppe.Model;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class Converter {

    // Date
    @TypeConverter
    public static Date fromTimestamp(Long val) {
        return val == null ? null : new Date(val);

    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    // URL
    @TypeConverter
    public static URL fromString(String str) {
        try {
            return str == null ? null : new URL(str);
        }
        catch (MalformedURLException err) {
            Log.w("Converters", "Erreur format URL : " + str);
            return null;
        }
    }

    @TypeConverter
    public static String toString(URL url) {
        return url == null ? null : url.toString();
    }
}