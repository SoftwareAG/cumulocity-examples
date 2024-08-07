package c8y.example.svensonparse;

import java.util.function.Predicate;

public class SoftwareFilter implements Predicate<Software> {

    private String softwareType;
    private String name;
    private String version;
    private String url;

    public SoftwareFilter(String softwareType, String name, String version, String url) {
        this.softwareType = softwareType;
        this.name = name;
        this.version = version;
        this.url = url;
    }

    @Override
    public boolean test(Software software) {
        return (softwareType == null || software.getSoftwareType().equals(softwareType)) &&
                (name == null || software.getName().equals(name)) &&
                (version == null || software.getVersion().equals(version)) &&
                (url == null || software.getUrl().equals(url));
    }
}

