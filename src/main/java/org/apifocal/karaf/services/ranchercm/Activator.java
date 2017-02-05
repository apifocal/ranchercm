package org.apifocal.karaf.services.ranchercm;

import org.apache.felix.cm.PersistenceManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    ServiceRegistration<PersistenceManager> registration;

    @Override
    public void start(BundleContext context) throws Exception {
        String url = System.getProperty(Constants.RANCHER_METADATA_URL,
                Constants.RANCHER_METADATA_URL_DEFAULT);

        RemotePersistenceManager pm = new RemotePersistenceManager(url, Constants.PATHS);
        registration = context.registerService(PersistenceManager.class, pm, null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (registration != null) {
            registration.unregister();
        }
    }

}
