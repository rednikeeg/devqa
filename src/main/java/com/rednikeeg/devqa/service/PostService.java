package com.rednikeeg.devqa.service;

import com.rednikeeg.devqa.dto.CommentDto;
import com.rednikeeg.devqa.dto.PostDto;
import com.rednikeeg.devqa.entity.Comment;
import com.rednikeeg.devqa.entity.Post;
import com.rednikeeg.devqa.entity.User;
import com.rednikeeg.devqa.repository.CommentRepository;
import com.rednikeeg.devqa.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    
    public List<PostDto> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Optional<PostDto> getPostById(Long id) {
        return postRepository.findById(id)
                .map(this::convertToDto);
    }
    
    public PostDto createPost(PostDto postDto, Long authorId) {
        Optional<User> userOpt = userService.findById(authorId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        Post post = Post.builder()
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .author(userOpt.get())
                .build();
        
        Post savedPost = postRepository.save(post);
        return convertToDto(savedPost);
    }
    
    public PostDto updatePost(Long postId, PostDto postDto, Long authorId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            throw new RuntimeException("Post not found");
        }
        
        Post post = postOpt.get();
        if (!post.getAuthor().getId().equals(authorId)) {
            throw new RuntimeException("You can only edit your own posts");
        }
        
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        
        Post updatedPost = postRepository.save(post);
        return convertToDto(updatedPost);
    }
    
    public void deletePost(Long postId, Long authorId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            throw new RuntimeException("Post not found");
        }
        
        Post post = postOpt.get();
        if (!post.getAuthor().getId().equals(authorId)) {
            throw new RuntimeException("You can only delete your own posts");
        }
        
        postRepository.delete(post);
    }
    
    public List<CommentDto> getCommentsForPost(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(this::convertCommentToDto)
                .collect(Collectors.toList());
    }
    
    public CommentDto createComment(CommentDto commentDto, Long authorId, Long postId) {
        Optional<User> userOpt = userService.findById(authorId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            throw new RuntimeException("Post not found");
        }
        
        Comment comment = Comment.builder()
                .content(commentDto.getContent())
                .author(userOpt.get())
                .post(postOpt.get())
                .build();
        
        Comment savedComment = commentRepository.save(comment);
        return convertCommentToDto(savedComment);
    }
    
    public void deleteComment(Long commentId, Long authorId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isEmpty()) {
            throw new RuntimeException("Comment not found");
        }
        
        Comment comment = commentOpt.get();
        if (!comment.getAuthor().getId().equals(authorId)) {
            throw new RuntimeException("You can only delete your own comments");
        }
        
        commentRepository.delete(comment);
    }
    
    private PostDto convertToDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAuthorName(post.getAuthor().getUsername());
        dto.setAuthorId(post.getAuthor().getId());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        
        // Convert comments with null check
        List<CommentDto> commentDtos = null;
        if (post.getComments() != null) {
            commentDtos = post.getComments().stream()
                    .map(this::convertCommentToDto)
                    .collect(Collectors.toList());
        }
        dto.setComments(commentDtos);
        
        return dto;
    }
    
    private CommentDto convertCommentToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setAuthorName(comment.getAuthor().getUsername());
        dto.setAuthorId(comment.getAuthor().getId());
        dto.setPostId(comment.getPost().getId());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        return dto;
    }
} 