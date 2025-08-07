chrome.runtime.onInstalled.addListener(() => {
    chrome.contextMenus.create({
        id: "sendToTTSReader",
        title: "Play in TTS Reader",
        contexts: ["selection"]
    });
});

chrome.contextMenus.onClicked.addListener(async (info, tab) => {
    if (info.menuItemId === "sendToTTSReader") {
        chrome.tabs.sendMessage(tab.id, { type: "GET_SELECTED_HTML" }, (response) => {
            if (response && response.text) {
                fetch("http://localhost:9024/tts", {
                    method: "POST",
                    body: response.text,
                    headers: {
                        "Content-Type": "text/plain"
                    }
                }).then(() => {
                    console.log("Text sent to TTS app");
                }).catch(err => {
                    console.error("Error sending to TTS app:", err);
                });
            }
        });
    }
});
