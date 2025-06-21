import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const ProtectedRoute = ({ children, requiredRoles = [] }) => {
  const { authenticated, loading, user, hasAnyRole } = useAuth();

  // Show loading spinner while checking authentication
  if (loading) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '100vh' 
      }}>
        <div>Loading...</div>
      </div>
    );
  }

  // Redirect to login if not authenticated
  if (!authenticated) {
    return <Navigate to="/" />;
  }

  // Check role-based access if required roles are specified
  if (requiredRoles.length > 0 && !hasAnyRole(requiredRoles)) {
    // Redirect to appropriate page based on user role
    switch (user?.role) {
      case 'ADMIN':
        return <Navigate to="/inventory" />;
      case 'SUPPLIER':
        return <Navigate to="/supplier" />;
      case 'CUSTOMER':
        return <Navigate to="/customer" />;
      default:
        return <Navigate to="/dashboard" />;
    }
  }

  return children;
};

export default ProtectedRoute;