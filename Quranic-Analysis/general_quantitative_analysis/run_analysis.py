#!/usr/bin/env python3
"""
Quran Analysis Script
Runs the comprehensive statistical analysis from the notebook
"""

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from collections import Counter, defaultdict
import re
import os
from pathlib import Path
import warnings
warnings.filterwarnings('ignore')

# Arabic text processing
import arabic_reshaper
from bidi.algorithm import get_display
import matplotlib.font_manager as fm

# Set up plotting
plt.style.use('default')
sns.set_palette("husl")

def clean_quran_text(df):
    """Clean and preprocess Quran text for analysis"""
    df_clean = df.copy()
    
    # Remove Basmala from first verse of each surah
    basmala = 'بسم الله الرحمن الرحيم'
    
    def remove_basmala(row):
        if row['aya'] == 1 and row['text'].startswith(basmala):
            return row['text'][len(basmala):].strip()
        return row['text']
    
    df_clean['text'] = df_clean.apply(remove_basmala, axis=1)
    
    # Remove diacritics and non-Arabic characters (keeping only Arabic letters and spaces)
    df_clean['text_clean'] = df_clean['text'].str.replace(r'[^ء-ي\s]', '', regex=True)
    df_clean['text_clean'] = df_clean['text_clean'].str.replace(r'\s+', ' ', regex=True).str.strip()
    
    # Keep original text with diacritics for comparison
    df_clean['text_with_diacritics'] = df_clean['text']
    
    # Remove empty verses after cleaning
    df_clean = df_clean[df_clean['text_clean'] != ''].copy()
    
    return df_clean

def calculate_basic_stats(df):
    """Calculate basic statistical measures for the text"""
    stats = {}
    
    # Character-level statistics
    all_text = ' '.join(df['text_clean'])
    stats['total_characters'] = len(all_text)
    stats['unique_characters'] = len(set(all_text.replace(' ', '')))
    stats['total_words'] = len(all_text.split())
    stats['unique_words'] = len(set(all_text.split()))
    
    # Per verse statistics
    df['char_count'] = df['text_clean'].str.len()
    df['word_count'] = df['text_clean'].str.split().str.len()
    
    stats['avg_chars_per_verse'] = df['char_count'].mean()
    stats['avg_words_per_verse'] = df['word_count'].mean()
    stats['std_chars_per_verse'] = df['char_count'].std()
    stats['std_words_per_verse'] = df['word_count'].std()
    
    return stats, df

def analyze_muqattaat(df):
    """Analyze Muqatta'at letters and their patterns"""
    
    # Define Muqatta'at letters (المقطعات)
    muqattaat_letters = {
        'alif_lam_mim': ['ا', 'ل', 'م'],
        'alif_lam_mim_sad': ['ا', 'ل', 'م', 'ص'],
        'alif_lam_ra': ['ا', 'ل', 'ر'],
        'kaf_ha_ya_ain_sad': ['ك', 'ه', 'ي', 'ع', 'ص'],
        'ta_ha': ['ط', 'ه'],
        'ta_sin_mim': ['ط', 'س', 'م'],
        'ta_sin': ['ط', 'س'],
        'ya_sin': ['ي', 'س'],
        'sad': ['ص'],
        'ha_mim': ['ح', 'م'],
        'ain_sin_qaf': ['ع', 'س', 'ق'],
        'qaf': ['ق'],
        'nun': ['ن']
    }
    
    # Get all unique Muqatta'at letters
    all_muqattaat = set()
    for letters in muqattaat_letters.values():
        all_muqattaat.update(letters)
    
    # Analyze frequency of Muqatta'at letters
    all_text = ' '.join(df['text_clean'])
    char_freq = Counter(all_text.replace(' ', ''))
    
    muqattaat_freq = {char: char_freq.get(char, 0) for char in all_muqattaat}
    
    # Calculate statistics
    total_chars = sum(char_freq.values())
    muqattaat_total = sum(muqattaat_freq.values())
    muqattaat_percentage = (muqattaat_total / total_chars) * 100
    
    return {
        'muqattaat_letters': all_muqattaat,
        'muqattaat_freq': muqattaat_freq,
        'total_muqattaat': muqattaat_total,
        'muqattaat_percentage': muqattaat_percentage,
        'muqattaat_groups': muqattaat_letters
    }

def main():
    print("=== QURAN QUANTITATIVE ANALYSIS ===")
    print("Loading and processing data...")
    
    # Load data
    data_path = Path("datasets/quran-simple-clean.csv")
    df = pd.read_csv(data_path)
    
    # Rename columns to match our analysis
    df = df.rename(columns={'sura': 'surah'})
    
    print(f"Original dataset: {df.shape}")
    
    # Clean text
    df_processed = clean_quran_text(df)
    print(f"After cleaning: {len(df_processed)} verses")
    print(f"Removed: {len(df) - len(df_processed)} empty verses")
    
    # Calculate basic statistics
    stats, df_with_stats = calculate_basic_stats(df_processed)
    
    print("\n=== BASIC STATISTICS ===")
    print(f"Total characters: {stats['total_characters']:,}")
    print(f"Unique characters: {stats['unique_characters']}")
    print(f"Total words: {stats['total_words']:,}")
    print(f"Unique words: {stats['unique_words']:,}")
    print(f"Average characters per verse: {stats['avg_chars_per_verse']:.2f}")
    print(f"Average words per verse: {stats['avg_words_per_verse']:.2f}")
    
    # Character frequency analysis
    all_text = ' '.join(df_with_stats['text_clean'])
    char_freq = Counter(all_text.replace(' ', ''))
    char_df = pd.DataFrame(char_freq.most_common(), columns=['character', 'frequency'])
    char_df['percentage'] = (char_df['frequency'] / char_df['frequency'].sum()) * 100
    
    print("\n=== TOP 10 MOST FREQUENT CHARACTERS ===")
    for i, row in char_df.head(10).iterrows():
        print(f"{row['character']}: {row['frequency']:,} ({row['percentage']:.2f}%)")
    
    # Muqatta'at analysis
    muqattaat_analysis = analyze_muqattaat(df_with_stats)
    
    print("\n=== MUQATTA'AT LETTERS ANALYSIS ===")
    print(f"Total Muqatta'at letters: {len(muqattaat_analysis['muqattaat_letters'])}")
    print(f"Total frequency of Muqatta'at letters: {muqattaat_analysis['total_muqattaat']:,}")
    print(f"Percentage of text: {muqattaat_analysis['muqattaat_percentage']:.2f}%")
    
    print("\n=== MUQATTA'AT LETTER FREQUENCIES ===")
    muqattaat_df = pd.DataFrame(
        list(muqattaat_analysis['muqattaat_freq'].items()),
        columns=['letter', 'frequency']
    ).sort_values('frequency', ascending=False)
    
    for i, row in muqattaat_df.iterrows():
        print(f"{row['letter']}: {row['frequency']:,}")
    
    # Surah statistics
    surah_lengths = df_with_stats.groupby('surah')['aya'].count()
    print(f"\n=== SURAH STATISTICS ===")
    print(f"Longest surah: Surah {surah_lengths.idxmax()} with {surah_lengths.max()} verses")
    print(f"Shortest surah: Surah {surah_lengths.idxmin()} with {surah_lengths.min()} verses")
    print(f"Average verses per surah: {surah_lengths.mean():.2f}")
    
    # Verse length statistics
    print(f"\n=== VERSE LENGTH STATISTICS ===")
    print(f"Longest verse: {df_with_stats['char_count'].max()} characters")
    print(f"Shortest verse: {df_with_stats['char_count'].min()} characters")
    print(f"Character count std: {df_with_stats['char_count'].std():.2f}")
    print(f"Word count std: {df_with_stats['word_count'].std():.2f}")
    
    print("\n=== ANALYSIS COMPLETE ===")
    print("The comprehensive Jupyter notebook is ready for detailed analysis!")
    print("Key findings:")
    print(f"- {stats['unique_characters']} unique Arabic characters used")
    print(f"- {stats['unique_words']:,} unique words")
    print(f"- Muqatta'at letters represent {muqattaat_analysis['muqattaat_percentage']:.2f}% of all text")
    print(f"- Average verse length: {stats['avg_chars_per_verse']:.1f} characters, {stats['avg_words_per_verse']:.1f} words")

if __name__ == "__main__":
    main()

