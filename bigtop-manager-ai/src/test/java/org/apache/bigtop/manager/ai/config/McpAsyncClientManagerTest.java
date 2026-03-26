/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.bigtop.manager.ai.config;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class McpAsyncClientManagerTest {

    @Test
    void resolveBaseUrlShouldPreferConfiguredValue() throws Exception {
        McpAsyncClientManager manager = new McpAsyncClientManager();
        setField(manager, "clientBaseUrl", "http://127.0.0.1:9000");
        setField(manager, "serverPort", "8080");

        String baseUrl = invokeResolveBaseUrl(manager);
        assertEquals("http://127.0.0.1:9000", baseUrl);
    }

    @Test
    void resolveBaseUrlShouldFallbackToServerPort() throws Exception {
        McpAsyncClientManager manager = new McpAsyncClientManager();
        setField(manager, "clientBaseUrl", "   ");
        setField(manager, "serverPort", "18080");

        String baseUrl = invokeResolveBaseUrl(manager);
        assertEquals("http://localhost:18080", baseUrl);
    }

    private String invokeResolveBaseUrl(McpAsyncClientManager manager) throws Exception {
        Method method = McpAsyncClientManager.class.getDeclaredMethod("resolveBaseUrl");
        method.setAccessible(true);
        return (String) method.invoke(manager);
    }

    private void setField(McpAsyncClientManager manager, String fieldName, String value) throws Exception {
        var field = McpAsyncClientManager.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(manager, value);
    }
}
