import { 
  searchProductByBarcode, 
  generateBarcode, 
  validateBarcode 
} from '../firebase/barcodeService';

export const barcodeService = {
  // Get barcode information
  getBarcodeInfo: async (barcode) => {
    return await searchProductByBarcode(barcode);
  },

  // Generate barcode
  generateBarcode: async (data, format = 'CODE_128') => {
    // For now, we'll use a simple barcode generation
    // You might want to integrate with a proper barcode library
    const barcode = generateBarcode(data);
    return { 
      success: true, 
      data: { 
        barcode,
        format,
        data 
      } 
    };
  },

  // Validate barcode
  validateBarcode: (barcode) => {
    return validateBarcode(barcode);
  }
}; 