package c8y;

import java.io.Serializable;

import org.svenson.AbstractDynamicProperties;

public class SetSosNumber extends AbstractDynamicProperties implements Serializable {

    private static final long serialVersionUID = 1608763081931360921L;

    private String phoneNumber;

    public SetSosNumber() {}

    public SetSosNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "SetSosNumber [phoneNumber=" + phoneNumber + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
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
        SetSosNumber other = (SetSosNumber) obj;
        if (phoneNumber == null) {
            if (other.phoneNumber != null)
                return false;
        } else if (!phoneNumber.equals(other.phoneNumber))
            return false;
        return true;
    }

}
