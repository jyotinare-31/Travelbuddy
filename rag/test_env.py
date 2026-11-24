from dotenv import load_dotenv
import os

load_dotenv()

print("API key loaded:", bool(os.getenv("OPENAI_API_KEY")))
print("DB host:", os.getenv("DB_HOST"))
print("DB port:", os.getenv("DB_PORT"))
print("DB name:", os.getenv("DB_NAME"))
print("DB user:", os.getenv("DB_USER"))