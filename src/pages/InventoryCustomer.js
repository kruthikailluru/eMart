import React, { useState, useEffect } from "react";
import { productService } from "../api";
import "../assets/inventory.css";

// Helper function to calculate days until a date
const getDaysUntil = (dateStr) => {
  const today = new Date();
  today.setHours(0, 0, 0, 0); // Normalize today's date
  const expiryDate = new Date(dateStr);
  const diffTime = expiryDate - today;
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
};

const InventoryCustomer = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [alerts, setAlerts] = useState([]); // Local state for banner alerts

  useEffect(() => {
    const loadAndCheckProducts = async () => {
      setLoading(true);
      try {
        const result = await productService.getAvailableProducts();
        if (result.success) {
          const sortedProducts = result.data.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
          setProducts(sortedProducts);
          
          const newAlerts = [];
          sortedProducts.forEach(product => {
            // Check for low stock
            if (product.quantity < 25) {
              newAlerts.push({ id: `${product.id}-stock`, type: 'warning', message: `${product.name} is selling fast. Only ${product.quantity} left!` });
            }
            // Check for imminent expiry
            const daysLeft = getDaysUntil(product.bestBefore);
            if (daysLeft >= 0 && daysLeft <= 3) {
              newAlerts.push({ id: `${product.id}-expiry`, type: 'error', message: `${product.name} will expire in ${daysLeft} day(s).` });
            }
          });
          setAlerts(newAlerts);

        } else {
          // Handle fetch error
          setAlerts([{ id: 'fetch-error', type: 'error', message: result.error || 'Failed to load available products.' }]);
        }
      } catch (error) {
        console.error("Error loading products:", error);
        setAlerts([{ id: 'critical-error', type: 'error', message: 'A critical error occurred while fetching data.' }]);
      }
      setLoading(false);
    };

    loadAndCheckProducts();
  }, []);

  if (loading) {
    return <div className="inventory-container page-container gradient-background">Loading available products...</div>;
  }

  return (
    <div className="page-container login-background">
        <div className="inventory-container">
            {/* Display Banner Alerts */}
            <div className="alerts-container">
                {alerts.map(alert => (
                <div key={alert.id} className={`alert-banner alert-${alert.type}`}>
                    {alert.message}
                </div>
                ))}
            </div>

            <h2>Available Products</h2>
            <p>Browse all products that have been approved and are ready for purchase.</p>
            
            <div className="table-container">
                <table className="inventory-table">
                <thead>
                    <tr>
                    <th>Product Name</th>
                    <th>Price</th>
                    <th>Quantity</th>
                    <th>Supplier</th>
                    <th>Best Before</th>
                    <th>Barcode</th>
                    </tr>
                </thead>
                <tbody>
                    {products.map((product) => (
                    <tr key={product.id}>
                        <td>{product.name}</td>
                        <td>${product.price.toFixed(2)}</td>
                        <td>{product.quantity}</td>
                        <td>{product.supplierName || 'eMart'}</td>
                        <td>{new Date(product.bestBefore).toLocaleDateString()}</td>
                        <td className="barcode-cell">
                        <span className="barcode-text">{product.barcode || 'N/A'}</span>
                        </td>
                    </tr>
                    ))}
                </tbody>
                </table>
            </div>
        </div>
    </div>
  );
};

export default InventoryCustomer; 