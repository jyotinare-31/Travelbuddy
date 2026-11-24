from docx import Document

doc = Document("SOLAPUR_city.docx")

text_parts = []

# Extract paragraphs
for paragraph in doc.paragraphs:
    text = paragraph.text.strip()
    if text:
        text_parts.append(text)

# Extract tables
for table in doc.tables:
    for row in table.rows:
        row_text = " | ".join(
            cell.text.strip()
            for cell in row.cells
        )
        if row_text:
            text_parts.append(row_text)

# Combine extracted text
text = "\n".join(text_parts)

# Save as TXT
with open("solapur_tourism.txt", "w", encoding="utf-8") as file:
    file.write(text)

print("Text extraction completed successfully!")
print("Characters extracted:", len(text))