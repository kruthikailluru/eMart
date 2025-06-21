import React, { useState } from "react";
import "../assets/rulescategory.css";

const RulesCategoryPage = () => {
  const [categories, setCategories] = useState(["Electronics", "Groceries"]);
  const [rules, setRules] = useState([
    { rule: "Low Inventory Alert", condition: "Quantity < 10" },
    { rule: "Damaged Goods Alert", condition: "Status = Damaged" },
  ]);
  const [newCategory, setNewCategory] = useState("");
  const [newRule, setNewRule] = useState("");
  const [newCondition, setNewCondition] = useState("");

  const addCategory = (e) => {
    e.preventDefault();
    setCategories([...categories, newCategory]);
    setNewCategory("");
  };

  const addRule = (e) => {
    e.preventDefault();
    setRules([...rules, { rule: newRule, condition: newCondition }]);
    setNewRule("");
    setNewCondition("");
  };

  return (
    <div className="rules-category-container">
      <h2>Rules & Category Management</h2>
      <div className="category-section">
        <h3>Categories</h3>
        <ul>
          {categories.map((cat, idx) => (
            <li key={idx}>{cat}</li>
          ))}
        </ul>
        <form onSubmit={addCategory}>
          <input
            type="text"
            placeholder="New Category"
            value={newCategory}
            onChange={(e) => setNewCategory(e.target.value)}
            required
          />
          <button type="submit">Add Category</button>
        </form>
      </div>
      <div className="rules-section">
        <h3>Rules</h3>
        <ul>
          {rules.map((r, idx) => (
            <li key={idx}>{r.rule} ({r.condition})</li>
          ))}
        </ul>
        <form onSubmit={addRule}>
          <input
            type="text"
            placeholder="Rule Name"
            value={newRule}
            onChange={(e) => setNewRule(e.target.value)}
            required
          />
          <input
            type="text"
            placeholder="Condition"
            value={newCondition}
            onChange={(e) => setNewCondition(e.target.value)}
            required
          />
          <button type="submit">Add Rule</button>
        </form>
      </div>
    </div>
  );
};

export default RulesCategoryPage; 