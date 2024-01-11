package com.socialnetwork.back.repository;

import com.socialnetwork.back.model.ImageModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageModelRepository extends JpaRepository<ImageModel, Long> {
    Optional<ImageModel> findByUserId(Long userId);

    Optional<ImageModel> findByPostId(Long postId);

}
