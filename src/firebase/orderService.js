import { 
  collection, 
  doc, 
  getDocs, 
  getDoc, 
  addDoc, 
  updateDoc, 
  deleteDoc, 
  query, 
  where, 
  orderBy, 
  limit,
  Timestamp,
  writeBatch
} from 'firebase/firestore';
import { db } from './config';
import { getProductById, updateProductStock } from './productService';

// Order status
export const ORDER_STATUS = {
  PENDING: 'PENDING',
  CONFIRMED: 'CONFIRMED',
  PROCESSING: 'PROCESSING',
  SHIPPED: 'SHIPPED',
  DELIVERED: 'DELIVERED',
  CANCELLED: 'CANCELLED'
};

// Payment status
export const PAYMENT_STATUS = {
  PENDING: 'PENDING',
  PAID: 'PAID',
  FAILED: 'FAILED',
  REFUNDED: 'REFUNDED'
};

// Create new order
export const createOrder = async (orderData) => {
  try {
    const { items, customerId, shippingAddress, paymentMethod } = orderData;
    
    // Validate items and check stock
    let totalAmount = 0;
    const validatedItems = [];
    
    for (const item of items) {
      const productResult = await getProductById(item.productId);
      if (!productResult.success) {
        return {
          success: false,
          error: `Product ${item.productId} not found.`
        };
      }
      
      const product = productResult.data;
      if (product.stock < item.quantity) {
        return {
          success: false,
          error: `Insufficient stock for ${product.name}. Available: ${product.stock}`
        };
      }
      
      const itemTotal = product.price * item.quantity;
      totalAmount += itemTotal;
      
      validatedItems.push({
        productId: item.productId,
        productName: product.name,
        productPrice: product.price,
        quantity: item.quantity,
        total: itemTotal
      });
    }
    
    // Create order
    const order = {
      customerId,
      items: validatedItems,
      totalAmount,
      shippingAddress,
      paymentMethod,
      status: ORDER_STATUS.PENDING,
      paymentStatus: PAYMENT_STATUS.PENDING,
      createdAt: Timestamp.now(),
      updatedAt: Timestamp.now()
    };
    
    const orderRef = await addDoc(collection(db, 'orders'), order);
    
    // Update product stock
    const batch = writeBatch(db);
    for (const item of validatedItems) {
      const productResult = await getProductById(item.productId);
      if (productResult.success) {
        const newStock = productResult.data.stock - item.quantity;
        const productRef = doc(db, 'products', item.productId);
        batch.update(productRef, { 
          stock: newStock,
          updatedAt: Timestamp.now()
        });
      }
    }
    await batch.commit();
    
    return {
      success: true,
      data: {
        id: orderRef.id,
        ...order,
        createdAt: order.createdAt.toDate(),
        updatedAt: order.updatedAt.toDate()
      }
    };
  } catch (error) {
    console.error('Create order error:', error);
    return {
      success: false,
      error: 'Failed to create order.'
    };
  }
};

// Get order by ID
export const getOrderById = async (orderId) => {
  try {
    const docRef = doc(db, 'orders', orderId);
    const docSnap = await getDoc(docRef);
    
    if (docSnap.exists()) {
      const order = {
        id: docSnap.id,
        ...docSnap.data(),
        createdAt: docSnap.data().createdAt?.toDate(),
        updatedAt: docSnap.data().updatedAt?.toDate()
      };
      
      return {
        success: true,
        data: order
      };
    } else {
      return {
        success: false,
        error: 'Order not found.'
      };
    }
  } catch (error) {
    console.error('Get order error:', error);
    return {
      success: false,
      error: 'Failed to fetch order.'
    };
  }
};

// Get orders by customer
export const getOrdersByCustomer = async (customerId, page = 1, size = 10) => {
  try {
    let q = query(
      collection(db, 'orders'),
      where('customerId', '==', customerId),
      orderBy('createdAt', 'desc'),
      limit(size)
    );
    
    const querySnapshot = await getDocs(q);
    const orders = [];
    
    querySnapshot.forEach((doc) => {
      orders.push({
        id: doc.id,
        ...doc.data(),
        createdAt: doc.data().createdAt?.toDate(),
        updatedAt: doc.data().updatedAt?.toDate()
      });
    });
    
    return {
      success: true,
      data: orders
    };
  } catch (error) {
    console.error('Get customer orders error:', error);
    return {
      success: false,
      error: 'Failed to fetch orders.'
    };
  }
};

// Get all orders (for admin)
export const getAllOrders = async (page = 1, size = 10, filters = {}) => {
  try {
    let q = collection(db, 'orders');
    
    // Apply filters
    if (filters.status) {
      q = query(q, where('status', '==', filters.status));
    }
    if (filters.paymentStatus) {
      q = query(q, where('paymentStatus', '==', filters.paymentStatus));
    }
    if (filters.customerId) {
      q = query(q, where('customerId', '==', filters.customerId));
    }
    
    // Apply ordering
    q = query(q, orderBy('createdAt', 'desc'), limit(size));
    
    const querySnapshot = await getDocs(q);
    const orders = [];
    
    querySnapshot.forEach((doc) => {
      orders.push({
        id: doc.id,
        ...doc.data(),
        createdAt: doc.data().createdAt?.toDate(),
        updatedAt: doc.data().updatedAt?.toDate()
      });
    });
    
    return {
      success: true,
      data: orders
    };
  } catch (error) {
    console.error('Get all orders error:', error);
    return {
      success: false,
      error: 'Failed to fetch orders.'
    };
  }
};

// Update order status
export const updateOrderStatus = async (orderId, newStatus) => {
  try {
    const orderRef = doc(db, 'orders', orderId);
    const orderSnap = await getDoc(orderRef);
    
    if (!orderSnap.exists()) {
      return {
        success: false,
        error: 'Order not found.'
      };
    }
    
    await updateDoc(orderRef, {
      status: newStatus,
      updatedAt: Timestamp.now()
    });
    
    return {
      success: true,
      message: 'Order status updated successfully.'
    };
  } catch (error) {
    console.error('Update order status error:', error);
    return {
      success: false,
      error: 'Failed to update order status.'
    };
  }
};

// Update payment status
export const updatePaymentStatus = async (orderId, newPaymentStatus) => {
  try {
    const orderRef = doc(db, 'orders', orderId);
    const orderSnap = await getDoc(orderRef);
    
    if (!orderSnap.exists()) {
      return {
        success: false,
        error: 'Order not found.'
      };
    }
    
    await updateDoc(orderRef, {
      paymentStatus: newPaymentStatus,
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

// Cancel order
export const cancelOrder = async (orderId) => {
  try {
    const orderRef = doc(db, 'orders', orderId);
    const orderSnap = await getDoc(orderRef);
    
    if (!orderSnap.exists()) {
      return {
        success: false,
        error: 'Order not found.'
      };
    }
    
    const order = orderSnap.data();
    
    // Check if order can be cancelled
    if (order.status === ORDER_STATUS.DELIVERED || order.status === ORDER_STATUS.CANCELLED) {
      return {
        success: false,
        error: 'Order cannot be cancelled.'
      };
    }
    
    // Update order status
    await updateDoc(orderRef, {
      status: ORDER_STATUS.CANCELLED,
      updatedAt: Timestamp.now()
    });
    
    // Restore product stock if order was confirmed
    if (order.status !== ORDER_STATUS.PENDING) {
      const batch = writeBatch(db);
      for (const item of order.items) {
        const productResult = await getProductById(item.productId);
        if (productResult.success) {
          const newStock = productResult.data.stock + item.quantity;
          const productRef = doc(db, 'products', item.productId);
          batch.update(productRef, { 
            stock: newStock,
            updatedAt: Timestamp.now()
          });
        }
      }
      await batch.commit();
    }
    
    return {
      success: true,
      message: 'Order cancelled successfully.'
    };
  } catch (error) {
    console.error('Cancel order error:', error);
    return {
      success: false,
      error: 'Failed to cancel order.'
    };
  }
};

// Get order statistics
export const getOrderStatistics = async (customerId = null) => {
  try {
    let q = collection(db, 'orders');
    
    if (customerId) {
      q = query(q, where('customerId', '==', customerId));
    }
    
    const querySnapshot = await getDocs(q);
    const orders = [];
    
    querySnapshot.forEach((doc) => {
      orders.push(doc.data());
    });
    
    const stats = {
      total: orders.length,
      totalAmount: orders.reduce((sum, order) => sum + order.totalAmount, 0),
      pending: orders.filter(order => order.status === ORDER_STATUS.PENDING).length,
      confirmed: orders.filter(order => order.status === ORDER_STATUS.CONFIRMED).length,
      processing: orders.filter(order => order.status === ORDER_STATUS.PROCESSING).length,
      shipped: orders.filter(order => order.status === ORDER_STATUS.SHIPPED).length,
      delivered: orders.filter(order => order.status === ORDER_STATUS.DELIVERED).length,
      cancelled: orders.filter(order => order.status === ORDER_STATUS.CANCELLED).length
    };
    
    return {
      success: true,
      data: stats
    };
  } catch (error) {
    console.error('Get order statistics error:', error);
    return {
      success: false,
      error: 'Failed to fetch order statistics.'
    };
  }
}; 