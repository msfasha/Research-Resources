"""
Data utilities for Quran Muqatta'at analysis.

This module provides functions for loading, cleaning, and processing the Quran dataset
for analysis of Muqatta'at (المقطعات) as potential checksums.
"""

import pandas as pd
import numpy as np
import re
from typing import Dict, List, Tuple, Optional
import arabic_reshaper
from bidi.algorithm import get_display


class QuranDataProcessor:
    """Main class for processing Quran data for Muqatta'at analysis."""
    
    def __init__(self, csv_path: str):
        """
        Initialize the processor with Quran CSV data.
        
        Args:
            csv_path: Path to the Quran CSV file
        """
        self.csv_path = csv_path
        self.df = None
        self.clean_df = None
        self.muqattaat_mapping = {}
        self._load_data()
        self._extract_muqattaat()
    
    def _load_data(self):
        """Load and initial processing of Quran CSV data."""
        self.df = pd.read_csv(self.csv_path)
        print(f"Loaded {len(self.df)} verses from {self.df['surah'].nunique()} surahs")
    
    def _extract_muqattaat(self):
        """
        Extract Muqatta'at letters from surahs that contain them.
        
        Muqatta'at are the disjointed letters at the beginning of certain surahs.
        This function identifies and extracts them from the first ayah of relevant surahs.
        """
        # Known surahs with Muqatta'at and their corresponding letters
        muqattaat_surahs = {
            2: "الم",      # Alif, Lam, Meem
            3: "الم",      # Alif, Lam, Meem  
            7: "المص",     # Alif, Lam, Meem, Sad
            10: "الر",     # Alif, Lam, Ra
            11: "الر",     # Alif, Lam, Ra
            12: "الر",     # Alif, Lam, Ra
            13: "المر",    # Alif, Lam, Meem, Ra
            14: "الر",     # Alif, Lam, Ra
            15: "الر",     # Alif, Lam, Ra
            19: "كهيعص",   # Kaf, Ha, Ya, Ain, Sad
            20: "طه",      # Ta, Ha
            26: "طسم",     # Ta, Sin, Meem
            27: "طس",      # Ta, Sin
            28: "طسم",     # Ta, Sin, Meem
            29: "الم",     # Alif, Lam, Meem
            30: "الم",     # Alif, Lam, Meem
            31: "الم",     # Alif, Lam, Meem
            32: "الم",     # Alif, Lam, Meem
            36: "يس",      # Ya, Sin
            38: "ص",       # Sad
            40: "حم",      # Ha, Meem
            41: "حم",      # Ha, Meem
            42: "حم عسق",  # Ha, Meem, Ain, Sin, Qaf
            43: "حم",      # Ha, Meem
            44: "حم",      # Ha, Meem
            45: "حم",      # Ha, Meem
            46: "حم",      # Ha, Meem
            50: "ق",       # Qaf
            68: "ن"        # Nun
        }
        
        self.muqattaat_mapping = muqattaat_surahs
        print(f"Identified {len(muqattaat_surahs)} surahs with Muqatta'at")
    
    def remove_basmala(self, text: str) -> str:
        """
        Remove Basmala (بسم الله الرحمن الرحيم) from text.
        
        Args:
            text: Arabic text that may contain Basmala
            
        Returns:
            Text with Basmala removed
        """
        basmala_patterns = [
            r'بسم الله الرحمن الرحيم\s*',
            r'بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ\s*',
            r'بِسْمِ اللَّهِ\s*'
        ]
        
        for pattern in basmala_patterns:
            text = re.sub(pattern, '', text)
        
        return text.strip()
    
    def clean_dataset(self) -> pd.DataFrame:
        """
        Clean the dataset by removing Basmala and Surah 1.
        
        Returns:
            Cleaned DataFrame
        """
        # Create a copy for cleaning
        clean_df = self.df.copy()
        
        # Remove Surah 1 (Al-Fatiha) entirely as it only contains Basmala
        clean_df = clean_df[clean_df['surah'] != 1]
        
        # Remove Basmala from the first ayah of each remaining surah
        for surah_num in clean_df['surah'].unique():
            surah_data = clean_df[clean_df['surah'] == surah_num]
            first_ayah_idx = surah_data.index[0]
            
            # Remove Basmala from first ayah
            original_text = clean_df.loc[first_ayah_idx, 'text']
            cleaned_text = self.remove_basmala(original_text)
            clean_df.loc[first_ayah_idx, 'text'] = cleaned_text
        
        # Remove any empty verses after Basmala removal
        clean_df = clean_df[clean_df['text'].str.strip() != '']
        
        self.clean_df = clean_df
        print(f"Cleaned dataset: {len(clean_df)} verses from {clean_df['surah'].nunique()} surahs")
        
        return clean_df
    
    def get_surah_text(self, surah_num: int, include_muqattaat: bool = True) -> str:
        """
        Get complete text of a surah.
        
        Args:
            surah_num: Surah number
            include_muqattaat: Whether to include Muqatta'at letters
            
        Returns:
            Complete surah text
        """
        if self.clean_df is None:
            self.clean_dataset()
        
        surah_verses = self.clean_df[self.clean_df['surah'] == surah_num]['text'].tolist()
        
        if not surah_verses:
            return ""
        
        # If this surah has Muqatta'at and we want to include them
        if include_muqattaat and surah_num in self.muqattaat_mapping:
            muqattaat = self.muqattaat_mapping[surah_num]
            return muqattaat + " " + " ".join(surah_verses)
        else:
            return " ".join(surah_verses)
    
    def get_muqattaat_letters(self, surah_num: int) -> Optional[str]:
        """
        Get Muqatta'at letters for a specific surah.
        
        Args:
            surah_num: Surah number
            
        Returns:
            Muqatta'at letters if surah has them, None otherwise
        """
        return self.muqattaat_mapping.get(surah_num)
    
    def get_surahs_with_muqattaat(self) -> List[int]:
        """Get list of surah numbers that have Muqatta'at."""
        return list(self.muqattaat_mapping.keys())
    
    def get_surahs_without_muqattaat(self) -> List[int]:
        """Get list of surah numbers that don't have Muqatta'at."""
        if self.clean_df is None:
            self.clean_dataset()
        
        all_surahs = set(self.clean_df['surah'].unique())
        surahs_with_muqattaat = set(self.muqattaat_mapping.keys())
        return sorted(list(all_surahs - surahs_with_muqattaat))
    
    def get_letter_frequency(self, text: str) -> Dict[str, int]:
        """
        Calculate letter frequency in Arabic text.
        
        Args:
            text: Arabic text
            
        Returns:
            Dictionary with letter frequencies
        """
        # Remove spaces and punctuation, keep only Arabic letters
        clean_text = re.sub(r'[^\u0600-\u06FF]', '', text)
        
        letter_freq = {}
        for char in clean_text:
            letter_freq[char] = letter_freq.get(char, 0) + 1
        
        return letter_freq
    
    def get_surah_letter_frequency(self, surah_num: int, include_muqattaat: bool = True) -> Dict[str, int]:
        """
        Get letter frequency for a specific surah.
        
        Args:
            surah_num: Surah number
            include_muqattaat: Whether to include Muqatta'at letters in frequency count
            
        Returns:
            Dictionary with letter frequencies
        """
        surah_text = self.get_surah_text(surah_num, include_muqattaat)
        return self.get_letter_frequency(surah_text)
    
    def get_all_surah_stats(self) -> pd.DataFrame:
        """
        Get basic statistics for all surahs.
        
        Returns:
            DataFrame with surah statistics
        """
        if self.clean_df is None:
            self.clean_dataset()
        
        stats = []
        
        for surah_num in sorted(self.clean_df['surah'].unique()):
            surah_data = self.clean_df[self.clean_df['surah'] == surah_num]
            
            # Get text with and without Muqatta'at
            text_with_muqattaat = self.get_surah_text(surah_num, include_muqattaat=True)
            text_without_muqattaat = self.get_surah_text(surah_num, include_muqattaat=False)
            
            # Calculate statistics
            stats.append({
                'surah': surah_num,
                'verse_count': len(surah_data),
                'has_muqattaat': surah_num in self.muqattaat_mapping,
                'muqattaat_letters': self.muqattaat_mapping.get(surah_num, ''),
                'total_chars_with_muqattaat': len(text_with_muqattaat),
                'total_chars_without_muqattaat': len(text_without_muqattaat),
                'unique_letters_with_muqattaat': len(set(text_with_muqattaat.replace(' ', ''))),
                'unique_letters_without_muqattaat': len(set(text_without_muqattaat.replace(' ', '')))
            })
        
        return pd.DataFrame(stats)


def load_quran_data(csv_path: str) -> QuranDataProcessor:
    """
    Convenience function to load Quran data.
    
    Args:
        csv_path: Path to Quran CSV file
        
    Returns:
        QuranDataProcessor instance
    """
    return QuranDataProcessor(csv_path)


# Example usage and testing
if __name__ == "__main__":
    # Test the data processor
    processor = QuranDataProcessor("datasets/quran-simple-clean.csv")
    
    # Clean the dataset
    clean_df = processor.clean_dataset()
    
    # Get some basic stats
    stats_df = processor.get_all_surah_stats()
    print("\nBasic Statistics:")
    print(stats_df.head(10))
    
    # Test Muqatta'at extraction
    print(f"\nSurahs with Muqatta'at: {processor.get_surahs_with_muqattaat()}")
    print(f"Surahs without Muqatta'at: {processor.get_surahs_without_muqattaat()}")
    
    # Test letter frequency for a specific surah
    surah_2_freq = processor.get_surah_letter_frequency(2, include_muqattaat=True)
    print(f"\nSurah 2 letter frequency (first 10): {dict(list(surah_2_freq.items())[:10])}")
