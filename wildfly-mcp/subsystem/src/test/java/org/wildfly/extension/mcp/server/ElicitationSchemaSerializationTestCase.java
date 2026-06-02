/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.wildfly.extension.mcp.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.util.List;
import org.junit.Test;
import org.wildfly.extension.mcp.injection.elicitation.BooleanProperty;
import org.wildfly.extension.mcp.injection.elicitation.EnumProperty;
import org.wildfly.extension.mcp.injection.elicitation.IntegerProperty;
import org.wildfly.extension.mcp.injection.elicitation.MultiEnumProperty;
import org.wildfly.extension.mcp.injection.elicitation.NumberProperty;
import org.wildfly.extension.mcp.injection.elicitation.StringProperty;

public class ElicitationSchemaSerializationTestCase {

    // ==================== BooleanSchema ====================

    @Test
    public void testBooleanSchemaMinimal() {
        JsonObject json = new BooleanProperty("foo").asJson().build();
        assertEquals("boolean", json.getString("type"));
        assertEquals(1, json.size());
    }

    @Test
    public void testBooleanSchemaRequired() {
        BooleanProperty schema = new BooleanProperty("foo", false, false);
        assertFalse(schema.required());
        JsonObject json = schema.asJson().build();
        assertEquals("boolean", json.getString("type"));
        assertEquals(false, json.getBoolean("default"));
    }

    // ==================== StringSchema ====================

    @Test
    public void testStringSchemaMinimal() {
        JsonObject json = new StringProperty("foo").asJson().build();
        assertEquals("string", json.getString("type"));
        assertEquals(1, json.size());
    }

    @Test
    public void testStringSchemaAllFields() {
        StringProperty schema = new StringProperty("foo", "My Title", "A description", 2, 100, "email", true, "default@example.com");
        assertEquals("foo", schema.name());
        assertTrue(schema.required());
        JsonObject json = schema.asJson().build();
        assertEquals("string", json.getString("type"));
        assertEquals("My Title", json.getString("title"));
        assertEquals("A description", json.getString("description"));
        assertEquals(2, json.getInt("minLength"));
        assertEquals(100, json.getInt("maxLength"));
        assertEquals("email", json.getString("format"));
        assertEquals("default@example.com", json.getString("default"));
    }

    @Test
    public void testStringSchemaRequiredOnly() {
        StringProperty schema = new StringProperty("foo");
        assertTrue(schema.required());
        JsonObject json = schema.asJson().build();
        assertEquals("string", json.getString("type"));
        assertFalse(json.containsKey("title"));
        assertFalse(json.containsKey("description"));
        assertFalse(json.containsKey("minLength"));
        assertFalse(json.containsKey("maxLength"));
        assertFalse(json.containsKey("format"));
        assertFalse(json.containsKey("default"));
    }

    @Test
    public void testStringSchemaRequiredWithTitleAndDescription() {
        StringProperty schema = new StringProperty("foo", true, "Title", "Desc");
        JsonObject json = schema.asJson().build();
        assertEquals("Title", json.getString("title"));
        assertEquals("Desc", json.getString("description"));
        assertFalse(json.containsKey("format"));
    }

    @Test
    public void testStringSchemaOptionalFieldsAbsent() {
        StringProperty schema = new StringProperty("foo", null, null, null, null, null, false, null);
        JsonObject json = schema.asJson().build();
        assertEquals("string", json.getString("type"));
        assertEquals(1, json.size());
    }

    // ==================== NumberSchema ====================

    @Test
    public void testNumberSchemaMinimal() {
        JsonObject json = new NumberProperty("foo").asJson().build();
        assertEquals("number", json.getString("type"));
        assertEquals(1, json.size());
    }

    @Test
    public void testNumberSchemaWithBounds() {
        NumberProperty schema = new NumberProperty("foo", 0.5, 99.9);
        assertTrue(schema.required());
        JsonObject json = schema.asJson().build();
        assertEquals("number", json.getString("type"));
        assertEquals(0.5, json.getJsonNumber("minimum").doubleValue(), 0.001);
        assertEquals(99.9, json.getJsonNumber("maximum").doubleValue(), 0.001);
    }

    @Test
    public void testNumberSchemaWithAllParameters() {
        NumberProperty schema = new NumberProperty("foo", false, "Number", "title for the number", 0.5, 99.9, 3.14);
        assertFalse(schema.required());
        JsonObject json = schema.asJson().build();
        assertEquals("number", json.getString("type"));
        assertEquals("Number", json.getString("title"));
        assertEquals("title for the number", json.getString("description"));
        assertEquals(0.5, json.getJsonNumber("minimum").doubleValue(), 0.001);
        assertEquals(99.9, json.getJsonNumber("maximum").doubleValue(), 0.001);
        assertEquals(3.14, json.getJsonNumber("default").doubleValue(), 0.001);
    }

    @Test
    public void testNumberSchemaOnlyMin() {
        NumberProperty schema = new NumberProperty("foo", 1.0, null);
        JsonObject json = schema.asJson().build();
        assertEquals("number", json.getString("type"));
        assertTrue(json.containsKey("minimum"));
        assertFalse(json.containsKey("maximum"));
    }

    // ==================== IntegerSchema ====================

    @Test
    public void testIntegerSchemaMinimal() {
        JsonObject json = new IntegerProperty("foo").asJson().build();
        assertEquals("integer", json.getString("type"));
        assertEquals(1, json.size());
    }

    @Test
    public void testIntegerSchemaWithBounds() {
        IntegerProperty schema = new IntegerProperty("foo", true, 1, 10);
        JsonObject json = schema.asJson().build();
        assertEquals("integer", json.getString("type"));
        assertEquals(1, json.getInt("minimum"));
        assertEquals(10, json.getInt("maximum"));
    }

    @Test
    public void testIntegerSchemaOnlyMax() {
        IntegerProperty schema = new IntegerProperty("foo", false, null, 100);
        JsonObject json = schema.asJson().build();
        assertFalse(json.containsKey("minimum"));
        assertEquals(100, json.getInt("maximum"));
    }

    // ==================== EnumSchema (single-select) ====================

    @Test
    public void testEnumSchemaWithoutTitles() {
        EnumProperty schema = new EnumProperty("foo", true, List.of("A", "B", "C"));
        JsonObject json = schema.asJson().build();
        assertEquals("string", json.getString("type"));
        JsonArray arr = json.getJsonArray("enum");
        assertNotNull(arr);
        assertEquals(3, arr.size());
        assertEquals("A", arr.getString(0));
        assertEquals("B", arr.getString(1));
        assertEquals("C", arr.getString(2));
        assertFalse(json.containsKey("oneOf"));
        assertTrue(schema.required());
    }

    @Test
    public void testEnumSchemaWithTitles() {
        EnumProperty schema = new EnumProperty("foo", false, List.of("en", "fr"), List.of("English", "French"));
        JsonObject json = schema.asJson().build();
        assertEquals("string", json.getString("type"));
        assertFalse(json.containsKey("enum"));
        JsonArray oneOf = json.getJsonArray("oneOf");
        assertNotNull(oneOf);
        assertEquals(2, oneOf.size());
        assertEquals("en", oneOf.getJsonObject(0).getString("const"));
        assertEquals("English", oneOf.getJsonObject(0).getString("title"));
        assertEquals("fr", oneOf.getJsonObject(1).getString("const"));
        assertEquals("French", oneOf.getJsonObject(1).getString("title"));
    }

    @Test
    public void testEnumSchemaWithDefault() {
        EnumProperty schema = new EnumProperty("foo", true, null, null, List.of("Red", "Green", "Blue"), null, "Red");
        JsonObject json = schema.asJson().build();
        assertEquals("Red", json.getString("default"));
        assertEquals(3, json.getJsonArray("enum").size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEnumSchemaEmptyValuesThrows() {
        new EnumProperty("foo", false, List.of());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEnumSchemaMismatchedTitlesThrows() {
        new EnumProperty("foo", false, List.of("a", "b"), List.of("Only One"));
    }

    // ==================== MultiEnumSchema (multi-select) ====================

    @Test
    public void testMultiEnumSchemaWithoutTitles() {
        MultiEnumProperty schema = new MultiEnumProperty("colors", true, List.of("Red", "Green", "Blue"));
        JsonObject json = schema.asJson().build();
        assertEquals("array", json.getString("type"));
        JsonObject items = json.getJsonObject("items");
        assertNotNull(items);
        assertEquals("string", items.getString("type"));
        JsonArray enumArr = items.getJsonArray("enum");
        assertEquals(3, enumArr.size());
        assertEquals("Red", enumArr.getString(0));
        assertEquals("Green", enumArr.getString(1));
        assertEquals("Blue", enumArr.getString(2));
        assertFalse(json.containsKey("minItems"));
        assertFalse(json.containsKey("maxItems"));
        assertTrue(schema.required());
    }

    @Test
    public void testMultiEnumSchemaWithTitles() {
        MultiEnumProperty schema = new MultiEnumProperty("colors", false, List.of("#FF0000", "#00FF00", "#0000FF"),
                List.of("Red", "Green", "Blue"));
        JsonObject json = schema.asJson().build();
        assertEquals("array", json.getString("type"));
        JsonObject items = json.getJsonObject("items");
        assertFalse(items.containsKey("enum"));
        JsonArray anyOf = items.getJsonArray("anyOf");
        assertNotNull(anyOf);
        assertEquals(3, anyOf.size());
        assertEquals("#FF0000", anyOf.getJsonObject(0).getString("const"));
        assertEquals("Red", anyOf.getJsonObject(0).getString("title"));
        assertEquals("#0000FF", anyOf.getJsonObject(2).getString("const"));
        assertEquals("Blue", anyOf.getJsonObject(2).getString("title"));
    }

    @Test
    public void testMultiEnumSchemaWithBoundsAndDefault() {
        MultiEnumProperty schema = new MultiEnumProperty("colors", true, null, "Choose colors",
                List.of("Red", "Green", "Blue"), null, 1, 2, List.of("Red", "Green"));
        JsonObject json = schema.asJson().build();
        assertEquals("array", json.getString("type"));
        assertEquals("Choose colors", json.getString("description"));
        assertEquals(1, json.getInt("minItems"));
        assertEquals(2, json.getInt("maxItems"));
        JsonArray defaults = json.getJsonArray("default");
        assertNotNull(defaults);
        assertEquals(2, defaults.size());
        assertEquals("Red", defaults.getString(0));
        assertEquals("Green", defaults.getString(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultiEnumSchemaEmptyValuesThrows() {
        new MultiEnumProperty("foo", false, List.of());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultiEnumSchemaMismatchedTitlesThrows() {
        new MultiEnumProperty("foo", false, List.of("a", "b"), List.of("Only One"));
    }
}
