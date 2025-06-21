import React, { useState, useEffect } from "react";
import { productService } from "../api";
import "../assets/inventory.css";

const InventoryPage = () => {
  const [inventory, setInventory] = useState([]);
  const [pending, setPending] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showNotification, setShowNotification] = useState(false);

  useEffect(() => {
    loadInventoryData();
  }, []);

  const loadInventoryData = async () => {
    setLoading(true);
    try {
      // Load approved products
      const approvedResult = await productService.getApprovedProducts();
      if (approvedResult.success) {
        setInventory(approvedResult.data);
      } else {
        console.error('Failed to load approved products');
      }

      // Load pending products
      const pendingResult = await productService.getPendingProducts();
      if (pendingResult.success) {
        setPending(pendingResult.data);
        setShowNotification(pendingResult.data.length > 0);
      } else {
        console.error('Failed to load pending products');
      }
    } catch (error) {
      console.error('Error loading inventory data', error);
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (productId) => {
    try {
      // Generate a unique barcode. Example: 'EMART-' + product ID + random string
      const barcodeValue = `EMART-${productId}-${Date.now().toString(36)}`;
      
      const result = await productService.approveProduct(productId, barcodeValue);
      if (result.success) {
        await loadInventoryData();
      } else {
        console.error(result.error || 'Failed to approve product');
      }
    } catch (error) {
      console.error('Error approving product', error);
    }
  };

  const handleReject = async (productId, reason) => {
    try {
      const result = await productService.rejectProduct(productId, reason);
      if (result.success) {
        await loadInventoryData();
      } else {
        console.error(result.error || 'Failed to reject product');
      }
    } catch (error) {
      console.error('Error rejecting product', error);
    }
  };

  const handleUpdateStock = async (productId, quantity) => {
    try {
      const result = await productService.updateStock(productId, quantity);
      if (result.success) {
        await loadInventoryData();
      } else {
        console.error(result.error || 'Failed to update stock');
      }
    } catch (error) {
      console.error('Error updating stock', error);
    }
  };

  if (loading) {
    return (
      <div className="inventory-container">
        <div className="loading-spinner">Loading inventory...</div>
      </div>
    );
  }

  return (
    <div className="inventory-container page-container gradient-background">
      <h2>Inventory Management</h2>
      {showNotification && (
        <div className="inventory-notification">
          <strong>New supplier entries pending approval!</strong>
        </div>
      )}
      
      {pending.length > 0 && (
        <div className="pending-table-section">
          <h3>Pending Supplier Entries</h3>
          <div className="table-container">
            <table className="inventory-table pending-table">
              <thead>
                <tr>
                  <th>Sl. No</th>
                  <th>Product Name</th>
                  <th>Quantity</th>
                  <th>Price</th>
                  <th>Best Before</th>
                  <th>Supplier</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {pending.map((product, idx) => (
                  <tr key={product.id}>
                    <td>{idx + 1}</td>
                    <td>{product.name}</td>
                    <td>{product.quantity}</td>
                    <td>${product.price.toFixed(2)}</td>
                    <td>{new Date(product.bestBefore).toLocaleDateString()}</td>
                    <td>{product.supplierName || 'N/A'}</td>
                    <td>
                      <button 
                        className="approve-btn" 
                        onClick={() => handleApprove(product.id)}
                      >
                        Approve
                      </button>
                      <button 
                        className="reject-btn" 
                        onClick={() => handleReject(product.id, 'Rejected by admin')}
                      >
                        Reject
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
      
      <h3>Approved Products</h3>
      <div className="table-container">
        <table className="inventory-table">
          <thead>
            <tr>
              <th>Sl. No</th>
              <th>Product Name</th>
              <th>Quantity</th>
              <th>Price</th>
              <th>Best Before</th>
              <th>Barcode</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {inventory.map((product, idx) => (
              <tr key={product.id} className="approved-item">
                <td>{idx + 1}</td>
                <td>
                  {product.name}
                  <span className="approved-badge">✓ Approved</span>
                </td>
                <td>
                  <input
                    type="number"
                    value={product.quantity}
                    onChange={(e) => handleUpdateStock(product.id, parseInt(e.target.value))}
                    min="0"
                    className="quantity-input"
                  />
                </td>
                <td>${product.price}</td>
                <td>{new Date(product.bestBefore).toLocaleDateString()}</td>
                <td>
                  {product.barcode ? (
                    <div className="barcode-cell">
                      <span className="barcode-text">{product.barcode}</span>
                      <button 
                        className="view-barcode-btn" 
                        onClick={() => window.open(`/barcode/${product.barcode}`, '_blank')}
                        title="View Barcode"
                      >
                        📊
                      </button>
                    </div>
                  ) : (
                    <span className="no-barcode">No barcode</span>
                  )}
                </td>
                <td>
                  <span className="status-badge approved">
                    Approved
                  </span>
                </td>
                <td>
                  <button 
                    className="edit-btn"
                    onClick={() => {/* Add edit functionality */}}
                  >
                    Edit
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default InventoryPage; 