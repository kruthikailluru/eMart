import axios from 'axios';

// Create axios instance with base configuration
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add JWT token to requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle common errors
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Health check function to test backend connectivity
export const checkBackendHealth = async () => {
  try {
    const response = await api.get('/auth/validate-token');
    return { success: true, data: response.data };
  } catch (error) {
    if (error.code === 'ERR_NETWORK') {
      return { success: false, error: 'Backend server is not running. Please start the Spring Boot application.' };
    }
    return { success: false, error: error.message };
  }
};

export default api; 