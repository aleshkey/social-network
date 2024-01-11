package com.socialnetwork.back.repository;

import com.socialnetwork.back.model.Post;
import com.socialnetwork.back.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUserOrderByCreatedDateDesc(User user);

    List<Post> findAllByOrderByCreatedDateDesc();

    Optional<Post> findByIdAndUser(Long id, User user);


}
