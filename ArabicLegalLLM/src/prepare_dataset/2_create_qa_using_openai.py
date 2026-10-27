import os
import json
from openai import OpenAI
from math import ceil

client = OpenAI(
    api_key="",
)

# Define the OpenAI model to use
MODEL = "gpt-3.5-turbo"  # Cost-effective model

# Function to split text into manageable chunks
def split_text_into_chunks(text, max_chunk_size=3500):
    words = text.split()
    chunks = []
    current_chunk = []

    for word in words:
        current_chunk.append(word)
        if len(" ".join(current_chunk)) > max_chunk_size:
            chunks.append(" ".join(current_chunk))
            current_chunk = []

    if current_chunk:
        chunks.append(" ".join(current_chunk))

    return chunks

# Function to generate questions and answers for a given article chunk
def generate_questions_and_answers(article_id, chunk_id, article_chunk):
    num_questions = max(1, len(article_chunk) // 100)  # 1 question per 100 characters
    prompt = (
        f"You are an expert in legal matters. Based on the following legal article segment, generate {num_questions} "
        f"question-and-answer pairs in Arabic. Use the format: Q: [Question] A: [Answer]. "
        f"Each question should test comprehension of the segment. "
        f"Here is the segment:\n\n{article_chunk}\n\n"
    )

    try:
        response = client.chat.completions.create(
            model=MODEL,
            messages=[
                {"role": "system", "content": "You are an assistant specializing in creating question-answer pairs."},
                {"role": "user", "content": prompt},
            ],
            max_tokens=2048,  # Limit tokens for output generation
            temperature=0.7,
        )

        # Extract response content
        response_content = response.choices[0].message.content.strip()

        # Parse QA pairs using the structured format "Q: [Question] A: [Answer]"
        qa_pairs = []
        qa_lines = response_content.split("\n")
        for i in range(0, len(qa_lines), 2):
            if i + 1 < len(qa_lines) and qa_lines[i].startswith("Q:") and qa_lines[i + 1].startswith("A:"):
                question = qa_lines[i][3:].strip()  # Remove "Q:" prefix
                answer = qa_lines[i + 1][3:].strip()  # Remove "A:" prefix
                qa_pairs.append({"question": question, "answer": answer})

        return qa_pairs

    except Exception as e:
        print(f"Error generating QA pairs for article {article_id}, chunk {chunk_id}: {e}")
        return []

# Function to process articles and generate QA pairs
def process_articles_and_generate_qa(articles_file, qa_output_file):
    with open(articles_file, "r", encoding="utf-8") as f:
        articles = json.load(f)

    qa_id = 1

    # Open the output file in append mode
    with open(qa_output_file, "w", encoding="utf-8") as f:
        f.write("[")  # Start the JSON array
        first_entry = True

        for article in articles:
            article_id = article["id"]
            article_content = article["article_content"]

            print(f"Processing Article ID {article_id}...")

            # Split long articles into manageable chunks
            chunks = split_text_into_chunks(article_content)

            for chunk_id, chunk in enumerate(chunks, start=1):
                print(f"Processing chunk {chunk_id} of Article ID {article_id}...")

                # Generate QA pairs for the chunk
                generated_qa = generate_questions_and_answers(article_id, chunk_id, chunk)

                # Write generated QA pairs directly to the file
                for qa in generated_qa:
                    qa_entry = {
                        "id": qa_id,
                        "article_id": article_id,
                        "chunk_id": chunk_id,
                        "question": qa["question"],
                        "answer": qa["answer"]
                    }
                    if not first_entry:
                        f.write(",\n")
                    f.write(json.dumps(qa_entry, ensure_ascii=False, indent=4))
                    first_entry = False
                    qa_id += 1

        f.write("]")  # Close the JSON array

    print(f"QA pairs saved to {qa_output_file}")

# Main function to execute the pipeline
if __name__ == "__main__":
    articles_input_file = "json/1_extracted_raw_articles.json"  # Input JSON file with articles
    qa_output_file = "json/2_qa_pairs_generated_by_openai.json"      # Output JSON file for QA pairs

    process_articles_and_generate_qa(articles_input_file, qa_output_file)
