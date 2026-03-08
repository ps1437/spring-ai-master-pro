import { useState, useRef, useEffect } from "react";

const API_BASE = "http://localhost:8089";

export default function LoginPage({ onLogin }) {
  const [customerId, setCustomerId] = useState("");
  const [loading, setLoading]       = useState(false);
  const [error, setError]           = useState("");
  const inputRef                    = useRef(null);

  useEffect(() => { inputRef.current?.focus(); }, []);

  const handleLogin = async () => {
    const id = customerId.trim();
    if (!id) { setError("Please enter your Customer ID."); return; }

    setError("");
    setLoading(true);

    try {
      const res = await fetch(`${API_BASE}/customers/${encodeURIComponent(id)}`);
      if (!res.ok) throw new Error("not found");
      const customer = await res.json();
      onLogin(customer);
    } catch {
      setError("Customer not found. Please check your ID.");
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center px-4">
      <div className="bg-white rounded-2xl shadow-md w-full max-w-sm p-8">

        {/* Logo */}
        <div className="flex items-center gap-2 mb-6">
          <span className="text-2xl">🛒</span>
          <h1 className="text-xl font-bold text-gray-800">ShopEasy Support</h1>
        </div>

        <h2 className="text-gray-700 font-semibold text-lg mb-1">Sign in</h2>
        <p className="text-gray-400 text-sm mb-6">Enter your Customer ID to continue</p>

        {/* Input */}
        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-600 mb-1">
            Customer ID
          </label>
          <input
            ref={inputRef}
            type="text"
            value={customerId}
            onChange={(e) => { setCustomerId(e.target.value); setError(""); }}
            onKeyDown={(e) => e.key === "Enter" && handleLogin()}
            placeholder="e.g. cust-001"
            className={`w-full border rounded-lg px-4 py-2.5 text-sm text-gray-700 outline-none
              focus:ring-2 focus:ring-teal-500 focus:border-transparent transition
              ${error ? "border-red-400" : "border-gray-300"}`}
          />
          {error && (
            <p className="text-red-500 text-xs mt-1.5">{error}</p>
          )}
          <p className="text-gray-400 text-xs mt-1.5">
            Try: cust-001, cust-002, cust-003
          </p>
        </div>

        {/* Button */}
        <button
          onClick={handleLogin}
          disabled={!customerId.trim() || loading}
          className={`w-full py-2.5 rounded-lg text-sm font-semibold transition
            ${customerId.trim() && !loading
              ? "bg-teal-600 hover:bg-teal-700 text-white"
              : "bg-gray-200 text-gray-400 cursor-not-allowed"
            }`}
        >
          {loading ? "Verifying..." : "Start Chat"}
        </button>

      </div>
    </div>
  );
}