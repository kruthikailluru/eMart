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
  startAfter,
  Timestamp
} from 'firebase/firestore';
import { ref, uploadBytes, getDownloadURL, deleteObject } from 'firebase/storage';
import { db, storage } from './config';

// Product status
export const PRODUCT_STATUS = {
  ACTIVE: 'ACTIVE',
  INACTIVE: 'INACTIVE',
  PENDING: 'PENDING',
  REJECTED: 'REJECTED',
  OUT_OF_STOCK: 'OUT_OF_STOCK'
};

// Get all products with pagination
export const getProducts = async (page = 1, size = 10, filters = {}) => {
  try {
    let q = collection(db, 'products');
    
    // Apply filters
    if (filters.status) {
      q = query(q, where('status', '==', filters.status));
    }
    if (filters.category) {
      q = query(q, where('category', '==', filters.category));
    }
    if (filters.supplierId) {
      q = query(q, where('supplierId', '==', filters.supplierId));
    }
    
    // Apply ordering
    q = query(q, orderBy('createdAt', 'desc'));
    
    // Apply pagination
    const offset = (page - 1) * size;
    if (offset > 0) {
      // For pagination, you might need to implement cursor-based pagination
      // This is a simplified version
    }
    q = query(q, limit(size));
    
    const querySnapshot = await getDocs(q);
    const products = [];
    
    querySnapshot.forEach((doc) => {
      products.push({
        id: doc.id,
        ...doc.data(),
        createdAt: doc.data().createdAt?.toDate(),
        updatedAt: doc.data().updatedAt?.toDate()
      });
    });
    
    return {
      success: true,
      data: products,
      total: products.length // In a real app, you'd get total count separately
    };
  } catch (error) {
    console.error('Get products error:', error);
    return {
      success: false,
      error: 'Failed to fetch products.'
    };
  }
};

// Get product by ID
export const getProductById = async (productId) => {
  try {
    const docRef = doc(db, 'products', productId);
    const docSnap = await getDoc(docRef);
    
    if (docSnap.exists()) {
      const product = {
        id: docSnap.id,
        ...docSnap.data(),
        createdAt: docSnap.data().createdAt?.toDate(),
        updatedAt: docSnap.data().updatedAt?.toDate()
      };
      
      return {
        success: true,
        data: product
      };
    } else {
      return {
        success: false,
        error: 'Product not found.'
      };
    }
  } catch (error) {
    console.error('Get product error:', error);
    return {
      success: false,
      error: 'Failed to fetch product.'
    };
  }
};

// Create new product
export const createProduct = async (productData, imageFile = null) => {
  try {
    let imageUrl = null;
    
    // Upload image if provided
    if (imageFile) {
      const imageRef = ref(storage, `products/${Date.now()}_${imageFile.name}`);
      const snapshot = await uploadBytes(imageRef, imageFile);
      imageUrl = await getDownloadURL(snapshot.ref);
    }
    
    const product = {
      ...productData,
      imageUrl,
      status: PRODUCT_STATUS.PENDING,
      createdAt: Timestamp.now(),
      updatedAt: Timestamp.now()
    };
    
    const docRef = await addDoc(collection(db, 'products'), product);
    
    return {
      success: true,
      data: {
        id: docRef.id,
        ...product,
        createdAt: product.createdAt.toDate(),
        updatedAt: product.updatedAt.toDate()
      }
    };
  } catch (error) {
    console.error('Create product error:', error);
    return {
      success: false,
      error: 'Failed to create product.'
    };
  }
};

// Update product
export const updateProduct = async (productId, updates, imageFile = null) => {
  try {
    const productRef = doc(db, 'products', productId);
    const productSnap = await getDoc(productRef);
    
    if (!productSnap.exists()) {
      return {
        success: false,
        error: 'Product not found.'
      };
    }
    
    const currentProduct = productSnap.data();
    let imageUrl = currentProduct.imageUrl;
    
    // Upload new image if provided
    if (imageFile) {
      // Delete old image if exists
      if (currentProduct.imageUrl) {
        try {
          const oldImageRef = ref(storage, currentProduct.imageUrl);
          await deleteObject(oldImageRef);
        } catch (error) {
          console.warn('Failed to delete old image:', error);
        }
      }
      
      const imageRef = ref(storage, `products/${Date.now()}_${imageFile.name}`);
      const snapshot = await uploadBytes(imageRef, imageFile);
      imageUrl = await getDownloadURL(snapshot.ref);
    }
    
    const updateData = {
      ...updates,
      imageUrl,
      updatedAt: Timestamp.now()
    };
    
    await updateDoc(productRef, updateData);
    
    return {
      success: true,
      message: 'Product updated successfully.'
    };
  } catch (error) {
    console.error('Update product error:', error);
    return {
      success: false,
      error: 'Failed to update product.'
    };
  }
};

// Delete product
export const deleteProduct = async (productId) => {
  try {
    const productRef = doc(db, 'products', productId);
    const productSnap = await getDoc(productRef);
    
    if (!productSnap.exists()) {
      return {
        success: false,
        error: 'Product not found.'
      };
    }
    
    const product = productSnap.data();
    
    // Delete image if exists
    if (product.imageUrl) {
      try {
        const imageRef = ref(storage, product.imageUrl);
        await deleteObject(imageRef);
      } catch (error) {
        console.warn('Failed to delete product image:', error);
      }
    }
    
    await deleteDoc(productRef);
    
    return {
      success: true,
      message: 'Product deleted successfully.'
    };
  } catch (error) {
    console.error('Delete product error:', error);
    return {
      success: false,
      error: 'Failed to delete product.'
    };
  }
};

// Search products
export const searchProducts = async (searchTerm, filters = {}) => {
  try {
    let q = collection(db, 'products');
    
    // Apply search filter (Firestore doesn't support full-text search natively)
    // This is a simplified implementation - you might want to use Algolia or similar
    if (searchTerm) {
      // For now, we'll search by name containing the term
      // In a real app, you'd implement proper search
    }
    
    // Apply other filters
    if (filters.status) {
      q = query(q, where('status', '==', filters.status));
    }
    if (filters.category) {
      q = query(q, where('category', '==', filters.category));
    }
    if (filters.minPrice !== undefined) {
      q = query(q, where('price', '>=', filters.minPrice));
    }
    if (filters.maxPrice !== undefined) {
      q = query(q, where('price', '<=', filters.maxPrice));
    }
    
    const querySnapshot = await getDocs(q);
    const products = [];
    
    querySnapshot.forEach((doc) => {
      const product = doc.data();
      // Simple client-side search
      if (!searchTerm || 
          product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
          product.description.toLowerCase().includes(searchTerm.toLowerCase())) {
        products.push({
          id: doc.id,
          ...product,
          createdAt: product.createdAt?.toDate(),
          updatedAt: product.updatedAt?.toDate()
        });
      }
    });
    
    return {
      success: true,
      data: products
    };
  } catch (error) {
    console.error('Search products error:', error);
    return {
      success: false,
      error: 'Failed to search products.'
    };
  }
};

// Get products by supplier
export const getProductsBySupplier = async (supplierId) => {
  return getProducts(1, 100, { supplierId });
};

// Update product stock
export const updateProductStock = async (productId, newStock) => {
  try {
    const productRef = doc(db, 'products', productId);
    const productSnap = await getDoc(productRef);
    
    if (!productSnap.exists()) {
      return {
        success: false,
        error: 'Product not found.'
      };
    }
    
    const product = productSnap.data();
    const status = newStock <= 0 ? PRODUCT_STATUS.OUT_OF_STOCK : PRODUCT_STATUS.ACTIVE;
    
    await updateDoc(productRef, {
      stock: newStock,
      status,
      updatedAt: Timestamp.now()
    });
    
    return {
      success: true,
      message: 'Product stock updated successfully.'
    };
  } catch (error) {
    console.error('Update stock error:', error);
    return {
      success: false,
      error: 'Failed to update product stock.'
    };
  }
}; 