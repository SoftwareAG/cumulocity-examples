package c8y.trackeragent.protocol.telic;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.cumulocity.agent.server.context.DeviceContextService;

import c8y.trackeragent.ConnectedTracker;
import c8y.trackeragent.devicebootstrap.DeviceBootstrapProcessor;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.protocol.telic.parser.TelicFragment;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectedTelicTracker extends ConnectedTracker<TelicFragment> {
    
    protected static Logger logger = LoggerFactory.getLogger(ConnectedTelicTracker.class);
    
    public static final int HEADER_LENGTH = 28;
    public static final int REPORT_SKIP = 4;

	@Autowired
	public ConnectedTelicTracker(DeviceContextService contextService,
			DeviceBootstrapProcessor bootstrapProcessor, DeviceCredentialsRepository credentialsRepository,
			List<TelicFragment> fragments) throws IOException {
		super(TelicConstants.REPORT_SEP, TelicConstants.FIELD_SEP, contextService, bootstrapProcessor,
				credentialsRepository, fragments);
	}
    
    @Override
    public void init(Socket client, InputStream in) throws Exception {
        super.init(client, eat(in, HEADER_LENGTH));
    }

    @Override
    public String readReport(InputStream is) throws IOException {
        logger.debug("Start reading telic report");
        if (eat(is, REPORT_SKIP) == null) {
            return null;
        }
        return super.readReport(is);
    }
    
    private static InputStream eat(InputStream bis, int bytesToRead) throws IOException {
        byte[] dummy = new byte[bytesToRead];
        int bytesRead = bis.read(dummy, 0, bytesToRead);
        if (bytesRead < bytesToRead) {
            logger.warn("{} bytes read from header but expected at least {}, skip this report!", bytesRead, bytesToRead);
            return null;
        }
        return bis;
    }
    
}
