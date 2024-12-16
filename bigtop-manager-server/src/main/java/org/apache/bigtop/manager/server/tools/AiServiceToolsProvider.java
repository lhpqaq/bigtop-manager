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
package org.apache.bigtop.manager.server.tools;

import org.apache.bigtop.manager.server.enums.ChatbotCommand;

import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderRequest;
import dev.langchain4j.service.tool.ToolProviderResult;

public class AiServiceToolsProvider implements ToolProvider {

    ChatbotCommand chatbotCommand;

    public AiServiceToolsProvider(ChatbotCommand chatbotCommand) {
        this.chatbotCommand = chatbotCommand;
    }

    public AiServiceToolsProvider() {
        this.chatbotCommand = null;
    }

    @Override
    public ToolProviderResult provideTools(ToolProviderRequest toolProviderRequest) {
        if (chatbotCommand.equals(ChatbotCommand.INFO)) {
            ClusterInfoTools clusterInfoTools = new ClusterInfoTools();
            return ToolProviderResult.builder().addAll(clusterInfoTools.list()).build();
        }
        return null;
    }
}