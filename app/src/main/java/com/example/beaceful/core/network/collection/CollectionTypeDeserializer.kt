package com.example.beaceful.core.network.collection

import com.example.beaceful.domain.model.CollectionType
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class CollectionTypeDeserializer : JsonDeserializer<CollectionType> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): CollectionType {
        val typeString = json?.asString?.uppercase() ?: return CollectionType.OTHER
        return try {
            CollectionType.valueOf(typeString)
        } catch (e: IllegalArgumentException) {
            CollectionType.OTHER
        }
    }
}