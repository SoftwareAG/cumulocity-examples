package c8y.lx.agent;

import c8y.lx.driver.Driver;

public interface DriversLoader {

    Iterable<Driver> loadDrivers();
}
