# BookNest - Complete Online Bookstore System

## 🚀 Project Overview

BookNest is a comprehensive online bookstore management system built with Java Spark framework, featuring role-based access control, complete CRUD operations, and modern web interface.

## 🏗️ System Architecture

### Backend Technologies
- **Java 23** - Core programming language
- **Spark Java 2.9.4** - Lightweight web framework
- **Microsoft SQL Server** - Database management
- **Maven** - Dependency management and build tool
- **Gson** - JSON processing
- **SLF4J** - Logging framework

### Frontend Technologies
- **HTML5** - Structure and semantic markup
- **CSS3** - Modern styling with responsive design
- **JavaScript (ES6+)** - Dynamic functionality and API integration
- **Bootstrap 5** - Responsive UI framework

## 📊 Database Schema

The system includes a comprehensive database schema with the following tables:

### Core Tables
- **users** - User management (customers, staff, admin)
- **categories** - Book categories and genres
- **books** - Book inventory with detailed information
- **suppliers** - Supplier management
- **book_suppliers** - Many-to-many relationship between books and suppliers

### Transaction Tables
- **cart_items** - Shopping cart functionality
- **orders** - Order management
- **order_items** - Order line items
- **delivery_assignments** - Delivery staff assignments

### Management Tables
- **promotions** - Marketing campaigns and discounts
- **reviews** - Customer book reviews and ratings
- **performance_records** - Staff performance tracking
- **email_campaigns** - Marketing email campaigns

### System Tables
- **system_logs** - System activity logging
- **user_activity_logs** - User action tracking

## 👥 User Roles & Permissions

### 1. Customer
- Browse and search books
- Add items to cart
- Place orders
- Write reviews
- Track order status

### 2. Inventory Manager
- Manage book inventory
- Update stock levels
- Handle supplier relationships
- Monitor low stock alerts

### 3. Marketing Manager
- Create and manage promotions
- Design email campaigns
- Analyze customer behavior
- Manage book reviews

### 4. Delivery Staff
- View assigned deliveries
- Update delivery status
- Complete delivery tasks
- Track performance metrics

### 5. System Administrator
- Full system access
- User management
- System monitoring
- Database maintenance

## 🛠️ Key Features

### Customer Features
- **Book Browsing**: Advanced search and filtering
- **Shopping Cart**: Persistent cart with quantity management
- **Order Management**: Complete order lifecycle
- **Review System**: Rate and review books
- **Account Management**: Profile updates and order history

### Staff Features
- **Inventory Management**: Real-time stock tracking
- **Order Processing**: Order fulfillment workflow
- **Delivery Management**: Assignment and tracking
- **Performance Tracking**: Staff metrics and analytics
- **Marketing Tools**: Campaign management

### Admin Features
- **User Management**: Role assignment and permissions
- **System Monitoring**: Activity logs and analytics
- **Database Management**: Schema maintenance
- **Security**: Access control and audit trails

## 🚀 Getting Started

### Prerequisites
- Java 23 or higher
- Microsoft SQL Server
- Maven 3.6+
- Git

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd LandSalesDZ
   ```

2. **Database Setup**
   - Create a SQL Server database named `bookNest`
   - Update database credentials in `DatabaseConnection.java`
   - The application will automatically create tables and sample data

3. **Build and Run**
   ```bash
   # Using Maven wrapper
   .\mvnw.cmd clean compile
   .\mvnw.cmd exec:java -Dexec.mainClass="com.example.booknest.Main"
   
   # Or using Maven directly
   mvn clean compile
   mvn exec:java -Dexec.mainClass="com.example.booknest.Main"
   ```

4. **Access the Application**
   - Open browser to `http://localhost:4567`
   - Default admin credentials: `admin` / `admin123`

## 📱 User Interfaces

### Customer Interface
- **Login/Register**: `customer-login.html`, `customer-register.html`
- **Dashboard**: `customer-dashboard.html`
- **Book Browsing**: Integrated search and filtering
- **Cart Management**: Add/remove items, quantity updates
- **Order Tracking**: Real-time order status

### Staff Interfaces
- **Admin Dashboard**: `admin-dashboard.html`
- **Marketing Dashboard**: `marketing-dashboard.html`
- **Delivery Dashboard**: `delivery-dashboard.html`
- **Inventory Management**: Integrated stock management

## 🔧 API Endpoints

### Authentication
- `POST /api/login` - User login
- `POST /api/register` - User registration
- `POST /api/logout` - User logout
- `GET /api/auth/check` - Check authentication status

### Books
- `GET /api/books` - Get all books
- `GET /api/books/{id}` - Get book by ID
- `GET /api/books/genre/{genre}` - Get books by genre
- `POST /api/books` - Add new book
- `PUT /api/books/{id}` - Update book
- `DELETE /api/books/{id}` - Delete book

### Cart Management
- `GET /api/cart` - Get cart items
- `POST /api/cart/add` - Add item to cart
- `PUT /api/cart/update` - Update cart item
- `DELETE /api/cart/remove/{bookId}` - Remove item from cart
- `DELETE /api/cart/clear` - Clear cart

### Orders
- `GET /api/orders` - Get user orders
- `POST /api/orders/checkout` - Create order from cart
- `GET /api/orders/{id}` - Get order details
- `PUT /api/orders/{id}/status` - Update order status

### Inventory Management
- `GET /api/inventory/low-stock` - Get low stock items
- `GET /api/inventory/summary` - Get inventory summary
- `POST /api/inventory/restock` - Restock books
- `GET /api/inventory/health` - Get inventory health metrics

### Marketing
- `GET /api/promotions` - Get all promotions
- `POST /api/promotions` - Create promotion
- `PUT /api/promotions/{id}` - Update promotion
- `DELETE /api/promotions/{id}` - Delete promotion
- `GET /api/marketing/analytics` - Get marketing analytics

### Delivery Management
- `GET /api/delivery/assignments` - Get delivery assignments
- `POST /api/delivery/assign` - Assign delivery
- `PUT /api/delivery/update-status` - Update delivery status
- `GET /api/delivery/analytics` - Get delivery analytics

## 🔒 Security Features

- **Password Hashing**: BCrypt encryption for all passwords
- **Session Management**: Secure user sessions
- **Role-Based Access**: Granular permission system
- **Input Validation**: Comprehensive data validation
- **SQL Injection Protection**: Prepared statements
- **XSS Protection**: Input sanitization

## 📊 Performance Optimizations

- **Database Indexing**: Optimized query performance
- **Connection Pooling**: Efficient database connections
- **Caching**: Strategic data caching
- **Lazy Loading**: On-demand data loading
- **Pagination**: Efficient large dataset handling

## 🧪 Testing

The system includes comprehensive testing capabilities:

- **Unit Tests**: Individual component testing
- **Integration Tests**: API endpoint testing
- **Database Tests**: Data integrity verification
- **User Flow Tests**: End-to-end user scenarios

## 🚀 Deployment

### Production Deployment
1. Configure production database
2. Update connection strings
3. Set up SSL certificates
4. Configure reverse proxy (nginx/Apache)
5. Set up monitoring and logging

### Docker Deployment
```dockerfile
FROM openjdk:23-jdk-slim
COPY target/online-book-nest-1.0.0.jar app.jar
EXPOSE 4567
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 📈 Monitoring & Analytics

- **System Logs**: Comprehensive activity logging
- **Performance Metrics**: Response time monitoring
- **User Analytics**: Behavior tracking
- **Error Tracking**: Exception monitoring
- **Database Monitoring**: Query performance

## 🔧 Configuration

### Database Configuration
Update `DatabaseConnection.java` with your SQL Server credentials:
```java
private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=bookNest;encrypt=true;trustServerCertificate=true";
private static final String USERNAME = "your_username";
private static final String PASSWORD = "your_password";
```

### Application Properties
Configure in `application.properties`:
```properties
server.port=4567
database.url=jdbc:sqlserver://localhost:1433;databaseName=bookNest
database.username=your_username
database.password=your_password
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the documentation

## 🎯 Future Enhancements

- **Mobile App**: React Native mobile application
- **Advanced Analytics**: Machine learning insights
- **Payment Integration**: Stripe/PayPal integration
- **Email Notifications**: Automated email system
- **Multi-language Support**: Internationalization
- **API Documentation**: Swagger/OpenAPI integration

---

**BookNest** - Your complete online bookstore solution! 📚✨