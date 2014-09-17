package com.cumulocity.greenbox.server.service;

import org.joda.time.DateTime;

class MeasurementWithSourceAndTime {

    private final String source;

    private final DateTime time;

    public MeasurementWithSourceAndTime(String source, DateTime time) {
        this.source = source;
        this.time = time;
    }

    public String getSource() {
        return source;
    }

    public DateTime getTime() {
        return time;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((time == null) ? 0 : time.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MeasurementWithSourceAndTime other = (MeasurementWithSourceAndTime) obj;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        if (time == null) {
            if (other.time != null)
                return false;
        } else if (!time.equals(other.time))
            return false;
        return true;
    }

}
