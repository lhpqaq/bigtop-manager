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
package org.apache.bigtop.manager.server.config;

import org.apache.bigtop.manager.server.holder.SessionUserHolder;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import jakarta.annotation.Nonnull;
import java.util.Optional;

@Configuration
public class JpaAuditConfig implements AuditorAware<Long> {

    @Nonnull
    @Override
    public Optional<Long> getCurrentAuditor() {
        try {
            return Optional.of(SessionUserHolder.getUserId());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
