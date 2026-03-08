import { useState } from "react";
import LoginPage from "./pages/LoginPage";
import ChatPage from "./pages/ChatPage";

export default function App() {
  const [customer, setCustomer] = useState(null);

  return customer
    ? <ChatPage customer={customer} onLogout={() => setCustomer(null)} />
    : <LoginPage onLogin={setCustomer} />;
}