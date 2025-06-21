import { 
  createUserWithEmailAndPassword, 
  signInWithEmailAndPassword, 
  signOut, 
  onAuthStateChanged,
  updateProfile,
  sendPasswordResetEmail
} from 'firebase/auth';
import { doc, setDoc, getDoc, updateDoc } from 'firebase/firestore';
import { auth, db } from './config';

// User roles
export const USER_ROLES = {
  ADMIN: 'ADMIN',
  SUPPLIER: 'SUPPLIER',
  CUSTOMER: 'CUSTOMER'
};

// Register new user
export const registerUser = async (userData) => {
  try {
    const { email, password, firstName, lastName, role = USER_ROLES.CUSTOMER } = userData;
    
    // Create user with Firebase Auth
    const userCredential = await createUserWithEmailAndPassword(auth, email, password);
    const user = userCredential.user;

    // Update profile with display name
    await updateProfile(user, {
      displayName: `${firstName} ${lastName}`
    });

    // Create user document in Firestore
    const userDoc = {
      uid: user.uid,
      email: user.email,
      firstName,
      lastName,
      role,
      createdAt: new Date(),
      isActive: true
    };

    await setDoc(doc(db, 'users', user.uid), userDoc);

    return {
      success: true,
      user: userDoc
    };
  } catch (error) {
    console.error('Registration error:', error);
    let errorMessage = 'Registration failed. Please try again.';
    
    switch (error.code) {
      case 'auth/email-already-in-use':
        errorMessage = 'Email already exists. Please use a different email.';
        break;
      case 'auth/weak-password':
        errorMessage = 'Password is too weak. Please use a stronger password.';
        break;
      case 'auth/invalid-email':
        errorMessage = 'Invalid email address.';
        break;
    }

    return {
      success: false,
      error: errorMessage
    };
  }
};

// Login user
export const loginUser = async (email, password) => {
  try {
    const userCredential = await signInWithEmailAndPassword(auth, email, password);
    const user = userCredential.user;

    // Get user data from Firestore
    const userDoc = await getDoc(doc(db, 'users', user.uid));
    
    if (!userDoc.exists()) {
      // This case should ideally not happen if registration is transactional
      await signOut(auth);
      throw new Error('User data not found in Firestore.');
    }

    const userData = userDoc.data();

    // Store user data in localStorage
    localStorage.setItem('user', JSON.stringify(userData));
    localStorage.setItem('token', user.uid); // Using UID as token

    return {
      success: true,
      user: userData
    };
  } catch (error) {
    let errorMessage = 'Login failed. Please check your credentials.';
    
    switch (error.code) {
      case 'auth/user-not-found':
        errorMessage = 'User not found. Please check your email.';
        break;
      case 'auth/wrong-password':
        errorMessage = 'Incorrect password. Please try again.';
        break;
      case 'auth/invalid-email':
        errorMessage = 'Invalid email address.';
        break;
      case 'auth/user-disabled':
        errorMessage = 'Account has been disabled.';
        break;
    }

    return {
      success: false,
      error: errorMessage
    };
  }
};

// Logout user
export const logoutUser = () => {
  localStorage.removeItem('user');
  localStorage.removeItem('token');
  return signOut(auth);
};

// Get current user
export const getCurrentUser = () => {
  const userStr = localStorage.getItem('user');
  return userStr ? JSON.parse(userStr) : null;
};

// Validate token (check if user exists and is active)
export const validateToken = async () => {
  try {
    const user = getCurrentUser();
    if (!user) {
      return { valid: false };
    }

    const userDoc = await getDoc(doc(db, 'users', user.uid));
    if (!userDoc.exists() || !userDoc.data().isActive) {
      return { valid: false };
    }

    return {
      valid: true,
      user: userDoc.data()
    };
  } catch (error) {
    console.error('Token validation error:', error);
    return { valid: false };
  }
};

// Reset password
export const resetPassword = async (email) => {
  try {
    await sendPasswordResetEmail(auth, email);
    return {
      success: true,
      message: 'Password reset email sent successfully.'
    };
  } catch (error) {
    console.error('Password reset error:', error);
    let errorMessage = 'Failed to send password reset email.';
    
    if (error.code === 'auth/user-not-found') {
      errorMessage = 'No account found with this email address.';
    }

    return {
      success: false,
      error: errorMessage
    };
  }
};

// Update user profile
export const updateUserProfile = async (uid, updates) => {
  try {
    const userRef = doc(db, 'users', uid);
    await updateDoc(userRef, updates);
    
    // Update localStorage
    const currentUser = getCurrentUser();
    if (currentUser && currentUser.uid === uid) {
      const updatedUser = { ...currentUser, ...updates };
      localStorage.setItem('user', JSON.stringify(updatedUser));
    }

    return {
      success: true,
      message: 'Profile updated successfully.'
    };
  } catch (error) {
    console.error('Profile update error:', error);
    return {
      success: false,
      error: 'Failed to update profile.'
    };
  }
};

// Auth state listener
export const onAuthStateChange = (callback) => {
  return onAuthStateChanged(auth, callback);
}; 