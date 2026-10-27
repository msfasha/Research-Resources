# *Parameter Efficient Fine Tuning Llama 3.1 for Answering Arabic Legal Questions: A Case Study on Jordanian Laws*

This repository contains the datasets and source code associated with the paper:

**Parameter Efficient Fine Tuning Llama 3.1 for Answering Arabic Legal Questions: A Case Study on Jordanian Laws**

### Authors

- **Mohammed Fasha**  
  Business Intelligence and Data Analytics Department  
  University of Petra, Amman, Jordan  
  mohammed.fasha@uop.edu.jo

- **Bassam Hammo**  
  Princess Sumaya University  
  Amman, Jordan  
  b.hammo@psut.edu.jo

- **Bilal Sawan**  
  Business Intelligence and Data Analytics Department  
  University of Petra, Amman, Jordan  
  bilal.sowan@uop.edu.jo

- **Husam Barham**  
  Business Intelligence and Data Analytics Department  
  University of Petra, Amman, Jordan  
  hbarham@uop.edu.jo

- **Esam Nsour**  
  Computer Science Department  
  University of Zarqa, Zarqa, Jordan  
  ealnsour@zu.edu.jo

---

## Overview

This repository provides:

1. **Datasets**: [Brief description of datasets, e.g., "Pre-processed legal texts and their annotations."]
2. **Source Code**: [Brief description of the code, e.g., "Scripts for model training, evaluation, and result generation."]

## Contents

- `/datasets/`: Contains the datasets used in the paper.
  - `dataset_name.csv`: [Description of the dataset.]
  - `readme.txt`: Additional details about the datasets.

- `/code/`: Contains source code used for the experiments.
  - `train.py`: Script for training the model.
  - `evaluate.py`: Script for evaluating the model.
  - `requirements.txt`: Python dependencies.

## Citation
If you use this repository in your research, please cite our paper:

### Plain Test

```
M. Fasha, B. Hammo, B. Sowan, H. Barham and E. Al-Nsour, "Parameter Efficient Fine Tuning Llama 3.1 for Answering Arabic Legal Questions: A Case Study on Jordanian Laws," 2025 1st International Conference on Computational Intelligence Approaches and Applications (ICCIAA), Amman, Jordan, 2025, pp. 1-5, doi: 10.1109/ICCIAA65327.2025.11013375.
```

### BibTeX Entry

```bibtex
@INPROCEEDINGS{11013375,
  author={Fasha, Mohammed and Hammo, Bassam and Sowan, Bilal and Barham, Husam and Al-Nsour, Esam},
  booktitle={2025 1st International Conference on Computational Intelligence Approaches and Applications (ICCIAA)}, 
  title={Parameter Efficient Fine Tuning Llama 3.1 for Answering Arabic Legal Questions: A Case Study on Jordanian Laws}, 
  year={2025},
  volume={},
  number={},
  pages={1-5},
  abstract={This study uses Jordanian law as a case study to explore the fine-tuning of the Llama-3.1 large language model for Arabic question-answering. Two versions of the model- Llama-3.1-8B-bnb-4bit and Llama-3.1-8B-Instruct-bnb-4bit-were fine-tuned using parameter-efficient fine-tuning (PEFT) with LoRA adapters and 4-bit quantized models, leveraging the Unsloth framework for accelerated and resource-efficient training. A custom dataset of 6000 legal question-answer pairs was curated from Jordanian laws and formatted into structured prompts. Performance was evaluated using the BLEU and the ROUGE metrics to compare the fine-tuned models to their respective base versions. Results demonstrated improved legal reasoning and accuracy while achieving resource efficiency through quantization and optimized fine-tuning strategies. This work underscores the potential of adapting large language models for Arabic legal domains and highlights effective techniques for fine-tuning domain-specific tasks.},
  keywords={Training;Adaptation models;Technological innovation;Accuracy;Text analysis;Law;Terminology;Large language models;Question answering (information retrieval);Tuning;Arabic NLP;LLM;Llama 3.1;PEFT;Legal Question Answering},
  doi={10.1109/ICCIAA65327.2025.11013375},
  ISSN={},
  month={April},}

```

## Requirements

To run the code, ensure you have the following dependencies:

- Python 3.8+
- Libraries listed in `requirements.txt`

Install dependencies using:

```bash
pip install -r requirements.txt
```

## Usage

1. Clone the repository:

```bash
git clone https://github.com/yourusername/yourrepository.git
cd yourrepository
```

2. Prepare the datasets by placing them in the `/datasets/` folder.

3. Run the training script:

```bash
python code/train.py
```

4. Evaluate the model:

```bash
python code/evaluate.py
```

## License

This repository is licensed under the [MIT License](LICENSE). Feel free to use and adapt the code and datasets for academic and non-commercial purposes.

## Contact

For questions or feedback, please contact:

- Mohammad Fasha  
  mohd.fasha@gmail.com
