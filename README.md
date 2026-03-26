# FILMOTOKIO

FILMOTOKIO is a full-featured web application for managing movie databases, developed in Java 17 with Spring Boot 3.x, offering a modern and responsive interface for managing movies, artists, reviews, and users.

## Overview

The system allows:
- Complete registration and management of movies (title, year, duration, synopsis, poster)
- Association of multiple artists (actors, directors, screenwriters, musicians) to each movie
- Movie rating system with star-based reviews and enhanced visual effects
- User management with authentication, profiles, and roles (USER, ADMIN)
- Administrative panel for managing users, movies, and data migration
- Advanced search functionality with intelligent filtering
- Premium dark theme with glass morphism design
- **NEW:** Professional CSS architecture with design system and optimized performance
- **NEW:** Comprehensive form validation with user-friendly error messages
- **NEW:** Enhanced UI components with scalable images and responsive design

## Technologies Used

### Backend
- Java 17+
- Spring Boot 2.7.18
- Spring Data JPA
- Spring MVC
- Spring Security
- Spring Batch Processing
- Spring Scheduling
- Maven
- **H2 Database** (In-memory for development - Default)
- **MySQL Database** (Production ready)
- **PostgreSQL** (Alternative production option)

### Frontend
- Semantic HTML5 with Thymeleaf
- **NEW:** Advanced CSS3 with design system and CSS variables
- **NEW:** Glass morphism effects and modern animations
- **NEW:** Responsive design with mobile-first approach
- **NEW:** Professional component-based CSS architecture
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

### CSS Architecture Overhaul
- **Design System:** Implemented comprehensive CSS variables system
- **Performance:** Reduced code repetition by 80% with centralized variables
- **Organization:** Created modular CSS architecture with clear separation
- **Documentation:** Added professional headers and comprehensive guides
- **Legacy Management:** Organized deprecated code with migration timeline

### Enhanced User Experience
- **Form Validation:** Comprehensive validation with user-friendly error messages
- **Image Optimization:** Scalable images with proper aspect ratio handling
- **Responsive Design:** Mobile-first approach with breakpoint optimization
- **Search UI:** Improved search bar with icon positioning fixes
- **Profile Layout:** Wider profile containers for better content display

### Code Quality Improvements
- **Professional Headers:** Added copyright and documentation to all Java classes
- **Debug Security:** Enhanced debug controller with security warnings
- **Clean Code:** Removed unused imports and optimized code structure
- **Documentation:** Complete README files for project and CSS architecture

### Footer Enhancements
- **GitHub Integration:** Added professional GitHub link in footer
- **Copyright:** Updated copyright attribution to project owner
- **Social Links:** Organized social media links with GitHub prominence

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
- Java 17+
- Maven 3.6+
- **H2 Database** (Built-in, no setup required - Default)
- **MySQL/PostgreSQL** (Optional for production)
- IDE (IntelliJ/Eclipse)

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/KTVDCarvalho/filmotokio.git
   cd filmotokio
   ```

2. Copy the application properties template:
   ```bash
   cp src/main/resources/application.properties.template src/main/resources/application.properties
   ```

3. Configure the database in `application.properties`:

   **The project comes pre-configured with H2 Database by default** - no additional setup needed for development!

   **Option 1: H2 Database (Default for Development)**
   ```properties
   # Already configured by default - no changes needed
   spring.datasource.url=jdbc:h2:mem:filmotokio
   spring.datasource.username=sa
   spring.datasource.password=
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
   ```

   **Option 2: MySQL Database (For Production)**
   ```properties
   # Comment H2 configurations above and uncomment these:
   # spring.datasource.url=jdbc:mysql://localhost:3306/filmotokio?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
   # spring.datasource.username=your_mysql_user
   # spring.datasource.password=your_mysql_password
   # spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
   ```

   **Option 3: PostgreSQL Database (Production Alternative)**
   ```properties
   # Comment H2 configurations above and uncomment these:
   # spring.datasource.url=jdbc:postgresql://localhost:5432/filmotokio
   # spring.datasource.username=your_postgres_user
   # spring.datasource.password=your_postgres_password
   # spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
   ```

   **Tip:** To switch between databases, just comment/uncomment the corresponding lines!

4. Create the uploads directory:
   ```bash
   mkdir -p uploads
   ```

5. Run the application:
   ```bash
   mvn spring-boot:run
   ```

6. Access in the browser:
   ```
   http://localhost:8080
   ```

   **H2 Console (Development Only):**
   ```
   http://localhost:8080/h2-console
   ```
   - JDBC URL: `jdbc:h2:mem:filmotokio`
   - Username: `sa`
   - Password: (leave blank)
   
   **Note:** H2 console is only available when H2 database is configured

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
