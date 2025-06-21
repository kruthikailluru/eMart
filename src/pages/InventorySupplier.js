import React, { useState, useEffect } from "react";
import { productService } from "../api";
import { useAuth } from "../context/AuthContext";
import "../assets/inventory.css";

const InventorySupplier = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Form state for new product
  const [name, setName] = useState("");
  const [quantity, setQuantity] = useState("");
  const [price, setPrice] = useState("");
  const [bestBefore, setBestBefore] = useState("");

  const { user } = useAuth();

  // Load the supplier's products when the component mounts
  useEffect(() => {
    loadSupplierProducts();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const loadSupplierProducts = async () => {
    setLoading(true);
    const result = await productService.getSupplierProducts();
    if (result.success) {
      setProducts(result.data);
    } else {
      console.error(result.error || 'Failed to load your products.');
    }
    setLoading(false);
  };

  // Handle the form submission to create a new product
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!name || !quantity || !price || !bestBefore) {
      // Maybe show a local error message in the future
      console.error("Please fill out all fields.");
      return;
    }

    const productData = {
      name,
      quantity: parseInt(quantity, 10),
      price: parseFloat(price),
      bestBefore,
      supplierId: user.uid,
      supplierName: user.firstName, // Add supplier name for the admin's view
    };

    // This calls the service that creates the product with a PENDING status
    const result = await productService.createProduct(productData);

    if (result.success) {
      // Clear form and reload the list
      setName("");
      setQuantity("");
      setPrice("");
      setBestBefore("");
      await loadSupplierProducts();
    } else {
      console.error(result.error || "Failed to submit product.");
    }
  };

  if (loading) {
    return <div className="inventory-container">Loading your products...</div>;
  }

  return (
    <div className="page-container login-background">
      <div className="inventory-container">
        <h2>Your Inventory</h2>
        <p>Submit a new product for admin approval. It will appear in the list below with a "PENDING" status.</p>

        <form className="inventory-form" onSubmit={handleSubmit}>
          <input type="text" placeholder="Product Name" value={name} onChange={(e) => setName(e.target.value)} required />
          <input type="number" placeholder="Quantity" value={quantity} onChange={(e) => setQuantity(e.target.value)} required />
          <input type="number" placeholder="Price ($)" value={price} step="0.01" onChange={(e) => setPrice(e.target.value)} required />
          <input type="date" value={bestBefore} onChange={(e) => setBestBefore(e.target.value)} required />
          <button type="submit">Submit for Approval</button>
        </form>

        <h3 className="submissions-title">Your Product Submissions</h3>
        <div className="table-container">
          <table className="inventory-table">
            <thead>
              <tr>
                <th>Product Name</th>
                <th>Quantity</th>
                <th>Price</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {products.map((product) => (
                <tr key={product.id}>
                  <td>{product.name}</td>
                  <td>{product.quantity}</td>
                  <td>${product.price.toFixed(2)}</td>
                  <td>
                    <span className={`status-badge status-${product.status?.toLowerCase()}`}>
                      {product.status}
                    </span>
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

export default InventorySupplier; 