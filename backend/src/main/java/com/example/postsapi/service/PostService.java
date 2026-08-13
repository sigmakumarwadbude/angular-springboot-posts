package com.example.postsapi.service;

import com.example.postsapi.domain.Post;
import com.example.postsapi.dto.PostRequest;
import com.example.postsapi.exception.PostNotFoundException;
import com.example.postsapi.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    public Post createPost(PostRequest request) {
        Post post = new Post(
                request.getTitle(),
                request.getBody()
        );

        return postRepository.save(post);
    }

    public Post updatePost(Long id, PostRequest request) {
        Post existingPost = getPostById(id);

        existingPost.setTitle(request.getTitle());
        existingPost.setBody(request.getBody());

        return postRepository.save(existingPost);
    }

    public void deletePost(Long id) {
        
        if(!postRepository.existsById(id)) {
            throw new PostNotFoundException(id);
        }
        postRepository.deleteById(id);
    }
}