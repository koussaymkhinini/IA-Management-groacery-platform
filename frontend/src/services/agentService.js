const API_BASE_URL = "http://localhost:8080/api";

export async function createConversation() {
    const response = await fetch(`${API_BASE_URL}/conversations`, {
        method: "POST"
    });

    if (!response.ok) {
        throw new Error(`Failed to create conversation: ${response.status}`);
    }

    return await response.json();
}

export async function getConversations() {
    const response = await fetch(`${API_BASE_URL}/conversations`);

    if (!response.ok) {
        throw new Error(`Failed to load conversations: ${response.status}`);
    }

    return await response.json();
}

export async function getMessages(conversationId) {
    const response = await fetch(
        `${API_BASE_URL}/conversations/${conversationId}/messages`
    );

    if (!response.ok) {
        throw new Error(`Failed to load messages: ${response.status}`);
    }

    return await response.json();
}

export async function deleteConversation(conversationId) {
    const response = await fetch(
        `${API_BASE_URL}/conversations/${conversationId}`,
        {
            method: "DELETE"
        }
    );

    if (!response.ok) {
        throw new Error(`Failed to delete conversation: ${response.status}`);
    }
}

export async function sendMessage(message, conversationId) {
    const response = await fetch(`${API_BASE_URL}/v1/agent/chat`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            message,
            conversationId
        })
    });

    if (!response.ok) {
        throw new Error(`Server error: ${response.status}`);
    }

    return await response.json();


}
export async function renameConversation(conversationId, title) {
        const response = await fetch(`${API_BASE_URL}/conversations/${conversationId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ title })
        });

        if (!response.ok) {
            throw new Error(`Failed to rename conversation: ${response.status}`);
        }

        return await response.json();
    }
