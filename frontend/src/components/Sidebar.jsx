import "../Conversations.css";
import { useState } from "react";
import {
    Bot,
    MessageSquare,
    Plus,
    BarChart3,
    PanelLeftClose,
    PanelLeftOpen,
    Trash2,
    Pencil,
    Check,
    X
} from "lucide-react";

function Sidebar({
    conversations,
    activeConversationId,
    onNewChat,
    onSelectConversation,
    onDeleteConversation,
    onRenameConversation,
    collapsed,
    onToggle,
    loading
}) {
    const [editingConversationId, setEditingConversationId] = useState(null);
    const [editingTitle, setEditingTitle] = useState("");

    function startEditing(conversation) {
        setEditingConversationId(conversation.id);
        setEditingTitle(conversation.title);
    }

    async function saveEditing(conversationId) {
        const title = editingTitle.trim();

        if (!title || loading) return;

        await onRenameConversation(conversationId, title);

        setEditingConversationId(null);
        setEditingTitle("");
    }

    function cancelEditing() {
        setEditingConversationId(null);
        setEditingTitle("");
    }

    return (
        <aside className={`sidebar ${collapsed ? "collapsed" : ""}`}>
            <div className="sidebar-top">
                <div className="brand">
                    <div className="brand-icon">
                        <Bot size={23} />
                    </div>

                    {!collapsed && (
                        <div className="brand-info">
                            <h2>GroceryAI</h2>
                            <span>Business Intelligence</span>
                        </div>
                    )}

                    <button
                        className="icon-button sidebar-collapse"
                        onClick={onToggle}
                        title={collapsed ? "Expand sidebar" : "Collapse sidebar"}
                        aria-label={collapsed ? "Expand sidebar" : "Collapse sidebar"}
                    >
                        {collapsed
                            ? <PanelLeftOpen size={18} />
                            : <PanelLeftClose size={18} />
                        }
                    </button>
                </div>

                <button
                    className="new-chat-button"
                    onClick={onNewChat}
                    title="New conversation"
                    disabled={loading}
                >
                    <Plus size={18} />
                    {!collapsed && <span>New conversation</span>}
                </button>

                <div className="sidebar-section">
                    {!collapsed && (
                        <span className="sidebar-label">WORKSPACE</span>
                    )}

                    <div className="sidebar-item active" title="AI Assistant">
                        <MessageSquare size={17} />
                        {!collapsed && <span>AI Assistant</span>}
                    </div>

                    <div className="sidebar-item" title="Analytics">
                        <BarChart3 size={17} />
                        {!collapsed && (
                            <>
                                <span>Analytics</span>
                                <span className="coming-soon">Soon</span>
                            </>
                        )}
                    </div>
                </div>

                {!collapsed && (
                    <div className="sidebar-section conversations-section">
                        <span className="sidebar-label">RECENT CHATS</span>

                        <div className="conversation-list">
                            {conversations.length === 0 ? (
                                <span className="empty-conversations">
                                    No conversations yet
                                </span>
                            ) : (
                                conversations.map((conversation) => {
                                    const isEditing =
                                        editingConversationId === conversation.id;

                                    return (
                                        <div
                                            key={conversation.id}
                                            className={`conversation-item ${
                                                activeConversationId === conversation.id
                                                    ? "active"
                                                    : ""
                                            } ${isEditing ? "editing" : ""}`}
                                        >
                                            {isEditing ? (
                                                <div className="conversation-edit">
                                                    <input
                                                        autoFocus
                                                        maxLength={200}
                                                        value={editingTitle}
                                                        onChange={(event) =>
                                                            setEditingTitle(event.target.value)
                                                        }
                                                        onKeyDown={(event) => {
                                                            if (event.key === "Enter") {
                                                                saveEditing(conversation.id);
                                                            }

                                                            if (event.key === "Escape") {
                                                                cancelEditing();
                                                            }
                                                        }}
                                                        disabled={loading}
                                                    />

                                                    <button
                                                        className="conversation-action save"
                                                        onClick={() =>
                                                            saveEditing(conversation.id)
                                                        }
                                                        disabled={loading || !editingTitle.trim()}
                                                        title="Save"
                                                    >
                                                        <Check size={15} />
                                                    </button>

                                                    <button
                                                        className="conversation-action cancel"
                                                        onClick={cancelEditing}
                                                        title="Cancel"
                                                    >
                                                        <X size={15} />
                                                    </button>
                                                </div>
                                            ) : (
                                                <>
                                                    <button
                                                        className="conversation-select"
                                                        onClick={() =>
                                                            onSelectConversation(conversation.id)
                                                        }
                                                        disabled={loading}
                                                        title={conversation.title}
                                                    >
                                                        <MessageSquare size={15} />
                                                        <span>{conversation.title}</span>
                                                    </button>

                                                    <div className="conversation-actions">
                                                        <button
                                                            className="conversation-action rename"
                                                            onClick={() =>
                                                                startEditing(conversation)
                                                            }
                                                            disabled={loading}
                                                            title="Rename conversation"
                                                            aria-label="Rename conversation"
                                                        >
                                                            <Pencil size={14} />
                                                        </button>

                                                        <button
                                                            className="conversation-action delete"
                                                            onClick={() =>
                                                                onDeleteConversation(conversation.id)
                                                            }
                                                            disabled={loading}
                                                            title="Delete conversation"
                                                            aria-label="Delete conversation"
                                                        >
                                                            <Trash2 size={14} />
                                                        </button>
                                                    </div>
                                                </>
                                            )}
                                        </div>
                                    );
                                })
                            )}
                        </div>
                    </div>
                )}
            </div>
        </aside>
    );
}

export default Sidebar;
