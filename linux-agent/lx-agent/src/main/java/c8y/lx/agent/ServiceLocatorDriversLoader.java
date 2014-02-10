package c8y.lx.agent;

import java.util.ServiceLoader;

import c8y.lx.driver.Driver;

public class ServiceLocatorDriversLoader implements DriversLoader {

    @Override
    public Iterable<Driver> loadDrivers() {
        return ServiceLoader.load(Driver.class);
    }
}
