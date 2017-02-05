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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class RancherMetadataFixture {

    private static final Logger LOG = LoggerFactory.getLogger(RancherMetadataFixture.class);

    public static final int PORT = 7999;

    protected static HttpServer server;

    @BeforeClass
    public static void startWebserver() throws IOException {
        RemoteJsonDictionaryTest.server = HttpServer.create(new InetSocketAddress(RemoteJsonDictionaryTest.PORT), 0);
        RemoteJsonDictionaryTest.server.createContext("/", (HttpExchange t) -> {
            String path = t.getRequestURI().getPath(); // this starts with a /, so use class.getResource(), not clasloader.getResource() below
            String resource = path + ".json";
            try {
                InputStream is = RemoteJsonDictionaryTest.class.getResourceAsStream(resource);
                if (is != null) {
                    byte[] bytes = IOUtils.toByteArray(is);
                    t.sendResponseHeaders(200, bytes.length);
                    try (final OutputStream os = t.getResponseBody()) {
                        os.write(bytes);
                    }
                } else {
                    LOG.warn("Replied with 404 for request path {}", path);
                    t.sendResponseHeaders(404, 0);
                }
            } catch (Exception ex) {
                LOG.warn("Replied with 500 for request path {}", path, ex);
                t.sendResponseHeaders(500, 0);
            }
        });
        RemoteJsonDictionaryTest.server.setExecutor(null); // creates a default executor
        RemoteJsonDictionaryTest.server.start();
    }

    @AfterClass
    public static void stopWebServer() {
        RemoteJsonDictionaryTest.server.stop(1);
    }

}
