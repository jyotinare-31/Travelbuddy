
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
import traceback

import search_rag as rag

app = FastAPI(title="Smart Tourism RAG API")

# Allow your frontend to call the API during local testing.
# Restrict this to your deployed website domain before production.
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=False,
    allow_methods=["GET", "POST"],
    allow_headers=["*"],
)


class ChatRequest(BaseModel):
    question: str = Field(min_length=1, max_length=2000)


@app.get("/")
def home():
    return {
        "status": "running",
        "service": "Smart Tourism RAG API"
    }


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/api/chat")
def chat(request: ChatRequest):
    question = request.question.strip()

    if not question:
        raise HTTPException(
            status_code=400,
            detail="Please enter a question."
        )

    try:
        # Detect whether the question mentions a known tourist place.
        places = rag.get_places()
        detected_place = rag.detect_place(question, places)

        # Reuse your existing retrieval functions.
        if detected_place:
            results = rag.retrieve_specific_place(
                question, detected_place
            )
        else:
            results = rag.retrieve_general(question)

        # Fall back to general retrieval if place-specific
        # retrieval returns no results.
        if not results and detected_place:
            results = rag.retrieve_general(question)

        if not results:
            return {
                "answer": (
                    "I couldn't find enough information in the "
                    "tourism knowledge base to answer that question."
                ),
                "sources": []
            }

        context = rag.build_context(results)
        question_type = rag.detect_question_type(question)

        answer = rag.generate_answer(
            question,
            context,
            specific_place=detected_place,
            question_type=question_type
        )

        # Return only the fields needed by the frontend.
        sources = []
        for item in results[:5]:
            if len(item) >= 4:
                doc_id, content, place, similarity = item[:4]
                sources.append({
                    "id": doc_id,
                    "place": place,
                    "similarity": float(similarity),
                    "content": str(content)[:300]
                })

        return {
            "answer": answer,
            "detected_place": detected_place,
            "sources": sources
        }

    except Exception as exc:
        traceback.print_exc()
        raise HTTPException(
            status_code=500,
            detail="The RAG assistant encountered an error. "
                   "Check the Python terminal for details."
        ) from exc
