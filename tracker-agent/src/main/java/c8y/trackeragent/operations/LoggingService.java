package c8y.trackeragent.operations;

import c8y.LogfileRequest;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Slf4j
@Service
public class LoggingService {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final DeviceControlRepository deviceControl;
    private final BinariesRepository binaries;
    private final String logfile;
    private final String timestampFormat;
    private final String applicationId;

    @Autowired
    public LoggingService(
            DeviceControlRepository deviceControl,
            BinariesRepository binaries,
            @Value("${C8Y.log.file.path}") String logfile,
            @Value("${C8Y.log.timestamp.format:}") String timestampFormat,
            @Value("${C8Y.application.id}") String applicationId
    ) {
        this.deviceControl = deviceControl;
        this.binaries = binaries;
        this.logfile = logfile;
        this.timestampFormat = timestampFormat;
        this.applicationId = applicationId;
    }

    public void readLog(OperationRepresentation operation) throws SDKException {
        LogfileRequest request = operation.get(LogfileRequest.class);
        if (request == null) {
            log.info("Could not handle operation with id: {} -> no AgentLogRequest fragment", operation.getId());
        } else {
            try {
                String command = this.buildCommand(request);
                String selfUrl = this.uploadLog(command, request);
                this.saveOperationWithLogLink(operation, request, selfUrl);
            } catch (LogFileCommandBuilder.InvalidSearchException e) {
                this.deviceControl.save(Operations.asFailedOperation(operation.getId(), e.getMessage()));
            } catch (Exception var6) {
                this.deviceControl.save(Operations.asFailedOperation(operation.getId(), "Error on reading log file: " + var6.getMessage()));
            }

        }
    }

    private String buildCommand(LogfileRequest request) throws LogFileCommandBuilder.InvalidSearchException {
        LogFileCommandBuilder builder = LogFileCommandBuilder.searchInFile(this.logfile);
        if (request.getTenant() != null) {
            builder.withTenant(request.getTenant());
        }

        if (request.getDeviceUser() != null) {
            builder.withDeviceUser(request.getDeviceUser());
        }

        if (request.getDateFrom() != null && request.getDateTo() != null) {
            if (StringUtils.isEmpty(this.timestampFormat)) {
                builder.withTimeRange(request.getDateFrom(), request.getDateTo());
            } else {
                builder.withTimeRangeAndFormat(request.getDateFrom(), request.getDateTo(), this.timestampFormat);
            }
        }

        if (request.getSearchText() != null && request.getSearchText().isEmpty()) {
            builder.withSearchText(request.getSearchText());
        }

        if (request.getMaximumLines() > 0) {
            builder.withMaximumLines(request.getMaximumLines());
        }

        return builder.build();
    }

    private String uploadLog(String command, LogfileRequest request) throws SDKException, IOException {
        Process process = null;

        String log;
        try {
            LoggingService.log.info("Run log command: {}", command);
            ProcessBuilder builder = new ProcessBuilder(new String[] {"/bin/sh", "-c", command});
            builder.redirectErrorStream(true);
            process = builder.start();
            LoggingService.log.info("Uploading log file");
            String filename = this.buildName(request);
            ManagedObjectRepresentation container = this.uploadFileDummy(filename);
            this.uploadLogFile(container.getId(), process.getInputStream());
            log = container.getSelf();
        } catch (SDKException e) {
            LoggingService.log.error("Could not upload log", e);
            throw e;
        } catch (IOException e) {
            LoggingService.log.error("Could not read log", e);
            throw e;
        } finally {
            if (process != null) {
                LoggingService.log.debug("Kill log read process");
                process.destroy();
            }

        }
        return log;
    }

    private String buildName(LogfileRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(this.applicationId);
        if (request.getDeviceUser() != null) {
            builder.append("_");
            builder.append(request.getDeviceUser());
        }

        if (request.getDateFrom() != null && request.getDateTo() != null) {
            builder.append("_");
            builder.append(DATE_FORMAT.format(request.getDateFrom()));
        }

        builder.append(".log");
        return builder.toString();
    }

    private ManagedObjectRepresentation uploadFileDummy(String filename) {
        ManagedObjectRepresentation container = new ManagedObjectRepresentation();
        container.setName(filename);
        container.setType("text/plain");
        return this.binaries.uploadFile(container, new byte[]{0});
    }

    private void saveOperationWithLogLink(OperationRepresentation operation, LogfileRequest request, String selfUrl) {
        request.setFile(selfUrl.replace("managedObjects", "binaries"));
        OperationRepresentation updatedOperation = new OperationRepresentation();
        updatedOperation.setId(operation.getId());
        updatedOperation.set(request, LogfileRequest.class);
        updatedOperation.setStatus(OperationStatus.SUCCESSFUL.toString());
        this.deviceControl.save(updatedOperation);
    }

    private void uploadLogFile(GId containerId, InputStream is) {
        this.binaries.replaceFile(containerId, "text/plain", is);
    }
}
