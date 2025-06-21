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

// Invoice status
export const INVOICE_STATUS = {
  DRAFT: 'DRAFT',
  SENT: 'SENT',
  PAID: 'PAID',
  OVERDUE: 'OVERDUE',
  CANCELLED: 'CANCELLED'
};

// Create invoice
export const createInvoice = async (invoiceData) => {
  try {
    const invoice = {
      ...invoiceData,
      status: INVOICE_STATUS.DRAFT,
      createdAt: Timestamp.now(),
      updatedAt: Timestamp.now()
    };
    
    const docRef = await addDoc(collection(db, 'invoices'), invoice);
    
    return {
      success: true,
      data: {
        id: docRef.id,
        ...invoice,
        createdAt: invoice.createdAt.toDate(),
        updatedAt: invoice.updatedAt.toDate()
      }
    };
  } catch (error) {
    console.error('Create invoice error:', error);
    return {
      success: false,
      error: 'Failed to create invoice.'
    };
  }
};

// Get invoice by ID
export const getInvoiceById = async (invoiceId) => {
  try {
    const docRef = doc(db, 'invoices', invoiceId);
    const docSnap = await getDoc(docRef);
    
    if (docSnap.exists()) {
      const invoice = {
        id: docSnap.id,
        ...docSnap.data(),
        createdAt: docSnap.data().createdAt?.toDate(),
        updatedAt: docSnap.data().updatedAt?.toDate()
      };
      
      return {
        success: true,
        data: invoice
      };
    } else {
      return {
        success: false,
        error: 'Invoice not found.'
      };
    }
  } catch (error) {
    console.error('Get invoice error:', error);
    return {
      success: false,
      error: 'Failed to fetch invoice.'
    };
  }
};

// Get invoices by customer
export const getInvoicesByCustomer = async (customerId) => {
  try {
    let q = query(
      collection(db, 'invoices'),
      where('customerId', '==', customerId),
      orderBy('createdAt', 'desc')
    );
    
    const querySnapshot = await getDocs(q);
    const invoices = [];
    
    querySnapshot.forEach((doc) => {
      invoices.push({
        id: doc.id,
        ...doc.data(),
        createdAt: doc.data().createdAt?.toDate(),
        updatedAt: doc.data().updatedAt?.toDate()
      });
    });
    
    return {
      success: true,
      data: invoices
    };
  } catch (error) {
    console.error('Get invoices by customer error:', error);
    return {
      success: false,
      error: 'Failed to fetch invoices.'
    };
  }
};

// Get all invoices (admin only)
export const getAllInvoices = async (page = 1, size = 10, filters = {}) => {
  try {
    let q = collection(db, 'invoices');
    
    // Apply filters
    if (filters.status) {
      q = query(q, where('status', '==', filters.status));
    }
    if (filters.customerId) {
      q = query(q, where('customerId', '==', filters.customerId));
    }
    
    // Apply ordering
    q = query(q, orderBy('createdAt', 'desc'), limit(size));
    
    const querySnapshot = await getDocs(q);
    const invoices = [];
    
    querySnapshot.forEach((doc) => {
      invoices.push({
        id: doc.id,
        ...doc.data(),
        createdAt: doc.data().createdAt?.toDate(),
        updatedAt: doc.data().updatedAt?.toDate()
      });
    });
    
    return {
      success: true,
      data: invoices
    };
  } catch (error) {
    console.error('Get all invoices error:', error);
    return {
      success: false,
      error: 'Failed to fetch invoices.'
    };
  }
};

// Update invoice status
export const updateInvoiceStatus = async (invoiceId, status) => {
  try {
    const invoiceRef = doc(db, 'invoices', invoiceId);
    const invoiceSnap = await getDoc(invoiceRef);
    
    if (!invoiceSnap.exists()) {
      return {
        success: false,
        error: 'Invoice not found.'
      };
    }
    
    await updateDoc(invoiceRef, {
      status,
      updatedAt: Timestamp.now()
    });
    
    return {
      success: true,
      message: 'Invoice status updated successfully.'
    };
  } catch (error) {
    console.error('Update invoice status error:', error);
    return {
      success: false,
      error: 'Failed to update invoice status.'
    };
  }
};

// Send invoice
export const sendInvoice = async (invoiceId) => {
  return await updateInvoiceStatus(invoiceId, INVOICE_STATUS.SENT);
};

// Mark invoice as paid
export const markInvoiceAsPaid = async (invoiceId) => {
  return await updateInvoiceStatus(invoiceId, INVOICE_STATUS.PAID);
};

// Cancel invoice
export const cancelInvoice = async (invoiceId) => {
  return await updateInvoiceStatus(invoiceId, INVOICE_STATUS.CANCELLED);
};

// Get invoice statistics
export const getInvoiceStatistics = async (customerId = null) => {
  try {
    let q = collection(db, 'invoices');
    
    if (customerId) {
      q = query(q, where('customerId', '==', customerId));
    }
    
    const querySnapshot = await getDocs(q);
    const invoices = [];
    
    querySnapshot.forEach((doc) => {
      invoices.push(doc.data());
    });
    
    const stats = {
      total: invoices.length,
      totalAmount: invoices.reduce((sum, invoice) => sum + invoice.totalAmount, 0),
      draft: invoices.filter(invoice => invoice.status === INVOICE_STATUS.DRAFT).length,
      sent: invoices.filter(invoice => invoice.status === INVOICE_STATUS.SENT).length,
      paid: invoices.filter(invoice => invoice.status === INVOICE_STATUS.PAID).length,
      overdue: invoices.filter(invoice => invoice.status === INVOICE_STATUS.OVERDUE).length,
      cancelled: invoices.filter(invoice => invoice.status === INVOICE_STATUS.CANCELLED).length
    };
    
    return {
      success: true,
      data: stats
    };
  } catch (error) {
    console.error('Get invoice statistics error:', error);
    return {
      success: false,
      error: 'Failed to fetch invoice statistics.'
    };
  }
}; 