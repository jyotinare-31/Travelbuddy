import re

INPUT_FILE = "solapur_tourism.txt"
OUTPUT_FILE = "chunks.txt"

MAX_CHARS = 1200


# ---------------------------------------------------------
# PLACE HEADING DETECTION
# ---------------------------------------------------------

def detect_place_heading(line):
    clean = line.strip()

    if not clean:
        return None

    # Remove extra spaces
    clean = re.sub(r"\s+", " ", clean)

    # -----------------------------------------------------
    # Explicit known place headings
    # -----------------------------------------------------

    known_places = {
        "siddheshwar temple solapur": "Siddheshwar Temple",
        "siddheshwar temple": "Siddheshwar Temple",

        "solapur bhuikot killa": "Solapur Bhuikot Fort",
        "solapur bhuikot fort": "Solapur Bhuikot Fort",

        "mahatma gandhi zoo": "Mahatma Gandhi Zoo",

        "great indian bustard sanctuary": "Great Indian Bustard Sanctuary",

        "kambar lake / moti baug lake": "Kambar Lake / Moti Baug Lake",
        "kambar lake": "Kambar Lake",

        "ujjani dam": "Ujjani Dam",

        "kudal sangam": "Kudal Sangam",

        "the shri revan siddheshwar temple": "Shri Revan Siddheshwar Temple",
        "revanisiddheshwar temple": "Revanisiddheshwar Temple",
        "revan siddheshwar temple": "Shri Revan Siddheshwar Temple",

        "asar mahal": "Asar Mahal",

        "bara kaman": "Bara Kaman",
    }

    normalized = clean.lower()

    if normalized in known_places:
        return known_places[normalized]

    # -----------------------------------------------------
    # Numbered place headings
    #
    # Examples:
    # 4. Great Indian Bustard Sanctuary
    # 5. Kambar Lake / Moti Baug Lake
    # 9. Asar Mahal
    # 10. Bara Kaman
    #
    # IMPORTANT:
    # This intentionally requires the text after the number
    # to start with a letter.
    # -----------------------------------------------------

    match = re.fullmatch(
        r"\d+\.\s*([A-Za-z][A-Za-z\s/&()\-]+)$",
        clean
    )

    if match:
        heading = match.group(1).strip()

        # Remove extra spaces
        heading = re.sub(r"\s+", " ", heading)

        # -------------------------------------------------
        # Ignore headings that are obviously NOT places
        # -------------------------------------------------

        ignored_headings = {
            "introduction",
            "history",
            "location",
            "how to reach",
            "best time to visit",
            "timings",
            "entry fee",
            "facilities",
            "activities",
            "overview",
            "references",
            "conclusion",
        }

        if (
            heading.lower() not in ignored_headings
            and len(heading) >= 4
            and not re.fullmatch(r"\d+", heading)
        ):
            return heading

    return None


# ---------------------------------------------------------
# READ SOURCE TEXT
# ---------------------------------------------------------

with open(INPUT_FILE, "r", encoding="utf-8") as f:
    text = f.read()


# ---------------------------------------------------------
# SPLIT INTO LINES
# ---------------------------------------------------------

lines = text.splitlines()


# ---------------------------------------------------------
# CHUNKING
# ---------------------------------------------------------

chunks = []

current_place = "Solapur Tourism"
current_text = []


def save_chunk():
    global current_text

    if not current_text:
        return

    content = "\n".join(current_text).strip()

    if not content:
        current_text = []
        return

    chunks.append({
        "place": current_place,
        "content": content
    })

    current_text = []


for line in lines:

    clean = line.strip()

    # Skip completely empty lines
    if not clean:
        continue

    # -----------------------------------------------------
    # Check whether this line is a PLACE heading
    # -----------------------------------------------------

    detected_place = detect_place_heading(clean)

    if detected_place:

        # Save previous chunk before changing place
        save_chunk()

        current_place = detected_place

        # Do NOT put heading itself into content
        continue

    # -----------------------------------------------------
    # Ignore obvious rating/review lines
    # -----------------------------------------------------

    if re.fullmatch(
        r"[\d\s,.]+Google reviews?",
        clean,
        re.IGNORECASE
    ):
        continue

    # Examples:
    # 6 528 Google reviews
    # 6,528 Google reviews
    if "google reviews" in clean.lower():
        continue

    # Ignore standalone numbers
    if re.fullmatch(r"\d+", clean):
        continue

    # Ignore rating-like lines
    if re.fullmatch(
        r"[\d\s.,()/·\-]+",
        clean
    ):
        continue

    # -----------------------------------------------------
    # Add text
    # -----------------------------------------------------

    current_text.append(clean)

    # -----------------------------------------------------
    # Create chunk when it becomes large
    # -----------------------------------------------------

    current_length = len("\n".join(current_text))

    if current_length >= MAX_CHARS:

        # Try to split at a sentence boundary
        content = "\n".join(current_text)

        # Find last sentence ending before MAX_CHARS
        split_position = max(
            content.rfind(". ", 0, MAX_CHARS),
            content.rfind("? ", 0, MAX_CHARS),
            content.rfind("! ", 0, MAX_CHARS)
        )

        if split_position > 300:
            first_part = content[:split_position + 1].strip()
            remaining_part = content[split_position + 1:].strip()

            chunks.append({
                "place": current_place,
                "content": first_part
            })

            if remaining_part:
                current_text = [remaining_part]
            else:
                current_text = []

        else:
            save_chunk()


## ---------------------------------------------------------
# SAVE LAST CHUNK
# ---------------------------------------------------------

save_chunk()


# ---------------------------------------------------------
# REMOVE FINAL REVIEW/SUMMARY CHUNKS
# ---------------------------------------------------------
# The source document contains additional review information
# after the main tourism-place sections. These chunks contain
# multiple different places inside one chunk, so they should
# not inherit the previous PLACE metadata.

if len(chunks) >= 3:
    last_three = chunks[-3:]

    combined_text = "\n".join(
        chunk["content"] for chunk in last_three
    ).lower()

    review_markers = [
        "rating:",
        "number of reviews:",
        "review:"
    ]

    review_marker_count = sum(
        combined_text.count(marker)
        for marker in review_markers
    )

    if review_marker_count >= 3:
        chunks = chunks[:-3]


# ---------------------------------------------------------
# WRITE OUTPUT FILE
# ---------------------------------------------------------

with open(OUTPUT_FILE, "w", encoding="utf-8") as f:

    for i, chunk in enumerate(chunks, start=1):

        f.write(f"--- CHUNK {i} ---\n")
        f.write(f"PLACE: {chunk['place']}\n")
        f.write(chunk["content"])
        f.write("\n\n")


# ---------------------------------------------------------
# WRITE OUTPUT FILE
# ---------------------------------------------------------

with open(OUTPUT_FILE, "w", encoding="utf-8") as f:

    for i, chunk in enumerate(chunks, start=1):

        f.write(f"--- CHUNK {i} ---\n")
        f.write(f"PLACE: {chunk['place']}\n")
        f.write(chunk["content"])
        f.write("\n\n")


# ---------------------------------------------------------
# DISPLAY RESULTS
# ---------------------------------------------------------

print("Improved chunking completed!")
print(f"Total chunks: {len(chunks)}")
print()
print("First 60 chunks:")

for i, chunk in enumerate(chunks[60:], start=61):
    print(f"{i}. {chunk['place']}")