from openai import OpenAI
from dotenv import load_dotenv
import os

load_dotenv()

client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

try:
    response = client.embeddings.create(
        model="text-embedding-3-small",
        input="Solapur tourism test"
    )

    print("OpenAI API connection successful!")
    print("Embedding dimensions:", len(response.data[0].embedding))

except Exception as e:
    print("OpenAI connection failed:")
    print(e)