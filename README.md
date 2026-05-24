# Short Link Generator API

URL shortener REST API built with Spring Boot.

## Technologies

- Java 21
- Spring Boot 4
- Spring Security + JWT
- PostgreSQL
- Flyway
- Testcontainers
- JUnit 5
- Mockito
- Swagger/OpenAPI 3 documentation

## Environment Variables

```env id="env3"
DB_URL=jdbc:postgresql://localhost:5432/url_shortener
DB_USERNAME=username
DB_PASSWORD=password
JWT_SECRET=secret
```

### Run the application via Docker Compose
Open Windows PowerShell in the root directory and execute:
```powershell
docker compose up --build
```

---

## API Documentation & Testing

Once the containers are running successfully, you can access the interactive API documentation:

* **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* **OpenAPI JSON Specs**: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

### How to use Authorized Endpoints in Swagger:
1. Generate a valid token using the Auth Controller (`/api/V1/auth/login`).
2. Click the **Authorize** 🔓 button in the top right corner of the Swagger page.
3. Paste your raw token into the **Value** field (Swagger prepends `Bearer` automatically).
4. Click **Authorize** and close the dialog. Secure endpoints (like `/api/V1/links`) are now ready to be tested!

---
