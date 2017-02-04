package org.apifocal.karaf.services.ranchercm;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class Activator implements BundleActivator {

    static final String PID_PREFIX = "org.apifocal.rancher.metadata.";

    ServiceRegistration<ConfigurationAdmin> registration;

    @Override
    public void start(BundleContext context) throws Exception {
        List<Configuration> configs = new ArrayList<>();

        for (String path : Constants.PATHS) {
            URI url = createRancherMetadataUrl(path);
            String pid = createPid(path);
            RemoteJsonConfigurationImpl config = new RemoteJsonConfigurationImpl(pid, url);
            configs.add(config);
        }

        RemoteConfigAdminImpl cm = new RemoteConfigAdminImpl(context, configs);
        registration = context.registerService(ConfigurationAdmin.class, cm, null);
    }

    public void stop(BundleContext context) throws Exception {
        if (registration != null) {
            registration.unregister();
        }
    }

    private String createPid(String path) {
        StringBuilder b = new StringBuilder(PID_PREFIX);
        b.append(path.replaceAll("/", "."));
        return b.toString();
    }

    private URI createRancherMetadataUrl(String path) {
        StringBuilder b = new StringBuilder(Constants.DEFAULT_URL);
        b.append("/");
        b.append(Constants.API_V2); // TODO: make this configurable
        b.append("/");
        b.append(path);
        return URI.create(b.toString());
    }

}
