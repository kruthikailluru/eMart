import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import Dashboard from "../pages/Dashboard";
import AdminPage from "../pages/AdminPage";
import CustomerPage from "../pages/CustomerPage";
import SupplierPage from "../pages/SupplierPage";
import LoginPage from "../pages/loginpage";
import InventoryPage from "../pages/InventoryPage";
import RulesCategoryPage from "../pages/RulesCategoryPage";
import InventoryMovementPage from "../pages/InventoryMovementPage";
import BarcodePage from "../pages/BarcodePage";
import SupplierLoginPage from "../pages/SupplierLoginPage";
import InventorySupplier from "../pages/InventorySupplier";
import InventoryCustomer from "../pages/InventoryCustomer";
import ProtectedRoute from "../components/ProtectedRoute";
import Home from "../components/Home";

const AppRoutes = () => (
  <Routes>
    {/* Redirect root to dashboard */}
    <Route path="/" element={<Navigate to="/dashboard" />} />
    
    {/* Login page, which will redirect to dashboard after success */}
    <Route path="/login" element={<LoginPage />} />
    <Route path="/supplier-login" element={<SupplierLoginPage />} />
    
    {/* Dashboard is public, with conditional rendering inside the component */}
    <Route path="/dashboard" element={<Dashboard />} />
    
    {/* Protected routes - Admin only */}
    <Route 
      path="/admin" 
      element={
        <ProtectedRoute requiredRoles={['ADMIN']}>
          <AdminPage />
        </ProtectedRoute>
      } 
    />
    <Route 
      path="/inventory" 
      element={
        <ProtectedRoute requiredRoles={['ADMIN']}>
          <InventoryPage />
        </ProtectedRoute>
      } 
    />
    <Route 
      path="/rules-category" 
      element={
        <ProtectedRoute requiredRoles={['ADMIN']}>
          <RulesCategoryPage />
        </ProtectedRoute>
      } 
    />
    <Route 
      path="/inventory-movement" 
      element={
        <ProtectedRoute requiredRoles={['ADMIN']}>
          <InventoryMovementPage />
        </ProtectedRoute>
      } 
    />
    <Route 
      path="/barcode" 
      element={
        <ProtectedRoute requiredRoles={['ADMIN']}>
          <BarcodePage />
        </ProtectedRoute>
      } 
    />
    
    {/* Protected routes - Supplier only */}
    <Route 
      path="/supplier" 
      element={
        <ProtectedRoute requiredRoles={['SUPPLIER']}>
          <SupplierPage />
        </ProtectedRoute>
      } 
    />
    <Route 
      path="/inventory-supplier" 
      element={
        <ProtectedRoute requiredRoles={['SUPPLIER']}>
          <InventorySupplier />
        </ProtectedRoute>
      } 
    />
    
    {/* Protected routes - Customer only */}
    <Route 
      path="/customer" 
      element={
        <ProtectedRoute requiredRoles={['CUSTOMER']}>
          <CustomerPage />
        </ProtectedRoute>
      } 
    />
    <Route 
      path="/inventory-customer" 
      element={
        <ProtectedRoute requiredRoles={['CUSTOMER']}>
          <InventoryCustomer />
        </ProtectedRoute>
      } 
    />
    
    {/* Catch all route - redirect to dashboard */}
    <Route path="*" element={<Navigate to="/dashboard" />} />
  </Routes>
);

export default AppRoutes;