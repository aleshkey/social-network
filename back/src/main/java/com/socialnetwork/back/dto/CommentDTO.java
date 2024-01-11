package com.socialnetwork.back.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CommentDTO {

    private Long id;

    @NotEmpty
    private String message;

    @NotEmpty
    private String username;

}
