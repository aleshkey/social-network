package com.socialnetwork.back.service;

import com.socialnetwork.back.error.ImageNotFoundException;
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
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class ImageUploadService {
    public static final Logger LOG = LoggerFactory.getLogger(ImageUploadService.class);

    @Autowired
    private ImageModelRepository imageModelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;


    public ImageModel uploadToUser(MultipartFile file, Principal principal) throws IOException{
        User user = getUserByPrincipal(principal);
        LOG.info("upload image to user {}", user.getUsername());

        ImageModel imageUserProfile = imageModelRepository.findByUserId(user.getId()).orElse(null);
        if (!ObjectUtils.isEmpty(imageUserProfile)) imageModelRepository.delete(imageUserProfile);
        ImageModel image = new ImageModel();
        image.setUserId(user.getId());
        image.setImageBytes(compressBytes(file.getBytes()));
        image.setName(file.getOriginalFilename());
        return imageModelRepository.save(image);
    }


    public ImageModel uploadToPost(MultipartFile file, Principal principal, Long postId) throws IOException{
        User user = getUserByPrincipal(principal);
        Post post = user.getPosts().stream().filter(p -> p.getId().equals(postId)).collect(toSinglePostCollector());

        ImageModel image = new ImageModel();
        image.setPostId(post.getId());
        image.setImageBytes(compressBytes(file.getBytes()));
        image.setName(file.getOriginalFilename());
        LOG.info("upload file for post {}", post.getId() );
        return imageModelRepository.save(image);
    }

    public ImageModel getToUser(Principal principal){
        User user = getUserByPrincipal(principal);
        ImageModel imageModel = imageModelRepository.findByUserId(user.getId()).orElse(null);
        if (!ObjectUtils.isEmpty(imageModel)){
            imageModel.setImageBytes(decompressBytes(imageModel.getImageBytes()));
        }
        return imageModel;
    }

    public ImageModel getToPost(Long postId){
        ImageModel imageModel = imageModelRepository.findByPostId(postId).orElseThrow(() -> new ImageNotFoundException("Cant find image to post "+ postId));
        if (!ObjectUtils.isEmpty(imageModel)){
            imageModel.setImageBytes(decompressBytes(imageModel.getImageBytes()));
        }
        return imageModel;
    }

    private byte[] compressBytes(byte[] data){
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()){
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LOG.info("Compressed image byte size - {}", outputStream.toByteArray().length);
        return outputStream.toByteArray();
    }

    private byte[] decompressBytes(byte[] data){
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try{
            while (!inflater.finished()){
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException | DataFormatException e) {
            LOG.error("Cant decompress Bytes");
        }
        LOG.info("Decompressed image byte size - {}", outputStream.toByteArray().length);
        return outputStream.toByteArray();
    }

    private User getUserByPrincipal(Principal principal){
        return userRepository.findByUsernameIgnoreCase(principal.getName()).orElseThrow(() -> {
            throw new UsernameNotFoundException("User "+principal.getName()+" not found");
        });
    }

    private <T> Collector<T, ?, T> toSinglePostCollector(){
        return Collectors.collectingAndThen(
          Collectors.toList(),
            list -> {
                if (list.size() != 1){
                    throw new IllegalStateException();
                }
                return list.get(0);
            }
        );
    }

}
