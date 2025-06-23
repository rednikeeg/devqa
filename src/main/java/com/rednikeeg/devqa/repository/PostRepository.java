package com.rednikeeg.devqa.repository;

import com.rednikeeg.devqa.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    List<Post> findAllByOrderByCreatedAtDesc();
    
    List<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId);
    
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.comments c LEFT JOIN FETCH c.author WHERE p.id = :postId")
    Post findByIdWithComments(@Param("postId") Long postId);
} 