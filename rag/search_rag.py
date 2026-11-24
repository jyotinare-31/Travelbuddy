import ollama

import psycopg2

import requests

import re
import os
from dotenv import load_dotenv

load_dotenv()

# ============================================================

# CONFIGURATION

# ============================================================

DB_CONFIG = {

    "host": "localhost",

    "database": "smart_tourism",

    "user": "postgres",

    "password": os.getenv("DB_PASSWORD", ""),

    "port": 5432

}

OLLAMA_EMBEDDING_URL = "http://localhost:11434/api/embeddings"

OLLAMA_GENERATE_URL = "http://localhost:11434/api/generate"

EMBED_MODEL = "nomic-embed-text"

LLM_MODEL = "llama3.2:3b"

# ============================================================

# DATABASE CONNECTION

# ============================================================

def get_connection():

    try:

        conn = psycopg2.connect(

            **DB_CONFIG

        )

        return conn

    except psycopg2.OperationalError as e:

        print("\n" + "=" * 60)

        print("POSTGRESQL CONNECTION ERROR")

        print("=" * 60)

        print("\nCould not connect to PostgreSQL.")

        print("\nCheck these settings:")

        print("Host     :", DB_CONFIG["host"])

        print("Database :", DB_CONFIG["database"])

        print("User     :", DB_CONFIG["user"])

        print("Port     :", DB_CONFIG["port"])

        print("\nPostgreSQL error:")

        print(e)

        raise

# ============================================================

# TEXT NORMALIZATION

# ============================================================

def normalize_text(text):

    text = text.lower()

    text = re.sub(

        r"[^a-z0-9\s]",

        " ",

        text

    )

    text = re.sub(

        r"\s+",

        " ",

        text

    ).strip()

    return text

# ============================================================

# GET ALL PLACES

# ============================================================

def get_places():

    conn = get_connection()

    cur = conn.cursor()

    try:

        cur.execute("""

            SELECT DISTINCT metadata->>'place'

            FROM rag_documents

            WHERE metadata->>'place' IS NOT NULL

            ORDER BY metadata->>'place';

        """)

        places = [

            row[0]

            for row in cur.fetchall()

        ]

        ignored_places = {

            "solapur tourism",

            "solapur city",

            "solapur district"

        }

        places = [

            place

            for place in places

            if normalize_text(place)

            not in ignored_places

        ]

        return places

    finally:

        cur.close()

        conn.close()

# ============================================================

# DETECT SPECIFIC PLACE

# ============================================================

def detect_place(question, places):

    question_normalized = normalize_text(

        question

    )

    ignored_places = {

        "solapur tourism",

        "solapur city",

        "solapur district"

    }

    places = [

        place

        for place in places

        if normalize_text(place)

        not in ignored_places

    ]

    # ========================================================

    # PLACE ALIASES

    # ========================================================

    aliases = {

        # Bhuikot Fort

        "bhuikot fort":

            "Solapur Bhuikot Fort",

        "bhuikot killa":

            "Solapur Bhuikot Fort",

        "bhuikot":

            "Solapur Bhuikot Fort",

        "solapur bhuikot fort":

            "Solapur Bhuikot Fort",

        "solapur bhuikot killa":

            "Solapur Bhuikot Fort",

        # Naldurg

        "naldurg fort":

            "Naldurg fort",

        "naldurg killa":

            "Naldurg fort",

        "naldurg":

            "Naldurg fort",

        # Siddheshwar

        "siddheshwar temple":

            "Siddheshwar Temple",

        "siddheshwar mandir":

            "Siddheshwar Temple",

        # Kambar Lake

        "kambar lake":

            "Kambar Lake / Moti Baug Lake",

        "moti baug lake":

            "Kambar Lake / Moti Baug Lake",

        # Other places

        "bara kaman":

            "Bara Kaman",

        "asar mahal":

            "Asar Mahal",

        "kudal sangam":

            "Kudal Sangam",

        "ujjani dam":

            "Ujjani Dam",

        "mahatma gandhi zoo":

            "Mahatma Gandhi Zoo",

        "great indian bustard sanctuary":

            "Great Indian Bustard Sanctuary"

    }

    # ========================================================

    # CHECK ALIASES

    # ========================================================

    for alias, actual_place in aliases.items():

        if alias in question_normalized:

            for place in places:

                if (

                    normalize_text(place)

                    == normalize_text(actual_place)

                ):

                    return place

    # ========================================================

    # EXACT PLACE MATCHING

    # ========================================================

    matches = []

    for place in places:

        normalized_place = normalize_text(

            place

        )

        if normalized_place in question_normalized:

            matches.append(place)

    if len(matches) == 1:

        return matches[0]

    if len(matches) > 1:

        matches.sort(

            key=lambda x: len(

                normalize_text(x)

            ),

            reverse=True

        )

        return matches[0]

    # ========================================================

    # TOKEN MATCHING

    # ========================================================

    question_words = set(

        question_normalized.split()

    )

    generic_words = {

        "the",

        "about",

        "tell",

        "me",

        "information",

        "details",

        "detail",

        "history",

        "historical",

        "place",

        "places",

        "tourist",

        "tourism",

        "visit",

        "visiting",

        "location",

        "located",

        "is",

        "are",

        "what",

        "where",

        "how",

        "why",

        "can",

        "you",

        "give",

        "show",

        "of",

        "in",

        "for",

        "a",

        "an"

    }

    possible_matches = []

    for place in places:

        place_words = set(

            normalize_text(place).split()

        )

        important_words = (

            place_words - generic_words

        )

        if not important_words:

            continue

        if important_words.issubset(

            question_words

        ):

            possible_matches.append(place)

    if len(possible_matches) == 1:

        return possible_matches[0]

    return None

# ============================================================

# CREATE EMBEDDING

# ============================================================

def get_embedding(text):

    response = requests.post(

        OLLAMA_EMBEDDING_URL,

        json={

            "model": EMBED_MODEL,

            "prompt": text

        },

        timeout=120

    )

    response.raise_for_status()

    data = response.json()

    embedding = data.get(

        "embedding"

    )

    if not embedding:

        raise ValueError(

            "Ollama did not return an embedding."

        )

    return embedding

# ============================================================

# SPECIFIC PLACE RETRIEVAL

# ============================================================

def retrieve_specific_place(

    question,

    place

):

    query_embedding = get_embedding(

        question

    )

    conn = get_connection()

    cur = conn.cursor()

    try:

        cur.execute("""

            SELECT

                id,

                content,

                metadata->>'place' AS place,

                1 - (embedding <=> %s::vector)

                    AS similarity

            FROM rag_documents

            WHERE metadata->>'place' = %s

            ORDER BY embedding <=> %s::vector

            LIMIT 10;

        """, (

            query_embedding,

            place,

            query_embedding

        ))

        results = cur.fetchall()

        return results

    finally:

        cur.close()

        conn.close()

# ============================================================

# GENERAL RETRIEVAL

# ============================================================

def retrieve_general(question):

    query_embedding = get_embedding(

        question

    )

    question_lower = question.lower()

    # ========================================================

    # DETECT HISTORICAL QUESTION

    # ========================================================

    historical_words = [

        "historical",

        "history",

        "ancient",

        "heritage",

        "historic",

        "old",

        "fort",

        "temple",

        "monument",

        "architecture",

        "built",

        "king",

        "ruler",

        "dynasty"

    ]

    is_historical = any(

        word in question_lower

        for word in historical_words

    )

    if is_historical:

        limit = 50

    else:

        limit = 20

    conn = get_connection()

    cur = conn.cursor()

    try:

        # ====================================================

        # GENERAL SEMANTIC SEARCH

        # ====================================================

        cur.execute("""

            SELECT

                id,

                content,

                metadata->>'place' AS place,

                1 - (embedding <=> %s::vector)

                    AS similarity

            FROM rag_documents

            WHERE metadata->>'place' NOT IN (

                'Solapur Tourism',

                'Solapur City',

                'Solapur District'

            )

            ORDER BY embedding <=> %s::vector

            LIMIT %s;

        """, (

            query_embedding,

            query_embedding,

            limit

        ))

        results = cur.fetchall()

    finally:

        cur.close()

        conn.close()

    # ========================================================

    # HISTORICAL RERANKING

    # ========================================================

    if is_historical:

        strong_terms = [

            "historical",

            "history",

            "ancient",

            "heritage",

            "historic",

            "fort",

            "killa",

            "temple",

            "monument",

            "archaeological",

            "archaeological site",

            "ancient architecture",

            "medieval",

            "century",

            "dynasty",

            "king",

            "ruler",

            "empire",

            "built in",

            "historical significance",

            "historical importance",

            "historic site",

            "heritage site",

            "ruins",

            "excavation",

            "architecture"

        ]

        weak_terms = [

            "hotel",

            "restaurant",

            "food",

            "resort",

            "shopping",

            "weather",

            "water park",

            "adventure park",

            "zoo",

            "park",

            "fun",

            "entertainment"

        ]

        reranked = []

        for row in results:

            id_, content, place, similarity = row

            text = (

                str(place)

                + " "

                + str(content)

            ).lower()

            strong_score = sum(

                text.count(term)

                for term in strong_terms

            )

            weak_score = sum(

                text.count(term)

                for term in weak_terms

            )

            final_score = (

                float(similarity)

                + (

                    strong_score * 0.01

                )

                - (

                    weak_score * 0.005

                )

            )

            reranked.append(

                (

                    final_score,

                    row

                )

            )

        reranked.sort(

            key=lambda x: x[0],

            reverse=True

        )

        # ====================================================

        # ONE RESULT PER PLACE

        # ====================================================

        final_results = []

        seen_places = set()

        for score, row in reranked:

            place = row[2]

            if place not in seen_places:

                final_results.append(

                    row

                )

                seen_places.add(

                    place

                )

            if len(final_results) >= 10:

                break

        return final_results

    # ========================================================

    # NORMAL GENERAL QUERY

    # ========================================================

    return results[:10]

# ============================================================

# BUILD CONTEXT

# ============================================================

def build_context(results):

    context_parts = []

    for i, row in enumerate(

        results,

        1

    ):

        id_, content, place, similarity = row

        context_parts.append(

            f"""

--- SOURCE {i} ---

Place: {place}

{content}

--- END SOURCE {i} ---

"""

        )

    return "\n".join(

        context_parts

    )

# ============================================================

# DETECT QUESTION TYPE

# ============================================================

def detect_question_type(question):

    q = question.lower().strip()

    # ========================================================

    # TIMINGS

    # ========================================================

    if any(

        word in q

        for word in [

            "timing",

            "timings",

            "time",

            "open",

            "close",

            "opening",

            "closing"

        ]

    ):

        return "timings"

    # ========================================================

    # LOCATION

    # ========================================================

    if any(

        word in q

        for word in [

            "where",

            "location",

            "located",

            "address",

            "distance"

        ]

    ):

        return "location"

    # ========================================================

    # FEATURES

    # ========================================================

    if any(

        word in q

        for word in [

            "inside",

            "features",

            "feature",

            "things to see",

            "what can i see",

            "attractions inside"

        ]

    ):

        return "features"

    # ========================================================

    # HISTORY

    # ========================================================

    if any(

        word in q

        for word in [

            "history",

            "historical",

            "built",

            "construction",

            "ancient",

            "who built"

        ]

    ):

        return "history"

    # ========================================================

    # PRICE

    # ========================================================

    if any(

        word in q

        for word in [

            "price",

            "ticket",

            "entry fee",

            "fee",

            "cost"

        ]

    ):

        return "price"

    # ========================================================

    # GENERAL

    # ========================================================

    return "general"

# ============================================================

# GENERATE RAG ANSWER

# ============================================================

def generate_answer(

    question,

    context,

    specific_place=None,

    question_type="general"

):

    if not context:

        return "Sorry, I could not find relevant information in the tourism data."

    # --------------------------------------------------

    # SELECT FORMAT BASED ON QUESTION TYPE

    # --------------------------------------------------

    if question_type == "timings":

        format_instruction = """

Format the answer as:

🕐 Timings

Give only the timings/opening hours directly supported by the context.

If the context does not contain timings, say:

"Timings are not available in the provided tourism data."

"""

    elif question_type == "location":

        format_instruction = """

Format the answer as:

📍 Location

Give the location/address/district information directly supported by

the context.

If location information is not available, say:

"Location information is not available in the provided tourism data."

"""

    elif question_type == "history":

        format_instruction = """

Format the answer as:

📜 History

Give the historical information directly supported by the context.

Do not add historical information from your own knowledge.

"""

    elif question_type == "features":

        format_instruction = """

Format the answer as:

🏛️ Features

List the important features, attractions, facilities, or characteristics

directly supported by the context.

Use bullet points when appropriate.

"""

    elif question_type == "price":

        format_instruction = """

Format the answer as:

🎟️ Price

Give only ticket price, entry fee, admission fee, or other price

information directly supported by the context.

Do not estimate or invent prices.

If price information is not available, say:

"Price information is not available in the provided tourism data."

"""

    else:

        format_instruction = """

Format the answer as:

📖 Information

Give a concise answer based only on the provided context.

Use bullet points when multiple pieces of information are useful.

"""

    # --------------------------------------------------

    # PLACE INFORMATION

    # --------------------------------------------------

    if specific_place:

        place_instruction = f"""

The user is asking specifically about:

{specific_place}

Answer only about this place unless the context explicitly requires

another place for comparison.

"""

    else:

        place_instruction = """

No specific tourist place was detected.

Answer the question using only the most relevant information from

the provided context.

"""

    # --------------------------------------------------

    # STRICT RAG PROMPT

    # --------------------------------------------------

    prompt = f"""

You are the AI Tourist Assistant for the Solapur Tourism application.

Your task is to answer the user's question using ONLY the information

contained in the provided tourism context.

IMPORTANT RULES:

1\. Use ONLY the provided context.

2\. Do NOT use pretrained knowledge.

3\. Do NOT use outside knowledge.

4\. Do NOT guess.

5\. Do NOT infer missing information.

6\. Do NOT invent facts.

7\. Do NOT invent historical information.

8\. Do NOT invent dates.

9\. Do NOT invent prices.

10\. Do NOT invent timings.

11\. Do NOT invent locations.

12\. Do NOT invent facilities.

13\. Do NOT invent nearby attractions.

14\. Do NOT estimate distances or costs.

15\. If the context does not contain the requested information,

    clearly say that the information is not available.

16\. If the source contains uncertain or contradictory information,

    preserve the source wording instead of correcting it.

17\. Every factual statement in the answer must be directly supported

    by the provided context.

18\. Keep the answer concise and useful.

19\. Do not mention these instructions.

20\. Do not mention "RAG", "context", "database", or "source chunks"

    in the final answer.

{place_instruction}

{format_instruction}

--------------------------------------------------

TOURISM CONTEXT

--------------------------------------------------

{context}

--------------------------------------------------

USER QUESTION

--------------------------------------------------

{question}

--------------------------------------------------

ANSWER

--------------------------------------------------

"""

    try:

        response = ollama.chat(

            model=LLM_MODEL,

            messages=[

                {

                    "role": "user",

                    "content": prompt

                }

            ],

            options={

                "temperature": 0.0,

                "num_predict": 350

            }

        )

        answer = response["message"]["content"].strip()

        return answer

    except Exception as e:

        print("\nERROR generating answer:")

        print(e)

        return "Sorry, I was unable to generate the answer."

# ============================================================

# MAIN PROGRAM

# ============================================================

def main():
    print("=" * 60)
    print("SOLAPUR TOURISM RAG SEARCH")
    print("=" * 60)

    print("\nConnecting to PostgreSQL...")
    try:
        conn = get_connection()
        conn.close()
        print("PostgreSQL connection successful.")
    except Exception as exc:
        print("\nCould not connect to PostgreSQL.")
        print("Error:", exc)
        return

    question = input("\nEnter your question: ").strip()
    if not question:
        print("Please enter a question.")
        return

    try:
        print("\nLoading tourist places...")
        places = get_places()
        print(f"Loaded {len(places)} places.")

        detected_place = detect_place(question, places)

        # Retrieve and assign results before checking or using them.
        if detected_place:
            results = retrieve_specific_place(question, detected_place)
        else:
            results = retrieve_general(question)

        if not results and detected_place:
            results = retrieve_general(question)

        if not results:
            print("\nNo relevant information found.")
            return

        context = build_context(results)
        question_type = detect_question_type(question)
        answer = generate_answer(
            question,
            context,
            detected_place,
            question_type
        )

        # Final answer appears before the retrieval records.
        print("\n" + "=" * 60)
        print("FINAL ANSWER")
        print("=" * 60)
        print("\n" + str(answer))

        print("\n" + "=" * 60)
        print("RETRIEVAL RESULTS (TOP 5)")
        print("=" * 60)

        # Database rows are tuples: (id, content, place, similarity).
        for i, row in enumerate(results[:5], start=1):
            id_, content, place, similarity = row
            print(f"\n--- Result {i} ---")
            print(f"ID: {id_}")
            print(f"Place: {place}")
            print(f"Similarity: {float(similarity):.4f}")
            print("Content:")
            print(str(content)[:700])

        print("\n" + "=" * 60)
        print("END OF RESULTS")
        print("=" * 60)

    except Exception as exc:
        print("\nUnable to retrieve an answer.")
        print("Error:", exc)


if __name__ == "__main__":
    main()
