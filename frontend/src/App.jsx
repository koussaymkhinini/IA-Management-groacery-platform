
import { useEffect, useState } from "react";

import Sidebar from "./components/Sidebar";
import ChatWindow from "./components/ChatWindow";

import {
    sendMessage,
    getConversations,
    getConversationMessages,
} from "./services/agentService";

function App() {
    const [messages, setMessages] = useState([]);
    const [message, setMessage] = useState("");

    const [loading, setLoading] = useState(false);
    const [loadingHistory, setLoadingHistory] = useState(false);

    const [conversationId, setConversationId] = useState(null);
    const [conversations, setConversations] = useState([]);

    const [sidebarCollapsed, setSidebarCollapsed] = useState(false);

    // Load conversation history when the application starts.
    async function refreshConversations() {
        try {
            const data = await getConversations();
            setConversations(data);
        } catch (error) {
            console.error("Failed to load conversations:", error);
        }
    }

    useEffect(() => {
        refreshConversations();
    }, []);

    // Send a message to the AI assistant.
    async function handleSend(text = message) {
        const userMessage = text.trim();

        if (!userMessage || loading || loadingHistory) return;

        setMessages(previous => [
            ...previous,
            { role: "user", content: userMessage },
        ]);

        setMessage("");
        setLoading(true);

        try {
            const response = await sendMessage(
                userMessage,
                conversationId
            );

            setConversationId(response.conversationId);

            setMessages(previous => [
                ...previous,
                {
                    role: "assistant",
                    content:
                        response.answer ??
                        "The assistant returned an empty response.",
                },
            ]);

            await refreshConversations();

        } catch (error) {
            console.error("GroceryAI error:", error);

            setMessages(previous => [
                ...previous,
                {
                    role: "assistant",
                    content:
                        "I couldn't process your request. Please check your backend connection and try again.",
                },
            ]);
        } finally {
            setLoading(false);
        }
    }

    // Start a fresh conversation.
    function handleNewChat() {
        if (loading || loadingHistory) return;

        setConversationId(null);
        setMessages([]);
        setMessage("");
    }

    // Open an existing conversation from the sidebar.
    async function handleSelectConversation(id) {
        if (loading || loadingHistory || id === conversationId) return;

        setLoadingHistory(true);

        try {
            const data = await getConversationMessages(id);

            const formattedMessages = data.map(item => ({
                id: item.id,
                role: item.role.toLowerCase(),
                content: item.content,
            }));

            setConversationId(id);
            setMessages(formattedMessages);
            setMessage("");

        } catch (error) {
            console.error("Failed to load conversation:", error);
        } finally {
            setLoadingHistory(false);
        }
    }

    function toggleSidebar() {
        setSidebarCollapsed(previous => !previous);
    }

    return (
        <div className="app">
            <Sidebar
                onNewChat={handleNewChat}
                collapsed={sidebarCollapsed}
                onToggle={toggleSidebar}
                conversations={conversations}
                activeConversationId={conversationId}
                onSelectConversation={handleSelectConversation}
            />

            <ChatWindow
                messages={messages}
                message={message}
                setMessage={setMessage}
                onSend={handleSend}
                loading={loading || loadingHistory}
            />
        </div>
    );
}

export default App;
