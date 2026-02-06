# Recipe API - Backend

A RESTful API for recipe management with JWT authentication and Spoonacular integration, built with Spring Boot.

## Features

* **User Authentication**: JWT-based registration and login with secure password hashing
* **Personal Recipe Management**: Full CRUD operations for user-created recipes
* **Spoonacular Integration**: Search external recipes and save to personal collection
* **Automatic Categorisation**: Maps Spoonacular dish types to recipe categories (Breakfast, Lunch, Dinner, Dessert)
* **Source Tracking**: Distinguishes between user-created and Spoonacular recipes
* **Pagination Support**: Efficient data retrieval with Spring Data pageable
* **Layered Architecture**: Clean separation (Controller → Service → Repository)
* **Comprehensive Testing**: 94+ passing tests across all layers
* **Professional Logging**: Structured logging 
## Tech Stack

* Java 21
* Spring Boot 3.x
* Spring Security (JWT)
* Spring Data JPA
* PostgreSQL (production)
* H2 (development/testing)
* Maven
* JUnit 5 & Mockito
* Spoonacular API

## API Endpoints

### Authentication

**Register New User**
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john",
  "email": "john@example.com",
  "password": "securepassword"
}

Response: { "token": "jwt-token", "username": "john" }
```

**Login**
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john",
  "password": "securepassword"
}

Response: { "token": "jwt-token", "username": "john" }
```

### Recipes (Protected - Requires JWT)

**All recipe endpoints require authentication header:**
```
Authorization: Bearer {jwt-token}
```

**Get All Recipes (Paginated)**
```http
GET /recipes?page=0&size=20&sort=id,desc
```
Returns paginated recipes, sorted by newest first.

**Get Single Recipe**
```http
GET /recipes/{id}
```

**Create Recipe**
```http
POST /recipes
Content-Type: application/json

{
  "name": "Spaghetti Carbonara",
  "ingredientsText": "400g spaghetti, 200g bacon, 4 eggs, 100g parmesan",
  "instructions": "1. Cook pasta\n2. Fry bacon\n3. Mix with eggs and cheese",
  "prepTimeMinutes": 30,
  "servings": 4,
  "category": "DINNER"
}
```

**Update Recipe**
```http
PUT /recipes/{id}
Content-Type: application/json

{
  "name": "Updated Recipe Name",
  "ingredientsText": "Updated ingredients",
  "instructions": "Updated instructions",
  "prepTimeMinutes": 25,
  "servings": 4,
  "category": "LUNCH"
}
```

**Delete Recipe**
```http
DELETE /recipes/{id}
```

### Spoonacular Integration

**Search Recipes**
```http
GET /recipes/search/spoonacular?query=pasta&number=10
```
Search Spoonacular API for recipes matching the query.

**Save Spoonacular Recipe**
```http
POST /recipes/spoonacular/{spoonacularId}
```
Save a Spoonacular recipe to user's personal collection. Automatically maps dish types to appropriate categories.

## Database Schema

### Recipe Entity
| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Primary key |
| `name` | String | Recipe name |
| `description` | String | Optional description |
| `ingredientsText` | String | Ingredients as formatted text |
| `instructions` | String | Step-by-step instructions |
| `prepTimeMinutes` | Integer | Preparation time |
| `servings` | Integer | Number of servings |
| `category` | Enum | BREAKFAST, LUNCH, DINNER, DESSERT |
| `source` | Enum | USER_CREATED, SPOONACULAR |
| `imageUrl` | String | URL to recipe image |
| `spoonacularId` | Integer | External recipe ID (if from Spoonacular) |
| `user` | User | Many-to-one relationship |

### User Entity
| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Primary key |
| `username` | String | Unique username |
| `email` | String | Unique email |
| `password` | String | BCrypt hashed password |
| `createdAt` | Timestamp | Account creation time |
| `recipes` | List\<Recipe\> | One-to-many relationship |

## Running the Application

### Prerequisites
* Java 21 or higher
* Maven 3.6+
* PostgreSQL (for production) or H2 (auto-configured for development)

### Development Mode
```bash
git clone https://github.com/Richard-Mackey/recipe-api.git
cd recipe-api
./mvnw spring-boot:run
```

API runs on: `http://localhost:8082`

### Production Mode
```bash
./mvnw clean package
java -jar target/recipe-api-0.0.1-SNAPSHOT.jar
```

## Testing

Run all tests:
```bash
./mvnw test
```

### Test Coverage
* Repository layer tests
* Service layer tests with mocked dependencies
* Controller integration tests
* Authentication flow tests

**Current test count: 94 passing tests**

## Configuration

### Database Configuration

**Development** - Uses H2 in-memory database (auto-configured)

### CORS Configuration
Currently configured for local frontend development at `http://localhost:5173`.

Update `SecurityConfig.java` for production deployment.

## Frontend Repository

React frontend repository: [Recipe API Frontend](https://github.com/yourusername/recipe-frontend)

## Future Enhancements

* **Nutritional Information**: Integrate nutrition data from Spoonacular
* **Recipe Ratings**: User ratings and reviews system
* **Advanced Search**: Full-text search with multiple filters
* **Image Upload**: Allow users to upload images for their recipes
* **Meal Planning**: Weekly meal planner feature
* **Recipe Sharing**: Social features for sharing recipes between users
* **Email Verification**: Confirm user emails on registration
* **Recipe Collections**: Organise recipes into custom collections/cookbooks
