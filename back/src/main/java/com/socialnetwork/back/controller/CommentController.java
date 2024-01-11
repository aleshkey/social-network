package com.socialnetwork.back.controller;

import com.socialnetwork.back.dto.CommentDTO;
import com.socialnetwork.back.facade.CommentFacade;
import com.socialnetwork.back.model.Comment;
import com.socialnetwork.back.payload.response.MessageResponse;
import com.socialnetwork.back.service.CommentService;
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
@RequestMapping("api/comment")
@CrossOrigin
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentFacade commentFacade;
    @Autowired
    private ResponseErrorValidator responseErrorValidator;


    @PostMapping("/{postId}/create")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDTO commentDTO,
                                                @PathVariable("postId") String postId,
                                                BindingResult bindingResult,
                                                Principal principal){
        ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);
        if (ObjectUtils.isEmpty(errors)) return errors;
        Comment comment = commentService.save(Long.parseLong(postId), commentDTO, principal);
        CommentDTO createdComment = commentFacade.commentToCommentDTO(comment);
        return  new ResponseEntity<>(createdComment, HttpStatus.OK);
    }

    @GetMapping("/{postId}/all")
    public ResponseEntity<List<CommentDTO>> getAllCommentsToPost(@PathVariable("postId") String postId){
        List<CommentDTO> commentDTOList = commentService.getAllForPost(Long.valueOf(postId)).stream().map(commentFacade::commentToCommentDTO).toList();
        return new ResponseEntity<>(commentDTOList, HttpStatus.OK);
    }

    @PostMapping("/{commentId}/delete")
    public ResponseEntity<MessageResponse> deleteComment(@PathVariable("commentId") String commentId){
        commentService.delete(Long.parseLong(commentId));
        return new ResponseEntity<>(new MessageResponse("Comment was deleted"), HttpStatus.OK);
    }

}