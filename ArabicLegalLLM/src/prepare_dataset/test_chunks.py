import os
import json
from math import ceil

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

# Function to process articles and generate QA pairs
def process_articles_and_generate_qa(articles_file, qa_output_file):
    with open(articles_file, "r", encoding="utf-8") as f:
        articles = json.load(f)

        qa_id = 1

        first_entry = True

        for article in articles:
            article_id = article["id"]
            article_content = article["article_content"]

            print(f"Processing Article ID {article_id}...")

            # Split long articles into manageable chunks
            chunks = split_text_into_chunks(article_content)

            for chunk_id, chunk in enumerate(chunks, start=1):
                print(f"Processing chunk {chunk_id} of Article ID {article_id}...{chunk}")
                    
    print(f"QA pairs saved to {qa_output_file}")

# Main function to execute the pipeline
if __name__ == "__main__":
    articles_input_file = "articles.json"  # Input JSON file with articles
    qa_output_file = "qa_pairs.json"      # Output JSON file for QA pairs

    process_articles_and_generate_qa(articles_input_file, qa_output_file)
