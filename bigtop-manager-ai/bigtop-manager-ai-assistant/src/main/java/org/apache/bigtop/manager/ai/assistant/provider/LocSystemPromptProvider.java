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
package org.apache.bigtop.manager.ai.assistant.provider;

import org.apache.bigtop.manager.ai.core.enums.SystemPrompt;
import org.apache.bigtop.manager.ai.core.provider.SystemPromptProvider;

import org.springframework.util.ResourceUtils;

import dev.langchain4j.data.message.SystemMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Slf4j
public class LocSystemPromptProvider implements SystemPromptProvider {

    private static final String SYSTEM_PROMPT_PATH =
            "/bigtop-manager-ai/bigtop-manager-ai-assistant/src/main/resources/";

    @Override
    public SystemMessage getSystemPrompt(SystemPrompt systemPrompt) {
        if (systemPrompt == SystemPrompt.DEFAULT_PROMPT) {
            systemPrompt = SystemPrompt.BIGDATA_PROFESSOR;
        }

        return loadPromptFromFile(systemPrompt.getValue());
    }

    @Override
    public SystemMessage getSystemPrompt() {
        return getSystemPrompt(SystemPrompt.DEFAULT_PROMPT);
    }

    private String loadTextFromFile(String fileName) {
        try {
            File file = ResourceUtils.getFile(fileName);
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error(
                    "Exception occurred while loading SystemPrompt from local. Here is some information:{}",
                    e.getMessage());
            return null;
        }
    }

    private SystemMessage loadPromptFromFile(String fileName) {
        final String filePath = System.getProperty("user.dir") + SYSTEM_PROMPT_PATH + fileName + ".st";
        String text = loadTextFromFile(filePath);
        if (text == null) {
            return SystemMessage.from("You are a helpful assistant.");
        } else {
            return SystemMessage.from(text);
        }
    }

    public SystemMessage getLanguagePrompt(String local) {
        final String filePath = System.getProperty("user.dir") + SYSTEM_PROMPT_PATH
                + SystemPrompt.LANGUAGE_PROMPT.getValue() + '-' + local + ".st";
        String text = loadTextFromFile(filePath);
        if (text == null) {
            return SystemMessage.from("Answer in " + local);
        } else {
            return SystemMessage.from(text);
        }
    }
}
