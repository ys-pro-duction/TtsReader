chrome.runtime.onMessage.addListener((msg, sender, sendResponse) => {
    if (msg.type === "GET_SELECTED_HTML") {
        const selection = window.getSelection();
        if (!selection.rangeCount) return;
        const container = document.createElement("div");
        for (let i = 0; i < selection.rangeCount; i++) {
            container.appendChild(selection.getRangeAt(i).cloneContents());
        }
        const textWithBreaks = container.innerText; // This preserves line breaks
        sendResponse({ text: textWithBreaks });
    }
    return true;
});
