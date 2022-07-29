package com.tagged_image.taggedimage;

import com.mongodb.client.MongoCollection;
import com.tagged_image.taggedimage.dto.PhotoWithTags;
import com.tagged_image.taggedimage.service.MongoPhotoService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = TaggedImageApplicationTests.class
)
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = {com.tagged_image.taggedimage.TaggedImageApplicationTests.Initializer.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TaggedImageApplicationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MongoPhotoService mongoPhotoService;

    @Autowired
    MongoCollection<PhotoWithTags> mongoPhotoCollection;

    private final static int MONGO_PORT = 27017;

    @Container
    public static GenericContainer<?> mongo = new GenericContainer<>(DockerImageName.parse("mongo"))
            .withExposedPorts(MONGO_PORT);

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.data.mongodb.uri=mongodb://"+ mongo.getHost() + ":" + MONGO_PORT + "/vkmedia"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @BeforeAll
    public void setUp() {
        PhotoWithTags image1 = new PhotoWithTags(
                ObjectId.get(),
                "1",
                10,
                "http://test.com/previewImgUrl1.jpg",
                "http://test.com/imgUrl1.jpg",
                "Btag Ztag Mtag Atag");
        PhotoWithTags image2 = new PhotoWithTags(
                ObjectId.get(),
                "2",
                10,
                "http://test.com/previewImgUrl2.jpg",
                "http://test.com/imgUrl2.jpg",
                "Qtag Atag Mtag");
        mongoPhotoService.putPhotoWithTags(image1);
        mongoPhotoService.putPhotoWithTags(image2);
    }

    @Test
    public void givenImagesWithDifferentTags_WhenCallGetExistingTagsRest_ThenGetExistingTags() throws Exception {
        mockMvc.perform(get("/photos/getExistingTagsRest").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals("[\"Atag\",\"Btag\",\"Mtag\",\"Qtag\",\"Ztag\"]", result.getResponse().getContentAsString()));
    }

    @Test
    public void givenImagesWithDifferentTags_WhenCallGetPhotosByTagRest_ThenGetPhoto() throws Exception {
        mockMvc.perform(get("/photos/getPhotosByTagRest")
                .param("tags", "Btag")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].vkId", is("1")));
    }

    @AfterAll
    public void cleanUp() {
        mongoPhotoCollection.drop();
    }
}
