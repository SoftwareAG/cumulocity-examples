package com.cumulocity.helloagent;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        (new HelloAgent()).sayHello();
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
    }

}
