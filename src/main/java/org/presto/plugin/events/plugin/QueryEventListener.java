/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.presto.plugin.events.plugin;

import com.facebook.airlift.log.Logger;
import com.facebook.presto.spi.eventlistener.EventListener;
import com.facebook.presto.spi.eventlistener.QueryCompletedEvent;
import com.facebook.presto.spi.eventlistener.QueryCreatedEvent;
import com.facebook.presto.spi.eventlistener.SplitCompletedEvent;
import org.presto.plugin.events.mapper.ObjectMapperFactory;
import org.presto.plugin.events.mapper.PrestoObjectMapper;
import org.presto.plugin.events.writer.EventWriter;
import org.presto.plugin.events.writer.EventWriterFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * Created by sudip.hc on 01/02/18.
 */

public class QueryEventListener implements EventListener {
    private static final Logger log = Logger.get(QueryEventListener.class);

    EventWriter queryCompleted; // queryCreated, querySplitted;
    PrestoObjectMapper mapper;

    public QueryEventListener(Map<String, String> config) {
        log.info("Query Logger Event Listener Config: %s", config);

        queryCompleted = EventWriterFactory.getEventWriter(config, EventWriter.Event.Complete);
        // queryCreated = EventWriterFactory.getEventWriter(config, EventWriter.Event.Create);
        // querySplitted = EventWriterFactory.getEventWriter(config, EventWriter.Event.Split);

        ObjectMapperFactory factory = new ObjectMapperFactory();
        mapper = factory.getPrestoObjectMapper(config.get("mapper"));

    }

    /**
     * 压缩事件数据以避免超过kafka消息大小限制
     * @param event 事件字符串
     * @return 压缩后的字符串
     */
    private String compressEvent(String event) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
            gzipOutputStream.write(event.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("Failed to compress event", e);
            log.error("event: %s", event);
            return event;
        }
        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }

    public void queryCompleted(QueryCompletedEvent queryCompletedEvent) {
        String event = mapper.getQueryCompletedEvent(queryCompletedEvent);
        queryCompleted.write(compressEvent(event));
    }

    public void queryCreated(QueryCreatedEvent queryCreatedEvent) {
        // String event = mapper.getQueryCreatedEvent(queryCreatedEvent);
        // queryCreated.write(compressEvent(event));
    }

    public void splitCompleted(SplitCompletedEvent splitCompletedEvent) {
        // String event = mapper.getQuerySplittedEvent(splitCompletedEvent);
        // querySplitted.write(compressEvent(event));
    }

}

