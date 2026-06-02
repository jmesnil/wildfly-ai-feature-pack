/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.wildfly.extension.mcp.injection.elicitation;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import java.util.List;
import java.util.Objects;

/**
 * Schema for a single-select enum elicitation property.
 *
 * <p>Without titles, serializes to {@code {"type":"string","enum":[...]}}.
 * With titles, serializes to {@code {"type":"string","oneOf":[{"const":"val","title":"Name"},...]}}.</p>
 *
 * @param enumValues   the allowed values (required, must not be empty)
 * @param enumTitles   optional display titles for each value (same length as enumValues)
 * @param defaultValue optional default value (must be one of enumValues if set)
 */
public record EnumProperty(String name, boolean required, String title, String description, List<String> enumValues, List<String> enumTitles, String defaultValue) implements ElicitationProperty<String> {

    public EnumProperty {
        Objects.requireNonNull(enumValues, "enumValues must not be null");
        if (enumValues.isEmpty()) {
            throw new IllegalArgumentException("enumValues must not be empty");
        }
        if (enumTitles != null && enumTitles.size() != enumValues.size()) {
            throw new IllegalArgumentException("enumTitles must have the same length as enumValues");
        }
    }

    public EnumProperty(String name, boolean required, String... enumValues) {
        this(name, required, null, null, List.of(enumValues), null, null);
    }

    public EnumProperty(String name, boolean required, List<String> enumValues) {
        this(name, required, null, null, enumValues, null, null);
    }

    public EnumProperty(String name, boolean required, List<String> enumValues, List<String> enumTitles) {
        this(name, required, null, null, enumValues, enumTitles, null);
    }

    @Override
    public JsonObjectBuilder asJson() {
        JsonObjectBuilder b = Json.createObjectBuilder().add("type", "string");

        if (title != null) b.add("title", title);
        if (description != null) b.add("description", description);

        if (enumTitles != null) {
            JsonArrayBuilder oneOf = Json.createArrayBuilder();
            for (int i = 0; i < enumValues.size(); i++) {
                oneOf.add(Json.createObjectBuilder()
                        .add("const", enumValues.get(i))
                        .add("title", enumTitles.get(i)));
            }
            b.add("oneOf", oneOf);
        } else {
            JsonArrayBuilder values = Json.createArrayBuilder();
            for (String v : enumValues) {
                values.add(v);
            }
            b.add("enum", values);
        }

        if (defaultValue != null) b.add("default", defaultValue);
        return b;
    }
}
