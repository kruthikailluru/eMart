import { 
  getProducts, 
  getProductById, 
  createProduct, 
  updateProduct, 
  deleteProduct, 
  searchProducts,
  getProductsBySupplier,
  updateProductStock,
  PRODUCT_STATUS
} from '../firebase/productService';

export const productService = {
  // Get all products (for admin/supplier)
  getAllProducts: async () => {
    return await getProducts(1, 100);
  },

  // Get available products (for customers)
  getAvailableProducts: async () => {
    return await getProducts(1, 100, { status: PRODUCT_STATUS.ACTIVE });
  },

  // Get approved products
  getApprovedProducts: async () => {
    return await getProducts(1, 100, { status: PRODUCT_STATUS.ACTIVE });
  },

  // Get pending products (admin only)
  getPendingProducts: async () => {
    return await getProducts(1, 100, { status: PRODUCT_STATUS.PENDING });
  },

  // Get product by ID
  getProductById: async (productId) => {
    return await getProductById(productId);
  },

  // Create new product
  createProduct: async (productData) => {
    return await createProduct(productData);
  },

  // Update product
  updateProduct: async (productId, productData) => {
    return await updateProduct(productId, productData);
  },

  // Delete product
  deleteProduct: async (productId) => {
    return await deleteProduct(productId);
  },

  // Approve product (admin only)
  approveProduct: async (productId, barcode) => {
    return await updateProduct(productId, { 
      status: PRODUCT_STATUS.ACTIVE,
      barcode: barcode,
      approvedAt: new Date(),
    });
  },

  // Reject product (admin only)
  rejectProduct: async (productId, reason) => {
    return await updateProduct(productId, { 
      status: PRODUCT_STATUS.REJECTED,
      rejectionReason: reason 
    });
  },

  // Update stock (admin only)
  updateStock: async (productId, quantity) => {
    return await updateProductStock(productId, quantity);
  },

  // Search products
  searchProducts: async (query, filters = {}) => {
    return await searchProducts(query, filters);
  },

  // Get products by price range
  getProductsByPriceRange: async (minPrice, maxPrice) => {
    return await searchProducts('', { minPrice, maxPrice });
  },

  // Get product by barcode
  getProductByBarcode: async (barcode) => {
    // For now, we'll search by barcode in the search function
    // You might want to create a separate index for barcodes
    return await searchProducts(barcode);
  },

  // Get supplier products
  getSupplierProducts: async () => {
    const currentUser = JSON.parse(localStorage.getItem('user') || '{}');
    return await getProductsBySupplier(currentUser.uid);
  },

  // Get low stock products (admin only)
  getLowStockProducts: async () => {
    // Get all products and filter for low stock on client side
    const result = await getProducts(1, 1000);
    if (result.success) {
      const lowStockProducts = result.data.filter(product => product.stock < 10);
      return {
        success: true,
        data: lowStockProducts
      };
    }
    return result;
  },

  // Get expired products (admin only)
  getExpiredProducts: async () => {
    // Get all products and filter for expired products on client side
    const result = await getProducts(1, 1000);
    if (result.success) {
      const currentDate = new Date();
      const expiredProducts = result.data.filter(product => {
        if (product.expiryDate) {
          return new Date(product.expiryDate) < currentDate;
        }
        return false;
      });
      return {
        success: true,
        data: expiredProducts
      };
    }
    return result;
  }
}; 