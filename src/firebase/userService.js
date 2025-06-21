import { 
  collection, 
  doc, 
  getDocs, 
  getDoc, 
  updateDoc, 
  query, 
  where, 
  orderBy, 
  limit,
  Timestamp
} from 'firebase/firestore';
import { db } from './config';
import { USER_ROLES } from './authService';

// Get all users (admin only)
export const getAllUsers = async (page = 1, size = 10, filters = {}) => {
  try {
    let q = collection(db, 'users');
    
    // Apply filters
    if (filters.role) {
      q = query(q, where('role', '==', filters.role));
    }
    if (filters.isActive !== undefined) {
      q = query(q, where('isActive', '==', filters.isActive));
    }
    
    // Apply ordering
    q = query(q, orderBy('createdAt', 'desc'), limit(size));
    
    const querySnapshot = await getDocs(q);
    const users = [];
    
    querySnapshot.forEach((doc) => {
      users.push({
        id: doc.id,
        ...doc.data(),
        createdAt: doc.data().createdAt?.toDate(),
        updatedAt: doc.data().updatedAt?.toDate()
      });
    });
    
    return {
      success: true,
      data: users
    };
  } catch (error) {
    console.error('Get users error:', error);
    return {
      success: false,
      error: 'Failed to fetch users.'
    };
  }
};

// Get user by ID
export const getUserById = async (userId) => {
  try {
    const docRef = doc(db, 'users', userId);
    const docSnap = await getDoc(docRef);
    
    if (docSnap.exists()) {
      const user = {
        id: docSnap.id,
        ...docSnap.data(),
        createdAt: docSnap.data().createdAt?.toDate(),
        updatedAt: docSnap.data().updatedAt?.toDate()
      };
      
      return {
        success: true,
        data: user
      };
    } else {
      return {
        success: false,
        error: 'User not found.'
      };
    }
  } catch (error) {
    console.error('Get user error:', error);
    return {
      success: false,
      error: 'Failed to fetch user.'
    };
  }
};

// Update user profile
export const updateUser = async (userId, updates) => {
  try {
    const userRef = doc(db, 'users', userId);
    const userSnap = await getDoc(userRef);
    
    if (!userSnap.exists()) {
      return {
        success: false,
        error: 'User not found.'
      };
    }
    
    const updateData = {
      ...updates,
      updatedAt: Timestamp.now()
    };
    
    await updateDoc(userRef, updateData);
    
    return {
      success: true,
      message: 'User updated successfully.'
    };
  } catch (error) {
    console.error('Update user error:', error);
    return {
      success: false,
      error: 'Failed to update user.'
    };
  }
};

// Get users by role
export const getUsersByRole = async (role) => {
  return getAllUsers(1, 100, { role });
};

// Get suppliers
export const getSuppliers = async () => {
  return getUsersByRole(USER_ROLES.SUPPLIER);
};

// Get customers
export const getCustomers = async () => {
  return getUsersByRole(USER_ROLES.CUSTOMER);
};

// Get admins
export const getAdmins = async () => {
  return getUsersByRole(USER_ROLES.ADMIN);
};

// Deactivate user
export const deactivateUser = async (userId) => {
  return updateUser(userId, { isActive: false });
};

// Activate user
export const activateUser = async (userId) => {
  return updateUser(userId, { isActive: true });
};

// Change user role
export const changeUserRole = async (userId, newRole) => {
  if (!Object.values(USER_ROLES).includes(newRole)) {
    return {
      success: false,
      error: 'Invalid role.'
    };
  }
  
  return updateUser(userId, { role: newRole });
};

// Get user statistics
export const getUserStatistics = async () => {
  try {
    const [suppliers, customers, admins] = await Promise.all([
      getSuppliers(),
      getCustomers(),
      getAdmins()
    ]);
    
    const stats = {
      total: (suppliers.success ? suppliers.data.length : 0) + 
             (customers.success ? customers.data.length : 0) + 
             (admins.success ? admins.data.length : 0),
      suppliers: suppliers.success ? suppliers.data.length : 0,
      customers: customers.success ? customers.data.length : 0,
      admins: admins.success ? admins.data.length : 0
    };
    
    return {
      success: true,
      data: stats
    };
  } catch (error) {
    console.error('Get user statistics error:', error);
    return {
      success: false,
      error: 'Failed to fetch user statistics.'
    };
  }
}; 