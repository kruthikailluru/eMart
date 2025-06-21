import React, { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../api';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [authenticated, setAuthenticated] = useState(false);

  // Check authentication status on mount
  useEffect(() => {
    const checkAuth = async () => {
      try {
        console.log('Checking authentication status...');
        const currentUser = authService.getCurrentUser();
        console.log('Current user from localStorage:', currentUser);
        
        if (currentUser) {
          const validation = await authService.validateToken();
          console.log('Token validation result:', validation);
          
          if (validation.valid) {
            setUser(validation.user);
            setAuthenticated(true);
            console.log('User authenticated:', validation.user);
          } else {
            // Token is invalid, clear storage
            console.log('Token invalid, clearing storage');
            authService.logout();
          }
        } else {
          console.log('No user found in localStorage');
        }
      } catch (error) {
        console.error('Auth check failed:', error);
        authService.logout();
      } finally {
        setLoading(false);
        console.log('Auth check completed, loading set to false');
      }
    };

    checkAuth();
  }, []);

  // Login function
  const login = async (email, password) => {
    setLoading(true);
    try {
      const result = await authService.login(email, password);
      
      if (result.success) {
        setUser(result.user);
        setAuthenticated(true);
        return { success: true };
      } else {
        return { success: false, error: result.error };
      }
    } catch (error) {
      const errorMessage = 'Login failed. Please try again.';
      return { success: false, error: errorMessage };
    } finally {
      setLoading(false);
    }
  };

  // Register function
  const register = async (userData) => {
    setLoading(true);
    try {
      const result = await authService.register(userData);
      if (result.success) {
        return { success: true };
      } else {
        return { success: false, error: result.error };
      }
    } catch (error) {
      const errorMessage = 'Registration failed. Please try again.';
      return { success: false, error: errorMessage };
    } finally {
      setLoading(false);
    }
  };

  // Logout function
  const logout = () => {
    console.log('Logging out user');
    authService.logout();
    setUser(null);
    setAuthenticated(false);
  };

  // Check if user has specific role
  const hasRole = (role) => {
    return user?.role === role;
  };

  // Check if user has any of the specified roles
  const hasAnyRole = (roles) => {
    return roles.includes(user?.role);
  };

  const value = {
    user,
    loading,
    authenticated,
    login,
    register,
    logout,
    hasRole,
    hasAnyRole,
    isAdmin: hasRole('ADMIN'),
    isSupplier: hasRole('SUPPLIER'),
    isCustomer: hasRole('CUSTOMER')
  };

  console.log('AuthContext state:', { user, loading, authenticated });

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}; 