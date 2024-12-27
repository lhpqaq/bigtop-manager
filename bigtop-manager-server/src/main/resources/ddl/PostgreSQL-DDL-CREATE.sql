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

CREATE TABLE audit_log
(
    id                BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
    args              TEXT,
    create_by         BIGINT,
    create_time       TIMESTAMP(0),
    operation_desc    VARCHAR(255),
    operation_summary VARCHAR(255),
    tag_desc          VARCHAR(255),
    tag_name          VARCHAR(255),
    update_by         BIGINT,
    update_time       TIMESTAMP(0),
    uri               VARCHAR(255),
    user_id           BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE "user"
(
    id          BIGINT CHECK (id > 0) NOT NULL GENERATED ALWAYS AS IDENTITY,
    username    VARCHAR(32)  DEFAULT NULL,
    password    VARCHAR(32)  DEFAULT NULL,
    nickname    VARCHAR(32)  DEFAULT NULL,
    status      BOOLEAN          DEFAULT TRUE,
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    create_by   BIGINT,
    update_by   BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uk_username UNIQUE (username)
);

COMMENT ON COLUMN "user".status IS '0-Disable, 1-Enable';

CREATE TABLE cluster
(
    id            BIGINT CHECK (id > 0) NOT NULL GENERATED ALWAYS AS IDENTITY,
    name          VARCHAR(255)                      DEFAULT NULL,
    display_name  VARCHAR(255)                      DEFAULT NULL,
    "desc"        VARCHAR(255)                      DEFAULT NULL,
    type          INT CHECK (cluster.type > 0) DEFAULT 1,
    user_group    VARCHAR(255),
    root_dir      VARCHAR(255),
    status        INT DEFAULT NULL,
    create_time   TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_time   TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    create_by     BIGINT,
    update_by     BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uk_name UNIQUE (name)
);

COMMENT ON COLUMN cluster.name IS 'Unique Name';
COMMENT ON COLUMN cluster.display_name IS 'Display Name';
COMMENT ON COLUMN cluster."desc" IS 'Cluster Description';
COMMENT ON COLUMN cluster.type IS '1-Physical Machine, 2-Kubernetes';
COMMENT ON COLUMN cluster.status IS '1-healthy, 2-unhealthy, 3-unknown';

CREATE TABLE host
(
    id                   BIGINT CHECK (id > 0)         NOT NULL GENERATED ALWAYS AS IDENTITY,
    cluster_id           BIGINT DEFAULT NULL,
    hostname             VARCHAR(255) DEFAULT NULL,
    ssh_user             VARCHAR(255) DEFAULT NULL,
    ssh_port             INT DEFAULT NULL,
    auth_type            INT DEFAULT NULL,
    ssh_password         VARCHAR(255) DEFAULT NULL,
    ssh_key_string       TEXT DEFAULT NULL,
    ssh_key_filename     VARCHAR(255) DEFAULT NULL,
    ssh_key_password     VARCHAR(255) DEFAULT NULL,
    grpc_port            INT DEFAULT NULL,
    ipv4                 VARCHAR(32)  DEFAULT NULL,
    ipv6                 VARCHAR(32)  DEFAULT NULL,
    arch                 VARCHAR(32)  DEFAULT NULL,
    os                   VARCHAR(32)  DEFAULT NULL,
    available_processors INT,
    free_disk            BIGINT,
    free_memory_size     BIGINT,
    total_disk           BIGINT,
    total_memory_size    BIGINT,
    "desc"               VARCHAR(255)  DEFAULT NULL,
    status               INT  DEFAULT NULL,
    err_info             TEXT  DEFAULT NULL,
    create_time          TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_time          TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    create_by            BIGINT,
    update_by            BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uk_hostname UNIQUE (hostname, cluster_id)
);

COMMENT ON COLUMN host.auth_type IS '1-password, 2-key, 3-no_auth';
COMMENT ON COLUMN host.status IS '1-healthy, 2-unhealthy, 3-unknown';

DROP INDEX IF EXISTS idx_host_cluster_id;
CREATE INDEX idx_host_cluster_id ON host (cluster_id);

CREATE TABLE repo
(
    id          BIGINT CHECK (id > 0)         NOT NULL GENERATED ALWAYS AS IDENTITY,
    name        VARCHAR(32)  DEFAULT NULL,
    arch        VARCHAR(32)  DEFAULT NULL,
    base_url    VARCHAR(64)  DEFAULT NULL,
    type        INTEGER  DEFAULT NULL,
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    create_by   BIGINT,
    update_by   BIGINT,
    PRIMARY KEY (id)
);

COMMENT ON COLUMN repo.type IS '1-services, 2-tools';

CREATE TABLE service
(
    id                BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
    name              VARCHAR(255) DEFAULT NULL,
    display_name      VARCHAR(255) DEFAULT NULL,
    "desc"            VARCHAR(1024) DEFAULT NULL,
    "user"            VARCHAR(255) DEFAULT NULL,
    version           VARCHAR(255) DEFAULT NULL,
    stack             VARCHAR(255) DEFAULT NULL,
    need_restart      BOOLEAN DEFAULT FALSE,
    cluster_id        BIGINT,
    status            INTEGER DEFAULT NULL,
    create_time       TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_time       TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    create_by         BIGINT,
    update_by         BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uk_service_name UNIQUE (name, cluster_id)
);

COMMENT ON COLUMN service.status IS '1-healthy, 2-unhealthy, 3-unknown';

CREATE INDEX idx_service_cluster_id ON service (cluster_id);

CREATE TABLE service_config
(
    id                BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
    name              VARCHAR(255),
    properties_json   TEXT,
    cluster_id        BIGINT,
    service_id        BIGINT,
    create_time       TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_time       TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    create_by         BIGINT,
    update_by         BIGINT,
    PRIMARY KEY (id)
);

CREATE INDEX idx_sc_cluster_id ON service_config (cluster_id);
CREATE INDEX idx_sc_service_id ON service_config (service_id);

CREATE TABLE service_config_snapshot
(
    id                BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
    name              VARCHAR(255),
    "desc"            VARCHAR(255),
    config_json       TEXT,
    service_id        BIGINT,
    create_time       TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_time       TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    create_by         BIGINT,
    update_by         BIGINT,
    PRIMARY KEY (id)
);

CREATE INDEX idx_scs_service_id ON service_config_snapshot (service_id);

CREATE TABLE component
(
    id              BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
    name            VARCHAR(255),
    display_name    VARCHAR(255),
    cluster_id      BIGINT,
    host_id         BIGINT,
    service_id      BIGINT,
    status          INTEGER DEFAULT NULL,
    create_time     TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    create_by       BIGINT,
    update_by       BIGINT,
    PRIMARY KEY (id)
);

DROP INDEX IF EXISTS idx_component_cluster_id;
DROP INDEX IF EXISTS idx_component_host_id;
DROP INDEX IF EXISTS idx_component_service_id;
CREATE INDEX idx_component_cluster_id ON component (cluster_id);
CREATE INDEX idx_component_host_id ON component (host_id);
CREATE INDEX idx_component_service_id ON component (service_id);

CREATE TABLE job
(
    id          BIGINT CHECK (id > 0) NOT NULL GENERATED ALWAYS AS IDENTITY,
    name        VARCHAR(255),
    context     TEXT                  NOT NULL,
    state       VARCHAR(32)           NOT NULL,
    cluster_id  BIGINT CHECK (cluster_id > 0) DEFAULT NULL,
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    create_by   BIGINT,
    update_by   BIGINT,
    PRIMARY KEY (id)
);

CREATE INDEX idx_job_cluster_id ON job (cluster_id);

CREATE TABLE stage
(
    id             BIGINT CHECK (id > 0)     NOT NULL GENERATED ALWAYS AS IDENTITY,
    name           VARCHAR(32)               NOT NULL,
    service_name   VARCHAR(255),
    component_name VARCHAR(255),
    context        TEXT,
    "order"        INTEGER,
    state          VARCHAR(32)               NOT NULL,
    cluster_id     BIGINT CHECK (cluster_id > 0) DEFAULT NULL,
    job_id         BIGINT CHECK (job_id > 0) NOT NULL,
    create_time    TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_time    TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    create_by      BIGINT,
    update_by      BIGINT,
    PRIMARY KEY (id)
);

CREATE INDEX idx_stage_cluster_id ON stage (cluster_id);
CREATE INDEX idx_job_id ON stage (job_id);

CREATE TABLE task
(
    id             BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
    name           VARCHAR(255),
    hostname       VARCHAR(255),
    service_name   VARCHAR(255),
    service_user   VARCHAR(255),
    component_name VARCHAR(255),
    command        VARCHAR(255),
    custom_command VARCHAR(255),
    content        TEXT,
    context        TEXT NOT NULL,
    state          VARCHAR(255),
    cluster_id     BIGINT,
    job_id         BIGINT,
    stage_id       BIGINT,
    create_time    TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_time    TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    create_by      BIGINT,
    update_by      BIGINT,
    PRIMARY KEY (id)
);

DROP INDEX IF EXISTS idx_task_cluster_id;
DROP INDEX IF EXISTS idx_task_job_id;
DROP INDEX IF EXISTS idx_task_stage_id;
CREATE INDEX idx_task_cluster_id ON task (cluster_id);
CREATE INDEX idx_task_job_id ON task (job_id);
CREATE INDEX idx_task_stage_id ON task (stage_id);

CREATE TABLE llm_platform
(
    id             BIGINT CHECK (id > 0) NOT NULL GENERATED ALWAYS AS IDENTITY,
    name           VARCHAR(255)          NOT NULL,
    credential     TEXT         DEFAULT NULL,
    "desc"         TEXT         DEFAULT NULL,
    support_models VARCHAR(255) DEFAULT NULL,
    create_time    TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_time    TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP /* ON UPDATE CURRENT_TIMESTAMP */,
    create_by      BIGINT       DEFAULT NULL,
    update_by      BIGINT       DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE llm_auth_platform
(
    id              BIGINT CHECK (id > 0)          NOT NULL GENERATED ALWAYS AS IDENTITY,
    platform_id     BIGINT CHECK (platform_id > 0) NOT NULL,
    credentials     TEXT                           NOT NULL,
    is_deleted      BOOLEAN             DEFAULT FALSE,
    status          SMALLINT            DEFAULT 0,
    access_level    SMALLINT            DEFAULT 1,
    model           VARCHAR(255)        NOT NULL,
    name            VARCHAR(255)        NOT NULL,
    "desc"          VARCHAR(255)        NOT NULL,
    create_time     TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP /* ON UPDATE CURRENT_TIMESTAMP */,
    create_by       BIGINT       DEFAULT NULL,
    update_by       BIGINT       DEFAULT NULL,
    PRIMARY KEY (id)
);
COMMENT ON COLUMN "llm_auth_platform".status IS '1-Active, 2-Available, 3-Unavailable';
COMMENT ON COLUMN "llm_auth_platform".access_level IS '0-Chat_only, 1-Readonly, 2-Write_guided, 3-Full-access';
CREATE INDEX idx_authorized_platform_id ON llm_auth_platform (platform_id);

CREATE TABLE llm_chat_thread
(
    id          BIGINT CHECK (id > 0)          NOT NULL GENERATED ALWAYS AS IDENTITY,
    auth_id     BIGINT CHECK (auth_id > 0)     NOT NULL,
    user_id     BIGINT CHECK (user_id > 0)     NOT NULL,
    name        VARCHAR(255) DEFAULT NULL,
    is_deleted  BOOLEAN      DEFAULT FALSE,
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP /* ON UPDATE CURRENT_TIMESTAMP */,
    create_by   BIGINT       DEFAULT NULL,
    update_by   BIGINT       DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_chatthread_auth_id ON llm_chat_thread (auth_id);
CREATE INDEX idx_chatthread_user_id ON llm_chat_thread (user_id);

CREATE TABLE llm_chat_message
(
    id          BIGINT CHECK (id > 0)        NOT NULL GENERATED ALWAYS AS IDENTITY,
    thread_id   BIGINT CHECK (thread_id > 0) NOT NULL,
    user_id     BIGINT CHECK (user_id > 0)   NOT NULL,
    message     TEXT                         NOT NULL,
    sender      VARCHAR(50)                  NOT NULL,
    is_deleted  BOOLEAN      DEFAULT FALSE,
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP /* ON UPDATE CURRENT_TIMESTAMP */,
    create_by   BIGINT       DEFAULT NULL,
    update_by   BIGINT       DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_thread_id ON llm_chat_message (thread_id);
CREATE INDEX idx_message_user_id ON llm_chat_message (user_id);

INSERT INTO "user" (username, password, nickname, status)
VALUES ('admin', '21232f297a57a5a743894a0e4a801fc3', 'Administrator', true);

INSERT INTO repo (name, arch, base_url, type)
VALUES
('Service tarballs', 'x86_64', 'http://your-repo/', 1),
('Service tarballs', 'aarch64', 'http://your-repo/', 1),
('BM tools', 'x86_64', 'http://your-repo/', 2),
('BM tools', 'aarch64', 'http://your-repo/', 2);

INSERT INTO llm_platform (credential, name, support_models)
VALUES
('{"apiKey": "API Key"}','OpenAI','gpt-3.5-turbo,gpt-4,gpt-4o,gpt-3.5-turbo-16k,gpt-4-turbo-preview,gpt-4-32k,gpt-4o-mini'),
('{"apiKey": "API Key"}','DashScope','qwen-1.8b-chat,qwen-max,qwen-plus,qwen-turbo'),
('{"apiKey": "API Key", "secretKey": "Secret Key"}','QianFan','Yi-34B-Chat,ERNIE-4.0-8K,ERNIE-3.5-128K,ERNIE-Speed-8K,Llama-2-7B-Chat,Fuyu-8B');

UPDATE llm_platform
SET "desc" = 'Get your API Key in https://platform.openai.com/api-keys'
WHERE "name" = 'OpenAI';

UPDATE llm_platform
SET "desc" = 'Get your API Key in https://bailian.console.aliyun.com/?apiKey=1#/api-key'
WHERE "name" = 'DashScope';

UPDATE llm_platform
SET "desc" = 'Get API Key and Secret Key in https://console.bce.baidu.com/qianfan/ais/console/applicationConsole/application/v1'
WHERE "name" = 'QianFan';
