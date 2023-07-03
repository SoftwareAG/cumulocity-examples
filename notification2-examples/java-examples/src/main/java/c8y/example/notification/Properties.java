package c8y.example.notification;

import com.cumulocity.model.idtype.GId;
import lombok.Getter;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

@Getter
public class Properties {

    private static final String SOURCE_ID = "example.source.id";
    private final GId sourceId;

    public Properties() throws ConfigurationException {
        final Configuration configuration = new FileBasedConfigurationBuilder<FileBasedConfiguration>(
                PropertiesConfiguration.class)
                .configure(new Parameters()
                        .properties()
                        .setFileName("notifications-example.properties"))
                .getConfiguration();
        this.sourceId = GId.asGId(configuration.getInt(SOURCE_ID));
    }

}
