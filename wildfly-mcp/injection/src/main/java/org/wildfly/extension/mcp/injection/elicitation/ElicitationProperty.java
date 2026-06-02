/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.wildfly.extension.mcp.injection.elicitation;

import jakarta.json.JsonObjectBuilder;

/**
 * Sealed interface for MCP elicitation schema property types.
 * Each implementation serializes itself to JSON Schema via {@link #asJson()}.
 *
 * @param <T> the Java type of this property
 */
public sealed interface ElicitationProperty<T> permits BooleanProperty, EnumProperty, IntegerProperty, MultiEnumProperty, NumberProperty, StringProperty {

    String name();

    boolean required();

    String title();

    String description();

    T defaultValue();

    /**
     * Returns a {@link JsonObjectBuilder} containing the JSON Schema representation
     * of this property. The caller is responsible for calling {@code .build()}.
     */
    JsonObjectBuilder asJson();
}
