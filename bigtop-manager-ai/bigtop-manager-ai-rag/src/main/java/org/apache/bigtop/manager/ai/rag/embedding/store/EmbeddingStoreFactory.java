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

package org.apache.bigtop.manager.ai.rag.embedding.store;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;

@Component
@Configuration
public class EmbeddingStoreFactory {
    //    @Value("${milvus.host}")
    private String milvusHost;

    //    @Value("${milvus.port}")
    private int milvusPort;

    public EmbeddingStore<TextSegment> create() {
        milvusHost = "localhost";
        milvusPort = 19530;
        EmbeddingStore<TextSegment> milvusStore = MilvusEmbeddingStore.builder()
                .host(milvusHost)
                .port(milvusPort)
                .dimension(128)
                .collectionName("example_collection")
                .build();
        return milvusStore;
    }
}
