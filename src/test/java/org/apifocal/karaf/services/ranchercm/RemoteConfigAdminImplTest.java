/*
 * Copyright 2017 apifocal LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apifocal.karaf.services.ranchercm;

import java.io.File;
import java.util.Dictionary;
import javax.inject.Inject;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.ConfigurationManager;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionBaseConfigurationOption;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.configureConsole;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.debugConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;

@RunWith(PaxExam.class)
public class RemoteConfigAdminImplTest extends RancherMetadataFixture {

    @Inject
    private BundleContext bc;

    @Inject
    private ConfigurationAdmin configAdmin;

    @Configuration
    public Option[] config() {

        MavenArtifactUrlReference karafUrl = maven()
                .groupId("org.apache.karaf")
                .artifactId("apache-karaf")
                .version(karafVersion())
                .type("zip");

        KarafDistributionBaseConfigurationOption karafDistro = karafDistributionConfiguration()
                .frameworkUrl(karafUrl)
                .unpackDirectory(new File("target", "exam"))
                .useDeployFolder(false);

        return new Option[]{
            karafDistro,
            // keepRuntimeFolder(),
             debugConfiguration("5007", true),
            configureConsole().ignoreLocalConsole(),
            systemProperty(Constants.RANCHER_METADATA_URL).value("http://localhost:" + PORT),
            mavenBundle("com.fasterxml.jackson.core", "jackson-annotations").versionAsInProject().start(),
            mavenBundle("com.fasterxml.jackson.core", "jackson-core").versionAsInProject().start(),
            mavenBundle("com.fasterxml.jackson.core", "jackson-databind").versionAsInProject().start(),
            mavenBundle("org.apifocal.karaf.services", "ranchercm").versionAsInProject().start()
        };
    }

    public static String karafVersion() {
        ConfigurationManager cm = new ConfigurationManager();
        String karafVersion = cm.getProperty("pax.exam.karaf.version", "4.0.8");
        return karafVersion;
    }

    @Test
    public void testConfigAdmin() throws Exception {
        // self info
        org.osgi.service.cm.Configuration containerConfig = configAdmin.getConfiguration("org.apifocal.rancher.metadata.self.container");
        org.osgi.service.cm.Configuration hostConfig = configAdmin.getConfiguration("org.apifocal.rancher.metadata.self.host");
        org.osgi.service.cm.Configuration serviceConfig = configAdmin.getConfiguration("org.apifocal.rancher.metadata.self.service");
        org.osgi.service.cm.Configuration stackConfig = configAdmin.getConfiguration("org.apifocal.rancher.metadata.self.stack");

        // others info
        org.osgi.service.cm.Configuration containersConfig = configAdmin.getConfiguration("org.apifocal.rancher.metadata.containers");
        org.osgi.service.cm.Configuration servicesConfig = configAdmin.getConfiguration("org.apifocal.rancher.metadata.services");
        org.osgi.service.cm.Configuration stacksConfig = configAdmin.getConfiguration("org.apifocal.rancher.metadata.stacks");

        assertEquals(Constants.PID_PREFIX + "self.container", containerConfig.getPid());
        assertEquals(Constants.PID_PREFIX + "self.host", hostConfig.getPid());
        assertEquals(Constants.PID_PREFIX + "self.service", serviceConfig.getPid());
        assertEquals(Constants.PID_PREFIX + "self.stack", stackConfig.getPid());
        assertEquals(Constants.PID_PREFIX + "containers", containersConfig.getPid());
        assertEquals(Constants.PID_PREFIX + "services", servicesConfig.getPid());
        assertEquals(Constants.PID_PREFIX + "stacks", stacksConfig.getPid());

        Dictionary<String, Object> containerProperties = containerConfig.getProperties();
        assertEquals("Default_test_1", containerProperties.get("name"));
    }

}
