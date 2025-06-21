import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';
import { getFirestore } from 'firebase/firestore';
import { getStorage } from 'firebase/storage';

// Your Firebase configuration
const firebaseConfig = {
  apiKey: "AIzaSyCmuC81M3czagowphT6Gji5tmg34Sgri4Y",
  authDomain: "emart-33a63.firebaseapp.com",
  projectId: "emart-33a63",
  storageBucket: "emart-33a63.firebasestorage.app",
  messagingSenderId: "641495488506",
  appId: "1:641495488506:web:6a78e014e45e757ddcb643",
  measurementId: "G-65RV390N7M"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);

// Initialize Firebase services
export const auth = getAuth(app);
export const db = getFirestore(app);
export const storage = getStorage(app);

export default app; 