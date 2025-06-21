import React from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "../assets/dashboard.css";

const users = [
  {
    role: "creative leader",
    name: "Admin",
    description: "Manage inventory, users, and view system alerts.",
    img: process.env.PUBLIC_URL + "/avatar.png",
    path: "/login",
    socials: [
      { icon: "fab fa-facebook-f", url: "#" },
      { icon: "fab fa-twitter", url: "#" },
      { icon: "fab fa-instagram", url: "#" },
      { icon: "fab fa-linkedin-in", url: "#" }
    ]
  },
  {
    role: "creative leader",
    name: "Customer",
    description: "Browse products, place orders, and view your order history.",
    img: process.env.PUBLIC_URL + "/avatar.png",
    path: "/inventory-customer",
    socials: [
      { icon: "fab fa-facebook-f", url: "#" },
      { icon: "fab fa-twitter", url: "#" },
      { icon: "fab fa-instagram", url: "#" },
      { icon: "fab fa-linkedin-in", url: "#" }
    ]
  },
  {
    role: "programming guru",
    name: "Supplier",
    description: "Manage your products, view orders, and update inventory.",
    img: process.env.PUBLIC_URL + "/avatar.png",
    path: "/supplier-login",
    socials: [
      { icon: "fab fa-facebook-f", url: "#" },
      { icon: "fab fa-twitter", url: "#" },
      { icon: "fab fa-instagram", url: "#" },
      { icon: "fab fa-linkedin-in", url: "#" }
    ]
  }
];

const Dashboard = () => {
  const navigate = useNavigate();
  const { authenticated, user, logout } = useAuth();

  if (authenticated) {
    return (
      <div className="dashboard-container gradient-background">
        <h1>Welcome to the Dashboard, {user?.firstName}!</h1>
        <p>You are logged in as a {user?.role}.</p>
        <p>From here, you can navigate to the different sections of the application.</p>
        
        <div className="dashboard-actions">
          {user?.role === 'ADMIN' && <button onClick={() => navigate('/inventory')}>Manage Inventory</button>}
          {user?.role === 'SUPPLIER' && <button onClick={() => navigate('/inventory-supplier')}>Supplier Inventory</button>}
          {user?.role === 'CUSTOMER' && <button onClick={() => navigate('/inventory-customer')}>Browse Products</button>}
          <button onClick={logout}>Logout</button>
        </div>
      </div>
    );
  }

  return (
    <div className="dashboard-container public-dashboard gradient-background">
        <h1>Welcome to eMart</h1>
        <p>Your one-stop solution for inventory and order management.</p>
        <div className="dashboard-actions">
          <button onClick={() => navigate('/login')}>Login to Get Started</button>
        </div>
    </div>
  );
};

export default Dashboard;
