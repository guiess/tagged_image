package com.tagged_image.taggedimage.service;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.tagged_image.taggedimage.dto.PhotoWithTags;
import com.tagged_image.taggedimage.dto.VkIdsDTO;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.junit.ClassRule;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest
@RunWith(SpringRunner.class)
public class MongoPhotoServiceTest {

    private final static int MONGO_PORT = 27017;

    private MongoClient mongoClient;
    private MongoCollection<PhotoWithTags> collection;

    private MongoPhotoService mongoPhotoService;

    @ClassRule
    @Container
    public static GenericContainer<?> mongo = new GenericContainer<>(DockerImageName.parse("mongo"))
            .withExposedPorts(MONGO_PORT);

    @BeforeEach
    public void setUp() {
        mongoClient = MongoClients.create("mongodb://" + mongo.getHost() + ":" + mongo.getMappedPort(MONGO_PORT));
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoDatabase database = mongoClient.getDatabase("test").withCodecRegistry(pojoCodecRegistry);
        collection = database.getCollection("testCollection", PhotoWithTags.class);
        PhotoWithTags image1 = new PhotoWithTags(
                new ObjectId("62d2216ab58f812918b757a2"),
                "1",
                10,
                "http://test.com/previewImgUrl1.jpg",
                "http://test.com/imgUrl1.jpg",
                "Btag Ztag Mtag Atag");
        PhotoWithTags image2 = new PhotoWithTags(
                new ObjectId("62d2216ab58f812918b757a4"),
                "2",
                10,
                "http://test.com/previewImgUrl2.jpg",
                "http://test.com/imgUrl2.jpg",
                "Qtag Atag Mtag");
        mongoPhotoService = new MongoPhotoService(collection);
        mongoPhotoService.putPhotoWithTags(image1);
        mongoPhotoService.putPhotoWithTags(image2);
    }

    @AfterEach
    public void afterEach() {
        collection.drop();
        mongoClient.close();
    }

    @Test
    public void givenExistingRecord_whenGetExistingTags_thenReturnListOfExistingTags() {
        String existingTags = String.join(";", mongoPhotoService.getExistingTags());
        assertEquals("Atag;Btag;Mtag;Qtag;Ztag", existingTags, "Error on getExistingTags");
    }

    @Test
    public void givenExistingRecord_whenGetPhotosByTag_thenReturnPhotos() {
        List<PhotoWithTags> result = mongoPhotoService.getPhotosByTag("Atag Mtag");
        assertEquals(result.size(), 2);

        result = mongoPhotoService.getPhotosByTag("Qtag Mtag");
        assertEquals(result.size(), 1);
    }

    @Test
    public void givenExistingRecord_whenPutExistingPhotoWithNewTags_thenUpdateTags() {
        PhotoWithTags photoWithSameId = new PhotoWithTags(
                new ObjectId("62d2216ab58f812918b757a2"),
                "0",
                0,
                "",
                "",
                "sameIdTag");
        mongoPhotoService.putPhotoWithTags(photoWithSameId);
        List<PhotoWithTags> result = mongoPhotoService.getPhotosByTag("sameIdTag");
        String existingTags = String.join(";", mongoPhotoService.getExistingTags());
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId().toString(), "62d2216ab58f812918b757a2");
        assertEquals("Atag;Mtag;Qtag;sameIdTag", existingTags);

        PhotoWithTags photoWithSameVkId = new PhotoWithTags(
                ObjectId.get(),
                "1",
                0,
                "",
                "",
                "sameVkIdTag");
        mongoPhotoService.putPhotoWithTags(photoWithSameVkId);
        result = mongoPhotoService.getPhotosByTag("sameVkIdTag");
        existingTags = String.join(";", mongoPhotoService.getExistingTags());
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getVkId(), "1");
        assertEquals("Atag;Mtag;Qtag;sameVkIdTag", existingTags);

        PhotoWithTags photoWithSameUrl = new PhotoWithTags(
                ObjectId.get(),
                "0",
                0,
                "",
                "http://test.com/imgUrl1.jpg",
                "sameUrlTag");
        mongoPhotoService.putPhotoWithTags(photoWithSameUrl);
        result = mongoPhotoService.getPhotosByTag("sameUrlTag");
        existingTags = String.join(";", mongoPhotoService.getExistingTags());
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getPhotoURI(), "http://test.com/imgUrl1.jpg");
        assertEquals("Atag;Mtag;Qtag;sameUrlTag", existingTags);
    }

    @Test
    public void givenExistingRecord_whenGetPhotosByIds_thenReturnPhotos() {
        List<PhotoWithTags> result = mongoPhotoService.getPhotosById(Collections.singletonList("62d2216ab58f812918b757a2"));
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getTags(), "Btag Ztag Mtag Atag");
    }

    @Test
    public void givenExistingRecord_whenGetPhotosByVkIds_thenReturnPhotos() {
        VkIdsDTO vkIdsDTO = new VkIdsDTO(Collections.singletonList("1"), 10);
        List<PhotoWithTags> result = mongoPhotoService.getPhotosByVkIds(vkIdsDTO);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getTags(), "Btag Ztag Mtag Atag");
    }
}
