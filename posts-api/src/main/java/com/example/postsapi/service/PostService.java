package com.example.postsapi.service;

import com.example.postsapi.domain.Post;
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
                .orElseThrow(() -> new RuntimeException("Post not found: " + id));
    }

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public Post updatePost(Long id, Post post) {
        Post existingPost = getPostById(id);

        existingPost.setTitle(post.getTitle());
        existingPost.setBody(post.getBody());

        return postRepository.save(existingPost);
    }

    public void deletePost(Long id) {
        Post existingPost = getPostById(id);
        postRepository.delete(existingPost);
    }
}