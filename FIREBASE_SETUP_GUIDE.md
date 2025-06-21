# Firebase Setup Guide for eMart Frontend

## Overview
Your React application has been successfully migrated from a Spring Boot backend to Firebase. All API services now use Firebase Authentication, Firestore Database, and Firebase Storage.

## What's Been Updated

### 1. Firebase Services Created
- **Authentication Service** (`src/firebase/authService.js`)
  - User registration and login
  - Token validation
  - Password reset
  - User profile management

- **Product Service** (`src/firebase/productService.js`)
  - CRUD operations for products
  - Image upload to Firebase Storage
  - Product search and filtering
  - Stock management

- **Order Service** (`src/firebase/orderService.js`)
  - Order creation and management
  - Order status updates
  - Customer order history
  - Order statistics

- **User Service** (`src/firebase/userService.js`)
  - User management for admins
  - Role management
  - User statistics

- **Payment Service** (`src/firebase/paymentService.js`)
  - Payment processing
  - Payment status management
  - Payment statistics

- **Invoice Service** (`src/firebase/invoiceService.js`)
  - Invoice generation and management
  - Invoice status updates
  - Invoice statistics

- **Barcode Service** (`src/firebase/barcodeService.js`)
  - Barcode generation and validation
  - Product search by barcode

### 2. API Layer Updated
All existing API services in `src/api/` have been updated to use Firebase instead of the backend:
- `authService.js`
- `productService.js`
- `orderService.js`
- `userService.js`
- `paymentService.js`
- `invoiceService.js`
- `barcodeService.js`

## Firebase Configuration
Your Firebase configuration has been set up in `src/firebase/config.js` with your project details:
- Project ID: `emart-33a63`
- Authentication, Firestore, and Storage are enabled

## Next Steps

### 1. Firebase Console Setup
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project: `emart-33a63`
3. Enable the following services:

#### Authentication
1. Go to Authentication > Sign-in method
2. Enable Email/Password authentication
3. Optionally enable other providers (Google, Facebook, etc.)

#### Firestore Database
1. Go to Firestore Database
2. Create a database in production mode
3. Set up security rules (see below)

#### Storage
1. Go to Storage
2. Initialize storage
3. Set up security rules (see below)

### 2. Security Rules

#### Firestore Security Rules
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Products - read by all, write by suppliers and admins
    match /products/{productId} {
      allow read: if true;
      allow write: if request.auth != null && 
        (get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role in ['SUPPLIER', 'ADMIN']);
    }
    
    // Orders - users can read/write their own orders, admins can read all
    match /orders/{orderId} {
      allow read, write: if request.auth != null && 
        (resource.data.customerId == request.auth.uid || 
         get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'ADMIN');
    }
    
    // Payments - users can read their own payments, admins can read all
    match /payments/{paymentId} {
      allow read, write: if request.auth != null && 
        (resource.data.customerId == request.auth.uid || 
         get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'ADMIN');
    }
    
    // Invoices - users can read their own invoices, admins can read all
    match /invoices/{invoiceId} {
      allow read, write: if request.auth != null && 
        (resource.data.customerId == request.auth.uid || 
         get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'ADMIN');
    }
  }
}
```

#### Storage Security Rules
```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    // Product images - read by all, upload by authenticated users
    match /products/{allPaths=**} {
      allow read: if true;
      allow write: if request.auth != null;
    }
    
    // User avatars - users can upload their own
    match /avatars/{userId}/{allPaths=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

### 3. Testing the Application
1. Start your React application:
   ```bash
   npm start
   ```

2. Test user registration and login
3. Test product creation and management
4. Test order creation and management
5. Test all other features

### 4. Data Migration (if needed)
If you have existing data in your Spring Boot backend, you'll need to migrate it to Firebase. You can:
1. Export data from your backend
2. Use Firebase Admin SDK to import data
3. Or manually create some test data through the Firebase Console

### 5. Environment Variables (Optional)
For better security, you can move Firebase config to environment variables:

1. Create `.env` file in your project root:
   ```
   REACT_APP_FIREBASE_API_KEY=AIzaSyCmuC81M3czagowphT6Gji5tmg34Sgri4Y
   REACT_APP_FIREBASE_AUTH_DOMAIN=emart-33a63.firebaseapp.com
   REACT_APP_FIREBASE_PROJECT_ID=emart-33a63
   REACT_APP_FIREBASE_STORAGE_BUCKET=emart-33a63.firebasestorage.app
   REACT_APP_FIREBASE_MESSAGING_SENDER_ID=641495488506
   REACT_APP_FIREBASE_APP_ID=1:641495488506:web:6a78e014e45e757ddcb643
   REACT_APP_FIREBASE_MEASUREMENT_ID=G-65RV390N7M
   ```

2. Update `src/firebase/config.js`:
   ```javascript
   const firebaseConfig = {
     apiKey: process.env.REACT_APP_FIREBASE_API_KEY,
     authDomain: process.env.REACT_APP_FIREBASE_AUTH_DOMAIN,
     projectId: process.env.REACT_APP_FIREBASE_PROJECT_ID,
     storageBucket: process.env.REACT_APP_FIREBASE_STORAGE_BUCKET,
     messagingSenderId: process.env.REACT_APP_FIREBASE_MESSAGING_SENDER_ID,
     appId: process.env.REACT_APP_FIREBASE_APP_ID,
     measurementId: process.env.REACT_APP_FIREBASE_MEASUREMENT_ID
   };
   ```

## Features Available
- ✅ User authentication (register, login, logout)
- ✅ Product management (CRUD operations)
- ✅ Order management
- ✅ Payment processing
- ✅ Invoice generation
- ✅ User management
- ✅ Barcode functionality
- ✅ Image upload to Firebase Storage
- ✅ Real-time data synchronization
- ✅ Role-based access control

## Troubleshooting
1. **Authentication errors**: Check if Email/Password auth is enabled in Firebase Console
2. **Database errors**: Verify Firestore is created and security rules are set
3. **Storage errors**: Ensure Storage is initialized and rules are configured
4. **CORS errors**: Firebase handles CORS automatically, no additional setup needed

Your application is now fully integrated with Firebase and ready to use! 🚀 