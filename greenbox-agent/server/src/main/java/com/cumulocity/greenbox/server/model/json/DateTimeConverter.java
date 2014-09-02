package com.cumulocity.greenbox.server.model.json;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.StdConverter;

public class DateTimeConverter extends StdConverter<Integer, DateTime> implements Converter<Integer, DateTime> {

    public static final int HOUR_SHIFT = 6;

    public static final int DAY_SHIFT = 11;

    public static final int MONTH_SHIRT = 16;

    public static final int YEAR_SHIFT = 20;

    @Override
    public DateTime convert(Integer value) {
        if (value == null || value == 0) {
            return DateTime.now();
        } else {
            return new DateTime(getYear(value), getMonth(value), getDay(value), getHour(value), getMinute(value), 0, 0);
        }
    }

    private int getYear(int value) {
        return (value & 0xfff00000) >>> YEAR_SHIFT;
    }

    private int getMonth(int value) {
        return (value & 0x000f0000) >>> MONTH_SHIRT;
    }

    private int getDay(int value) {
        return (value & 0x0000f800) >>> DAY_SHIFT;
    }

    private int getHour(int value) {
        return (value & 0x000007c0) >> HOUR_SHIFT;
    }

    private int getMinute(int value) {
        return (value & 0x0000003f);
    }

}
