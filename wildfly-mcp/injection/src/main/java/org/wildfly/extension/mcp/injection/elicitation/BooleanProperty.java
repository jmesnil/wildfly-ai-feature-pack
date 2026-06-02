/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.wildfly.extension.mcp.injection.elicitation;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;

/**
 * Schema for a boolean elicitation property.
 * Serializes to {@code {"type":"boolean"}}.
 */
public record BooleanProperty(String name, boolean required, String title, String description, Boolean defaultValue) implements ElicitationProperty<Boolean> {

    public BooleanProperty(String name) {
        this(name, true, null, null, null);
    }

    public BooleanProperty(String name, boolean required, Boolean defaultValue) {
        this(name, required, null, null, defaultValue);
    }

    @Override
    public JsonObjectBuilder asJson() {
        JsonObjectBuilder b = Json.createObjectBuilder().add("type", "boolean");
        if (title != null) b.add("title", title);
        if (description != null) b.add("description", description);
        if (defaultValue != null) b.add("default", defaultValue);
        return b;
    }
}
