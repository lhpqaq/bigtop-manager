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
package org.apache.bigtop.manager.stack.infra.v1_0_0.prometheus;

import org.apache.bigtop.manager.common.message.entity.payload.CommandPayload;
import org.apache.bigtop.manager.stack.core.annotations.GlobalParams;
import org.apache.bigtop.manager.stack.core.spi.param.Params;
import org.apache.bigtop.manager.stack.core.utils.LocalSettings;
import org.apache.bigtop.manager.stack.infra.param.InfraParams;

import com.google.auto.service.AutoService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Slf4j
@AutoService(Params.class)
@NoArgsConstructor
public class PrometheusParams extends InfraParams {

    private List<Map<String, Object>> prometheusScrapeJobs;
    private String prometheusContent;

    public PrometheusParams(CommandPayload commandPayload) {
        super(commandPayload);
        globalParamsMap.put("scrape_jobs", prometheusScrapeJobs);
    }

    public String dataDir() {
        return MessageFormat.format("{0}/data", serviceHome());
    }

    public String confDir() {
        return MessageFormat.format("{0}", serviceHome());
    }

    @Override
    public String getServiceName() {
        return "prometheus";
    }

    @GlobalParams
    public Map<String, Object> scrapeConfigs() {
        List<Map<String, Object>> jobs = new ArrayList<>();
        Map<String, Object> configuration = LocalSettings.configurations(getServiceName(), "prometheus");
        prometheusContent = (String) configuration.get("content");
        log.info(configuration.toString());
        @SuppressWarnings("unchecked")
        Map<String, Object> scrapeJobs = (Map<String, Object>) configuration.get("scrape_jobs");
        log.info(scrapeJobs.toString());
        for (Map.Entry<String, Object> entry : scrapeJobs.entrySet()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> scrapeJob = (Map<String, Object>) entry.getValue();
            Map<String, Object> job = new HashMap<>();
            job.put("name", scrapeJob.get("job_name"));
            job.put("targets", scrapeJob.get("job_targets"));
            job.put("scrape_interval", scrapeJob.get("job_scrape_interval"));
            jobs.add(job);
            log.info(job.toString());
        }
        log.info(jobs.toString());
        prometheusScrapeJobs = jobs;
        return configuration;
    }
}
