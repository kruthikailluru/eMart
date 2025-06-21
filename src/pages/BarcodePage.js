import React, { useState, useEffect, useRef } from "react";
import JsBarcode from "jsbarcode";
import "../assets/barcode.css";

const BarcodePage = () => {
  const [item, setItem] = useState("");
  const [barcode, setBarcode] = useState("");
  const [productBarcodes, setProductBarcodes] = useState([]);
  const barcodeRef = useRef(null);

  useEffect(() => {
    // Load approved inventory with barcodes
    const approvedInventory = JSON.parse(localStorage.getItem("approvedInventory") || "[]");
    const itemsWithBarcodes = approvedInventory.filter(item => item.barcode);
    setProductBarcodes(itemsWithBarcodes);
  }, []);

  useEffect(() => {
    // Generate barcode when barcode value changes
    if (barcode && barcodeRef.current) {
      try {
        JsBarcode(barcodeRef.current, barcode, {
          format: "CODE128",
          width: 2,
          height: 100,
          displayValue: true,
          fontSize: 16,
          margin: 10
        });
      } catch (error) {
        console.error("Barcode generation error:", error);
      }
    }
  }, [barcode]);

  const handleGenerate = (e) => {
    e.preventDefault();
    if (item.trim()) {
      // Generate unique barcode
      const timestamp = Date.now().toString().slice(-6);
      const productCode = item.replace(/\s+/g, '').toUpperCase().slice(0, 3);
      const randomNum = Math.floor(Math.random() * 1000).toString().padStart(3, '0');
      const generatedBarcode = `${productCode}${timestamp}${randomNum}`;
      setBarcode(generatedBarcode);
    }
  };

  const handlePrint = () => {
    if (barcodeRef.current) {
      const printWindow = window.open('', '_blank');
      printWindow.document.write(`
        <html>
          <head>
            <title>Barcode Print</title>
            <style>
              body { font-family: Arial, sans-serif; text-align: center; padding: 20px; }
              .barcode-container { margin: 20px; }
              @media print { body { margin: 0; } }
            </style>
          </head>
          <body>
            <div class="barcode-container">
              <h3>Product Barcode</h3>
              <div>${barcodeRef.current.outerHTML}</div>
              <p>Barcode: ${barcode}</p>
            </div>
          </body>
        </html>
      `);
      printWindow.document.close();
      printWindow.print();
    }
  };

  return (
    <div className="barcode-container">
      <h2>Barcode Generation</h2>
      
      {/* Generate New Barcode */}
      <div className="barcode-section">
        <h3>Generate New Barcode</h3>
        <form className="barcode-form" onSubmit={handleGenerate}>
          <input
            type="text"
            placeholder="Item Name or Code"
            value={item}
            onChange={(e) => setItem(e.target.value)}
            required
          />
          <button type="submit">Generate Barcode</button>
        </form>
        {barcode && (
          <div className="barcode-display">
            <svg ref={barcodeRef}></svg>
            <div className="barcode-actions">
              <button onClick={handlePrint} className="print-btn">Print Barcode</button>
              <button onClick={() => setBarcode("")} className="clear-btn">Clear</button>
            </div>
          </div>
        )}
      </div>

      {/* View Existing Product Barcodes */}
      {productBarcodes.length > 0 && (
        <div className="product-barcodes-section">
          <h3>Product Barcodes</h3>
          <div className="product-barcodes-grid">
            {productBarcodes.map((product, index) => (
              <div key={index} className="product-barcode-item">
                <h4>{product.name}</h4>
                <p>Quantity: {product.quantity}</p>
                <p>Barcode: {product.barcode}</p>
                <button 
                  onClick={() => setBarcode(product.barcode)}
                  className="view-barcode-btn"
                >
                  View Barcode
                </button>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default BarcodePage; 