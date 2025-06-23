package com.rednikeeg.devqa.config;

import com.rednikeeg.devqa.entity.Comment;
import com.rednikeeg.devqa.entity.Post;
import com.rednikeeg.devqa.entity.User;
import com.rednikeeg.devqa.repository.CommentRepository;
import com.rednikeeg.devqa.repository.PostRepository;
import com.rednikeeg.devqa.repository.UserRepository;
import com.rednikeeg.devqa.service.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PasswordService passwordService;
    
    @Override
    public void run(String... args) throws Exception {
        // Only initialize if no users exist
        if (userRepository.count() == 0) {
            log.info("Initializing sample data...");
            initializeSampleData();
            log.info("Sample data initialization completed!");
        }
    }
    
    private void initializeSampleData() {
        // Create sample users
        User john = User.builder()
                .username("john_doe")
                .email("john@example.com")
                .password(passwordService.encodePassword("password123"))
                .firstName("John")
                .lastName("Doe")
                .createdAt(LocalDateTime.now().minusDays(5))
                .updatedAt(LocalDateTime.now().minusDays(5))
                .build();
        userRepository.save(john);
        
        User jane = User.builder()
                .username("jane_smith")
                .email("jane@example.com")
                .password(passwordService.encodePassword("password123"))
                .firstName("Jane")
                .lastName("Smith")
                .createdAt(LocalDateTime.now().minusDays(3))
                .updatedAt(LocalDateTime.now().minusDays(3))
                .build();
        userRepository.save(jane);
        
        User admin = User.builder()
                .username("admin")
                .email("admin@devqa.com")
                .password(passwordService.encodePassword("admin123"))
                .firstName("Admin")
                .lastName("User")
                .createdAt(LocalDateTime.now().minusDays(10))
                .updatedAt(LocalDateTime.now().minusDays(10))
                .build();
        userRepository.save(admin);
        
        // Create sample posts
        Post springBootPost = Post.builder()
                .title("Getting Started with Spring Boot")
                .content("Spring Boot is a powerful framework for building Java applications. " +
                        "It provides auto-configuration, embedded servers, and production-ready features. " +
                        "In this post, I'll show you how to create your first Spring Boot application.\n\n" +
                        "## Prerequisites\n" +
                        "- Java 17 or higher\n" +
                        "- Maven or Gradle\n" +
                        "- Your favorite IDE\n\n" +
                        "## Step 1: Create a New Project\n" +
                        "You can use Spring Initializr to bootstrap your project:\n" +
                        "1. Go to https://start.spring.io/\n" +
                        "2. Choose your project settings\n" +
                        "3. Download and extract the project\n\n" +
                        "## Step 2: Run the Application\n" +
                        "```bash\n" +
                        "mvn spring-boot:run\n" +
                        "```\n\n" +
                        "That's it! Your Spring Boot application is now running.")
                .author(john)
                .createdAt(LocalDateTime.now().minusDays(4))
                .updatedAt(LocalDateTime.now().minusDays(4))
                .build();
        postRepository.save(springBootPost);
        
        Post jpaPost = Post.builder()
                .title("JPA Best Practices for Database Operations")
                .content("JPA (Java Persistence API) is essential for database operations in Java applications. " +
                        "Here are some best practices I've learned over the years:\n\n" +
                        "## 1. Use Proper Entity Relationships\n" +
                        "Always define clear relationships between entities using annotations like @OneToMany, @ManyToOne, etc.\n\n" +
                        "## 2. Optimize Queries\n" +
                        "Use @Query annotations for complex queries and consider using @EntityGraph for eager loading.\n\n" +
                        "## 3. Handle Transactions Properly\n" +
                        "Use @Transactional annotation appropriately and understand propagation levels.\n\n" +
                        "## 4. Use DTOs for Data Transfer\n" +
                        "Don't expose entities directly to the presentation layer. Use DTOs instead.\n\n" +
                        "These practices will help you build more maintainable and performant applications.")
                .author(jane)
                .createdAt(LocalDateTime.now().minusDays(2))
                .updatedAt(LocalDateTime.now().minusDays(2))
                .build();
        postRepository.save(jpaPost);
        
        Post securityPost = Post.builder()
                .title("Implementing Authentication in Spring Boot")
                .content("Security is crucial for any web application. In this post, I'll show you how to implement " +
                        "authentication in Spring Boot applications.\n\n" +
                        "## Authentication Options\n" +
                        "1. **Form-based authentication** - Traditional username/password forms\n" +
                        "2. **JWT tokens** - Stateless authentication\n" +
                        "3. **OAuth2** - Third-party authentication\n\n" +
                        "## Basic Implementation\n" +
                        "For a simple implementation, you can use Spring Security with form-based authentication:\n\n" +
                        "```java\n" +
                        "@Configuration\n" +
                        "@EnableWebSecurity\n" +
                        "public class SecurityConfig {\n" +
                        "    // Configuration here\n" +
                        "}\n" +
                        "```\n\n" +
                        "This provides a solid foundation for securing your application.")
                .author(admin)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
        postRepository.save(securityPost);
        
        // Create sample comments
        Comment comment1 = Comment.builder()
                .content("Great post! I especially liked the step-by-step approach. " +
                        "This helped me get my first Spring Boot app running in no time.")
                .author(jane)
                .post(springBootPost)
                .createdAt(LocalDateTime.now().minusDays(3))
                .updatedAt(LocalDateTime.now().minusDays(3))
                .build();
        commentRepository.save(comment1);
        
        Comment comment2 = Comment.builder()
                .content("Thanks for sharing these best practices. " +
                        "I've been struggling with JPA performance issues, and these tips are really helpful.")
                .author(john)
                .post(jpaPost)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
        commentRepository.save(comment2);
        
        Comment comment3 = Comment.builder()
                .content("Excellent overview of authentication options. " +
                        "Would you consider writing a follow-up post about JWT implementation?")
                .author(jane)
                .post(securityPost)
                .createdAt(LocalDateTime.now().minusHours(12))
                .updatedAt(LocalDateTime.now().minusHours(12))
                .build();
        commentRepository.save(comment3);
        
        Comment comment4 = Comment.builder()
                .content("I agree with the DTO approach. " +
                        "It really helps keep the layers separated and makes the code more maintainable.")
                .author(admin)
                .post(jpaPost)
                .createdAt(LocalDateTime.now().minusHours(6))
                .updatedAt(LocalDateTime.now().minusHours(6))
                .build();
        commentRepository.save(comment4);
    }
} 