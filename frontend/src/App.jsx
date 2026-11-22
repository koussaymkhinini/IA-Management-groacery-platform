
import { useState } from "react";
import Sidebar from "./components/Sidebar";
import ChatWindow from "./components/ChatWindow";
import { sendMessage } from "./services/agentService";

function App() {
    const [messages, setMessages] = useState([]);
    const [message, setMessage] = useState("");
    const [loading, setLoading] = useState(false);
    const [sidebarCollapsed, setSidebarCollapsed] = useState(false);

    async function handleSend(text = message) {
        const userMessage = text.trim();

        if (!userMessage || loading) return;

        setMessages((previous) => [
            ...previous,
            { role: "user", content: userMessage }
        ]);

        setMessage("");
        setLoading(true);

        try {
            const response = await sendMessage(userMessage);

            setMessages((previous) => [
                ...previous,
                {
                    role: "assistant",
                    content: response.answer ?? "The assistant returned an empty response."
                }
            ]);
        } catch (error) {
            console.error("GroceryAI error:", error);

            setMessages((previous) => [
                ...previous,
                {
                    role: "assistant",
                    content: "I couldn't process your request. Please check your backend connection and try again."
                }
            ]);
        } finally {
            setLoading(false);
        }
    }

    function handleNewChat() {
        if (loading) return;

        setMessages([]);
        setMessage("");
    }

    function toggleSidebar() {
        setSidebarCollapsed((previous) => !previous);
    }

    return (
        <div className="app">
            <Sidebar
                onNewChat={handleNewChat}
                collapsed={sidebarCollapsed}
                onToggle={toggleSidebar}
            />

            <ChatWindow
                messages={messages}
                message={message}
                setMessage={setMessage}
                onSend={handleSend}
                loading={loading}
            />
        </div>
    );
}

export default App;
