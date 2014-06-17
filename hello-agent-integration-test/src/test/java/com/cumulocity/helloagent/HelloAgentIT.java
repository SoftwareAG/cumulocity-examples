package com.cumulocity.helloagent;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.MavenUtils;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.sdk.client.PlatformImpl;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class HelloAgentIT {
    
    private static File getFileFromClasspath(final String filePath) throws FileNotFoundException {
        try {
            URL fileURL = MavenUtils.class.getClassLoader().getResource(filePath);
            if (fileURL == null) {
                if (fileURL == null) {
                    // try the TCCL for getResource
                    fileURL = Thread.currentThread().getContextClassLoader().getResource(filePath);
                }
            }
            if (fileURL == null) {
                throw new FileNotFoundException("File [" + filePath + "] could not be found in classpath");
            }
            return new File(fileURL.toURI());
        } catch (URISyntaxException e) {
            throw new FileNotFoundException("File [" + filePath + "] could not be found: " + e.getMessage());
        }
    }

    private static Properties loadDependencies() {
        final Properties dependencies = new Properties();
        try {
            dependencies.load(new FileInputStream(getFileFromClasspath("META-INF/maven/dependencies.properties")));
            return dependencies;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class Artifact {
        private final String groupId;

        private final String artifactId;

        public Artifact(String groupId, String artifactId) {
            this.groupId = groupId;
            this.artifactId = artifactId;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getArtifactId() {
            return artifactId;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
            result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
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
            Artifact other = (Artifact) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (artifactId == null) {
                if (other.artifactId != null)
                    return false;
            } else if (!artifactId.equals(other.artifactId))
                return false;
            if (groupId == null) {
                if (other.groupId != null)
                    return false;
            } else if (!groupId.equals(other.groupId))
                return false;
            return true;
        }

        private HelloAgentIT getOuterType() {
            return HelloAgentIT.this;
        }

        @Override
        public String toString() {
            return "Artifact [groupId=" + groupId + ", artifactId=" + artifactId + "]";
        }

    }

    @Configuration
    public Option[] config() {

        Properties dependencies = loadDependencies();
        Set<Artifact> artifacts = new LinkedHashSet<Artifact>();
        for (Object key : dependencies.keySet()) {
            final String[] artifact = key.toString().split("/");
            if (artifact.length > 1) {
                final Artifact artifactValue = new Artifact(artifact[0], artifact[1]);
                final Object scope = dependencies.get(artifactValue.getGroupId() + "/" + artifactValue.getArtifactId() + "/scope");
                if (scope != null && scope.equals("compile")) {
                    artifacts.add(artifactValue);
                }
            }

        }
        final DefaultCompositeOption composite = new DefaultCompositeOption();

        for (Artifact artifact : artifacts) {
            composite.add(mavenBundle(artifact.getGroupId(), artifact.getArtifactId()).versionAsInProject().update());
        }
        return options(CoreOptions.cleanCaches(),composite, mavenBundle("c8y.example", "hello-agent").versionAsInProject());
    }

    private boolean groupIdStartsWith(Artifact artifact, String... prefixes) {
        for (String prefix : prefixes) {
            if (artifact.getGroupId().startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void shouldCreateManagedObjectWithNameHelloWorld() {
        HelloAgent agent = new HelloAgent(new PlatformImpl("integration.cumulocity.com", CumulocityCredentials.Builder
                .cumulocityCredentials("test", "test").withTenantId("test").build()));
        agent.sayHello();
    }

}
