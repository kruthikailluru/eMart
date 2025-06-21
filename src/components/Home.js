import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Home = () => {
  const { authenticated, loading, user } = useAuth();

  if (loading) {
    return <div>Loading...</div>;
  }

  // After login, this component redirects user to the correct page based on their role.
  if (!authenticated) {
    // If somehow user lands here without being authenticated, send to login.
    return <Navigate to="/login" />;
  }
  
  switch (user?.role) {
    case 'ADMIN':
      return <Navigate to="/inventory" />;
    case 'SUPPLIER':
      return <Navigate to="/inventory-supplier" />;
    case 'CUSTOMER':
      return <Navigate to="/inventory-customer" />;
    default:
      return <Navigate to="/dashboard" />;
  }
};

export default Home; 
 