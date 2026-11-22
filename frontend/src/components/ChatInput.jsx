import { ArrowUp, Paperclip } from "lucide-react";

function ChatInput({ message, setMessage, onSend, loading }) {
    function handleSubmit(event) {
        event.preventDefault();

        if (message.trim() && !loading) {
            onSend();
        }
    }

    function handleKeyDown(event) {
        if (event.key === "Enter" && !event.shiftKey) {
            event.preventDefault();
            handleSubmit(event);
        }
    }

    return (
        <div className="input-wrapper">
            <form className="chat-input-container" onSubmit={handleSubmit}>
                <textarea
                    value={message}
                    onChange={(event) => setMessage(event.target.value)}
                    onKeyDown={handleKeyDown}
                    placeholder="Ask anything about your grocery business..."
                    rows={1}
                    disabled={loading}
                />

                <div className="input-actions">

                    <button
                        type="submit"
                        className="send-button"
                        disabled={!message.trim() || loading}
                        title="Send message"
                    >
                        <ArrowUp size={19} />
                    </button>
                </div>
            </form>

            <div className="input-hint">
                <span>Enter to send</span>
                <span>·</span>
                <span>Shift + Enter for a new line</span>
            </div>
        </div>
    );
}

export default ChatInput;