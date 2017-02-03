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

import java.io.IOException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 *
 */
public class ConfigAdminImpl implements ConfigurationAdmin {

    @Override
    public Configuration createFactoryConfiguration(String factoryPid) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Configuration createFactoryConfiguration(String factoryPid, String location) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Configuration getConfiguration(String pid, String location) throws IOException {
        return getConfiguration(pid);
    }

    @Override
    public Configuration getConfiguration(String pid) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Configuration[] listConfigurations(String filter) throws IOException, InvalidSyntaxException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
