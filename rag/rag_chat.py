import requests
import psycopg2
from dotenv import load_dotenv
import os

load_dotenv()

# -----------------------------
# PostgreSQL connection
# -----------------------------
connection = psycopg2.connect(
    host=os.getenv("DB_HOST"),
    port=os.getenv("DB_PORT"),
    database=os.getenv("DB_NAME"),
    user=os.getenv("DB_USER"),
    password=os.getenv("DB_PASSWORD")
)

cursor = connection.cursor()


# -----------------------------
# Get user question
# -----------------------------
question = input("\nAsk about Solapur tourism: ")


# -----------------------------
# Create question embedding
# -----------------------------
embedding_response = requests.post(
    "http://localhost:11434/api/embeddings",
    json={
        "model": "nomic-embed-text",
        "prompt": question
    }
)

embedding_response.raise_for_status()

query_embedding = embedding_response.json()["embedding"]


# -----------------------------
# Retrieve relevant chunks
# -----------------------------
cursor.execute(
    """
    SELECT content
    FROM rag_documents
    ORDER BY embedding <=> %s::vector
    LIMIT 5;
    """,
    (query_embedding,)
)

results = cursor.fetchall()


# -----------------------------
# Combine retrieved context
# -----------------------------
context = "\n\n".join(
    result[0] for result in results
)


# -----------------------------
# Create RAG prompt
# -----------------------------
prompt = f"""
You are a helpful AI tourism assistant for Solapur.

Answer the user's question using ONLY the information provided
in the context below.

If the answer is not present in the context, say:
"I don't have enough information in the tourism database."

Do not invent facts.

Context:
{context}

User question:
{question}

Answer:
"""


# -----------------------------
# Generate answer using Ollama
# -----------------------------
llm_response = requests.post(
    "http://localhost:11434/api/generate",
    json={
        "model": "llama3.2:3b",
        "prompt": prompt,
        "stream": False
    }
)

llm_response.raise_for_status()

answer = llm_response.json()["response"]


# -----------------------------
# Display answer
# -----------------------------
print("\n================================")
print("SOLAPUR TOURISM AI")
print("================================")
print(answer)
print("================================")


cursor.close()
connection.close()