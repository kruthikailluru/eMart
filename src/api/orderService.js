import api from './config';

export const orderService = {
  // Get all orders (admin only)
  getAllOrders: async () => {
    try {
      const response = await api.get('/orders');
      return { success: true, data: response.data };
    } catch (error) {
      return { 
        success: false, 
        error: error.response?.data?.message || 'Failed to fetch orders' 
      };
    }
  },

  // Get customer orders
  getCustomerOrders: async () => {
    try {
      const response = await api.get('/orders/customer');
      return { success: true, data: response.data };
    } catch (error) {
      return { 
        success: false, 
        error: error.response?.data?.message || 'Failed to fetch customer orders' 
      };
    }
  },

  // Get order by ID
  getOrderById: async (orderId) => {
    try {
      const response = await api.get(`/orders/${orderId}`);
      return { success: true, data: response.data };
    } catch (error) {
      return { 
        success: false, 
        error: error.response?.data?.message || 'Failed to fetch order' 
      };
    }
  },

  // Create new order
  createOrder: async (orderData) => {
    try {
      const response = await api.post('/orders', orderData);
      return { success: true, data: response.data };
    } catch (error) {
      return { 
        success: false, 
        error: error.response?.data?.message || 'Failed to create order' 
      };
    }
  },

  // Update order status (admin only)
  updateOrderStatus: async (orderId, status) => {
    try {
      const response = await api.put(`/orders/${orderId}/status`, { status });
      return { success: true, data: response.data };
    } catch (error) {
      return { 
        success: false, 
        error: error.response?.data?.message || 'Failed to update order status' 
      };
    }
  },

  // Update payment status (admin only)
  updatePaymentStatus: async (orderId, paymentStatus) => {
    try {
      const response = await api.put(`/orders/${orderId}/payment-status`, { paymentStatus });
      return { success: true, data: response.data };
    } catch (error) {
      return { 
        success: false, 
        error: error.response?.data?.message || 'Failed to update payment status' 
      };
    }
  },

  // Cancel order (customer only)
  cancelOrder: async (orderId) => {
    try {
      const response = await api.put(`/orders/${orderId}/cancel`);
      return { success: true, data: response.data };
    } catch (error) {
      return { 
        success: false, 
        error: error.response?.data?.message || 'Failed to cancel order' 
      };
    }
  },

  // Get pending orders (admin only)
  getPendingOrders: async () => {
    try {
      const response = await api.get('/orders/pending');
      return { success: true, data: response.data };
    } catch (error) {
      return { 
        success: false, 
        error: error.response?.data?.message || 'Failed to fetch pending orders' 
      };
    }
  },

  // Get orders with pending payments (admin only)
  getPendingPaymentOrders: async () => {
    try {
      const response = await api.get('/orders/pending-payments');
      return { success: true, data: response.data };
    } catch (error) {
      return { 
        success: false, 
        error: error.response?.data?.message || 'Failed to fetch orders with pending payments' 
      };
    }
  },

  // Get revenue statistics (admin only)
  getRevenueStats: async (period = 'monthly') => {
    try {
      const response = await api.get(`/orders/revenue/${period}`);
      return { success: true, data: response.data };
    } catch (error) {
      return { 
        success: false, 
        error: error.response?.data?.message || 'Failed to fetch revenue statistics' 
      };
    }
  },

  // Get order statistics
  getOrderStats: async () => {
    try {
      const response = await api.get('/orders/stats');
      return { success: true, data: response.data };
    } catch (error) {
      return { 
        success: false, 
        error: error.response?.data?.message || 'Failed to fetch order statistics' 
      };
    }
  }
}; 