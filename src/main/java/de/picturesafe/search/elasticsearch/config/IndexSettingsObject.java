/*
 * Copyright 2020 picturesafe media/data/bank GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.picturesafe.search.elasticsearch.config;

import de.picturesafe.search.elasticsearch.model.DocumentBuilder;
import de.picturesafe.search.elasticsearch.model.IndexObject;
import de.picturesafe.search.util.logging.CustomJsonToStringStyle;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;

import java.io.IOException;
import java.util.Map;

import static de.picturesafe.search.elasticsearch.connect.util.ElasticDocumentUtils.getString;

/**
 * Configuration of a custom index settings object.
 */
public class IndexSettingsObject implements IndexObject<IndexSettingsObject> {

    private String name;
    private String json;
    private XContentBuilder content;

    /**
     * ONLY FOR INTERNAL USAGE
     */
    public IndexSettingsObject() {
    }

    /**
     * Constructor
     * @param name Name of the index settings object
     */
    public IndexSettingsObject(String name) {
        this.name = name;
        try {
            this.content = JsonXContent.contentBuilder();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructor
     * @param name Name of the index settings object
     * @param json Settings of the object as JSON
     */
    public IndexSettingsObject(String name, String json) {
        Validate.notEmpty(name, "Parameter 'name' may not be null or empty!");
        Validate.notEmpty(json, "Parameter 'json' may not be null or empty!");

        this.name = name;
        this.json = json;
    }

    /**
     * Gets the name of the index settings object.
     * @return Name of the index settings object
     */
    public String name() {
        return name;
    }

    /**
     * Gets the settings of the object as JSON.
     * @return Settings of the object as JSON
     */
    public String json() {
        return (json != null) ? json : Strings.toString(content);
    }

    /**
     * Gets the settings of the object as XContentBuilder.
     * @return Settings of the object as XContentBuilder
     */
    public XContentBuilder content() {
        return content;
    }

    @Override
    public Map<String, Object> toDocument() {
        return DocumentBuilder.withoutId().put("name", name()).put("json", json()).build();
    }

    @Override
    public IndexSettingsObject fromDocument(Map<String, Object> document) {
        name = getString(document, "name");
        json = getString(document, "json");
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final IndexSettingsObject that = (IndexSettingsObject) o;
        return new EqualsBuilder().append(name, that.name).append(json, that.json).isEquals();
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, new CustomJsonToStringStyle()) //--
                .append("name", name) //--
                .append("json", json) //--
                .append("content", content) //--
                .toString();
    }
}
