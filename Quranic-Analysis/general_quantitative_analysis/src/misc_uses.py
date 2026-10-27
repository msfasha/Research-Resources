import pandas as pd


# Fix the working directory issue
# This is a workaround for the issue of the working directory not being set correctly in Jupyter Notebook
# This happened because i am working in a subfolder of the main folder
# and the relative path to the CSV file is not correct.
import os
print("Current Working Directory:", os.getcwd())
os.chdir(os.path.dirname(os.path.abspath(__file__)))
print("Current Working Directory:", os.getcwd())


# Load the CSV file into a DataFrame
path = "datasets\\quran-simple-clean.csv"
# path = "D:\win11-vm\OneDrive - UNIVERSITY OF PETRA\Petra\Cloud Clode\Research_Github\quran_analysis\datasets\quran-simple-clean.csv"
df = pd.read_csv(path)

# Display the DataFrame
print(len(df))
print(df.head())