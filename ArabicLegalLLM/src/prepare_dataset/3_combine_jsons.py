import json
import os

def combine_jsons():
    os.chdir('/media/me/Disk1-Repo 1/my_code/code_experiments/finetune_llm/law/extract_text/')

    # Load the first JSON file
    with open('json/2_qa_pairs_generated_by_openai.json', 'r', encoding='utf-8') as file:
        qa_pairs = json.load(file)

    # Load the second JSON file
    with open('json/1_extracted_raw_articles.json', 'r', encoding='utf-8') as file:
        articles = json.load(file)

    # Create a dictionary for quick lookup of article content by article ID
    article_dict = {article['id']: article for article in articles}

    # Combine the data
    combined_data = []
    for qa in qa_pairs:
        article_id = qa['article_id']
        if article_id in article_dict:
            article = article_dict[article_id]
            combined_entry = {
                "id": qa["id"],
                "article_id": qa["article_id"],
                "question": qa["question"],
                "answer": qa["answer"],
                "article": {
                    "file_name": article["file_name"],
                    "article_number": article["article_number"],
                    "article_content": article["article_content"]
                }
            }
            combined_data.append(combined_entry)

    # Save the combined data to a new JSON file
    combined_file_path = 'json/3_combine_extracted_and_generated.json'
    with open(combined_file_path, 'w', encoding='utf-8') as file:
        json.dump(combined_data, file, ensure_ascii=False, indent=4)

combine_jsons()