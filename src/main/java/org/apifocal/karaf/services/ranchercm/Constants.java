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

public class Constants {

    /**
     * OSGi system property defining the rancher metadata service URL.
     *
     * It is usually set to {@link #RANCHER_METADATA_URL_DEFAULT}, but one can change it if needed (e.g. for
     * unit-testing).
     */
    static final String RANCHER_METADATA_URL = "rancher.metadata.url";

    /**
     * Rancher metadata service URL.
     */
    static final String RANCHER_METADATA_URL_DEFAULT = "http://rancher-metadata";

    static final String API_V1 = "2015-07-25";
    static final String API_V2 = "2015-12-19";

    static final String[] PATHS = {
        "self/container",
        "self/service",
        "self/stack",
        "self/host",
        "containers",
        "services",
        "stacks"
    };
    static final String PID_PREFIX = "org.apifocal.rancher.metadata.";

}
