import requests
import psycopg2
from dotenv import load_dotenv
import os
import json

load_dotenv()

# ============================================================
# DATABASE CONNECTION
# ============================================================

connection = psycopg2.connect(
    host=os.getenv("DB_HOST"),
    port=os.getenv("DB_PORT"),
    database=os.getenv("DB_NAME"),
    user=os.getenv("DB_USER"),
    password=os.getenv("DB_PASSWORD")
)

cursor = connection.cursor()

print("Connected to PostgreSQL successfully!")


# ============================================================
# CLEAR OLD EMBEDDINGS
# ============================================================
# IMPORTANT:
# Remove the old 231 records before inserting the new 186
# correctly chunked records.

print("Clearing old RAG documents...")

cursor.execute("""
    TRUNCATE TABLE rag_documents RESTART IDENTITY;
""")

connection.commit()

print("Old embeddings cleared.")


# ============================================================
# READ NEW CHUNKS
# ============================================================
# chunk_text.py now creates:
#
# chunks.txt
#
# NOT:
#
# solapur_chunks.txt

with open(
    "chunks.txt",
    "r",
    encoding="utf-8"
) as file:

    text = file.read()


# ============================================================
# SPLIT CHUNKS
# ============================================================

raw_chunks = text.split("--- CHUNK ")

chunks = []

for chunk in raw_chunks:

    chunk = chunk.strip()

    if not chunk:
        continue

    lines = chunk.splitlines()

    place = "Solapur Tourism"
    content_lines = []

    for line in lines:

        # Extract place
        if line.startswith("PLACE:"):

            place = line.replace(
                "PLACE:",
                "",
                1
            ).strip()

        # Ignore chunk separator
        elif line.startswith("---"):

            continue

        # Normal content
        else:

            content_lines.append(line)

    content = "\n".join(
        content_lines
    ).strip()

    if content:

        chunks.append({
            "place": place,
            "content": content
        })


# ============================================================
# SHOW CHUNK COUNT
# ============================================================

print()
print("======================================")
print(f"Total chunks found: {len(chunks)}")
print("======================================")
print()


# ============================================================
# SAFETY CHECK
# ============================================================

if len(chunks) == 0:

    print("ERROR: No chunks found!")

    cursor.close()
    connection.close()

    exit()


if len(chunks) != 186:

    print(
        f"WARNING: Expected around 186 chunks, "
        f"but found {len(chunks)} chunks."
    )

    print(
        "Check chunks.txt before continuing."
    )

    cursor.close()
    connection.close()

    exit()


# ============================================================
# GENERATE EMBEDDINGS AND INSERT
# ============================================================

for i, chunk in enumerate(chunks, 1):

    place = chunk["place"]
    content = chunk["content"]

    # --------------------------------------------------------
    # Generate embedding using Ollama
    # --------------------------------------------------------

    response = requests.post(
        "http://localhost:11434/api/embeddings",
        json={
            "model": "nomic-embed-text",
            "prompt": content
        },
        timeout=120
    )

    response.raise_for_status()

    embedding = response.json()["embedding"]


    # --------------------------------------------------------
    # Check embedding dimension
    # --------------------------------------------------------

    if len(embedding) != 768:

        raise ValueError(
            f"Wrong embedding dimension for chunk {i}. "
            f"Expected 768, got {len(embedding)}."
        )


    # --------------------------------------------------------
    # Metadata
    # --------------------------------------------------------

    metadata = {
        "source": "SOLAPUR_city.docx",
        "place": place
    }


    # --------------------------------------------------------
    # Insert into PostgreSQL
    # --------------------------------------------------------

    cursor.execute(
        """
        INSERT INTO rag_documents
        (
            content,
            metadata,
            embedding
        )
        VALUES
        (
            %s,
            %s,
            %s
        )
        """,
        (
            content,
            json.dumps(metadata),
            embedding
        )
    )


    print(
        f"Inserted chunk {i}/{len(chunks)} - {place}"
    )


# ============================================================
# COMMIT
# ============================================================

connection.commit()


# ============================================================
# CLOSE DATABASE
# ============================================================

cursor.close()
connection.close()


# ============================================================
# SUCCESS
# ============================================================

print()
print("======================================")
print("All embeddings inserted successfully!")
print(f"Total embeddings inserted: {len(chunks)}")
print("======================================")