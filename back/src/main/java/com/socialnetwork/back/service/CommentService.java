package com.socialnetwork.back.service;

import com.socialnetwork.back.dto.CommentDTO;
import com.socialnetwork.back.error.PostNotFoundException;
import com.socialnetwork.back.model.Comment;
import com.socialnetwork.back.model.Post;
import com.socialnetwork.back.model.User;
import com.socialnetwork.back.repository.CommentRepository;
import com.socialnetwork.back.repository.PostRepository;
import com.socialnetwork.back.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    public static final Logger LOG = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public Comment save(Long postId, CommentDTO commentDTO, Principal principal){
        User user = getUserByPrincipal(principal);
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Cant find post username: "+user.getEmail()));
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUserId(user.getId());
        comment.setUsername(user.getUsername());
        comment.setMessage(commentDTO.getMessage());
        LOG.info("Save comment for post {}", post.getId());
        return commentRepository.save(comment);
    }

    public List<Comment> getAllForPost(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Cant find post"));
        return commentRepository.findAllByPost(post);
    }

    public void delete(Long commentId){
        Optional<Comment> comment = commentRepository.findById(commentId);
        comment.ifPresent(commentRepository::delete);
    }

    private User getUserByPrincipal(Principal principal){
        return userRepository.findByUsernameIgnoreCase(principal.getName()).orElseThrow(() -> {
            throw new UsernameNotFoundException("User "+principal.getName()+" not found");
        });
    }

}
