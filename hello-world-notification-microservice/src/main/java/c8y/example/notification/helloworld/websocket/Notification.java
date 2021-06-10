package c8y.example.notification.helloworld.websocket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Notification {
    public final String ackHeader;
    public final List<String> notificationHeaders;
    public final String message;

    private Notification(String ackHeader, List<String> notificationHeaders, String message) {
        this.ackHeader = ackHeader;
        this.notificationHeaders = Collections.unmodifiableList(notificationHeaders);
        this.message = message;
    }

    static Notification parse(String message) {
        ArrayList<String> headers = new ArrayList<>(8);
        while (true) {
            int i = message.indexOf('\n');
            if (i == -1) {
                break;
            }
            String header = message.substring(0, i);
            message = message.substring(i + 1);
            if (header.length() == 0) {
                break;
            }
            headers.add(header);
        }
        if (headers.isEmpty()) {
            return new Notification(null, headers, message);
        }
        return new Notification(headers.get(0), headers.subList(1, headers.size()), message);
    }

    public String toString(){
        final StringBuilder sb = new StringBuilder();
        sb.append("Notification{ackHeader:").append(this.ackHeader)
          .append(", notificationHeaders:").append(this.notificationHeaders)
          .append(", message:").append(this.message)
          .append('}');
        return sb.toString();
    }

    public String toPrintString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("AckHeader: ").append(this.ackHeader).append("\nNotificationHeaders:\n");
        for(String header: this.notificationHeaders) {
            sb.append("    ").append(header).append('\n');
        }
        sb.append("Message: ").append(this.message);
        return sb.toString();
    }
}
