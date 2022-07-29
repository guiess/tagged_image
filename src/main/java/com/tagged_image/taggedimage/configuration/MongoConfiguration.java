package com.tagged_image.taggedimage.configuration;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.tagged_image.taggedimage.dto.PhotoWithTags;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfiguration {

    @Bean
    CodecRegistry pojoCodecRegistry() {
        return CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    }

    @Bean
    MongoCollection<PhotoWithTags> mongoPhotoCollection(MongoTemplate mongoTemplate, CodecRegistry pojoCodecRegistry) {
        return mongoTemplate.getCollection("photos")
                .withDocumentClass(PhotoWithTags.class)
                .withCodecRegistry(pojoCodecRegistry);
    }
}
