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
 * Schema for a multi-select enum elicitation property.
 *
 * <p>Without titles, serializes to {@code {"type":"array","items":{"type":"string","enum":[...]}}}.
 * With titles, serializes to {@code {"type":"array","items":{"anyOf":[{"const":"val","title":"Name"},...]}}}.</p>
 *
 * @param enumValues   the allowed values (required, must not be empty)
 * @param enumTitles   optional display titles for each value (same length as enumValues)
 * @param minItems     optional minimum number of selections
 * @param maxItems     optional maximum number of selections
 * @param defaultValue optional default selections
 */
public record MultiEnumProperty(String name, boolean required, String title, String description,
                                List<String> enumValues, List<String> enumTitles,
                                Integer minItems, Integer maxItems,
                                List<String> defaultValue) implements ElicitationProperty<List<String>> {

    public MultiEnumProperty {
        Objects.requireNonNull(enumValues, "enumValues must not be null");
        if (enumValues.isEmpty()) {
            throw new IllegalArgumentException("enumValues must not be empty");
        }
        if (enumTitles != null && enumTitles.size() != enumValues.size()) {
            throw new IllegalArgumentException("enumTitles must have the same length as enumValues");
        }
    }

    public MultiEnumProperty(String name, boolean required, List<String> enumValues) {
        this(name, required, null, null, enumValues, null, null, null, null);
    }

    public MultiEnumProperty(String name, boolean required, List<String> enumValues, List<String> enumTitles) {
        this(name, required, null, null, enumValues, enumTitles, null, null, null);
    }

    @Override
    public JsonObjectBuilder asJson() {
        JsonObjectBuilder b = Json.createObjectBuilder().add("type", "array");

        if (title != null) b.add("title", title);
        if (description != null) b.add("description", description);

        if (enumTitles != null) {
            JsonArrayBuilder anyOf = Json.createArrayBuilder();
            for (int i = 0; i < enumValues.size(); i++) {
                anyOf.add(Json.createObjectBuilder()
                        .add("const", enumValues.get(i))
                        .add("title", enumTitles.get(i)));
            }
            b.add("items", Json.createObjectBuilder().add("anyOf", anyOf));
        } else {
            JsonArrayBuilder values = Json.createArrayBuilder();
            for (String v : enumValues) {
                values.add(v);
            }
            b.add("items", Json.createObjectBuilder()
                    .add("type", "string")
                    .add("enum", values));
        }

        if (minItems != null) b.add("minItems", minItems);
        if (maxItems != null) b.add("maxItems", maxItems);

        if (defaultValue != null) {
            JsonArrayBuilder defaults = Json.createArrayBuilder();
            for (String v : defaultValue) {
                defaults.add(v);
            }
            b.add("default", defaults);
        }
        return b;
    }
}
