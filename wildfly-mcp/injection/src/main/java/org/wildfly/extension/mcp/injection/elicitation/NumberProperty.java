/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.wildfly.extension.mcp.injection.elicitation;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;

/**
 * Schema for a decimal number elicitation property.
 * Serializes to {@code {"type":"number","minimum":...,"maximum":...}}.
 */
public record NumberProperty(String name, boolean required, String title, String description, Double min, Double max, Double defaultValue) implements ElicitationProperty<Double> {

    public NumberProperty(String name) {
        this(name, true, null, null, null, null, null);
    }

    public NumberProperty(String name, boolean required, Double defaultValue) {
        this(name, required, null, null, null, null, defaultValue);
    }

    public NumberProperty(String name, Double min, Double max) {
        this(name, true, null, null, min, max, null);
    }

    @Override
    public JsonObjectBuilder asJson() {
        JsonObjectBuilder b = Json.createObjectBuilder().add("type", "number");
        if (title != null) b.add("title", title);
        if (description != null) b.add("description", description);
        if (min != null) b.add("minimum", min);
        if (max != null) b.add("maximum", max);
        if (defaultValue != null) b.add("default", defaultValue);
        return b;
    }
}
