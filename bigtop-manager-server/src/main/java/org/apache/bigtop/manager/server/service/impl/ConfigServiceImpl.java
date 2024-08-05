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
package org.apache.bigtop.manager.server.service.impl;

import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.dao.po.ClusterPO;
import org.apache.bigtop.manager.dao.po.ServiceConfigPO;
import org.apache.bigtop.manager.dao.po.ServicePO;
import org.apache.bigtop.manager.dao.po.TypeConfigPO;
import org.apache.bigtop.manager.dao.repository.ClusterRepository;
import org.apache.bigtop.manager.dao.repository.ServiceConfigRepository;
import org.apache.bigtop.manager.dao.repository.ServiceRepository;
import org.apache.bigtop.manager.dao.repository.TypeConfigRepository;
import org.apache.bigtop.manager.server.model.converter.ServiceConfigConverter;
import org.apache.bigtop.manager.server.model.converter.TypeConfigConverter;
import org.apache.bigtop.manager.server.model.dto.PropertyDTO;
import org.apache.bigtop.manager.server.model.dto.TypeConfigDTO;
import org.apache.bigtop.manager.server.model.vo.ServiceConfigVO;
import org.apache.bigtop.manager.server.service.ConfigService;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {

    @Resource
    private ClusterRepository clusterRepository;

    @Resource
    private ServiceRepository serviceRepository;

    @Resource
    private ServiceConfigRepository serviceConfigRepository;

    @Resource
    private TypeConfigRepository typeConfigRepository;

    @Override
    public List<ServiceConfigVO> list(Long clusterId) {
        ClusterPO clusterPO = clusterRepository.getReferenceById(clusterId);
        Sort sort = Sort.by(Sort.Direction.DESC, "version");
        List<ServiceConfigPO> list = serviceConfigRepository.findAllByClusterPO(clusterPO, sort);
        return ServiceConfigConverter.INSTANCE.fromPO2VO(list);
    }

    @Override
    public List<ServiceConfigVO> latest(Long clusterId) {
        ClusterPO clusterPO = clusterRepository.getReferenceById(clusterId);
        List<ServiceConfigPO> list = serviceConfigRepository.findAllByClusterPOAndSelectedIsTrue(clusterPO);
        return ServiceConfigConverter.INSTANCE.fromPO2VO(list);
    }

    @Override
    public void upsert(Long clusterId, Long serviceId, List<TypeConfigDTO> configs) {
        // Save configs
        ClusterPO clusterPO = clusterRepository.getReferenceById(clusterId);
        ServicePO servicePO = serviceRepository.getReferenceById(serviceId);
        ServiceConfigPO serviceCurrentConfig = findServiceCurrentConfig(clusterPO, servicePO);
        if (serviceCurrentConfig == null) {
            // Add config for new service
            addServiceConfig(clusterPO, servicePO, configs);
        } else {
            // Upsert config for existing service
            upsertServiceConfig(clusterPO, servicePO, serviceCurrentConfig, configs);
        }
    }

    private ServiceConfigPO findServiceCurrentConfig(ClusterPO clusterPO, ServicePO servicePO) {
        return serviceConfigRepository.findByClusterPOAndServicePOAndSelectedIsTrue(clusterPO, servicePO);
    }

    private void upsertServiceConfig(
            ClusterPO clusterPO, ServicePO servicePO, ServiceConfigPO currentConfig, List<TypeConfigDTO> configs) {
        List<TypeConfigDTO> existConfigs = TypeConfigConverter.INSTANCE.fromPO2DTO(currentConfig.getConfigs());
        if (shouldUpdateConfig(existConfigs, configs)) {
            // Unselect current config
            currentConfig.setSelected(false);
            serviceConfigRepository.save(currentConfig);

            // Create a new config
            String configDesc = "Update config for " + servicePO.getServiceName();
            Integer version = currentConfig.getVersion() + 1;
            addServiceConfig(clusterPO, servicePO, configs, configDesc, version);
        }
    }

    private Boolean shouldUpdateConfig(List<TypeConfigDTO> existConfigs, List<TypeConfigDTO> newConfigs) {
        if (existConfigs.size() != newConfigs.size()) {
            return true;
        }

        for (TypeConfigDTO newConfig : newConfigs) {
            for (TypeConfigDTO existConfig : existConfigs) {
                if (existConfig.getTypeName().equals(newConfig.getTypeName())) {
                    if (!existConfig.getProperties().equals(newConfig.getProperties())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void addServiceConfig(ClusterPO clusterPO, ServicePO servicePO, List<TypeConfigDTO> configs) {
        String configDesc = "Initial config for " + servicePO.getServiceName();
        Integer version = 1;
        addServiceConfig(clusterPO, servicePO, configs, configDesc, version);
    }

    private void addServiceConfig(
            ClusterPO clusterPO, ServicePO servicePO, List<TypeConfigDTO> configs, String configDesc, Integer version) {
        ServiceConfigPO serviceConfigPO = new ServiceConfigPO();
        serviceConfigPO.setClusterPO(clusterPO);
        serviceConfigPO.setServicePO(servicePO);
        serviceConfigPO.setConfigDesc(configDesc);
        serviceConfigPO.setVersion(version);
        serviceConfigPO.setSelected(true);
        serviceConfigRepository.save(serviceConfigPO);

        for (TypeConfigDTO typeConfigDTO : configs) {
            String typeName = typeConfigDTO.getTypeName();
            List<PropertyDTO> properties = typeConfigDTO.getProperties();
            TypeConfigPO typeConfigPO = new TypeConfigPO();
            typeConfigPO.setTypeName(typeName);
            typeConfigPO.setPropertiesJson(JsonUtils.writeAsString(properties));
            typeConfigPO.setServiceConfigPO(serviceConfigPO);
            typeConfigRepository.save(typeConfigPO);
        }
    }
}
