package c8y.trackeragent.operations;

import com.google.common.collect.Lists;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

class LogFileCommandBuilder {
    private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z0-9_ ]*$");
    private StringBuilder command = new StringBuilder();
    private List<String> filters = Lists.newArrayList();
    private int maximumLines = -1;

    public LogFileCommandBuilder() {
    }

    static LogFileCommandBuilder searchInFile(String file) {
        LogFileCommandBuilder builder = new LogFileCommandBuilder();
        builder.appendCatCommand(file);
        return builder;
    }

    LogFileCommandBuilder withMaximumLines(int lines) {
        this.maximumLines = lines;
        return this;
    }

    LogFileCommandBuilder withTenant(String tenant) throws LogFileCommandBuilder.InvalidSearchException {
        if (!PATTERN.matcher(tenant).matches()) {
            throw new LogFileCommandBuilder.InvalidSearchException("Cannot build command. Search parameters only allow the following characters [a-zA-Z0-9_]");
        } else {
            this.filters.add(String.format("\\- %s \\-", tenant));
            return this;
        }
    }

    LogFileCommandBuilder withDeviceUser(String deviceUser) throws LogFileCommandBuilder.InvalidSearchException {
        if (!PATTERN.matcher(deviceUser).matches()) {
            throw new LogFileCommandBuilder.InvalidSearchException("Cannot build command. Search parameters only allow the following characters [a-zA-Z0-9_]");
        } else {
            this.filters.add(String.format("\\- %s \\-", deviceUser));
            return this;
        }
    }

    LogFileCommandBuilder withSearchText(String text) throws LogFileCommandBuilder.InvalidSearchException {
        if (!PATTERN.matcher(text).matches()) {
            throw new LogFileCommandBuilder.InvalidSearchException("Cannot build command. Search parameters only allow the following characters [a-zA-Z0-9_]");
        } else {
            this.filters.add(text);
            return this;
        }
    }

    LogFileCommandBuilder withTimeRange(Date dateFrom, Date dateTo) throws LogFileCommandBuilder.InvalidSearchException {
        if (isToday(dateFrom) && isToday(dateTo)) {
            String filter = dateFilter(dateFrom, dateTo, "HH:mm");
            if (filter != null) {
                this.filters.add(filter);
            }

            return this;
        } else {
            throw new LogFileCommandBuilder.InvalidSearchException("Can only search the log from the current day");
        }
    }

    LogFileCommandBuilder withTimeRangeAndFormat(Date dateFrom, Date dateTo, String timestampFormat) {
        String filter = dateFilter(dateFrom, dateTo, timestampFormat);
        if (filter != null) {
            this.filters.add(filter);
        }

        return this;
    }

    String build() {
        Iterator iterator = this.filters.iterator();

        while(iterator.hasNext()) {
            String filter = (String)iterator.next();
            this.appendPipe();
            this.appendEgrepCommand(filter);
        }

        if (this.maximumLines > 0) {
            this.appendPipe();
            this.appendTailCommand();
        }

        return this.command.toString();
    }

    private static String dateFilter(Date dateFrom, Date dateTo, String timestampFormat) {
        if (dateTo.before(dateFrom)) {
            return null;
        } else {
            StringBuilder filter = new StringBuilder("^");
            int hoursFrom = dateFrom.getHours();
            int minutesFrom = dateFrom.getMinutes();
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateTo);
            cal.add(12, -1);
            dateTo = cal.getTime();
            int hoursTo = dateTo.getHours();
            int minutesTo = dateTo.getMinutes();
            if (hoursTo == hoursFrom) {
                filter.append(getFilterForSameHour(timestampFormat, hoursFrom, minutesFrom, minutesTo));
            } else if (hoursTo == hoursFrom + 1) {
                filter.append(getFilterForConnectedHours(timestampFormat, hoursFrom, minutesFrom, hoursTo, minutesTo));
            } else {
                filter.append(getFilterForHourRange(timestampFormat, hoursFrom, minutesFrom, hoursTo, minutesTo));
            }

            return filter.toString();
        }
    }

    private static boolean isToday(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        Date today = new Date();
        return fmt.format(date).equals(fmt.format(today));
    }

    private static String getFilterForHourRange(String timestampFormat, int hoursFrom, int minutesFrom, int hoursTo, int minutesTo) {
        String firstHour = replacePlaceholders(getLeadingZeroString(hoursFrom), numberRangeFilter(minutesFrom, 59), timestampFormat);
        String lastHour = replacePlaceholders(getLeadingZeroString(hoursTo), numberRangeFilter(0, minutesTo), timestampFormat);
        String hoursBetween = replacePlaceholders(numberRangeFilter((hoursFrom + 1) % 24, (hoursTo - 1) % 24), "", timestampFormat);
        return String.format("(%s)|(%s)|(%s)", firstHour, hoursBetween, lastHour);
    }

    private static String getFilterForConnectedHours(String timestampFormat, int hoursFrom, int minutesFrom, int hoursTo, int minutesTo) {
        String firstHour = replacePlaceholders(getLeadingZeroString(hoursFrom), numberRangeFilter(minutesFrom, 59), timestampFormat);
        String lastHour = replacePlaceholders(getLeadingZeroString(hoursTo), numberRangeFilter(0, minutesTo), timestampFormat);
        return String.format("(%s)|(%s)", firstHour, lastHour);
    }

    private static String getFilterForSameHour(String timestampFormat, int hoursFrom, int minutesFrom, int minutesTo) {
        return replacePlaceholders(getLeadingZeroString(hoursFrom), numberRangeFilter(minutesFrom, minutesTo), timestampFormat);
    }

    private static String getLeadingZeroString(int number) {
        return number < 10 ? "0" + number : String.valueOf(number);
    }

    private static String replacePlaceholders(String hours, String minutes, String format) {
        return format.replace("HH", hours).replace("mm", minutes);
    }

    private static String numberRangeFilter(int from, int to) {
        StringBuilder filter = new StringBuilder();
        filter.append("(");
        filter.append(getLeadingZeroString(from));

        for(int i = from + 1; i <= to; ++i) {
            filter.append("|" + getLeadingZeroString(i));
        }

        filter.append(")");
        return filter.toString();
    }

    private void appendCatCommand(String file) {
        this.command.append("cat " + file);
    }

    private void appendEgrepCommand(String filter) {
        this.command.append(String.format("egrep '%s'", filter));
    }

    private void appendTailCommand() {
        this.command.append("tail -n " + this.maximumLines);
    }

    private void appendPipe() {
        this.command.append(" | ");
    }

    public class InvalidSearchException extends Exception {
        public InvalidSearchException(String exception) {
            super(exception);
        }
    }
}
