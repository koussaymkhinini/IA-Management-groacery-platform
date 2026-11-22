
const API_BASE_URL = "http://localhost:8080/api/v1/agent";

// Send a message, optionally continuing an existing conversation.
export async function sendMessage(message, conversationId = null) {
    const response = await fetch(`${API_BASE_URL}/chat`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            message,
            conversationId,
        }),
    });

    if (!response.ok) {
        throw new Error(`Server error: ${response.status}`);
    }

    return await response.json();
}

// Retrieve all previous conversations.
export async function getConversations() {
    const response = await fetch(`${API_BASE_URL}/conversations`);

    if (!response.ok) {
        throw new Error(`Server error: ${response.status}`);
    }

    return await response.json();
}

// Retrieve messages belonging to a specific conversation.
export async function getConversationMessages(conversationId) {
    const response = await fetch(
        `${API_BASE_URL}/conversations/${conversationId}/messages`
    );

    if (!response.ok) {
        throw new Error(`Server error: ${response.status}`);
    }

    return await response.json();
}
