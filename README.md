# E-Mart
-by CodeBusters

A React-based application for the E-Mart e-commerce platform with full backend integration.

## Features

- **Authentication System**: Firebase-based authentication with role-based access control
- **Product Management**: CRUD operations for products with approval workflow
- **Order Management**: Complete order lifecycle management
- **Inventory Management**: Real-time inventory tracking and management
- **Barcode System**: Product barcode generation and scanning
- **Invoice Management**: Automated invoice generation and management
- **User Management**: Admin panel for user management
- **Notification System**: Real-time notifications for all user types

## Backend Integration

This application is fully integrated with the Spring Boot backend API. The integration includes:

### API Services

- **Authentication Service** (`src/api/authService.js`): Login, registration, token validation
- **Product Service** (`src/api/productService.js`): Product CRUD, approval, search, barcode
- **Order Service** (`src/api/orderService.js`): Order management, status updates
- **Payment Service** (`src/api/paymentService.js`): Payment processing, refunds
- **User Service** (`src/api/userService.js`): User management (admin only)
- **Barcode Service** (`src/api/barcodeService.js`): Barcode generation and info
- **Invoice Service** (`src/api/invoiceService.js`): Invoice generation and management

### Authentication & Authorization

- Firebase-based authentication
- Role-based access control (ADMIN, SUPPLIER, CUSTOMER)
- Protected routes with automatic redirection
- Token validation and automatic logout on expiration

### Context Providers

- **AuthContext**: Manages authentication state and user information
- **NotificationContext**: Handles application-wide notifications

## Getting Started

### Prerequisites

- Node.js (v14 or higher)
- npm or yarn
- Running Spring Boot backend on `http://localhost:8080`

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd emart-frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

The application will be available at `http://localhost:3000`

### Backend Setup

Make sure the Spring Boot backend is running on port 8080 with the following configuration:

- JWT secret configured in `application.yml`
- CORS configured to allow requests from `http://localhost:3000`

## API Configuration

The API configuration is in `src/api/config.js`:

```javascript
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});
```

### Authentication Flow

1. User enters credentials on login page
2. Frontend sends login request to `/api/auth/login`
3. Backend validates credentials and returns JWT token
4. Frontend stores token in localStorage
5. All subsequent requests include token in Authorization header
6. Token is validated on each request

### Role-Based Access

- **ADMIN**: Full access to all features including user management
- **SUPPLIER**: Product management, order fulfillment
- **CUSTOMER**: Product browsing, ordering, payment

## Usage Examples

### Using API Services

```javascript
import { productService, orderService } from '../api';

// Get all products
const result = await productService.getAllProducts();
if (result.success) {
  setProducts(result.data);
}

// Create an order
const orderData = {
  items: [
    { productId: 1, quantity: 2 },
    { productId: 2, quantity: 1 }
  ],
  shippingAddress: "123 Main St",
  paymentMethod: "CREDIT_CARD"
};

const orderResult = await orderService.createOrder(orderData);
```

### Using Authentication Context

```javascript
import { useAuth } from '../context/AuthContext';

const MyComponent = () => {
  const { user, authenticated, login, logout, isAdmin } = useAuth();
  
  if (!authenticated) {
    return <div>Please log in</div>;
  }
  
  return (
    <div>
      <h1>Welcome, {user.name}!</h1>
      {isAdmin && <AdminPanel />}
      <button onClick={logout}>Logout</button>
    </div>
  );
};
```

### Using Notifications

```javascript
import { useNotifications } from '../context/NotificationContext';

const MyComponent = () => {
  const { addNotification } = useNotifications();
  
  const handleSuccess = () => {
    addNotification('Operation completed successfully!', 'success');
  };
  
  const handleError = () => {
    addNotification('Something went wrong!', 'error');
  };
};
```

## Project Structure

```
src/
├── api/                    # API services
│   ├── config.js          # Axios configuration
│   ├── authService.js     # Authentication API
│   ├── productService.js  # Product API
│   ├── orderService.js    # Order API
│   ├── paymentService.js  # Payment API
│   ├── userService.js     # User management API
│   ├── barcodeService.js  # Barcode API
│   ├── invoiceService.js  # Invoice API
│   └── index.js           # Service exports
├── components/            # Reusable components
│   ├── ProtectedRoute.js  # Route protection
│   └── NotificationDisplay.js # Notification UI
├── context/              # React contexts
│   ├── AuthContext.js    # Authentication context
│   └── NotificationContext.js # Notification context
├── hooks/                # Custom hooks
│   └── useAuth.js        # Authentication hook
├── pages/                # Page components
└── routes/               # Route configuration
    └── AppRoutes.js      # Main routing
```

## Error Handling

The application includes comprehensive error handling:

- API errors are caught and displayed as notifications
- Network errors trigger automatic retry logic
- Authentication errors redirect to login
- Form validation with user-friendly error messages

## Security Features

- JWT token validation on every request
- Automatic token refresh
- Role-based route protection
- XSS protection through proper input sanitization
- CSRF protection via token-based authentication

## Development

### Adding New API Endpoints

1. Add the endpoint to the appropriate service file in `src/api/`
2. Follow the established pattern of returning `{ success: boolean, data?: any, error?: string }`
3. Update the service index file if needed

### Adding New Protected Routes

```javascript
<Route 
  path="/new-route" 
  element={
    <ProtectedRoute requiredRoles={['ADMIN']}>
      <NewComponent />
    </ProtectedRoute>
  } 
/>
```

### WorkFlow
**drive link:** https://drive.google.com/file/d/1w8V30qUuoUwBFcKO3wKXVTLUiCWRjhHJ/view?usp=sharing
[Click here to view the file](https://drive.google.com/file/d/1w8V30qUuoUwBFcKO3wKXVTLUiCWRjhHJ/view?usp=sharing)

### ScreenShots
## Dashboard:
![image](https://github.com/user-attachments/assets/2d48e788-fd7a-4d64-b41d-843276855bdd)


## Login window:
![image](https://github.com/user-attachments/assets/348b0f33-650e-4db5-aa5f-99ee310e2575)

## Supplier workflow:
![image](https://github.com/user-attachments/assets/a5afb084-eb8d-4f77-965d-afa24c3a9570)

![image](https://github.com/user-attachments/assets/d216fc1f-a4c3-4144-a919-b3ee66e14079)

![image](https://github.com/user-attachments/assets/1cccb710-9b43-4076-a1d2-29754cb60b7c)


## Admin workflow:
![image](https://github.com/user-attachments/assets/066d405a-f493-4e40-a286-06fb87148a3a)

![image](https://github.com/user-attachments/assets/97765215-92c9-4719-bfe2-aa3e8d285b34)

![image](https://github.com/user-attachments/assets/dc621171-f6f0-4713-94f7-a41ff1dd92ac)

![image](https://github.com/user-attachments/assets/6cccf74a-2e50-4eaf-a78c-bd15bf733db6)


## Suppliers Inventory Status when admin approves the product:
![image](https://github.com/user-attachments/assets/0b26964b-98d0-440b-8ff9-ae1472719407)

## Customers Workflow:
![image](https://github.com/user-attachments/assets/e99d080a-eb36-43f1-a2e3-1b60f87613c7)

![image](https://github.com/user-attachments/assets/7bc52ece-ad43-40d4-b024-6783a21d2535)

![image](https://github.com/user-attachments/assets/e36ca8fc-590c-4a76-8ade-794c3acfa991)

 



## Troubleshooting

### Common Issues

1. **CORS Errors**: Ensure backend CORS is configured for `http://localhost:3000`
2. **Authentication Failures**: Check JWT secret configuration in backend
3. **API Connection**: Verify backend is running on port 8080
4. **Database Connection**: Ensure MongoDB is running and accessible

### Debug Mode

Enable debug logging by setting `localStorage.debug = 'emart:*'` in browser console.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

### Contributors
  **@kruthikailluru**
  **@mahitha63**
  **@puneeth2004**
  
## License

This project is licensed under the MIT License.
