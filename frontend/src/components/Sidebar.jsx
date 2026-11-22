
import {
    Bot,
    MessageSquare,
    Plus,
    BarChart3,
    PanelLeftClose,
    PanelLeftOpen
} from "lucide-react";

function Sidebar({ onNewChat, collapsed, onToggle }) {
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
                >
                    <Plus size={18} />
                    {!collapsed && <span>New conversation</span>}
                </button>

                <div className="sidebar-section">
                    {!collapsed && (
                        <span className="sidebar-label">WORKSPACE</span>
                    )}

                    <div
                        className="sidebar-item active"
                        title="AI Assistant"
                    >
                        <MessageSquare size={17} />
                        {!collapsed && <span>AI Assistant</span>}
                    </div>

                    <div
                        className="sidebar-item"
                        title="Analytics"
                    >
                        <BarChart3 size={17} />
                        {!collapsed && (
                            <>
                                <span>Analytics</span>
                                <span className="coming-soon">Soon</span>
                            </>
                        )}
                    </div>
                </div>
            </div>
        </aside>
    );
}

export default Sidebar;
