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
package org.apache.bigtop.manager.server.command.validator;

import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.dao.po.ComponentPO;
import org.apache.bigtop.manager.dao.po.ServicePO;
import org.apache.bigtop.manager.dao.query.ComponentQuery;
import org.apache.bigtop.manager.dao.repository.ComponentDao;
import org.apache.bigtop.manager.dao.repository.ServiceDao;
import org.apache.bigtop.manager.server.command.CommandIdentifier;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.exception.ApiException;

import org.apache.commons.collections4.CollectionUtils;

import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;

@Component
public class ClusterStartValidator implements CommandValidator {

    @Resource
    private ServiceDao serviceDao;

    @Resource
    private ComponentDao componentDao;

    @Override
    public List<CommandIdentifier> getCommandIdentifiers() {
        return List.of(new CommandIdentifier(CommandLevel.CLUSTER, Command.START));
    }

    @Override
    public void validate(ValidatorContext context) {
        Long clusterId = context.getCommandDTO().getClusterId();
        List<ServicePO> servicePOList = serviceDao.findByClusterId(clusterId);

        if (CollectionUtils.isEmpty(servicePOList)) {
            throw new ApiException(ApiExceptionEnum.CLUSTER_HAS_NO_SERVICES);
        }

        ComponentQuery componentQuery =
                ComponentQuery.builder().clusterId(clusterId).build();
        List<ComponentPO> componentPOList = componentDao.findByQuery(componentQuery);
        if (CollectionUtils.isEmpty(componentPOList)) {
            throw new ApiException(ApiExceptionEnum.CLUSTER_HAS_NO_COMPONENTS);
        }
    }
}
