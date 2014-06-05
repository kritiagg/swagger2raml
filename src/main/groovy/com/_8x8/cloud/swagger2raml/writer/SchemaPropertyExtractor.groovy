package com._8x8.cloud.swagger2raml.writer

import com._8x8.cloud.swagger2raml.model.ArraySchemaProperty
import com._8x8.cloud.swagger2raml.model.ObjectSchemaProperty
import com._8x8.cloud.swagger2raml.model.PrimitiveSchemaProperty
import com._8x8.cloud.swagger2raml.model.SchemaProperty

/**
 * @author Jacek Kunicki
 */
class SchemaPropertyExtractor {

    public static extractSchemaProperties(Collection<SchemaProperty> schemaProperties) {
        return schemaProperties.collectEntries { SchemaProperty schemaProperty ->
            if (schemaProperty.type instanceof PrimitiveSchemaProperty) {
                return extractPrimitiveProperty(schemaProperty)
            } else if (schemaProperty.type instanceof ArraySchemaProperty) {
                return extractArrayProperty(schemaProperty)
            } else {
                return extractObjectProperty(schemaProperty)
            }
        }
    }

    private static extractArrayProperty(SchemaProperty schemaProperty) {
        ArraySchemaProperty arraySchemaProperty = schemaProperty.type as ArraySchemaProperty
        def itemSchema

        if (arraySchemaProperty.itemType instanceof ObjectSchemaProperty) {
            itemSchema = [
                    type: arraySchemaProperty.itemType.name,
                    properties: extractSchemaPropertyType(arraySchemaProperty.itemType as ObjectSchemaProperty)
            ]
        } else {
            itemSchema = [
                    type: arraySchemaProperty.itemType.name
            ]
        }

        return [
                (schemaProperty.name): [
                        type: schemaProperty.type.name,
                        item: itemSchema
                ]
        ]
    }

    private static extractObjectProperty(SchemaProperty schemaProperty) {
        return [
                (schemaProperty.name): [
                        type      : schemaProperty.type.name,
                        properties: extractSchemaPropertyType(schemaProperty.type as ObjectSchemaProperty)
                ]
        ]
    }

    private static extractSchemaPropertyType(ObjectSchemaProperty objectSchemaProperty) {
        return objectSchemaProperty.properties.collectEntries {
            [
                    (it.key): [
                            type    : it.value.name,
                            required: it.value.required
                    ]
            ]
        }
    }

    private static extractPrimitiveProperty(SchemaProperty schemaProperty) {
        return [
                (schemaProperty.name): [
                        type    : schemaProperty.type.name,
                        required: schemaProperty.type.required
                ]
        ]
    }
}