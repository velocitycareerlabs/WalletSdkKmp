# velocityexchangeverifiers

**Multiplatform SDK for Verifiable Credential JWT Verification**

`velocityexchangeverifiers` is a Kotlin Multiplatform library designed for verifying Verifiable Credential JWTs in compliance with [OpenID4VC](https://openid.net/wg/connect/) standards. This SDK targets **Android**, **iOS**, **JavaScript**, and **WebAssembly**, enabling cross-platform credential verification logic with a single codebase.

---

## ✨ Features

- ✅ Verifies VC JWT signatures, claims, and structures
- 🔒 Supports multiple signature algorithms (ES256, ES256K, etc.)
- 🧩 Modular verifier design for customization
- 🧪 Shared test suite for all platforms
- 🧬 Kotlinx Serialization integration
- 📦 Available for Android (AAR), iOS (XCFramework), JS (`.js`), and WASM (`.wasm`) builds


⸻

🛠 Build Targets

Run this command to build all platforms:

./gradlew :velocityexchangeverifiers:assembleAllTargets

Output artifacts will be available in:
	•	build/outputs/aar/ – Android AAR
	•	build/XCFrameworks/release/ – iOS XCFramework
	•	build/dist/js/ – JS library
	•	build/dist/wasmJs/ – WASM files

⸻

🧪 Testing

./gradlew :velocityexchangeverifiers:allTests
