package c8y.trackeragent.utils;

import java.io.IOException;
import java.io.InputStream;

public class ReportReader {

    private final InputStream is;
    private final char reportSeparator;

    public ReportReader(InputStream is, char reportSeparator) {
        this.is = is;
        this.reportSeparator = reportSeparator;
    }

    public Result readReport() throws IOException {
        StringBuffer result = new StringBuffer();
        while (is.available() > 0) {
            int c = is.read();
            if (c == -1) {
                return Result.endOf();
            }
            if ((char) c == reportSeparator) {
                return Result.success(result.toString());
            }
            if ((char) c == '\n') {
                continue;
            }
            result.append((char) c);
        }
        return Result.noContent();
    }

    public static enum ResulType {
        END_OF, NO_CONTENT, SUCCESS;
    }

    public static class Result {

        private final ResulType type;
        private final String text;
        
        public static Result success(String report) {
            return new Result(ResulType.SUCCESS, report);
        }
        
        public static Result endOf() {
            return new Result(ResulType.END_OF, null);
        }
        
        public static Result noContent() {
            return new Result(ResulType.NO_CONTENT, null);
        }

        private Result(ResulType type, String report) {
            this.type = type;
            this.text = report;
        }

        public ResulType getType() {
            return type;
        }

        public String getText() {
            return text;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((text == null) ? 0 : text.hashCode());
            result = prime * result + ((type == null) ? 0 : type.hashCode());
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
            Result other = (Result) obj;
            if (text == null) {
                if (other.text != null)
                    return false;
            } else if (!text.equals(other.text))
                return false;
            if (type != other.type)
                return false;
            return true;
        }

    }
}
