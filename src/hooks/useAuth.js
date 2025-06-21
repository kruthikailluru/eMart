import { useState, useEffect, useCallback } from 'react';
import { authService } from '../api';
import { useNotifications } from '../context/NotificationContext';

export const useAuth = () => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [authenticated, setAuthenticated] = useState(false);
  const { addNotification } = useNotifications();

  // Check authentication status on mount
  useEffect(() => {
    const checkAuth = async () => {
      try {
        const currentUser = authService.getCurrentUser();
        if (currentUser) {
          const validation = await authService.validateToken();
          if (validation.valid) {
            setUser(validation.user);
            setAuthenticated(true);
          } else {
            // Token is invalid, clear storage
            authService.logout();
          }
        }
      } catch (error) {
        console.error('Auth check failed:', error);
        authService.logout();
      } finally {
        setLoading(false);
      }
    };

    checkAuth();
  }, []);

  // Login function
  const login = useCallback(async (email, password) => {
    setLoading(true);
    try {
      const result = await authService.login(email, password);
      if (result.success) {
        setUser(result.user);
        setAuthenticated(true);
        addNotification('Login successful!', 'success');
        return { success: true };
      } else {
        addNotification(result.error, 'error');
        return { success: false, error: result.error };
      }
    } catch (error) {
      const errorMessage = 'Login failed. Please try again.';
      addNotification(errorMessage, 'error');
      return { success: false, error: errorMessage };
    } finally {
      setLoading(false);
    }
  }, [addNotification]);

  // Register function
  const register = useCallback(async (userData) => {
    setLoading(true);
    try {
      const result = await authService.register(userData);
      if (result.success) {
        addNotification('Registration successful! Please login.', 'success');
        return { success: true };
      } else {
        addNotification(result.error, 'error');
        return { success: false, error: result.error };
      }
    } catch (error) {
      const errorMessage = 'Registration failed. Please try again.';
      addNotification(errorMessage, 'error');
      return { success: false, error: errorMessage };
    } finally {
      setLoading(false);
    }
  }, [addNotification]);

  // Logout function
  const logout = useCallback(() => {
    authService.logout();
    setUser(null);
    setAuthenticated(false);
    addNotification('Logged out successfully', 'info');
  }, [addNotification]);

  // Check if user has specific role
  const hasRole = useCallback((role) => {
    return user?.role === role;
  }, [user]);

  // Check if user has any of the specified roles
  const hasAnyRole = useCallback((roles) => {
    return roles.includes(user?.role);
  }, [user]);

  return {
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
}; 