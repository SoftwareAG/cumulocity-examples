package c8y.trackeragent.tracker;

import java.util.ArrayList;
import java.util.List;

import c8y.trackeragent.utils.ByteHelper;

public class BaseReportSplitter implements ReportSplitter {

    private final char reportSep;

    public BaseReportSplitter(String reportSeparator) {
        this.reportSep = reportSeparator.charAt(0);
    }

    @Override
    public List<String> split(byte[] reports) {
        List<String> result = new ArrayList<String>();
        while (reports != null && reports.length > 0) {
            reports = extractNext(result, reports);
        }
        return result;
    }

    protected byte[] extractNext(List<String> result, byte[] tail) {
        StringBuilder report = new StringBuilder();
        int headLength = 0;
        for (byte reportByte : tail) {
            char reportChar = (char) reportByte;
            headLength++;
            if (reportChar == reportSep) {
                break;
            }
            report.append(reportChar);
        }
        result.add(report.toString());
        return ByteHelper.stripHead(tail, headLength);
    }

}
