# FILMOTOKIO

FILMOTOKIO is a full-featured web application for managing movie databases, developed in Java 17 with Spring Boot 2.7.18, offering a modern and responsive interface for managing movies, artists, reviews, and users.

## Overview

The system allows:
- Complete registration and management of movies (title, year, duration, synopsis, poster)
- Association of multiple artists (actors, directors, screenwriters, musicians) to each movie
- Movie rating system with star-based reviews and enhanced visual effects
- User management with authentication, profiles, and roles (USER, ADMIN)
- Administrative panel for managing users, movies, and data migration
- Advanced search functionality with intelligent filtering
- Premium dark theme with glass morphism design
- **H2 Database** with file-based persistence for development
- **Poster management** with working image display system
- **Java 17 compatibility** with proper Lombok configuration

## Technologies Used

### Backend
- Java 17
- Spring Boot 2.7.18
- Spring Data JPA
- Spring MVC
- Spring Security
- Spring Batch Processing
- Spring Scheduling
- Maven
- **H2 Database** (File-based for development - Default)
- **MySQL Database** (Production ready)
- **PostgreSQL** (Alternative production option)

### Frontend
- Semantic HTML5 with Thymeleaf
- **Advanced CSS3** with design system and CSS variables
- **Glass morphism effects** and modern animations
- **Responsive design** with mobile-first approach
- **Professional component-based CSS architecture
- Bootstrap 5.3.2
- Font Awesome 6.5.1
- Vanilla JavaScript

### Design Features
- Glass morphism effects with backdrop filters
- Enhanced star rating animations (left-to-right sweep effects)
- Responsive grid layouts for all screen sizes
- Interactive hover states and smooth transitions
- Premium dark theme with gradient backgrounds

## Recent Updates (2026)

### Database & Configuration Fixes
- **Java 17 Compatibility:** Fixed Lombok compatibility issues for Java 17
- **H2 Database:** Configured with file-based persistence for data retention
- **Spring Batch:** Fixed sequence creation for H2 database compatibility
- **Database-Agnostic Sequences:** NEW - Automatic detection and support for both H2 and MySQL databases
- **Multi-Database Support:** Seamless switching between H2 (development) and MySQL (production)
- **Poster Display:** Resolved poster image loading issues with correct path mapping
- **Web Configuration:** Updated static resource handling for uploads directory

### Enhanced User Experience
- **Poster System:** Working poster display for all films (home, detail, search pages)
- **Data Initialization:** Automatic sample data creation with proper poster paths
- **Home Page:** Streamlined layout with featured films section
- **Image Management:** Proper upload directory configuration and serving

### Code Quality Improvements
- **Java 17 Support:** Full compatibility with proper environment setup
- **Database Schema:** Fixed Spring Batch sequence creation for H2
- **Database-Agnostic Architecture:** Intelligent database detection and sequence handling
- **Fallback Mechanisms:** Robust error handling with multiple database syntax support
- **Static Resources:** Corrected file serving configuration
- **Error Handling:** Improved startup and runtime error resolution

## Project Structure

```
filmotokio/
├── src/main/java/filmotokio/
│   ├── controller/          # REST controllers and web endpoints
│   ├── model/              # JPA entities (Film, Person, User, Review)
│   ├── repository/         # Spring Data repositories
│   ├── service/            # Business logic services
│   ├── util/               # Utility classes (StarRatingUtil, etc.)
│   └── config/             # Security and configuration
├── src/main/resources/
│   ├── templates/          # Thymeleaf HTML templates
│   │   ├── fragments/      # Reusable template fragments
│   │   │   ├── footer.html # Enhanced footer with GitHub link
│   │   │   └── navbar.html # Navigation component
│   │   ├── admin/          # Admin panel pages
│   │   └── *.html          # Main application pages
│   └── static/
│       ├── css/            # NEW: Professional CSS architecture
│       │   ├── variables.css    # Design system and CSS variables
│       │   ├── globals.css      # Global styles and utilities
│       │   ├── legacy.css       # NEW: Organized deprecated styles
│       │   ├── auth.css         # UPDATED: Enhanced with variables
│       │   ├── navbar.css       # UPDATED: Professional documentation
│       │   ├── profile.css      # UPDATED: Enhanced with design system
│       │   ├── home.css         # Home page styles
│       │   ├── search.css       # Search functionality
│       │   ├── film.css         # Individual film pages
│       │   ├── form.css         # Enhanced form components
│       │   ├── footer.css       # Site footer
│       │   ├── error.css        # Error pages
│       │   ├── migration.css    # Data migration interface
│       │   └── README.md        # NEW: CSS architecture documentation
│       ├── js/             # JavaScript files
│       └── images/         # Static images
├── uploads/                # User uploaded content
├── pom.xml                 # Maven configuration
├── LICENSE                 # MIT License
└── README.md              # This file
```

## Main Features

### Movie Management
- Full movie registration with poster uploads
- Advanced search by title, director, or actor
- Enhanced star ratings (1-5 stars) with directional glow animations
- Comments and review system with user profiles
- Visual catalog with responsive cards and hover effects
- Film details page with comprehensive information

### Artist Management
- Registration of people (actors, directors, screenwriters, musicians)
- Multiple associations between movies and artists
- Filtering by participation type with visual indicators
- Artist profiles with filmography

### User Management
- Authentication system (login/register) with secure validation
- User profiles with photo upload and personal information
- Role-based access: USER and ADMIN
- Enhanced profile page with activity statistics
- Profile photo management with modal upload interface

### Data Migration
- **NEW:** Fixed SQL compatibility for H2 database
- **NEW:** Spring Batch job for film data export to CSV
- **NEW:** Migration status monitoring
- **NEW:** Custom row mapper for cross-database queries

### Database Features
- **H2:** In-memory database for development and testing (default)
- **MySQL:** Production-ready with full feature support
- **PostgreSQL:** Alternative production option
- **Automatic schema management** with Hibernate DDL
- **Database-agnostic queries** for cross-platform compatibility
- **Migration system** for data export and backup

## Database Configuration

### Database-Agnostic Spring Batch Sequences (NEW 2026)

The application now features **intelligent database detection** for Spring Batch sequences, automatically supporting both H2 and MySQL databases:

#### **Automatic Database Detection**
- **H2 Database:** Uses `CREATE SEQUENCE` syntax
- **MySQL Database:** Uses `CREATE TABLE ... AUTO_INCREMENT` syntax
- **Fallback Mechanism:** Graceful error handling with multiple fallback strategies

#### **How It Works**
```java
// Automatic detection and sequence creation
if (databaseType == H2) {
    CREATE SEQUENCE IF NOT EXISTS BATCH_JOB_EXECUTION_SEQ START WITH 0 INCREMENT BY 1
} else if (databaseType == MySQL) {
    CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION_SEQ (
        ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY
    )
}
```

#### **Benefits**
- **Zero Configuration:** Works automatically with both databases
- **Production Ready:** Seamlessly switch from H2 (dev) to MySQL (prod)
- **Error Resilient:** Multiple fallback mechanisms
- **Clear Logging:** Detailed database detection and creation logs

### Database Options

#### **H2 Database (Default - Development)**
- **Type:** File-based persistence
- **Configuration:** Pre-configured out of the box
- **Console:** Available at `http://localhost:8080/h2-console`
- **Data Location:** `./data/filmotokio` (project root)
- **Benefits:** Zero setup, data persistence between restarts

#### **MySQL Database (Production)**
- **Setup:** Uncomment MySQL configuration in `application.properties`
- **Requirements:** MySQL server running locally or remotely
- **Configuration:** Update username/password in properties file
- **Benefits:** Production-ready, scalable, multi-user support

#### **Database Switching**
Simply comment/uncomment the appropriate database configuration in `application.properties`:

```properties
# For H2 (Default)
spring.datasource.url=jdbc:h2:file:./data/filmotokio;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1

# For MySQL (Production)
# spring.datasource.url=jdbc:mysql://localhost:3306/filmotokio?createDatabaseIfNotExist=true
# spring.datasource.username=your_username
# spring.datasource.password=your_password
```

## Design and UX/UI

### Visual Features
- Modern, responsive interface with mobile-first approach
- Premium dark theme with advanced visual effects
- Glass morphism design with backdrop filters and gradients
- Interactive navbar with smooth transitions
- Enhanced footer with multiple sections and links
- Forms with visual validation and real-time feedback
- Cards and buttons with smooth animations and hover states

### Enhanced Elements
- Star rating system with left-to-right sweep animations
- Search interface with icon positioning and visual feedback
- Profile containers with optimized width and spacing
- Overlay effects on movie posters with perfect centering
- Responsive grids that adapt to all screen sizes

## Installation and Setup

### Prerequisites
- Java 17 (Required - project configured for Java 17)
- Maven 3.6+
- **H2 Database** (Built-in, file-based persistence - Default)
- **MySQL/PostgreSQL** (Optional for production)
- IDE (IntelliJ/Eclipse)

### Quick Start with H2 (Recommended for Development)

The project comes pre-configured with H2 Database with file-based persistence - no additional setup needed!

1. Clone the repository:
   ```bash
   git clone https://github.com/KTVDCarvalho/filmotokio.git
   cd filmotokio
   ```

2. **IMPORTANT:** Set Java 17 environment (required for this project):
   ```bash
   # The project is configured for Java 17 - using Java 25+ will cause compilation errors
   export JAVA_HOME=/Library/Java/JavaVirtualMachines/microsoft-17.jdk/Contents/Home
   
   # Verify Java version
   java -version
   # Should show: java version "17.0.18" or similar 17.x version
   ```

3. The uploads directory should already exist with sample poster images:
   ```bash
   # Verify uploads directory exists (should contain poster images)
   ls -la uploads/
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

5. Access in the browser:
   ```
   http://localhost:8080
   ```

6. **H2 Console (Development):**
   ```
   http://localhost:8080/h2-console
   ```
   - JDBC URL: `jdbc:h2:file:./data/filmotokio;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1`
   - Username: `sa`
   - Password: (leave blank)

### Default Login Credentials
- Username: `tokioschool`
- Password: `Tokioschool`
- Role: `ADMIN`

### Sample Data
The application automatically creates sample films with working posters:
- Interstellar (2014) - Poster: `/uploads/Interstellar.jpg`
- Dark (2017) - Poster: `/uploads/Dark.jpg`
- From (2022) - Poster: `/uploads/From.jpg`
- The Shawshank Redemption (1994) - Poster: `/uploads/The Shawshank Redemption.jpg`

### Production Setup (MySQL/PostgreSQL)

For production deployment, you can switch to MySQL or PostgreSQL:

1. **MySQL Database Setup:**
   ```properties
   # In application.properties, comment H2 config and uncomment:
   spring.datasource.url=jdbc:mysql://localhost:3306/filmotokio?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
   spring.datasource.username=your_mysql_user
   spring.datasource.password=your_mysql_password
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
   ```

2. **PostgreSQL Database Setup:**
   ```properties
   # In application.properties, comment H2 config and uncomment:
   spring.datasource.url=jdbc:postgresql://localhost:5432/filmotokio
   spring.datasource.username=your_postgres_user
   spring.datasource.password=your_postgres_password
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
   ```

### Database Features
- **H2:** File-based persistence (data survives restarts)
- **Automatic schema management** with Hibernate DDL
- **Database-agnostic queries** for cross-platform compatibility
- **Migration system** for data export and backup

## Troubleshooting

### Common Issues and Solutions

#### **Java Version Issues**
```
Error: Fatal error compiling: java.lang.ExceptionInInitializerError
```
**Solution:** The project requires Java 17. Using Java 25+ will cause compilation errors.
```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/microsoft-17.jdk/Contents/Home
java -version  # Should show 17.x.x
```

#### **Port 8080 Already in Use**
```
Error: Port 8080 was already in use
```
**Solution:** Kill the process using port 8080
```bash
lsof -ti:8080 | xargs kill -9
```

#### **Spring Batch Sequence Errors**
```
Error: Sequence "BATCH_JOB_SEQ" not found
```
**Solution:** The application now handles this automatically with database-agnostic sequences. If issues persist, restart the application.

#### **MySQL Connection Errors**
```
Error: Access denied for user 'filomotokio'@'localhost'
```
**Solution:** Ensure MySQL database and user are properly configured:
```sql
CREATE DATABASE filmotokio;
CREATE USER 'filomotokio'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON filmotokio.* TO 'filomotokio'@'localhost';
```

#### **Poster Images Not Displaying**
**Solution:** Ensure uploads directory exists with poster images:
```bash
ls -la uploads/
# Should contain: Interstellar.jpg, Dark.jpg, From.jpg, The Shawshank Redemption.jpg
```

### Getting Help
If you encounter issues:
1. Check the application logs for detailed error messages
2. Verify Java 17 is being used: `java -version`
3. Ensure database configuration is correct in `application.properties`
4. Check that port 8080 is available

## Security

- Authentication with Spring Security
- Passwords encrypted with BCrypt
- Role-based access control (USER/ADMIN)
- CSRF protection and input validation
- Secure file uploads with type validation
- Session management with timeout controls

## Data Model

### Core Entities
- Film: id, title, year, duration, synopsis, poster_url
- Person: id, name, surname, type (ACTOR, DIRECTOR, etc.)
- User: id, name, surname, username, email, password, role, image
- Review: id, rating, comment, user_id, film_id
- Role: id, name (USER, ADMIN)

### Key Relationships
- Film ↔ Person: Many-to-many (through film_person table)
- Film ↔ Review: One-to-many
- User ↔ Review: One-to-many
- User ↔ Role: Many-to-one

## APIs and Endpoints

### Public Endpoints
- GET / - Home page with featured films
- GET /films - Movie list with pagination
- GET /films/{id} - Movie details page
- POST /films - Create new movie (ADMIN)
- GET /search - Movie search interface
- POST /login - User authentication
- POST /register - User registration
- GET /profile - User profile page

### Admin Endpoints
- GET /admin - Admin dashboard
- POST /admin/register - Register new users
- GET /admin/migration - Data migration interface
- POST /profile/upload-photo - Profile photo upload

## Highlights

### Premium Interface
- Glass morphism effects with backdrop filters
- Enhanced star rating system with directional animations
- Responsive design that works on all devices
- Smooth transitions and micro-interactions

### Performance
- Optimized database queries with JPA
- Efficient image handling with proper caching
- Minified CSS/JS for faster loading
- Lazy loading for large datasets

### Technical Excellence
- Clean, well-structured code with proper documentation
- Comprehensive error handling and validation
- Unit tests for critical business logic
- Debug endpoints for development support

## Future Improvements

### Short Term
- REST API for mobile applications
- Movie recommendation system based on user preferences
- Integration with external APIs (TMDB, IMDb)
- Enhanced image upload with compression and optimization

### Long Term
- Notification system for new releases and reviews
- Real-time chat for film discussions
- Dockerization and container deployment
- CI/CD pipeline with automated testing
- Mobile applications (React Native/Flutter)

## Author

Kiniame Tarquinio Vieira Dias de Carvalho

- GitHub: https://github.com/KTVDCarvalho
- Email: kiniame.carvalho@icloud.com

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

## Support

If you have any questions or need support, please:
- Open an issue on GitHub
- Contact the author directly
- Check the documentation

---

FILMOTOKIO - Your Complete Movie Management Solution
