package com.socialnetwork.back.service;

import com.socialnetwork.back.dto.PostDTO;
import com.socialnetwork.back.error.PostNotFoundException;
import com.socialnetwork.back.model.ImageModel;
import com.socialnetwork.back.model.Post;
import com.socialnetwork.back.model.User;
import com.socialnetwork.back.repository.ImageModelRepository;
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
public class PostService {

    public static final Logger LOG = LoggerFactory.getLogger(PostService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageModelRepository imageModelRepository;
    @Autowired
    private PostRepository postRepository;

    public Post create(PostDTO postDTO, Principal principal){
        User user = getUserByPrincipal(principal);
        Post post = new Post();
        post.setUser(user);
        post.setCaption(postDTO.getCaption());
        post.setLocation(postDTO.getLocation());
        post.setTitle(postDTO.getTitle());
        post.setLikes(0);

        LOG.info("Saving post for user {}", user.getEmail());

        return postRepository.save(post);
    }

    public List<Post> getAll(){
        return postRepository.findAllByOrderByCreatedDateDesc();
    }

    public List<Post> getAllForUser(Principal principal){
        User user = getUserByPrincipal(principal);
        return postRepository.findAllByUserOrderByCreatedDateDesc(user);
    }

    public Post like(Long postId, String username){
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found"));
        Optional<String> userLiked = post.getLikedUsers()
                .stream().filter(u -> u.equals(username)).findAny();
        if (userLiked.isPresent()){
            post.setLikes(post.getLikes()-1);
            post.getLikedUsers().remove(username);
        }
        else{
            post.setLikes(post.getLikes()+1);
            post.getLikedUsers().add(username);
        }

        return postRepository.save(post);
    }

    public void delete(Long postId, Principal principal){
        Post post =  getById(postId, principal);
        Optional<ImageModel> imageModel = imageModelRepository.findByPostId(post.getId());
        postRepository.delete(post);
        imageModel.ifPresent(imageModelRepository::delete);
    }

    public Post getById(Long postId, Principal principal){
        User user = getUserByPrincipal(principal);
        return postRepository.findByIdAndUser(postId, user)
                .orElseThrow(()-> new PostNotFoundException("cant find post for "+ user.getEmail()));
    }

    private User getUserByPrincipal(Principal principal){
        return userRepository.findByUsernameIgnoreCase(principal.getName()).orElseThrow(() -> {
            throw new UsernameNotFoundException("User "+principal.getName()+" not found");
        });
    }

}
