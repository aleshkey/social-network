package com.socialnetwork.back.facade;

import com.socialnetwork.back.dto.PostDTO;
import com.socialnetwork.back.model.Post;
import org.springframework.stereotype.Component;

@Component
public class PostFacade {

    public PostDTO postToPostDTO(Post post){
        PostDTO postDTO = new PostDTO();

        postDTO.setUsername(post.getUser().getUsername());
        postDTO.setTitle(post.getTitle());
        postDTO.setLikes(post.getLikes());
        postDTO.setCaption(post.getCaption());
        postDTO.setLocation(post.getLocation());
        postDTO.setUserLiked(post.getLikedUsers());
        postDTO.setId(post.getId());
        return postDTO;

    }

}
