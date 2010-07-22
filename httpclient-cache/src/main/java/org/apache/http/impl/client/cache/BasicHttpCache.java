/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.http.impl.client.cache;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.annotation.ThreadSafe;
import org.apache.http.client.cache.HttpCache;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheOperationException;
import org.apache.http.client.cache.HttpCacheUpdateCallback;

/**
 * Implements {@link HttpCache} using LinkedHashMap for backing store
 *
 * @since 4.1
 */
@ThreadSafe
public class BasicHttpCache implements HttpCache {

    private final LinkedHashMap<String, HttpCacheEntry> baseMap = new LinkedHashMap<String, HttpCacheEntry>(
            20, 0.75f, true) {

        private static final long serialVersionUID = -7750025207539768511L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, HttpCacheEntry> eldest) {
            return size() > maxEntries;
        }

    };

    private final int maxEntries;

    public BasicHttpCache(int maxEntries) {
        this.maxEntries = maxEntries;
    }

    /**
     * Places a HttpCacheEntry in the cache
     *
     * @param url
     *            Url to use as the cache key
     * @param entry
     *            HttpCacheEntry to place in the cache
     */
    public synchronized void putEntry(String url, HttpCacheEntry entry) {
        baseMap.put(url, entry);
    }

    /**
     * Gets an entry from the cache, if it exists
     *
     * @param url
     *            Url that is the cache key
     * @return HttpCacheEntry if one exists, or null for cache miss
     */
    public synchronized HttpCacheEntry getEntry(String url) {
        return baseMap.get(url);
    }

    /**
     * Removes a HttpCacheEntry from the cache
     *
     * @param url
     *            Url that is the cache key
     */
    public synchronized void removeEntry(String url) {
        baseMap.remove(url);
    }

    public synchronized void updateEntry(
            String url,
            HttpCacheUpdateCallback callback) throws HttpCacheOperationException {
        HttpCacheEntry existingEntry = baseMap.get(url);
        baseMap.put(url, callback.update(existingEntry));
    }

}
