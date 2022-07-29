package com.tagged_image.taggedimage.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "photos")
public class PhotoWithTags {
    @Id
    @BsonId
    @BsonProperty("_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String vkId;
    private int albumId;
    private String previewPhotoURI;
    private String photoURI;
    private String tags;

    public boolean isValid() {
        return StringUtils.isNotEmpty(photoURI) && StringUtils.isNotEmpty(vkId);
    }
}
