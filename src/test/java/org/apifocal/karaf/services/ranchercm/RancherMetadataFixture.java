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
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.file.Files;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 *
 */
public class RancherMetadataFixture {

    public static final int PORT = 7999;

    protected static HttpServer server;

    @BeforeClass
    public static void startWebserver() throws IOException {
        RemoteJsonConfigurationImplTest.server = HttpServer.create(new InetSocketAddress(RemoteJsonConfigurationImplTest.PORT), 0);
        RemoteJsonConfigurationImplTest.server.createContext("/", (HttpExchange t) -> {
            try {
                String path = t.getRequestURI().getPath(); // this starts with a /, so use class.getResource(), not clasloader.getResource() below
                String resource = path + ".json";
                URL url = RemoteJsonConfigurationImplTest.class.getResource(resource);
                if (url != null) {
                    File file = new File(url.toURI());
                    t.sendResponseHeaders(200, file.length());
                    try (final OutputStream os = t.getResponseBody()) {
                        Files.copy(file.toPath(), os);
                    }
                } else {
                    t.sendResponseHeaders(404, 0);
                }
            } catch (Exception ex) {
                t.sendResponseHeaders(500, 0);
            }
        });
        RemoteJsonConfigurationImplTest.server.setExecutor(null); // creates a default executor
        RemoteJsonConfigurationImplTest.server.start();
    }

    @AfterClass
    public static void stopWebServer() {
        RemoteJsonConfigurationImplTest.server.stop(1);
    }

}
