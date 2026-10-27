import json
import os

def convert_into_load_dataset_format():
    # Load your original dataset
    with open("json/3_combine_extracted_and_generated.json", "r", encoding="utf-8") as f:
        data = json.load(f)

    # Transform the data
    formatted_data = []
    for item in data:
        formatted_data.append({
            "id": item["id"],
            "question": item["question"],
            "context": item["article"]["article_content"],
            "answer": item["answer"]
        })

    # Save the transformed dataset
    with open("json/4_final_qa.json", "w", encoding="utf-8") as f:
        json.dump(formatted_data, f, ensure_ascii=False, indent=2)

convert_into_load_dataset_format()