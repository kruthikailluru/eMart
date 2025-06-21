import React, { useState } from "react";
import "../assets/inventorymovement.css";

const InventoryMovementPage = () => {
  const [item, setItem] = useState("");
  const [quantity, setQuantity] = useState("");
  const [destination, setDestination] = useState("");
  const [movements, setMovements] = useState([]);

  const handleMove = (e) => {
    e.preventDefault();
    setMovements([
      ...movements,
      { item, quantity, destination, date: new Date().toLocaleString() },
    ]);
    setItem("");
    setQuantity("");
    setDestination("");
  };

  return (
    <div className="inventory-movement-container">
      <h2>Inventory Movement</h2>
      <form className="movement-form" onSubmit={handleMove}>
        <input
          type="text"
          placeholder="Item Name"
          value={item}
          onChange={(e) => setItem(e.target.value)}
          required
        />
        <input
          type="number"
          placeholder="Quantity"
          value={quantity}
          onChange={(e) => setQuantity(e.target.value)}
          required
        />
        <input
          type="text"
          placeholder="Destination (e.g., Shop 1)"
          value={destination}
          onChange={(e) => setDestination(e.target.value)}
          required
        />
        <button type="submit">Move Inventory</button>
      </form>
      <h3>Recent Movements</h3>
      <table className="movement-table">
        <thead>
          <tr>
            <th>Item</th>
            <th>Quantity</th>
            <th>Destination</th>
            <th>Date</th>
          </tr>
        </thead>
        <tbody>
          {movements.map((m, idx) => (
            <tr key={idx}>
              <td>{m.item}</td>
              <td>{m.quantity}</td>
              <td>{m.destination}</td>
              <td>{m.date}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default InventoryMovementPage; 