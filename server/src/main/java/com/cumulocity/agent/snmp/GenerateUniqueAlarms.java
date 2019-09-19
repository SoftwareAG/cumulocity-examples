package com.cumulocity.agent.snmp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateUniqueAlarms {

    public static void main(String[] args) {

        String message = "A;{\"severity\":\"MAJOR\",\"type\":\"com_cumulocity_events_TamperEvent_{0}\",\"time\":\"2017-09-10T00:00:00.001Z\",\"text\":\"Created using API via E2E service.\",\"status\":\"ACTIVE\",\"source\":{\"id\":\"249230\"}}";
        int COUNT = 1000;
        try (BufferedWriter messageWriter = new BufferedWriter(new FileWriter("C:\\Users\\bbyreddy\\.snmp\\snmp-agent\\messages1.csv"));) {
            for(int i=0; i<COUNT; i++) {
                messageWriter.write(message.replace("_{0}", "_" + String.valueOf(i)));
                messageWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
