import { Bot, User } from "lucide-react";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";

function Message({ role, content }) {
    const isUser = role === "user";

    return (
        <div className={`message-row ${isUser ? "user-row" : "assistant-row"}`}>
            <div className={`message-avatar ${isUser ? "user-avatar" : "ai-avatar"}`}>
                {isUser ? <User size={17} /> : <Bot size={17} />}
            </div>

            <div className="message-body">
                <div className="message-author">
                    {isUser ? "You" : "GroceryAI"}
                </div>

                <div className={`message-content ${isUser ? "user-content" : "ai-content"}`}>
                    {isUser ? (
                        <p>{content}</p>
                    ) : (
                        <ReactMarkdown remarkPlugins={[remarkGfm]}>
                            {content}
                        </ReactMarkdown>
                    )}
                </div>
            </div>
        </div>
    );
}

export default Message;