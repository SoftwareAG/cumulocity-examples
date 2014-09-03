package com.cumulocity.greenbox.server.model.json;

import static com.cumulocity.greenbox.server.model.json.DateTimeConverter.*;
import static org.assertj.jodatime.api.Assertions.assertThat;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Test;

public class DateTimeConverterTest {

    @Test
    public void shouldConvert_2014_09_02_08_35_ToDateTime() {
        int value = (2014 << YEAR_SHIFT) | (9 << MONTH_SHIRT) | (02 << DAY_SHIFT) | (8 << HOUR_SHIFT) | (35);
        DateTimeConverter converter = new DateTimeConverter();

        final DateTime convert = converter.convert(value);

        assertThat(convert).isEqualTo("2014-09-02T8:35:00");
    }

    @Test
    public void shouldConvertToDateTime() {
        int value = 2110067236;
        DateTimeConverter converter = new DateTimeConverter();

        final DateTime convert = converter.convert(value);

        assertThat(convert).isEqualTo("2012-05-02T8:36:00");
    }

    @Test
    public void shouldReturnNowWhenValueIsNull() {
        freezeTime();
        DateTimeConverter converter = new DateTimeConverter();

        final DateTime convert = converter.convert(null);

        assertThat(convert).isEqualTo(DateTime.now());
    }

    private void freezeTime() {
        DateTimeUtils.setCurrentMillisFixed(System.currentTimeMillis());
    }

    @Test
    public void shouldReturnNowWhenValueIsZero() {
        freezeTime();
        DateTimeConverter converter = new DateTimeConverter();

        final DateTime convert = converter.convert(0);

        assertThat(convert).isEqualTo(DateTime.now());
    }
}
