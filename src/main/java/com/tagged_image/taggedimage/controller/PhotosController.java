package com.tagged_image.taggedimage.controller;

import com.tagged_image.taggedimage.dto.PhotoWithTags;
import com.tagged_image.taggedimage.service.MongoPhotoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/photos")
public class PhotosController {

    MongoPhotoService mongoPhotoService;

    public PhotosController(MongoPhotoService mongoPhotoService) {
        this.mongoPhotoService = mongoPhotoService;
    }

    @GetMapping("/findPhotosByTag")
    public String findPhotosByTag(Model model) {
        model.addAttribute("tags", mongoPhotoService.getExistingTags());
        return "findPhotos";
    }

    @PostMapping("/findPhotosByTag")
    public String findPhotosByTag(
            @ModelAttribute("tag") String tag,
            Model model
    ) {
        model.addAttribute("tags", mongoPhotoService.getExistingTags());
        if (tag == null || tag.isEmpty()) {
            model.addAttribute("result", "Not found");
        } else {
            model.addAttribute("tag", tag);
            try {
                List<PhotoWithTags> photos = mongoPhotoService.getPhotosByTag(tag.toLowerCase());
                if (!photos.isEmpty()) {
                    model.addAttribute("photos", photos);
                } else {
                    model.addAttribute("result", "Not found");
                }
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", e);
            }
        }
        return "findPhotos";
    }

    //Comment atm to exclude any unauthorized insertions
    //ToDo revise if it is needed at all
    /*@GetMapping("/addPhotoWithTag")
    public String addPhoto(Model model) {
        model.addAttribute("tags", mongoPhotoService.getExistingTags());
        return "addPhoto";
    }

    @PostMapping("/addPhotoWithTag")
    public String addPhoto(
            @ModelAttribute("imageUrl") String imageUrl,
            @ModelAttribute("tag") String tag,
            Model model
    ) {
        model.addAttribute("tags", mongoPhotoService.getExistingTags());
        if (imageUrl == null || imageUrl.isEmpty()) {
            model.addAttribute("result", "Image url should be set");
        } else if (tag == null || tag.isEmpty()) {
            model.addAttribute("result", "tag should be set");
        } else {
            try {
                PhotoWithTags newPhoto = new PhotoWithTags();
                newPhoto.setTags(tag.toLowerCase());
                newPhoto.setPhotoURI(imageUrl);
                mongoPhotoService.putPhotoWithTags(newPhoto);
                model.addAttribute("result", "photo added");
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", e);
            }
        }
        return "addPhoto";
    }*/
}
