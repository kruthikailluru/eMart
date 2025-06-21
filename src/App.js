import React from "react";
import { BrowserRouter } from "react-router-dom";
import AppRoutes from "./routes/AppRoutes";
import { AuthProvider } from "./context/AuthContext";
import BackendStatus from "./components/BackendStatus";

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <AppRoutes />
        <BackendStatus />
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;