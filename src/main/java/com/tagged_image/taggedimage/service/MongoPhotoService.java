package com.tagged_image.taggedimage.service;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.tagged_image.taggedimage.dto.PhotoWithTags;
import com.tagged_image.taggedimage.dto.VkIdsDTO;
import org.apache.commons.lang3.StringUtils;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MongoPhotoService {

    MongoCollection<PhotoWithTags> mongoPhotoCollection;

    public MongoPhotoService(MongoCollection<PhotoWithTags> mongoPhotoCollection) {
        this.mongoPhotoCollection = mongoPhotoCollection;
    }

    public List<PhotoWithTags> getPhotosByTag(String tag) {
        List<PhotoWithTags> photos = new ArrayList<>();
        Bson regex = Filters.regex("tags", convertToRegex(tag));
        FindIterable<PhotoWithTags> iterable = getMongoCollection().find(regex, PhotoWithTags.class);
        iterable.forEach(photos::add);
        return photos;
    }

    private String convertToRegex(String tags) {
        return "^" +
                Arrays.stream(tags.split(" ")).map(tag -> "(?=.*?" + tag + ")").collect(Collectors.joining());
    }

    public void putPhotoWithTags(PhotoWithTags photo) {
        if (photo != null &&
                StringUtils.isNotEmpty(photo.getTags())) {
            boolean isUpdated = updateTags(photo);
            if (!isUpdated) {
                getMongoCollection().insertOne(photo);
            }
        }
    }

    public List<String> getExistingTags() {
        List<String> tags = new ArrayList<>();
        getMongoCollection().distinct("tags", String.class).forEach(tags::add);
        return tags.stream()
                .map(tag -> Arrays.asList(tag.split(" ")))
                .flatMap(List::stream)
                .filter(str -> str != null && !str.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<PhotoWithTags> getPhotosById(List<String> ids) {
        List<PhotoWithTags> result = new ArrayList<>();
        getMongoCollection()
                .find(Filters.in("_id", ids.stream().map(ObjectId::new).collect(Collectors.toList())))
                .forEach(result::add);
        return result;
    }

    public List<PhotoWithTags> getPhotosByVkIds(VkIdsDTO vkIdsDTO) {
        List<PhotoWithTags> result = new ArrayList<>();
        getMongoCollection()
                .find(Filters.and(
                        Filters.in("vkId", vkIdsDTO.getVkIds()),
                        Filters.eq("albumId", vkIdsDTO.getAlbumId())))
                .forEach(result::add);
        return result;
    }

    private boolean updateTags(PhotoWithTags photo) {
        return getMongoCollection()
                .updateOne(
                        Filters.or(
                                Filters.eq("_id", photo.getId()),
                                Filters.eq("vkId", photo.getVkId()),
                                Filters.eq("photoURI", photo.getPhotoURI())
                        ),
                        Updates.set("tags", photo.getTags()))
                .getModifiedCount() > 0;
    }

    protected MongoCollection<PhotoWithTags> getMongoCollection() {
        return mongoPhotoCollection;
    }
}
