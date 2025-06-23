# DevQA - Developer Q&A Platform

A server-rendered web application built with Spring Boot for developers to share knowledge, ask questions, and engage in discussions.

## Features

### 🔐 Authentication & User Management
- **User Registration**: Create new accounts with username, email, and password
- **User Login**: Secure login with username/email and password
- **Password Reset**: Complete password reset flow with email-based tokens
- **Session Management**: Simple session-based authentication (no Spring Security)

### 📝 Posts & Content Management
- **Create Posts**: Write and publish technical articles and questions
- **Edit Posts**: Update existing posts (author only)
- **Delete Posts**: Remove posts (author only)
- **View Posts**: Browse all posts with author information and timestamps

### 💬 Comments System
- **Add Comments**: Comment on posts (authenticated users only)
- **Delete Comments**: Remove own comments
- **View Comments**: See all comments on posts with author information

### 🎨 Modern UI
- **Responsive Design**: Works on desktop, tablet, and mobile devices
- **Bootstrap 5**: Modern, clean interface with smooth animations
- **Font Awesome Icons**: Professional iconography throughout the app
- **Server-Rendered**: Fast loading with Thymeleaf templates

## Technology Stack

- **Backend**: Spring Boot 3.5.3
- **Database**: H2 (in-memory)
- **ORM**: Spring Data JPA
- **Templates**: Thymeleaf
- **Build Tool**: Maven
- **Java Version**: 21
- **Password Encoding**: Base64 (as requested)

## Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

## Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd devqa
```

### 2. Run the Application
```bash
mvn spring-boot:run
```

### 3. Access the Application
- **Main Application**: http://localhost:8080
- **H2 Database Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:devqa`
  - Username: `sa`
  - Password: `password`

## Sample Data

The application comes with pre-loaded sample data:

### Users
- **john_doe** / password123
- **jane_smith** / password123  
- **admin** / admin123

### Sample Posts
- "Getting Started with Spring Boot" by john_doe
- "JPA Best Practices for Database Operations" by jane_smith
- "Implementing Authentication in Spring Boot" by admin

## Application Structure

```
src/main/java/com/rednikeeg/devqa/
├── config/
│   └── DataInitializer.java          # Sample data initialization
├── controller/
│   ├── AuthController.java           # Authentication endpoints
│   └── PostController.java           # Posts and comments endpoints
├── dto/
│   ├── CommentDto.java               # Comment data transfer object
│   ├── LoginDto.java                 # Login form data
│   ├── NewPasswordDto.java           # Password reset form data
│   ├── PasswordResetDto.java         # Password reset request data
│   ├── PostDto.java                  # Post data transfer object
│   └── UserRegistrationDto.java      # Registration form data
├── entity/
│   ├── Comment.java                  # Comment entity
│   ├── Post.java                     # Post entity
│   └── User.java                     # User entity
├── repository/
│   ├── CommentRepository.java        # Comment data access
│   ├── PostRepository.java           # Post data access
│   └── UserRepository.java           # User data access
└── service/
    ├── PasswordService.java          # Password encoding/decoding
    ├── PostService.java              # Post and comment business logic
    └── UserService.java              # User and authentication logic
```

## API Endpoints

### Authentication
- `GET /` - Redirect to posts
- `GET /register` - Registration form
- `POST /register` - Create new user
- `GET /login` - Login form
- `POST /login` - Authenticate user
- `GET /logout` - Logout user
- `GET /forgot-password` - Password reset form
- `POST /forgot-password` - Request password reset
- `GET /reset-password` - New password form
- `POST /reset-password` - Set new password

### Posts
- `GET /posts` - List all posts
- `GET /posts/{id}` - View specific post
- `GET /posts/new` - Create post form
- `POST /posts` - Create new post
- `GET /posts/{id}/edit` - Edit post form
- `POST /posts/{id}/edit` - Update post
- `POST /posts/{id}/delete` - Delete post

### Comments
- `POST /posts/{postId}/comments` - Add comment to post
- `POST /posts/comments/{commentId}/delete` - Delete comment

## Password Reset Flow

1. User clicks "Forgot Password" on login page
2. User enters email address
3. System generates reset token and stores it with expiry (24 hours)
4. Token is printed to console (in production, this would be sent via email)
5. User clicks reset link with token
6. User enters new password
7. Password is updated and token is cleared

## Security Features

- **Password Encoding**: Base64 encoding (as requested)
- **Session Management**: HttpSession-based authentication
- **Input Validation**: Bean Validation annotations on DTOs
- **Authorization**: Users can only edit/delete their own content
- **CSRF Protection**: Built-in Spring Boot CSRF protection

## Database Schema

### Users Table
- `id` (Primary Key)
- `username` (Unique)
- `email` (Unique)
- `password` (Base64 encoded)
- `first_name`
- `last_name`
- `reset_token`
- `reset_token_expiry`
- `created_at`
- `updated_at`

### Posts Table
- `id` (Primary Key)
- `title`
- `content`
- `author_id` (Foreign Key to Users)
- `created_at`
- `updated_at`

### Comments Table
- `id` (Primary Key)
- `content`
- `author_id` (Foreign Key to Users)
- `post_id` (Foreign Key to Posts)
- `created_at`
- `updated_at`

## Development

### Running Tests
```bash
mvn test
```

### Building JAR
```bash
mvn clean package
```

### Running JAR
```bash
java -jar target/devqa-0.0.1-SNAPSHOT.jar
```

## Configuration

Key configuration in `application.properties`:
- H2 database setup
- JPA configuration
- Thymeleaf settings
- Server port (8080)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For questions or issues, please create an issue in the repository. 