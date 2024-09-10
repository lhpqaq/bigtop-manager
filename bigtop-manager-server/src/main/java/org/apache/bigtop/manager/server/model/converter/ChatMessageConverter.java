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
package org.apache.bigtop.manager.server.model.converter;

import org.apache.bigtop.manager.ai.core.enums.MessageSender;
import org.apache.bigtop.manager.dao.po.ChatMessagePO;
import org.apache.bigtop.manager.server.config.MapStructSharedConfig;
import org.apache.bigtop.manager.server.model.vo.ChatMessageVO;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapStructSharedConfig.class)
public interface ChatMessageConverter {
    ChatMessageConverter INSTANCE = Mappers.getMapper(ChatMessageConverter.class);

    ChatMessageVO fromPO2VO(ChatMessagePO chatMessagePO);

    default MessageSender mapStringToMessageSender(String sender) {
        if (sender == null) {
            return null;
        }
        try {
            return MessageSender.valueOf(sender.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}