/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.wildfly.extension.mcp.injection.elicitation;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;

/**
 * Schema for an integer elicitation property.
 * Serializes to {@code {"type":"integer","minimum":...,"maximum":...}}.
 */
public record IntegerProperty(String name, boolean required, String title, String description, Integer min, Integer max, Integer defaultValue) implements ElicitationProperty<Integer> {

    public IntegerProperty(String name) {
        this(name, true, null, null, null, null, null);
    }

    public IntegerProperty(String name, boolean required, Integer defaultValue) {
        this(name, required, null, null, null, null, defaultValue);
    }

    public IntegerProperty(String name, boolean required, Integer min, Integer max) {
        this(name, required, null, null, min, max, null);
    }

    @Override
    public JsonObjectBuilder asJson() {
        JsonObjectBuilder b = Json.createObjectBuilder().add("type", "integer");
        if (title != null) b.add("title", title);
        if (description != null) b.add("description", description);
        if (min != null) b.add("minimum", min);
        if (max != null) b.add("maximum", max);
        if (defaultValue != null) b.add("default", defaultValue);
        return b;
    }
}
