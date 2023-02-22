package com.tagged_image.taggedimage.controller;

import com.tagged_image.taggedimage.dto.PhotoWithTags;
import com.tagged_image.taggedimage.dto.VkIdsDTO;
import com.tagged_image.taggedimage.service.MongoPhotoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/photos")
public class PhotoRestController {

    @Value("${photo.api.write.token}")
    private String writeToken;

    MongoPhotoService mongoPhotoService;

    @Value("${test.spring.data.mongodb.uri}")
    private String mongoUri;

    PhotoRestController(MongoPhotoService mongoPhotoService) {
        this.mongoPhotoService = mongoPhotoService;
    }

    @GetMapping("/test")
    public String test() {
        return mongoUri;
    }

    @PutMapping("/putPhotoWithTagRest")
    public String putPhotoTags(@RequestBody PhotoWithTags photo, String token) {
        if (StringUtils.isEmpty(token) || !writeToken.equals(token)) {
            return "Fail: Token is invalid";
        }
        if (photo == null || !photo.isValid()) {
            return "Fail: Photo is not valid";
        }
        try {
            mongoPhotoService.putPhotoWithTags(photo);
        } catch (Exception e) {
            return "Fail: " + e.getMessage();
        }
        return "Success";
    }

    @GetMapping("/getPhotosByTagRest")
    public List<PhotoWithTags> getPhotoByTag(String tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        return mongoPhotoService.getPhotosByTag(tags);
    }

    @GetMapping("/getExistingTagsRest")
    public List<String> getExistingTagsRest() {
        return mongoPhotoService.getExistingTags();
    }

    @GetMapping("/getPhotosByIdsRest")
    public List<PhotoWithTags> getPhotosByIds(@RequestBody List<String> ids) {
        return mongoPhotoService.getPhotosById(ids);
    }

    @PostMapping("/getPhotosByAlbumAndVkIdsRest")
    public List<PhotoWithTags> getPhotosByAlbumAndVkIds(@RequestBody VkIdsDTO vkIdsDTO) {
        return mongoPhotoService.getPhotosByVkIds(vkIdsDTO);
    }
}
