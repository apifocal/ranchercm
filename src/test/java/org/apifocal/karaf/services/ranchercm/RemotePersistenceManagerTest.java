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
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import static org.apifocal.karaf.services.ranchercm.RancherMetadataFixture.PORT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class RemotePersistenceManagerTest extends RancherMetadataFixture {

    private static final String TEST_BASE_URL = "http://localhost:" + PORT;
    private static final String[] TEST_PATHS = {
        "self/container",
        "self/host"
    };

    @Test
    public void testExists() throws IOException {
        RemotePersistenceManager pm = new RemotePersistenceManager(TEST_BASE_URL, TEST_PATHS);
        assertTrue(pm.exists("org.apifocal.rancher.metadata.self.container"));
        assertTrue(pm.exists("org.apifocal.rancher.metadata.self.host"));
        assertFalse(pm.exists("org.apifocal.rancher.metadata.self.service"));
        assertFalse(pm.exists("org.apifocal.rancher.metadata.services"));
    }

    @Test
    public void testLoad() throws IOException {
        RemotePersistenceManager pm = new RemotePersistenceManager(TEST_BASE_URL, TEST_PATHS);
        Dictionary properties = pm.load("org.apifocal.rancher.metadata.self.container");
        assertEquals("org.apifocal.rancher.metadata.self.container", properties.get(org.osgi.framework.Constants.SERVICE_PID));
        assertEquals("Default_test_1", properties.get("name"));
        assertEquals(1, properties.get("create_index"));
        assertEquals("1", properties.get("service_index"));
    }

    @Test(expected = IOException.class)
    public void testLoadMissingPid() throws IOException {
        RemotePersistenceManager pm = new RemotePersistenceManager(TEST_BASE_URL, TEST_PATHS);
        pm.load("org.apifocal.rancher.metadata.services");
    }

    @Test
    public void testGetDictionaries() throws IOException {
        RemotePersistenceManager pm = new RemotePersistenceManager(TEST_BASE_URL, TEST_PATHS);

        Enumeration dictionaries = pm.getDictionaries();
        @SuppressWarnings("unchecked")
        List<Dictionary<String,Object>> dicts = Collections.list(dictionaries);

        assertEquals(2, dicts.size());
        assertEquals("org.apifocal.rancher.metadata.self.container", dicts.get(0).get(org.osgi.framework.Constants.SERVICE_PID));
        assertEquals("org.apifocal.rancher.metadata.self.host", dicts.get(1).get(org.osgi.framework.Constants.SERVICE_PID));
    }

    @Test
    public void testGetDictionariesMissing() throws IOException {
        // assumes you don't have a host called rancher-metadata...
        // URLs - should get no exceptions out, else other persistencemanagers get messed up
        RemotePersistenceManager pm = new RemotePersistenceManager(Constants.RANCHER_METADATA_URL_DEFAULT, TEST_PATHS);

        Enumeration dictionaries = pm.getDictionaries();
        @SuppressWarnings("unchecked")
        List<Dictionary<String,Object>> dicts = Collections.list(dictionaries);

        assertEquals(2, dicts.size());
        assertNull(dicts.get(0).get(org.osgi.framework.Constants.SERVICE_PID));
        assertNull(dicts.get(1).get(org.osgi.framework.Constants.SERVICE_PID));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testStore() throws IOException {
        RemotePersistenceManager pm = new RemotePersistenceManager(TEST_BASE_URL, TEST_PATHS);
        pm.store("org.apifocal.rancher.metadata.self.container", new Hashtable<>());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDelete() throws IOException {
        RemotePersistenceManager pm = new RemotePersistenceManager(TEST_BASE_URL, TEST_PATHS);
        pm.delete("org.apifocal.rancher.metadata.self.container");
    }

}
