package com.socialnetwork.back.controller;

import com.socialnetwork.back.model.ImageModel;
import com.socialnetwork.back.payload.response.MessageResponse;
import com.socialnetwork.back.repository.PostRepository;
import com.socialnetwork.back.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("api/image")
@CrossOrigin
public class ImageController {

    @Autowired
    private ImageUploadService imageUploadService;
    @Autowired
    private PostRepository postRepository;

    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadImageToUser(@RequestParam("file")MultipartFile multipartFile, Principal principal) throws IOException {
        imageUploadService.uploadToUser(multipartFile, principal);
        return new ResponseEntity<>(new MessageResponse("Image uploaded successfully"), HttpStatus.OK);
    }

    @PostMapping("/{postId}/upload")
    public ResponseEntity<MessageResponse> uploadImageToPost(@PathVariable("postId") String postId,
                                                             @RequestParam("file") MultipartFile file,
                                                             Principal principal) throws IOException{
        imageUploadService.uploadToPost(file, principal, Long.parseLong(postId));
        return new ResponseEntity<>(new MessageResponse("Image uploaded successfully"), HttpStatus.OK);
    }

    @GetMapping("/profileImage")
    public ResponseEntity<ImageModel> getImageForUser(Principal principal){
        ImageModel userImage = imageUploadService.getToUser(principal);
        return new ResponseEntity<>(userImage, HttpStatus.OK);
    }

    @GetMapping("/{postId}/image")
    public ResponseEntity<ImageModel> getImageForPost(@PathVariable("postId") String postId){
        ImageModel postImage = imageUploadService.getToPost(Long.parseLong(postId));
        return new ResponseEntity<>(postImage, HttpStatus.OK);
    }
}