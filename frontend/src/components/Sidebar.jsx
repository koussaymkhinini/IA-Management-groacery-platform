import {
    Bot,
    MessageSquare,
    Plus,
    BarChart3,
    Settings,
    PanelLeftClose
} from "lucide-react";

function Sidebar({ onNewChat }) {
    return (
        <aside className="sidebar">
            <div className="sidebar-top">
                <div className="brand">
                    <div className="brand-icon">
                        <Bot size={23} />
                    </div>

                    <div className="brand-info">
                        <h2>GroceryAI</h2>
                        <span>Business Intelligence</span>
                    </div>

                    <button className="icon-button sidebar-collapse" title="Sidebar">
                        <PanelLeftClose size={18} />
                    </button>
                </div>

                <button className="new-chat-button" onClick={onNewChat}>
                    <Plus size={18} />
                    <span>New conversation</span>
                </button>

                <div className="sidebar-section">
                    <span className="sidebar-label">WORKSPACE</span>

                    <div className="sidebar-item active">
                        <MessageSquare size={17} />
                        <span>AI Assistant</span>
                    </div>

                    <div className="sidebar-item">
                        <BarChart3 size={17} />
                        <span>Analytics</span>
                        <span className="coming-soon">Soon</span>
                    </div>
                </div>
            </div>

            <div className="sidebar-bottom">
                <div className="sidebar-item">
                    <Settings size={17} />
                    <span>Settings</span>
                </div>

                <div className="sidebar-profile">
                    <div className="profile-avatar">G</div>

                    <div className="profile-info">
                        <strong>GroceryAI</strong>
                        <span>AI Workspace</span>
                    </div>

                    <span className="status-dot" />
                </div>
            </div>
        </aside>
    );
}

export default Sidebar;