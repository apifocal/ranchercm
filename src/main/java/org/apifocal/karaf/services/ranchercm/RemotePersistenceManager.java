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
import java.net.URI;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import org.apache.felix.cm.PersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemotePersistenceManager implements PersistenceManager {

    private static final Logger LOG = LoggerFactory.getLogger(RemotePersistenceManager.class);

    private final Map<String, URL> pids;

    public RemotePersistenceManager(String baseUrl, String... paths) throws IOException {
        this.pids = new HashMap<>(paths.length);
        for (String path : paths) {
            pids.put(
                    createPid(Constants.PID_PREFIX, path),
                    createUrl(baseUrl, path).toURL()
            );
        }
    }

    private String createPid(String prefix, String path) {
        StringBuilder b = new StringBuilder(prefix);
        b.append(path.replaceAll("/", "."));
        return b.toString();
    }

    private URI createUrl(String baseUrl, String path) {
        StringBuilder b = new StringBuilder(baseUrl);
        b.append("/");
        b.append(Constants.API_V2); // TODO: make this configurable
        b.append("/");
        b.append(path);
        return URI.create(b.toString());
    }

    @Override
    public boolean exists(String pid) {
        return pids.containsKey(pid);
    }

    @Override
    public Dictionary load(String pid) throws IOException {
        URL url = pids.get(pid);
        if (url == null) {
            throw new IOException("No configuration available for pid " + pid);
        }

        Dictionary<String, Object> properties = RemoteJsonDictionary.fetchProperties(url);
        properties.put(org.osgi.framework.Constants.SERVICE_PID, pid); // NPE in TargetedPid if missing
        return properties;
    }

    @Override
    public Enumeration getDictionaries() throws IOException {
        return new DictionaryEnumeration();
    }

    @Override
    public void store(String pid, Dictionary properties) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(String pid) throws IOException {
        throw new UnsupportedOperationException();
    }

    private class DictionaryEnumeration implements Enumeration {

        private final Iterator<String> iterator;

        public DictionaryEnumeration() {
            this.iterator = pids.keySet().iterator();
        }

        @Override
        public boolean hasMoreElements() {
            return iterator.hasNext();
        }

        @Override
        public Object nextElement() {
            String pid = iterator.next();
            try {
                return load(pid);
            } catch (IOException ex) {
                LOG.error("Failed to read configuration for {}", pid, ex);
                return new Hashtable<>();
            }
        }
    }

}
