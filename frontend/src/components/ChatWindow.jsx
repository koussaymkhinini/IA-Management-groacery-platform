import { Sparkles, TrendingUp, Package, Users } from "lucide-react";
import Message from "./Message";
import ChatInput from "./ChatInput";

const suggestions = [
    {
        icon: TrendingUp,
        title: "Sales performance",
        description: "Analyze total sales and business performance.",
        prompt: "Give me an overview of sales performance."
    },
    {
        icon: Package,
        title: "Product insights",
        description: "Explore products, prices and categories.",
        prompt: "How many products are in the database?"
    },
    {
        icon: Users,
        title: "Customer analysis",
        description: "Understand your customers and their activity.",
        prompt: "How many customers are in the database?"
    }
];

function ChatWindow({
    messages,
    message,
    setMessage,
    onSend,
    loading
}) {
    const hasMessages = messages.length > 0;

    return (
        <main className="chat-main">
            <header className="chat-header">
                <div>
                    <div className="header-title">AI Assistant</div>
                    <div className="header-subtitle">
                        Your grocery business intelligence assistant
                    </div>
                </div>

                <div className="connection-status">
                    <span className="status-dot" />
                    AI connected
                </div>
            </header>

            <div className="chat-content">
                {!hasMessages ? (
                    <div className="welcome-screen">
                        <div className="welcome-icon">
                            <Sparkles size={27} />
                        </div>

                        <div className="welcome-heading">
                            <span className="welcome-eyebrow">
                                YOUR BUSINESS, UNDERSTOOD
                            </span>

                            <h1>
                                What would you like
                                <br />
                                to <span>discover</span> today?
                            </h1>

                            <p>
                                Ask questions about your grocery business.
                                Get insights from your database using natural language.
                            </p>
                        </div>

                        <div className="suggestion-grid">
                            {suggestions.map((item) => {
                                const Icon = item.icon;

                                return (
                                    <button
                                        className="suggestion-card"
                                        key={item.title}
                                        onClick={() => onSend(item.prompt)}
                                        disabled={loading}
                                    >
                                        <div className="suggestion-icon">
                                            <Icon size={19} />
                                        </div>

                                        <strong>{item.title}</strong>
                                        <span>{item.description}</span>
                                    </button>
                                );
                            })}
                        </div>
                    </div>
                ) : (
                    <div className="messages-container">
                        {messages.map((msg, index) => (
                            <Message
                                key={index}
                                role={msg.role}
                                content={msg.content}
                            />
                        ))}

                        {loading && (
                            <div className="message-row assistant-row">
                                <div className="message-avatar ai-avatar">
                                    <Sparkles size={17} />
                                </div>

                                <div className="typing-indicator">
                                    <span />
                                    <span />
                                    <span />
                                </div>
                            </div>
                        )}
                    </div>
                )}
            </div>

            <ChatInput
                message={message}
                setMessage={setMessage}
                onSend={onSend}
                loading={loading}
            />

            <footer className="chat-footer">
                GroceryAI can make mistakes. Verify important business decisions.
            </footer>
        </main>
    );
}

export default ChatWindow;