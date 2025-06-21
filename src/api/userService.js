import { 
  getAllUsers, 
  getUserById, 
  updateUser, 
  changeUserRole, 
  getUserStatistics 
} from '../firebase/userService';

export const userService = {
  // Get all users (admin only)
  getAllUsers: async () => {
    return await getAllUsers(1, 100);
  },

  // Get user by ID
  getUserById: async (userId) => {
    return await getUserById(userId);
  },

  // Update user
  updateUser: async (userId, userData) => {
    return await updateUser(userId, userData);
  },

  // Delete user (not implemented in Firebase - you might want to deactivate instead)
  deleteUser: async (userId) => {
    // For now, we'll deactivate the user instead of deleting
    return await updateUser(userId, { isActive: false });
  },

  // Update user role
  updateUserRole: async (userId, role) => {
    return await changeUserRole(userId, role);
  },

  // Get user statistics
  getUserStats: async () => {
    return await getUserStatistics();
  }
}; 