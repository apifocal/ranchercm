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

import java.net.URL;
import java.util.Dictionary;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RemoteJsonDictionaryTest extends RancherMetadataFixture {

    private static final String V2_SELF_CONTAINER = "http://localhost:" + PORT + "/2015-12-19/self/container";
    private static final String V2_SELF_HOST = "http://localhost:" + PORT + "/2015-12-19/self/host";

    @Test
    public void testTypeMapping() throws Exception {

        Dictionary<String, Object> p = RemoteJsonDictionary.fetchProperties(new URL(V2_SELF_CONTAINER));

        // test some nulls
        assertNull(p.get("this_property_should_be_missing"));
        assertNull(p.get("hostname")); // explicit null

        // objects go into long properties
        assertNull(p.get("labels"));
        assertEquals("Default", p.get("labels.io.rancher.stack.name"));
        assertEquals("Default/test", p.get("labels.io.rancher.stack_service.name"));

        // some basic types
        assertEquals(1, p.get("create_index")); // int
        assertEquals("1", p.get("service_index")); // string that looks like an int
        assertEquals("test", p.get("service_name")); // string
        assertEquals("true", p.get("labels.io.rancher.container.start_once")); // string that looks like bool
        assertEquals(false, p.get("bool_for_unit_test")); // bool; no bools in real-world responses from rancher

        // TODO: add tests for arrays when time comes
        assertNull(p.get("ips"));
    }

    @Test
    public void testSelfContainer() throws Exception {
        Dictionary<String, Object> p = RemoteJsonDictionary.fetchProperties(new URL(V2_SELF_CONTAINER));

        assertEquals(1, p.get("create_index"));
        assertEquals("7bba6c6eaf5369484aca83176f834c4c348b8c40fbd6334c0ef9cfadef98b877", p.get("external_id"));
        assertEquals(null, p.get("health_state"));
        assertEquals("feadf169-36c9-497a-9d68-642a8a29b644", p.get("host_uuid"));
        assertEquals(null, p.get("hostname"));
        assertEquals("10.42.39.12/16", p.get("labels.io.rancher.container.ip"));
        assertEquals("Default_test_1", p.get("labels.io.rancher.container.name"));
        assertEquals("always", p.get("labels.io.rancher.container.pull_image"));
        assertEquals("true", p.get("labels.io.rancher.container.start_once"));
        assertEquals("c70485a5-8c5f-4ee3-baf7-6e1515689c73", p.get("labels.io.rancher.container.uuid"));
        assertEquals("Default", p.get("labels.io.rancher.project.name"));
        assertEquals("Default/test", p.get("labels.io.rancher.project_service.name"));
        assertEquals("964df115-6acf-42c8-9dd2-ca57c3ca7f95", p.get("labels.io.rancher.service.deployment.unit"));
        assertEquals("io.rancher.service.primary.launch.config", p.get("labels.io.rancher.service.launch.config"));
        assertEquals("Default", p.get("labels.io.rancher.stack.name"));
        assertEquals("Default/test", p.get("labels.io.rancher.stack_service.name"));
        assertEquals("Default_test_1", p.get("name"));
        assertEquals("10.42.39.12", p.get("primary_ip"));
        assertEquals("1", p.get("service_index"));
        assertEquals("test", p.get("service_name"));
        assertEquals("Default", p.get("stack_name"));
        assertEquals(1, p.get("start_count"));
        assertEquals("running", p.get("state"));
        assertEquals("c70485a5-8c5f-4ee3-baf7-6e1515689c73", p.get("uuid"));
    }

    @Test
    public void testSelfHost() throws Exception {
        Dictionary<String, Object> p = RemoteJsonDictionary.fetchProperties(new URL(V2_SELF_HOST));

        // test that all properties were read properly
        assertEquals("192.168.0.42", p.get("agent_ip"));
        assertEquals(3, p.get("hostId"));
        assertEquals("rc01", p.get("hostname"));
        assertEquals("1.13", p.get("labels.io.rancher.host.docker_version"));
        assertEquals("4.4", p.get("labels.io.rancher.host.linux_kernel_version"));
        assertEquals("rc01", p.get("name"));
        assertEquals("feadf169-36c9-497a-9d68-642a8a29b644", p.get("uuid"));
    }

}
