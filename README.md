# Recipe API

A RESTful API for creating and managing recipes, built using Spring Boot

## Features

- Full CRUD operations for recipes
- Layered architecture (Controller - Service - Repository)
- JPA/Hibernate for database persistence
- Custom exception handling
- Comprehensive test coverage
- Professional logging

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- H2 Database
- Maven
- JUnit 5
- Mockito

## API Endpoints

### Get all recipes
```
GET /api/recipes
```

### Get recipe by ID
```
GET /api/recipes/{id}
```

### Create recipe
```
POST /api/recipes
Content-Type: application/json

{
  "name": "Spaghetti Carbonara",
  "ingredients": "pasta, eggs, bacon, parmesan",
  "instructions": "Cook pasta, fry bacon, mix with eggs and cheese"
}
```

### Update recipe
```
PUT /api/recipes/{id}
```

### Delete recipe
```
DELETE /api/recipes/{id}
```

## Running the Application

1. Clone the repository
```bash
git clone https://github.com/Richard-Mackey/recipe-api.git
cd recipe-api
```

2. Run with Maven
```bash
./mvnw spring-boot:run
```

3. The API will be available at: `http://localhost:8080`

## Testing

Run all tests:
```bash
./mvnw test
```

## Future Enhancements

- **Authentication:** User accounts and authorization
- **Integration with External API (Spoonacular):** Discover external recipes, save favourites, modify and store custom versions
- **Advanced Features:** Categories, tags, search, meal planning, ratings
- **React Frontend:** Full user interface for recipe management
- **Cloud Deployment:** Production deployment with CI/CD pipeline