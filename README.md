# TtsReader 🗣️ | Smart Offline Text-to-Speech Reader

**TtsReader** is a cross-platform, offline TTS app built with **Kotlin Multiplatform** and **Jetpack Compose**, using the **[Kokoro TTS model](https://huggingface.co/hexgrad/Kokoro-82M)** through **[sherpa-onnx](https://github.com/k2-fsa/sherpa-onnx)** via **JNI**. It offers intelligent sentence-by-sentence speech with real-time highlighting — no internet or cloud required.

---

<p align="center">
  <img src="screenshots/img_1.png" alt="TtsReader UI" width="30%" />
  &nbsp;
  <img src="screenshots/img_2.png" alt="Chrome Extension Context Menu" width="30%" />
</p>


<h3>Demo <a href="https://lnkd.in/p/ghxGBSdk">https://lnkd.in/p/ghxGBSdk</a></h3>


## ✨ Key Features

- 🖥️ **Kotlin Multiplatform Desktop App** (Jetpack Compose UI)
- 🎤 **Offline TTS** using Kokoro + sherpa-onnx + JNI
- 📖 **Smart Sentence Playback**: one sentence at a time with buffering
- 🔦 **Live Highlighting**: currently spoken sentence is highlighted
- 🌐 **Chrome Extension Support**:
  - Right-click selected text → “Play with TtsReader”


## 🚀 Getting Started 


### 📦 Windows Installer ![GitHub Downloads (all assets, all releases)](https://img.shields.io/github/downloads/ys-pro-duction/TtsReader/total)


Download the prebuilt `.msi`:

* [TtsReader.msi](https://github.com/ys-pro-duction/TtsReader/releases)


--- 
### 🔧 Requirements

- JDK 17+
- Git
- Chrome (for browser extension)

### 🛠 Run from Source

```bash
git clone https://github.com/ys-pro-duction/TtsReader.git
cd TtsReader
./gradlew run
````


## 🌐 Browser Extension

(Optional) browser extension for sending selected text to the desktop app.

### Chrome extention

1. Download: [chrome-extension.zip](https://github.com/ys-pro-duction/TtsReader/releases)
2. Extract it
3. Open Chrome → `chrome://extensions`
4. Enable **Developer Mode**
5. Click **“Load unpacked”** → select extracted folder
6. Select text on any webpage → right-click → **“Play with TtsReader”**

### Firefox addon
https://addons.mozilla.org/en-US/firefox/addon/ttsreader-text-to-speech/

TtsReader desktop app must be running in the background.

---

## 🎙️ How It Works

* Text is broken into sentences using punctuation
* The app:

    * **Plays 1 sentence at a time**
    * **Buffers the next 2 sentences** for seamless delivery
    * **Highlights the active sentence** in the UI
* Playback uses native **Kokoro ONNX** models via **JNI**

---

## 📦 Models

First launch will download required models to:

```
~/.TtsReader/
├── model.onnx
├── tokens.txt
├── config.json
```

---


**TtsReader** — Read your text aloud, smoothly and intelligently, all offline.

