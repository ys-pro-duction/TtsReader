// Create context menu item
browser.contextMenus.create({
    id: "send-to-tts",
    title: "Send to TTS",
    contexts: ["selection"]
});

// Handle context menu clicks
browser.contextMenus.onClicked.addListener((info, tab) => {
    if (info.menuItemId === "send-to-tts" && info.selectionText) {
        sendTextToServer(info.selectionText);
    }
});

function sendTextToServer(text) {
    fetch("http://localhost:9024/tts", {
        method: "POST",
        headers: {
            "Content-Type": "text/plain"
        },
        body: text
    })
        .then(response => {
            if (!response.ok) throw new Error('Network response was not ok');
            console.log("Text successfully sent to server");
        })
        .catch(error => {
            console.error("Error sending text to server:", error);
        });
}
