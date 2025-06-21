import { 
  collection, 
  doc, 
  getDocs, 
  getDoc, 
  addDoc, 
  updateDoc, 
  query, 
  where, 
  orderBy, 
  limit,
  Timestamp
} from 'firebase/firestore';
import { db } from './config';

// Payment status
export const PAYMENT_STATUS = {
  PENDING: 'PENDING',
  COMPLETED: 'COMPLETED',
  FAILED: 'FAILED',
  REFUNDED: 'REFUNDED',
  CANCELLED: 'CANCELLED'
};

// Payment method
export const PAYMENT_METHOD = {
  CASH: 'CASH',
  CREDIT_CARD: 'CREDIT_CARD',
  DEBIT_CARD: 'DEBIT_CARD',
  BANK_TRANSFER: 'BANK_TRANSFER',
  DIGITAL_WALLET: 'DIGITAL_WALLET'
};

// Create payment
export const createPayment = async (paymentData) => {
  try {
    const payment = {
      ...paymentData,
      status: PAYMENT_STATUS.PENDING,
      createdAt: Timestamp.now(),
      updatedAt: Timestamp.now()
    };
    
    const docRef = await addDoc(collection(db, 'payments'), payment);
    
    return {
      success: true,
      data: {
        id: docRef.id,
        ...payment,
        createdAt: payment.createdAt.toDate(),
        updatedAt: payment.updatedAt.toDate()
      }
    };
  } catch (error) {
    console.error('Create payment error:', error);
    return {
      success: false,
      error: 'Failed to create payment.'
    };
  }
};

// Get payment by ID
export const getPaymentById = async (paymentId) => {
  try {
    const docRef = doc(db, 'payments', paymentId);
    const docSnap = await getDoc(docRef);
    
    if (docSnap.exists()) {
      const payment = {
        id: docSnap.id,
        ...docSnap.data(),
        createdAt: docSnap.data().createdAt?.toDate(),
        updatedAt: docSnap.data().updatedAt?.toDate()
      };
      
      return {
        success: true,
        data: payment
      };
    } else {
      return {
        success: false,
        error: 'Payment not found.'
      };
    }
  } catch (error) {
    console.error('Get payment error:', error);
    return {
      success: false,
      error: 'Failed to fetch payment.'
    };
  }
};

// Get payments by order ID
export const getPaymentsByOrder = async (orderId) => {
  try {
    let q = query(
      collection(db, 'payments'),
      where('orderId', '==', orderId),
      orderBy('createdAt', 'desc')
    );
    
    const querySnapshot = await getDocs(q);
    const payments = [];
    
    querySnapshot.forEach((doc) => {
      payments.push({
        id: doc.id,
        ...doc.data(),
        createdAt: doc.data().createdAt?.toDate(),
        updatedAt: doc.data().updatedAt?.toDate()
      });
    });
    
    return {
      success: true,
      data: payments
    };
  } catch (error) {
    console.error('Get payments by order error:', error);
    return {
      success: false,
      error: 'Failed to fetch payments.'
    };
  }
};

// Get payments by customer
export const getPaymentsByCustomer = async (customerId) => {
  try {
    let q = query(
      collection(db, 'payments'),
      where('customerId', '==', customerId),
      orderBy('createdAt', 'desc')
    );
    
    const querySnapshot = await getDocs(q);
    const payments = [];
    
    querySnapshot.forEach((doc) => {
      payments.push({
        id: doc.id,
        ...doc.data(),
        createdAt: doc.data().createdAt?.toDate(),
        updatedAt: doc.data().updatedAt?.toDate()
      });
    });
    
    return {
      success: true,
      data: payments
    };
  } catch (error) {
    console.error('Get payments by customer error:', error);
    return {
      success: false,
      error: 'Failed to fetch payments.'
    };
  }
};

// Update payment status
export const updatePaymentStatus = async (paymentId, status) => {
  try {
    const paymentRef = doc(db, 'payments', paymentId);
    const paymentSnap = await getDoc(paymentRef);
    
    if (!paymentSnap.exists()) {
      return {
        success: false,
        error: 'Payment not found.'
      };
    }
    
    await updateDoc(paymentRef, {
      status,
      updatedAt: Timestamp.now()
    });
    
    return {
      success: true,
      message: 'Payment status updated successfully.'
    };
  } catch (error) {
    console.error('Update payment status error:', error);
    return {
      success: false,
      error: 'Failed to update payment status.'
    };
  }
};

// Process payment (simplified - in real app, integrate with payment gateway)
export const processPayment = async (paymentId) => {
  try {
    // Simulate payment processing
    // In a real application, you would integrate with a payment gateway
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    // For demo purposes, we'll mark it as completed
    return await updatePaymentStatus(paymentId, PAYMENT_STATUS.COMPLETED);
  } catch (error) {
    console.error('Process payment error:', error);
    return {
      success: false,
      error: 'Failed to process payment.'
    };
  }
};

// Get payment statistics
export const getPaymentStatistics = async (customerId = null) => {
  try {
    let q = collection(db, 'payments');
    
    if (customerId) {
      q = query(q, where('customerId', '==', customerId));
    }
    
    const querySnapshot = await getDocs(q);
    const payments = [];
    
    querySnapshot.forEach((doc) => {
      payments.push(doc.data());
    });
    
    const stats = {
      total: payments.length,
      totalAmount: payments.reduce((sum, payment) => sum + payment.amount, 0),
      pending: payments.filter(payment => payment.status === PAYMENT_STATUS.PENDING).length,
      completed: payments.filter(payment => payment.status === PAYMENT_STATUS.COMPLETED).length,
      failed: payments.filter(payment => payment.status === PAYMENT_STATUS.FAILED).length,
      refunded: payments.filter(payment => payment.status === PAYMENT_STATUS.REFUNDED).length
    };
    
    return {
      success: true,
      data: stats
    };
  } catch (error) {
    console.error('Get payment statistics error:', error);
    return {
      success: false,
      error: 'Failed to fetch payment statistics.'
    };
  }
}; 