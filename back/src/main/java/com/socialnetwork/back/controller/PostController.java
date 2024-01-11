package com.socialnetwork.back.controller;

import com.socialnetwork.back.dto.PostDTO;
import com.socialnetwork.back.facade.PostFacade;
import com.socialnetwork.back.model.Post;
import com.socialnetwork.back.model.User;
import com.socialnetwork.back.payload.response.MessageResponse;
import com.socialnetwork.back.service.PostService;
import com.socialnetwork.back.validators.ResponseErrorValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/post")
@CrossOrigin
public class PostController {

    @Autowired
    private PostFacade postFacade;

    @Autowired
    private PostService postService;

    @Autowired
    private ResponseErrorValidator responseErrorValidator;

    @PostMapping("/create")
    public ResponseEntity<Object> createPost(@Valid @RequestBody PostDTO postDTO, BindingResult bindingResult, Principal principal){

        ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);
        if (ObjectUtils.isEmpty(errors)) return errors;

        Post post = postService.create(postDTO, principal);
        PostDTO createdPost = postFacade.postToPostDTO(post);

        return new ResponseEntity<>(createdPost, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PostDTO>> getAllPosts(){
        List<PostDTO> postDTOList = postService.getAll().stream().map(postFacade::postToPostDTO).toList();
        return new ResponseEntity<>(postDTOList, HttpStatus.OK);
    }

    @GetMapping("/user/posts")
    public ResponseEntity<List<PostDTO>> getAllPostsForUser(Principal principal){
        List<PostDTO> postDTOList = postService.getAllForUser(principal).stream().map(postFacade::postToPostDTO).toList();
        return new ResponseEntity<>(postDTOList, HttpStatus.OK);
    }

    @PostMapping("/{postId}/{username}/like")
    public ResponseEntity<PostDTO> likePost(@PathVariable("postId") String postId, @PathVariable("username") String username){
        Post post = postService.like(Long.parseLong(postId), username);
        PostDTO postDTO = postFacade.postToPostDTO(post);

        return new ResponseEntity<>(postDTO, HttpStatus.OK);
    }


    @PostMapping("/{postId}/delete")
    public ResponseEntity<MessageResponse> deletePost(@PathVariable("postId") String postId, Principal principal){
        postService.delete(Long.parseLong(postId), principal);
        return new ResponseEntity<>(new MessageResponse("Post was deleted"), HttpStatus.OK);
    }

}
