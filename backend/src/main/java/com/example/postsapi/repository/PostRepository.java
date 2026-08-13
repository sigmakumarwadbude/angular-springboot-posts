package com.example.postsapi.repository;

import com.example.postsapi.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>{ 
}