import os
import re
import json
from docx import Document

folder_path = "D:\\current_local\\DATASETS\\Jordan Law Dataset for LLM Fine Tunning\\copy docx\\laws"

# Function to extract text from a DOCX
def extract_text_from_docx(docx_path):
    doc = Document(docx_path)
    text = ""
    for paragraph in doc.paragraphs:
        text += paragraph.text + "\n"
    return text

# Function to split text into articles
def split_into_articles(text):
    articles = re.split(r"(?=\b(?:\u0627\u0644\u0645\u0627\u062F\u0629|\u0627\u0644\u0645\u0627\u062F\u0647)\s*\d+\b)", text)
    return [article.strip() for article in articles if article.strip()]

# Function to extract the article number from an article's text
def extract_article_number(article_text):
    match = re.match(r"(?:\u0627\u0644\u0645\u0627\u062F\u0629|\u0627\u0644\u0645\u0627\u062F\u0647)\s*(\d+)", article_text)
    if match:
        return int(match.group(1))  # Extract the number directly after المادة or الماده
    return None

# Function to process all DOCX files in a folder
def process_law_documents(folder_path, output_file):
    all_articles = []
    unique_id = 1  # Initialize the unique ID counter

    for filename in os.listdir(folder_path):
        if filename.endswith(".docx"):
            print(f"Processing {filename}...")
            file_path = os.path.join(folder_path, filename)
            text = extract_text_from_docx(file_path)
            articles = split_into_articles(text)

            law_name = os.path.splitext(filename)[0]  # Get the file name without extension

            for article_text in articles:
                article_number = extract_article_number(article_text)
                if article_number is not None:
                    all_articles.append({
                        "id": unique_id,  # Add a unique numeric ID
                        "file_name": law_name,
                        "article_number": article_number,
                        "article_content": article_text
                    })
                    unique_id += 1  # Increment the unique ID

    # Save articles to a JSON file
    with open(output_file, 'w', encoding='utf-8') as json_file:
        json.dump(all_articles, json_file, ensure_ascii=False, indent=4)

    print(f"Articles saved to {output_file}")

# Main function to execute the pipeline
if __name__ == "__main__":
    current_directory = os.getcwd()
    print("Current Working Directory:", current_directory)

    input_folder = folder_path
    articles_output_file = "json/1_extracted_raw_articles.json"
    process_law_documents(input_folder, articles_output_file)
