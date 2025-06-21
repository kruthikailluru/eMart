import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../assets/login.css";

const Toast = ({ message, onClose }) => {
  useEffect(() => {
    if (message) {
      const timer = setTimeout(onClose, 3000);
      return () => clearTimeout(timer);
    }
  }, [message, onClose]);

  if (!message) return null;
  return <div className="toast-notification">{message}</div>;
};

const SupplierLoginPage = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [toast, setToast] = useState("");
  const navigate = useNavigate();

  const validateUsername = (username) => /[a-zA-Z]/.test(username);
  const validatePassword = (password) =>
    password.length >= 6 && /[a-zA-Z]/.test(password) && /[0-9]/.test(password);

  const handleLogin = (e) => {
    e.preventDefault();
    if (!validateUsername(username)) {
      setToast("Username must contain at least one alphabet.");
      return;
    }
    if (!validatePassword(password)) {
      setToast(
        "Password must be at least 6 characters long and contain at least one alphabet and one number."
      );
      return;
    }
    // Simple supplier authentication
    if (username === "supplier" && password === "supplier123") {
      setToast("");
      navigate("/inventory-supplier");
    } else {
      setToast("Invalid credentials. Please try again.");
    }
  };

  return (
    <div className="login-container page-container gradient-background">
      <Toast message={toast} onClose={() => setToast("")} />
      <form className="login-form" onSubmit={handleLogin}>
        <h2>Supplier Login</h2>
        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit">Login</button>
      </form>
    </div>
  );
};

export default SupplierLoginPage; 