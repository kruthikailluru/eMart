# E-Mart Frontend

A React-based frontend application for the E-Mart e-commerce platform with full backend integration.

## Features

- **Authentication System**: JWT-based authentication with role-based access control
- **Product Management**: CRUD operations for products with approval workflow
- **Order Management**: Complete order lifecycle management
- **Payment Processing**: Integrated payment system with multiple payment methods
- **Inventory Management**: Real-time inventory tracking and management
- **Barcode System**: Product barcode generation and scanning
- **Invoice Management**: Automated invoice generation and management
- **User Management**: Admin panel for user management
- **Notification System**: Real-time notifications for all user types

## Backend Integration

This frontend is fully integrated with the Spring Boot backend API. The integration includes:

### API Services

- **Authentication Service** (`src/api/authService.js`): Login, registration, token validation
- **Product Service** (`src/api/productService.js`): Product CRUD, approval, search, barcode
- **Order Service** (`src/api/orderService.js`): Order management, status updates
- **Payment Service** (`src/api/paymentService.js`): Payment processing, refunds
- **User Service** (`src/api/userService.js`): User management (admin only)
- **Barcode Service** (`src/api/barcodeService.js`): Barcode generation and info
- **Invoice Service** (`src/api/invoiceService.js`): Invoice generation and management

### Authentication & Authorization

- JWT token-based authentication
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

- MongoDB database running on `localhost:27017`
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

## License

This project is licensed under the MIT License.
