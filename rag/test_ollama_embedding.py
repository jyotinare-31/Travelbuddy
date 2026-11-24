import requests

response = requests.post(
    "http://localhost:11434/api/embeddings",
    json={
        "model": "nomic-embed-text",
        "prompt": "Solapur tourism"
    }
)

data = response.json()

print("Embedding dimensions:", len(data["embedding"]))