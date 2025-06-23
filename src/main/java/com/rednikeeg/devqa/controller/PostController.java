package com.rednikeeg.devqa.controller;

import com.rednikeeg.devqa.dto.CommentDto;
import com.rednikeeg.devqa.dto.PostDto;
import com.rednikeeg.devqa.service.PostService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    
    private final PostService postService;
    
    @GetMapping
    public String getAllPosts(Model model) {
        List<PostDto> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);
        return "posts/list";
    }
    
    @GetMapping("/{id}")
    public String getPostById(@PathVariable Long id, Model model, HttpSession session) {
        return postService.getPostById(id)
                .map(post -> {
                    model.addAttribute("post", post);
                    model.addAttribute("commentDto", new CommentDto());
                    model.addAttribute("isAuthor", isAuthor(post, session));
                    return "posts/detail";
                })
                .orElse("redirect:/posts");
    }
    
    @GetMapping("/new")
    public String showCreatePostForm(Model model, HttpSession session) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        model.addAttribute("postDto", new PostDto());
        return "posts/create";
    }
    
    @PostMapping
    public String createPost(@Valid @ModelAttribute PostDto postDto,
                            BindingResult bindingResult,
                            Model model,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        
        if (bindingResult.hasErrors()) {
            return "posts/create";
        }
        
        try {
            Long userId = (Long) session.getAttribute("userId");
            PostDto createdPost = postService.createPost(postDto, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Post created successfully!");
            return "redirect:/posts/" + createdPost.getId();
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "posts/create";
        }
    }
    
    @GetMapping("/{id}/edit")
    public String showEditPostForm(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        
        return postService.getPostById(id)
                .map(post -> {
                    if (!isAuthor(post, session)) {
                        return "redirect:/posts/" + id;
                    }
                    model.addAttribute("postDto", post);
                    return "posts/edit";
                })
                .orElse("redirect:/posts");
    }
    
    @PostMapping("/{id}/edit")
    public String updatePost(@PathVariable Long id,
                            @Valid @ModelAttribute PostDto postDto,
                            BindingResult bindingResult,
                            Model model,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        
        if (bindingResult.hasErrors()) {
            return "posts/edit";
        }
        
        try {
            Long userId = (Long) session.getAttribute("userId");
            postService.updatePost(id, postDto, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Post updated successfully!");
            return "redirect:/posts/" + id;
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "posts/edit";
        }
    }
    
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        
        try {
            Long userId = (Long) session.getAttribute("userId");
            postService.deletePost(id, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Post deleted successfully!");
            return "redirect:/posts";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/posts/" + id;
        }
    }
    
    @PostMapping("/{postId}/comments")
    public String createComment(@PathVariable Long postId,
                               @Valid @ModelAttribute CommentDto commentDto,
                               BindingResult bindingResult,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Comment content is required.");
            return "redirect:/posts/" + postId;
        }
        
        try {
            Long userId = (Long) session.getAttribute("userId");
            postService.createComment(commentDto, userId, postId);
            redirectAttributes.addFlashAttribute("successMessage", "Comment added successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/posts/" + postId;
    }
    
    @PostMapping("/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        
        try {
            Long userId = (Long) session.getAttribute("userId");
            postService.deleteComment(commentId, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Comment deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/posts";
    }
    
    private boolean isAuthor(PostDto post, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return userId != null && userId.equals(post.getAuthorId());
    }
} 