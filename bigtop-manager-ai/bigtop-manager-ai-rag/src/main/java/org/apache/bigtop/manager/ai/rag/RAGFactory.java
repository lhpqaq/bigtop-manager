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
package org.apache.bigtop.manager.ai.rag;

import org.apache.bigtop.manager.ai.rag.embedding.store.EmbeddingStoreFactory;

import org.springframework.stereotype.Component;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.google.customsearch.GoogleCustomWebSearchEngine;

import jakarta.annotation.Resource;

@Component
public class RAGFactory {
    @Resource
    private EmbeddingStoreFactory embeddingStoreFactory;

    public ContentRetriever createContentRetriever() {
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStoreFactory.create())
                .embeddingModel(embeddingModel)
                .maxResults(5)
                .minScore(0.75)
                .build();
    }

    public ContentRetriever createWebSearchContentRetriever() {
        WebSearchEngine webSearchEngine = GoogleCustomWebSearchEngine.withApiKeyAndCsi(
                "Get in https://developers.google.com/custom-search/v1/introduction?hl=zh-cn",
                "Get in https://programmablesearchengine.google.com/cse/");
        return WebSearchContentRetriever.builder()
                .webSearchEngine(webSearchEngine)
                .maxResults(5)
                .build();
    }

    public RetrievalAugmentor createRetrievalAugmentor() {
        QueryRouter queryRouter = new DefaultQueryRouter(createContentRetriever(), createWebSearchContentRetriever());
        return DefaultRetrievalAugmentor.builder().queryRouter(queryRouter).build();
    }
}
