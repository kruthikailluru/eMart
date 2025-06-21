import { 
  loginUser, 
  registerUser, 
  logoutUser, 
  getCurrentUser, 
  validateToken 
} from '../firebase/authService';

export const authService = {
  // Login user
  login: async (email, password) => {
    return await loginUser(email, password);
  },

  // Register user
  register: async (userData) => {
    return await registerUser(userData);
  },

  // Validate token
  validateToken: async () => {
    return await validateToken();
  },

  // Logout user
  logout: () => {
    logoutUser();
    window.location.href = '/login';
  },

  // Get current user from localStorage
  getCurrentUser: () => {
    return getCurrentUser();
  },

  // Check if user is authenticated
  isAuthenticated: () => {
    const user = getCurrentUser();
    return !!user;
  }
}; 