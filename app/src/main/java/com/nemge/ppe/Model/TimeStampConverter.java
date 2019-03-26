package com.nemge.ppe.Model;

import android.arch.persistence.room.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeStampConverter {

        static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

        @TypeConverter
        public static Date fromTimestamp(String value) {
            if (value != null) {
                try {
                    return df.parse(value);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return null;
            } else {
                return null;
            }
        }
}
