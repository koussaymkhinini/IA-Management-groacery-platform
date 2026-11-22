import { useEffect, useState } from "react";
import Sidebar from "./components/Sidebar";
import ChatWindow from "./components/ChatWindow";

import {
    sendMessage,
    createConversation,
    getConversations,
    getMessages,
    deleteConversation,
    renameConversation
} from "./services/agentService";

function App() {
    const [messages, setMessages] = useState([]);
    const [message, setMessage] = useState("");

    const [conversations, setConversations] = useState([]);
    const [activeConversationId, setActiveConversationId] = useState(null);

    const [loading, setLoading] = useState(false);
    const [sidebarCollapsed, setSidebarCollapsed] = useState(false);

    useEffect(() => {
        loadConversations();
    }, []);

    async function loadConversations() {
        try {
            const data = await getConversations();
            setConversations(data);
        } catch (error) {
            console.error("Failed to load conversations:", error);
        }
    }

    async function handleSend(text = message) {
        const userMessage = text.trim();

        if (!userMessage || loading) return;

        setMessage("");
        setLoading(true);

        setMessages((previous) => [
            ...previous,
            { role: "user", content: userMessage }
        ]);

        try {
            let conversationId = activeConversationId;

            // Create a conversation when the first message is sent.
            if (!conversationId) {
                const conversation = await createConversation();

                conversationId = conversation.id;
                setActiveConversationId(conversationId);
            }

            const response = await sendMessage(
                userMessage,
                conversationId
            );
            console.log("BACKEND RESPONSE:", response);
            console.log("CHART:", response.chart);

            setMessages((previous) => [
                ...previous,
                {
                    role: "assistant",
                    content: response.answer ??
                        "The assistant returned an empty response.",
                    chart: response.chart ?? null
                }
            ]);

            await loadConversations();

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

        setActiveConversationId(null);
        setMessages([]);
        setMessage("");
    }

    async function handleSelectConversation(conversationId) {
        if (loading || conversationId === activeConversationId) return;

        try {
            setLoading(true);

            const data = await getMessages(conversationId);

            setActiveConversationId(conversationId);
            setMessages(
                data.map((item) => ({
                    role: item.role.toLowerCase(),
                    content: item.content
                }))
            );

            setMessage("");
        } catch (error) {
            console.error("Failed to open conversation:", error);
        } finally {
            setLoading(false);
        }
    }

    async function handleDeleteConversation(conversationId) {
        if (loading) return;

        try {
            await deleteConversation(conversationId);

            setConversations((previous) =>
                previous.filter((item) => item.id !== conversationId)
            );

            if (activeConversationId === conversationId) {
                setActiveConversationId(null);
                setMessages([]);
                setMessage("");
            }
        } catch (error) {
            console.error("Failed to delete conversation:", error);
        }
    }

    async function handleRenameConversation(conversationId, title) {
        const newTitle = title.trim();

        if (!newTitle || loading) return;

        try {
            const updatedConversation = await renameConversation(
                conversationId,
                newTitle
            );

            setConversations((previous) =>
                previous.map((item) =>
                    item.id === conversationId
                        ? updatedConversation
                        : item
                )
            );
        } catch (error) {
            console.error("Failed to rename conversation:", error);
        }
    }

    function toggleSidebar() {
        setSidebarCollapsed((previous) => !previous);
    }

    return (
        <div className="app">
            <Sidebar
                conversations={conversations}
                activeConversationId={activeConversationId}
                onNewChat={handleNewChat}
                onSelectConversation={handleSelectConversation}
                onDeleteConversation={handleDeleteConversation}
                onRenameConversation={handleRenameConversation}
                collapsed={sidebarCollapsed}
                onToggle={toggleSidebar}
                loading={loading}
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