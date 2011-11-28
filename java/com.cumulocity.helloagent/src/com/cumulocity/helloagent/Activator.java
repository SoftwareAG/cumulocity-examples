package com.cumulocity.helloagent;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;

public class Activator implements BundleActivator {

    private static BundleContext context;

    static BundleContext getContext() {
        return context;
    }

    public void start(BundleContext bundleContext) throws Exception {
        Activator.context = bundleContext;

        // A FrameworkListener is used so that agent is notified until all OSGi bundles are loaded
        FrameworkListener listener = new FrameworkListener() {

            @Override
            public void frameworkEvent(FrameworkEvent event) {
                if (event.getType() == FrameworkEvent.STARTLEVEL_CHANGED) {
                    synchronized (this) {
                        this.notify();
                    }
                }
            }
        };

        new Thread(new HelloAgent(listener)).start();
        context.addFrameworkListener(listener);
    }

    public void stop(BundleContext bundleContext) throws Exception {
        Activator.context = null;
    }

}
