import { useState, useRef, useEffect, useCallback } from "react";
import ReactMarkdown from "react-markdown";

const API_BASE = "http://localhost:8089";

const QUICK_REPLIES = [
    "Where is my order?",
    "I want to cancel my order",
    "What is your refund policy?",
    "When will it be delivered?",
];

function TypingIndicator() {
    return (
        <div className="flex items-end gap-2 mb-4">
            <div className="w-8 h-8 rounded-full bg-teal-600 flex items-center justify-center text-white text-xs font-bold flex-shrink-0">
                AI
            </div>
            <div className="bg-white border border-gray-200 rounded-2xl rounded-bl-sm px-4 py-3 shadow-sm">
                <div className="flex gap-1 items-center h-5">
                    {[0, 1, 2].map((i) => (
                        <div
                            key={i}
                            className="w-2 h-2 rounded-full bg-teal-400"
                            style={{
                                animation: "typingBounce 1.2s infinite",
                                animationDelay: `${i * 0.2}s`,
                            }}
                        />
                    ))}
                </div>
            </div>
        </div>
    );
}

const markdownComponents = {
    p: ({ children }) => <p className="mb-1 last:mb-0 leading-relaxed">{children}</p>,
    strong: ({ children }) => <strong className="font-semibold text-gray-900">{children}</strong>,
    em: ({ children }) => <em className="italic">{children}</em>,
    ul: ({ children }) => <ul className="list-disc pl-4 my-1 space-y-0.5">{children}</ul>,
    ol: ({ children }) => <ol className="list-decimal pl-4 my-1 space-y-0.5">{children}</ol>,
    li: ({ children }) => <li className="leading-relaxed">{children}</li>,
    h1: ({ children }) => <h1 className="text-base font-bold text-gray-900 mt-2 mb-1">{children}</h1>,
    h2: ({ children }) => <h2 className="text-sm font-bold text-gray-900 mt-2 mb-1">{children}</h2>,
    h3: ({ children }) => <h3 className="text-sm font-semibold text-gray-800 mt-1.5 mb-0.5">{children}</h3>,
    code: ({ children }) => <code className="bg-gray-100 text-gray-800 px-1 py-0.5 rounded text-xs font-mono">{children}</code>,
    hr: () => <hr className="my-2 border-gray-200" />,
};

function MessageBubble({ message }) {
    const isBot = message.role === "assistant";
    return (
        <div className={`flex items-end gap-2 mb-4 ${isBot ? "" : "flex-row-reverse"}`}>

            {/* Avatar */}
            <div className={`w-8 h-8 rounded-full flex items-center justify-center text-white text-xs font-bold flex-shrink-0
        ${isBot ? "bg-teal-600" : "bg-violet-500"}`}>
                {isBot ? "AI" : message.avatar}
            </div>

            {/* Bubble */}
            <div className={`max-w-[72%] px-4 py-3 text-sm rounded-2xl shadow-sm
        ${isBot
                    ? "bg-white border border-gray-200 rounded-bl-sm text-gray-700"
                    : "bg-teal-600 rounded-br-sm text-white"
                }`}>

                {isBot ? (
                    <div className="markdown-body">
                        <ReactMarkdown components={markdownComponents}>
                            {message.content}
                        </ReactMarkdown>
                        {message.streaming && (
                            <span className="inline-block w-0.5 h-4 bg-teal-400 ml-0.5 animate-pulse align-text-bottom" />
                        )}
                    </div>
                ) : (
                    <p className="whitespace-pre-wrap break-words leading-relaxed">
                        {message.content}
                    </p>
                )}

                <p className={`text-xs mt-1.5 ${isBot ? "text-gray-400" : "text-teal-200"}`}>
                    {message.time}
                </p>
            </div>
        </div>
    );
}

function ChatPage({ customer, onLogout }) {
    const getTime = () =>
        new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });

    const [messages, setMessages] = useState([{
        id: 1,
        role: "assistant",
        content: `Hi **${customer.name}**! 👋 I'm Maya, your ShopEasy support agent.\nHow can I help you today?`,
        time: getTime(),
        streaming: false,
    }]);

    const [input, setInput] = useState("");
    const [isStreaming, setIsStreaming] = useState(false);
    const [isTyping, setIsTyping] = useState(false);
    const [error, setError] = useState("");
    const messagesEndRef = useRef(null);
    const inputRef = useRef(null);

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages, isTyping]);

    const sendMessage = useCallback(async (text) => {
        const userText = text || input.trim();
        if (!userText || isStreaming) return;

        setError("");
        setInput("");

        setMessages((prev) => [...prev, {
            id: Date.now(),
            role: "user",
            content: userText,
            time: getTime(),
            avatar: customer.name[0],
            streaming: false,
        }]);

        setIsTyping(true);
        await new Promise((r) => setTimeout(r, 500));
        setIsTyping(false);
        setIsStreaming(true);

        // Add empty bot message placeholder
        const botId = Date.now() + 1;
        setMessages((prev) => [...prev, {
            id: botId,
            role: "assistant",
            content: "",
            time: getTime(),
            streaming: true,
        }]);

        // SSE Stream
        try {
            const response = await fetch(
                `${API_BASE}/support/stream?customerId=${encodeURIComponent(customer.id)}&message=${encodeURIComponent(userText)}`
            );

            if (!response.ok) throw new Error("Backend error");

            const reader = response.body.getReader();
            const decoder = new TextDecoder();

            while (true) {
                const { done, value } = await reader.read();
                if (done) break;

                const chunk = decoder.decode(value, { stream: true });
                const lines = chunk.split("\n");

                for (const line of lines) {
                    if (line.startsWith("data:")) {
                        const token = line.slice(5); // preserves " your" " order" etc
                        if (token === "") continue;
                        setMessages((prev) =>
                            prev.map((m) =>
                                m.id === botId ? { ...m, content: m.content + token } : m
                            )
                        );
                    }
                }
            }

            setIsStreaming(false);
            setMessages((prev) =>
                prev.map((m) => m.id === botId ? { ...m, streaming: false } : m)
            );
            inputRef.current?.focus();

        } catch {
            setIsTyping(false);
            setIsStreaming(false);
            setError("Could not connect to backend. Is Spring Boot running on port 8089?");
        } s
    }, [input, isStreaming, customer]);

    const handleClearSession = async () => {
        await fetch(`${API_BASE}/support/session/${customer.id}`, { method: "DELETE" });
        setMessages([{
            id: Date.now(),
            role: "assistant",
            content: `Session cleared! Hi again **${customer.name}**! 👋 How can I help you?`,
            time: getTime(),
            streaming: false,
        }]);
    };

    return (
        <div className="flex flex-col h-screen bg-gray-50">

            {/* Keyframe for typing dots */}
            <style>{`
        @keyframes typingBounce {
          0%, 60%, 100% { transform: translateY(0); }
          30% { transform: translateY(-5px); }
        }
      `}</style>

            {/* ── Header ── */}
            <div className="bg-white border-b border-gray-200 px-4 py-3 flex items-center justify-between shadow-sm">
                <div className="flex items-center gap-3">
                    <span className="text-xl">🛒</span>
                    <div>
                        <h1 className="font-semibold text-gray-800 text-sm">ShopEasy Support</h1>
                        <p className="text-xs text-teal-600 flex items-center gap-1">
                            <span className="w-1.5 h-1.5 bg-teal-500 rounded-full inline-block animate-pulse" />
                            AI Agent Online
                        </p>
                    </div>
                </div>

                <div className="flex items-center gap-2">
                    <span className="text-sm text-gray-600 font-medium">{customer.name}</span>
                    <span className="text-xs bg-teal-50 text-teal-700 border border-teal-200 px-2 py-0.5 rounded-full font-medium">
                        {customer.tier}
                    </span>
                    <button
                        onClick={handleClearSession}
                        title="Clear session memory"
                        className="text-gray-400 hover:text-gray-600 text-sm px-2 py-1 rounded hover:bg-gray-100 transition"
                    >
                        🗑️
                    </button>
                    <button
                        onClick={onLogout}
                        className="text-xs text-gray-400 hover:text-red-500 px-2 py-1 rounded hover:bg-red-50 transition"
                    >
                        Sign out
                    </button>
                </div>
            </div>

            {/* ── Messages ── */}
            <div className="flex-1 overflow-y-auto px-4 py-4">
                <div className="max-w-2xl mx-auto">

                    {/* Date divider */}
                    <div className="flex items-center gap-3 mb-4">
                        <div className="flex-1 h-px bg-gray-200" />
                        <span className="text-xs text-gray-400">Today</span>
                        <div className="flex-1 h-px bg-gray-200" />
                    </div>

                    {messages.map((msg) => (
                        <MessageBubble key={msg.id} message={msg} />
                    ))}

                    {isTyping && <TypingIndicator />}

                    {error && (
                        <div className="bg-red-50 border border-red-200 text-red-600 text-xs px-4 py-3 rounded-xl mb-4">
                            ⚠️ {error}
                        </div>
                    )}

                    <div ref={messagesEndRef} />
                </div>
            </div>

            {/* ── Quick Replies ── */}
            {messages.length <= 2 && !isStreaming && (
                <div className="px-4 pb-2">
                    <div className="max-w-2xl mx-auto flex flex-wrap gap-2">
                        {QUICK_REPLIES.map((q) => (
                            <button
                                key={q}
                                onClick={() => sendMessage(q)}
                                className="text-xs px-3 py-1.5 bg-white border border-teal-200 text-teal-700 rounded-full hover:bg-teal-50 transition shadow-sm"
                            >
                                {q}
                            </button>
                        ))}
                    </div>
                </div>
            )}

            {/* ── Input ── */}
            <div className="bg-white border-t border-gray-200 px-4 py-3">
                <div className="max-w-2xl mx-auto flex gap-2">
                    <input
                        ref={inputRef}
                        type="text"
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        onKeyDown={(e) => e.key === "Enter" && sendMessage()}
                        disabled={isStreaming}
                        placeholder={isStreaming ? "Maya is typing..." : "Type your message..."}
                        className="flex-1 border border-gray-300 rounded-xl px-4 py-2.5 text-sm
              text-gray-700 outline-none focus:ring-2 focus:ring-teal-500
              focus:border-transparent transition disabled:bg-gray-50"
                    />
                    <button
                        onClick={() => sendMessage()}
                        disabled={!input.trim() || isStreaming}
                        className={`px-5 py-2.5 rounded-xl text-sm font-semibold transition
              ${input.trim() && !isStreaming
                                ? "bg-teal-600 hover:bg-teal-700 text-white shadow-sm"
                                : "bg-gray-200 text-gray-400 cursor-not-allowed"
                            }`}
                    >
                        {isStreaming ? (
                            <svg className="animate-spin h-4 w-4 text-gray-400" viewBox="0 0 24 24" fill="none">
                                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
                            </svg>
                        ) : "Send"}
                    </button>
                </div>
                <p className="text-center text-xs text-gray-400 mt-2">
                    Press <kbd className="bg-gray-100 border border-gray-200 rounded px-1">Enter</kbd> to send
                </p>
            </div>
        </div>
    );
}

export default ChatPage;