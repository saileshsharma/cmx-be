# CMX-BE Startup Scripts

This directory contains startup scripts to easily run the CMX Claims Management Backend with different profiles.

## Available Scripts

### Development Profile
- **Windows**: `start-dev.bat`
- **Linux/Mac**: `start-dev.sh`

### Production Profile
- **Windows**: `start-prod.bat`
- **Linux/Mac**: `start-prod.sh`

## Usage

### Development Environment

#### Windows
```cmd
start-dev.bat
```

#### Linux/Mac
```bash
./start-dev.sh
```

**Development Configuration:**
- Profile: `dev`
- Database: localhost:5432/cmx_new_db1
- Kafka: 192.168.1.172:9092
- Keycloak: http://localhost:9091/realms/auto-insurance
- GraphiQL: Enabled for API testing
- Hot reload: Enabled
- Detailed logging: Enabled

### Production Environment

#### Windows
```cmd
# Set required environment variables first
set SPRING_DATASOURCE_PASSWORD=your_prod_password
set OAUTH_ISSUER_URI=https://your-keycloak.com/realms/auto-insurance
set CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com

start-prod.bat
```

#### Linux/Mac
```bash
# Set required environment variables first
export SPRING_DATASOURCE_PASSWORD=your_prod_password
export OAUTH_ISSUER_URI=https://your-keycloak.com/realms/auto-insurance
export CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com

./start-prod.sh
```

**Production Configuration:**
- Profile: `prod`
- Optimized JVM settings
- Builds JAR before running
- Requires production environment variables
- GraphiQL: Disabled
- Security: Enhanced
- Logging: Optimized

## Required Environment Variables

### Development (Auto-configured)
All environment variables are automatically set by the dev script using values from `.env.dev`.

### Production (Must be provided)

#### Required
- `SPRING_DATASOURCE_PASSWORD` - Production database password
- `OAUTH_ISSUER_URI` - OAuth2/Keycloak issuer URI

#### Optional (with defaults)
- `SPRING_DATASOURCE_URL` - Database connection URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `KAFKA_BROKERS` - Kafka broker addresses
- `CORS_ALLOWED_ORIGINS` - Allowed CORS origins
- `OTEL_EXPORTER_OTLP_ENDPOINT` - OpenTelemetry endpoint (optional)

## Prerequisites

### Required
- **Java 17+** - Runtime environment
- **Maven 3.6+** - Build tool
- **PostgreSQL** - Database server
- **Network access** to Kafka and Keycloak servers

### Recommended for Development
- **Docker** - For running dependent services locally
- **Git** - Version control
- **IDE** - IntelliJ IDEA or VS Code with Java extensions

## Application Endpoints

Once started, the application will be available at:

### Development
- **Main Application**: http://localhost:8080
- **GraphQL API**: http://localhost:8080/graphql
- **GraphiQL IDE**: http://localhost:8080/graphiql
- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/prometheus
- **All Actuator**: http://localhost:8080/actuator

### Production
- **Main Application**: http://localhost:8080
- **GraphQL API**: http://localhost:8080/graphql
- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/prometheus

## Troubleshooting

### Common Issues

#### 1. "Maven not found"
**Solution**: Install Maven and add to PATH
```bash
# Verify Maven installation
mvn --version
```

#### 2. "Java not found"
**Solution**: Install Java 17+ and add to PATH
```bash
# Verify Java installation
java -version
```

#### 3. "Database connection failed"
**Solutions**:
- Ensure PostgreSQL is running
- Check database credentials
- Verify database exists
- Check network connectivity

#### 4. "Kafka connection timeout"
**Solutions**:
- Verify Kafka broker address
- Check network connectivity to Kafka
- Ensure Kafka is running and accessible

#### 5. "OAuth2/Keycloak errors"
**Solutions**:
- Verify Keycloak is running
- Check OAuth2 issuer URI
- Validate client credentials
- Ensure realm exists

#### 6. "Port 8080 already in use"
**Solutions**:
- Stop existing application on port 8080
- Change server port in application properties
- Use `netstat -ano | findstr :8080` (Windows) or `lsof -i :8080` (Linux/Mac) to find process

### Development Tips

1. **Faster Startup**: Use `mvn compile` before running if you've made changes
2. **Debug Mode**: Add `-Ddebug` to JAVA_OPTS for debug logging
3. **Profile Override**: Add `-Dspring.profiles.active=your-profile` to change profile
4. **Port Change**: Add `-Dserver.port=8081` to change port

### Logs Location

- **Console**: All logs are displayed in the console
- **File Logging**: Check `application-{profile}.properties` for log file configuration
- **Docker**: If running in container, use `docker logs <container-id>`

## Script Customization

You can modify the scripts to:
- Change default ports
- Add additional environment variables
- Modify JVM options
- Add pre-startup checks
- Include post-startup verification

## Security Notes

⚠️ **Important**:
- Never commit production passwords to version control
- Use environment variables or external configuration for sensitive data
- Ensure production Keycloak uses HTTPS
- Review CORS settings for production
- Use proper SSL/TLS certificates in production

## Support

For issues with:
- **Application**: Check application logs and Spring Boot documentation
- **Database**: Verify PostgreSQL setup and connection
- **Kafka**: Check Kafka broker status and connectivity
- **Authentication**: Verify Keycloak configuration and network access