import { 
  getInvoiceById, 
  getInvoicesByCustomer, 
  getAllInvoices, 
  createInvoice, 
  updateInvoiceStatus, 
  sendInvoice, 
  markInvoiceAsPaid, 
  cancelInvoice,
  getInvoiceStatistics,
  INVOICE_STATUS
} from '../firebase/invoiceService';

export const invoiceService = {
  // Get customer invoices
  getCustomerInvoices: async () => {
    const currentUser = JSON.parse(localStorage.getItem('user') || '{}');
    return await getInvoicesByCustomer(currentUser.uid);
  },

  // Get invoice by ID
  getInvoiceById: async (invoiceId) => {
    return await getInvoiceById(invoiceId);
  },

  // Generate invoice
  generateInvoice: async (orderId) => {
    // For now, create a simple invoice
    // You might want to fetch order details and create a proper invoice
    const currentUser = JSON.parse(localStorage.getItem('user') || '{}');
    return await createInvoice({
      orderId,
      customerId: currentUser.uid,
      totalAmount: 0, // You would calculate this from order
      items: [] // You would get this from order
    });
  },

  // Sign invoice (admin only)
  signInvoice: async (invoiceId) => {
    // For now, just update status
    return await updateInvoiceStatus(invoiceId, INVOICE_STATUS.SENT);
  },

  // Update invoice status (admin only)
  updateInvoiceStatus: async (invoiceId, status) => {
    return await updateInvoiceStatus(invoiceId, status);
  },

  // Send invoice (admin only)
  sendInvoice: async (invoiceId) => {
    return await sendInvoice(invoiceId);
  },

  // Get overdue invoices (admin only)
  getOverdueInvoices: async () => {
    return await getAllInvoices(1, 100, { status: INVOICE_STATUS.OVERDUE });
  },

  // Get revenue statistics (admin only)
  getRevenueStats: async (period = 'monthly') => {
    const stats = await getInvoiceStatistics();
    return {
      success: true,
      data: {
        totalRevenue: stats.data?.totalAmount || 0,
        totalInvoices: stats.data?.total || 0
      }
    };
  },

  // Get invoice summary (admin only)
  getInvoiceSummary: async () => {
    return await getInvoiceStatistics();
  }
}; 