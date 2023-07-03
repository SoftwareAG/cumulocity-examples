package c8y.example.notification;

import org.apache.commons.configuration2.ex.ConfigurationException;

public class Example1 {
    public static void main(String[] args) throws ConfigurationException {
        final Properties properties = new Properties();
        System.out.println(properties.getSourceId());
    }

}
