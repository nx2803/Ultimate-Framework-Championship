from google import genai
import os
from dotenv import load_dotenv

# .env 파일 로드 (현재 디렉토리 기준)
load_dotenv()

api_key = os.getenv("UFCLLM_GEMINI_API_KEY")

print(f"--- Gemini Diagnostics (New SDK) ---")
print(f"API Key (masked): {api_key[:8]}...{api_key[-4:] if api_key else 'None'}")

if not api_key:
    print("Error: API Key not found in .env")
    exit(1)

client = genai.Client(api_key=api_key)

print("\nListing available models for this API Key:")
try:
    for m in client.models.list():
        print(f"- {m.name}")
except Exception as e:
    print(f"Failed to list models: {str(e)}")


print("\n--- End of Diagnostics ---")
