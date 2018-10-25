package c8y.remoteaccess.util;

import java.io.Closeable;

public class CumulocityIOUtils {

    public static final int EOF = -1;

    public static void closeQuietly(Closeable ...closeables) {
        for(Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    ; //ignored
                }
            }
        }
    }
}
