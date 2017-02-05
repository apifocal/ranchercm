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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import org.osgi.service.cm.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maps the contents at a remote URL to a {@link Configuration}.
 *
 * The config is static, in that updates are not supported, and change detection is also not supported.
 */
public class RemoteJsonDictionary {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteJsonDictionary.class);

    public static Dictionary<String, Object> fetchProperties(URL url) throws IOException {
        Dictionary<String, Object> properties = new Hashtable<>();
        ObjectMapper mapper = new ObjectMapper();
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            urlConnection.setRequestProperty("Accept", "application/json");
            JsonNode tree = mapper.readTree(new BufferedInputStream(urlConnection.getInputStream()));
            loadProperties(properties, tree, "");
        } finally {
            urlConnection.disconnect();
        }
        return properties;
    }

    private static void loadProperties(Dictionary<String, Object> properties, JsonNode node, String parent) {
        if (node.isObject()) {
            node.fields().forEachRemaining((Map.Entry<String, JsonNode> entry) -> {
                StringBuilder newPrefix = new StringBuilder(parent);
                newPrefix.append(parent.isEmpty() ? "" : ".");
                newPrefix.append(entry.getKey());
                loadProperties(properties, entry.getValue(), newPrefix.toString());
            });
        } else if (node.isValueNode()) {
            if (!node.isNull()) {
                // Hashtable<> does not support null values
                properties.put(parent, valueOf(node));
            }
        } else {
            LOG.debug("Ignored property {} of type {}", parent, node.getNodeType());
        }
    }

    private static Object valueOf(JsonNode node) {
        if (node.isNull()) {
            return null;
        } else if (node.isBoolean()) {
            return node.asBoolean();
        } else if (node.isFloatingPointNumber()) {
            return node.asDouble();
        } else if (node.canConvertToInt()) {
            return node.asInt();
        } else if (node.canConvertToLong()) {
            return node.asLong();
        } else {
            return node.asText();
        }
    }

}
