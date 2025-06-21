import { 
  createPayment, 
  getPaymentById, 
  getPaymentsByCustomer, 
  updatePaymentStatus, 
  processPayment, 
  getPaymentStatistics,
  PAYMENT_STATUS
} from '../firebase/paymentService';

export const paymentService = {
  // Process payment
  processPayment: async (paymentData) => {
    const currentUser = JSON.parse(localStorage.getItem('user') || '{}');
    const payment = await createPayment({
      ...paymentData,
      customerId: currentUser.uid
    });
    
    if (payment.success) {
      // Process the payment
      return await processPayment(payment.data.id);
    }
    
    return payment;
  },

  // Get customer payments
  getCustomerPayments: async () => {
    const currentUser = JSON.parse(localStorage.getItem('user') || '{}');
    return await getPaymentsByCustomer(currentUser.uid);
  },

  // Get payment by ID
  getPaymentById: async (paymentId) => {
    return await getPaymentById(paymentId);
  },

  // Update payment status (admin only)
  updatePaymentStatus: async (paymentId, status) => {
    return await updatePaymentStatus(paymentId, status);
  },

  // Process refund (admin only)
  processRefund: async (paymentId, refundData) => {
    return await updatePaymentStatus(paymentId, PAYMENT_STATUS.REFUNDED);
  },

  // Get revenue statistics (admin only)
  getRevenueStats: async (period = 'monthly') => {
    const stats = await getPaymentStatistics();
    return {
      success: true,
      data: {
        totalRevenue: stats.data?.totalAmount || 0,
        totalPayments: stats.data?.total || 0
      }
    };
  },

  // Get payment methods summary (admin only)
  getPaymentMethodsSummary: async () => {
    // For now, return empty summary
    // You might want to implement this based on your payment data
    return {
      success: true,
      data: {
        cash: 0,
        creditCard: 0,
        debitCard: 0,
        bankTransfer: 0,
        digitalWallet: 0
      }
    };
  },

  // Get payment statistics
  getPaymentStats: async () => {
    return await getPaymentStatistics();
  }
}; 