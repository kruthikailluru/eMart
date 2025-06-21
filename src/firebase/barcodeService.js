import { searchProducts } from './productService';

// Generate barcode for product
export const generateBarcode = (productId) => {
  // Simple barcode generation - you might want to use a proper barcode library
  // For now, we'll use the product ID as the barcode
  return productId;
};

// Search product by barcode
export const searchProductByBarcode = async (barcode) => {
  try {
    // Search for products with this barcode
    const result = await searchProducts(barcode);
    
    if (result.success && result.data.length > 0) {
      // Find exact barcode match
      const product = result.data.find(p => p.barcode === barcode);
      if (product) {
        return {
          success: true,
          data: product
        };
      }
    }
    
    return {
      success: false,
      error: 'Product not found for this barcode.'
    };
  } catch (error) {
    console.error('Barcode search error:', error);
    return {
      success: false,
      error: 'Failed to search product by barcode.'
    };
  }
};

// Validate barcode format
export const validateBarcode = (barcode) => {
  if (!barcode || barcode.trim() === '') {
    return {
      valid: false,
      error: 'Barcode cannot be empty.'
    };
  }
  
  // Basic validation - you might want to add more specific validation
  if (barcode.length < 3) {
    return {
      valid: false,
      error: 'Barcode must be at least 3 characters long.'
    };
  }
  
  return {
    valid: true
  };
}; 