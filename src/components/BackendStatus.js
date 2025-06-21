import React, { useState, useEffect } from 'react';
import { db } from '../firebase/config';
import { collection, getDocs, limit, query } from 'firebase/firestore';
import './BackendStatus.css'; // Import the new CSS file

const BackendStatus = () => {
  const [status, setStatus] = useState('checking');
  const [error, setError] = useState('');
  const [isVanishing, setIsVanishing] = useState(false); // To control fade-out

  useEffect(() => {
    const checkFirebaseStatus = async () => {
      try {
        // Test Firebase connection by trying to read from Firestore
        const testQuery = query(collection(db, 'users'), limit(1));
        await getDocs(testQuery);
        setStatus('connected');

        // Set a timer to start the fade-out effect
        setTimeout(() => {
          setIsVanishing(true);
        }, 2500); // Start fading after 2.5s

      } catch (err) {
        console.error('Firebase connection error:', err);
        setStatus('error');
        setError('Failed to connect to Firebase');
      }
    };

    checkFirebaseStatus();
  }, []);

  if (status === 'checking') {
    return (
      <div className="backend-status checking">
        🔄 Checking Firebase connection...
      </div>
    );
  }

  if (status === 'error') {
    return (
      <div className="backend-status error">
        ❌ Firebase Connection Error
        <br />
        <small>{error}</small>
        <br />
        <small>Please check your Firebase configuration and internet connection</small>
      </div>
    );
  }

  if (status === 'connected') {
    // Determine the className based on whether it should be fading out
    const statusClass = `backend-status connected ${isVanishing ? 'fading-out' : ''}`;
    return (
      <div className={statusClass}>
        ✅ Firebase Connected
      </div>
    );
  }

  return null;
};

export default BackendStatus; 