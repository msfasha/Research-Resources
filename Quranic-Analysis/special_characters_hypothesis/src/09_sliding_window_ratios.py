"""
Sliding Window Special Letters Ratio Analysis (Inclusive & Exclusive)

================================================================================
PURPOSE
================================================================================

This module performs comprehensive ratio analysis of special (Muqatta'at) letters
to other letters in the Quran text. It calculates ratios at two different levels:

1. SURAH-LEVEL: Ratios across the entire surah
2. WINDOW-LEVEL: Ratios within 10-verse sliding windows

For each level, it computes both INCLUSIVE and EXCLUSIVE ratios to provide
different perspectives on how special letters relate to the rest of the text.

================================================================================
RATIO DEFINITIONS
================================================================================

INCLUSIVE RATIO:
    Formula: special_letters / total_letters
    Where: total_letters = special_letters + other_letters
    Meaning: Proportion of special letters out of ALL letters in the text
    Range: 0.0 to 1.0 (0% to 100%)
    Example: If there are 100 special letters and 900 other letters:
             Inclusive ratio = 100 / 1000 = 0.10 (10%)

EXCLUSIVE RATIO:
    Formula: special_letters / other_letters
    Where: other_letters = total_letters - special_letters
    Meaning: Ratio of special letters to NON-special letters only
    Range: 0.0 to infinity (typically 0.0 to 1.0 for most cases)
    Example: If there are 100 special letters and 900 other letters:
             Exclusive ratio = 100 / 900 = 0.111... (11.1%)

================================================================================
ANALYSIS LEVELS
================================================================================

1. SURAH-LEVEL ANALYSIS:
   - Analyzes the entire surah as a single unit
   - Calculates total counts of special letters and other letters
   - Computes inclusive and exclusive ratios for the whole surah
   - Useful for understanding overall surah characteristics

2. WINDOW-LEVEL ANALYSIS:
   - Uses sliding windows of 10 consecutive verses
   - Window slides by 1 verse at a time (e.g., verses 1-10, 2-11, 3-12, ...)
   - Calculates ratios for each window independently
   - Useful for understanding local patterns and variations within surahs

================================================================================
METHODOLOGY
================================================================================

For each surah containing Muqatta'at letters:

1. Surah-Level Processing:
   - Extract all verses from the surah (excluding Basmala)
   - Count all letters in the surah
   - Identify special letters (Muqatta'at letters for that surah)
   - Count special letters and other letters separately
   - Calculate inclusive and exclusive ratios

2. Window-Level Processing:
   - Create sliding windows of 10 consecutive verses
   - For each window:
     * Extract verses in the window
     * Count all letters in the window
     * Count special letters in the window
     * Calculate other letters = total - special
     * Calculate inclusive and exclusive ratios
   - Slide window by 1 verse and repeat

================================================================================
OUTPUT FILES
================================================================================

1. 09_sliding_window_ratios_surah_level.csv
   - Contains surah-level ratio analysis
   - Columns: surah, total_letters, special_letters_count, other_letters_count,
             inclusive_ratio, exclusive_ratio, muqattaat_letters

2. 09_sliding_window_ratios_window_level.csv
   - Contains window-level ratio analysis
   - Columns: surah, window_start, window_end, window_size,
             total_letters_in_window, special_letters_count, other_letters_count,
             inclusive_ratio, exclusive_ratio, count_{letter} for each letter

================================================================================
USE CASES
================================================================================

This analysis helps answer questions such as:
- What is the proportion of special letters in each surah?
- How do special letters relate to other letters in local (10-verse) contexts?
- Are the ratios consistent across different sections of a surah?
- What is the difference between inclusive and exclusive perspectives?
- Do ratios vary significantly within surahs or remain relatively stable?

================================================================================
RELATIONSHIP TO OTHER MODULES
================================================================================

- 05_muqattaat_simple_analysis.py: Basic surah-level analysis
- 06_muqattaat_sliding_window.py: Sliding window analysis with ratios to total
- 07_muqattaat_sliding_window_counts.py: Sliding window with absolute counts
- 08_comprehensive_10verse_analysis.py: Comprehensive pattern analysis

This module extends the analysis by:
- Providing both inclusive and exclusive ratio perspectives
- Combining surah-level and window-level analysis in one module
- Focusing specifically on the relationship between special and other letters

================================================================================
AUTHOR & DATE
================================================================================

Created: 2024
Purpose: Research on special characters hypothesis in Quran analysis
"""

import csv
import re
from typing import Dict, List, Tuple, Optional
from collections import Counter


class SpecialLettersRatioAnalyzer:
    """Analyzer for special letters ratios (inclusive and exclusive)."""
    
    def __init__(self, csv_path: str):
        """
        Initialize the analyzer with Quran CSV data.
        
        Args:
            csv_path: Path to the Quran CSV file
        """
        # Handle relative paths by making them absolute
        import os
        if not os.path.isabs(csv_path):
            # Get the directory of this script
            script_dir = os.path.dirname(os.path.abspath(__file__))
            # Go up one level to the project root
            project_root = os.path.dirname(script_dir)
            # Construct the full path
            self.csv_path = os.path.join(project_root, csv_path)
        else:
            self.csv_path = csv_path
            
        self.data = []
        self.muqattaat_mapping = {}
        self.surah_names = {}
        self._load_data()
        self._extract_muqattaat()
        self._load_surah_names()
    
    def _load_surah_names(self):
        """Load surah name mapping."""
        # Mapping of surah numbers to their names (English transliteration)
        surah_names = {
            1: "Al-Fatiha", 2: "Al-Baqarah", 3: "Al-Imran", 4: "An-Nisa", 5: "Al-Maidah",
            6: "Al-An'am", 7: "Al-A'raf", 8: "Al-Anfal", 9: "At-Tawbah", 10: "Yunus",
            11: "Hud", 12: "Yusuf", 13: "Ar-Ra'd", 14: "Ibrahim", 15: "Al-Hijr",
            16: "An-Nahl", 17: "Al-Isra", 18: "Al-Kahf", 19: "Maryam", 20: "Ta-Ha",
            21: "Al-Anbiya", 22: "Al-Hajj", 23: "Al-Mu'minun", 24: "An-Nur", 25: "Al-Furqan",
            26: "Ash-Shu'ara", 27: "An-Naml", 28: "Al-Qasas", 29: "Al-Ankabut", 30: "Ar-Rum",
            31: "Luqman", 32: "As-Sajdah", 33: "Al-Ahzab", 34: "Saba", 35: "Fatir",
            36: "Ya-Sin", 37: "As-Saffat", 38: "Sad", 39: "Az-Zumar", 40: "Ghafir",
            41: "Fussilat", 42: "Ash-Shura", 43: "Az-Zukhruf", 44: "Ad-Dukhan", 45: "Al-Jathiyah",
            46: "Al-Ahqaf", 47: "Muhammad", 48: "Al-Fath", 49: "Al-Hujurat", 50: "Qaf",
            51: "Adh-Dhariyat", 52: "At-Tur", 53: "An-Najm", 54: "Al-Qamar", 55: "Ar-Rahman",
            56: "Al-Waqi'ah", 57: "Al-Hadid", 58: "Al-Mujadila", 59: "Al-Hashr", 60: "Al-Mumtahanah",
            61: "As-Saff", 62: "Al-Jumu'ah", 63: "Al-Munafiqun", 64: "At-Taghabun", 65: "At-Talaq",
            66: "At-Tahrim", 67: "Al-Mulk", 68: "Al-Qalam", 69: "Al-Haqqah", 70: "Al-Ma'arij",
            71: "Nuh", 72: "Al-Jinn", 73: "Al-Muzzammil", 74: "Al-Muddaththir", 75: "Al-Qiyamah",
            76: "Al-Insan", 77: "Al-Mursalat", 78: "An-Naba", 79: "An-Nazi'at", 80: "Abasa",
            81: "At-Takwir", 82: "Al-Infitar", 83: "Al-Mutaffifin", 84: "Al-Inshiqaq", 85: "Al-Buruj",
            86: "At-Tariq", 87: "Al-A'la", 88: "Al-Ghashiyah", 89: "Al-Fajr", 90: "Al-Balad",
            91: "Ash-Shams", 92: "Al-Layl", 93: "Ad-Duha", 94: "Ash-Sharh", 95: "At-Tin",
            96: "Al-Alaq", 97: "Al-Qadr", 98: "Al-Bayyinah", 99: "Az-Zalzalah", 100: "Al-Adiyat",
            101: "Al-Qari'ah", 102: "At-Takathur", 103: "Al-Asr", 104: "Al-Humazah", 105: "Al-Fil",
            106: "Quraysh", 107: "Al-Ma'un", 108: "Al-Kawthar", 109: "Al-Kafirun", 110: "An-Nasr",
            111: "Al-Masad", 112: "Al-Ikhlas", 113: "Al-Falaq", 114: "An-Nas"
        }
        self.surah_names = surah_names
    
    def get_surah_name(self, surah_num: int) -> str:
        """Get the name of a surah by its number."""
        return self.surah_names.get(surah_num, f"Unknown-{surah_num}")
    
    def _load_data(self):
        """Load Quran data from CSV file."""
        with open(self.csv_path, 'r', encoding='utf-8') as file:
            reader = csv.DictReader(file)
            for row in reader:
                self.data.append({
                    'surah': int(row['surah']),
                    'aya': int(row['aya']),
                    'text': row['text']
                })
        
        print(f"Loaded {len(self.data)} verses from {len(set(row['surah'] for row in self.data))} surahs")
    
    def _extract_muqattaat(self):
        """Extract Muqatta'at letters from surahs that contain them."""
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
        """Remove Basmala from text."""
        basmala_patterns = [
            r'بسم الله الرحمن الرحيم\s*',
            r'بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ\s*',
            r'بِسْمِ اللَّهِ\s*'
        ]
        
        for pattern in basmala_patterns:
            text = re.sub(pattern, '', text)
        
        return text.strip()
    
    def get_surah_verses(self, surah_num: int) -> List[str]:
        """Get all verses for a specific surah."""
        surah_verses = []
        for row in self.data:
            if row['surah'] == surah_num:
                # Remove Basmala from first verse
                text = row['text']
                if row['aya'] == 1:
                    text = self.remove_basmala(text)
                
                # Only add non-empty verses
                if text.strip():
                    surah_verses.append(text.strip())
        
        return surah_verses
    
    def get_muqattaat_letters_for_surah(self, surah_num: int) -> List[str]:
        """Get the Muqatta'at letters for a specific surah."""
        muqattaat = self.muqattaat_mapping.get(surah_num, "")
        if muqattaat:
            # Split by space first, then split each group into individual letters
            letter_groups = [group.strip() for group in muqattaat.split() if group.strip()]
            letters = []
            for group in letter_groups:
                # Split each group into individual Arabic letters
                for char in group:
                    if char.strip():  # Only add non-empty characters
                        letters.append(char)
            return letters
        return []
    
    def count_letters_in_text(self, text: str) -> Dict[str, int]:
        """Count individual letters in Arabic text."""
        # Remove spaces, punctuation, and keep only Arabic letters
        clean_text = re.sub(r'[^\u0600-\u06FF]', '', text)
        return Counter(clean_text)
    
    def calculate_surah_level_ratios(self, surah_num: int) -> Dict:
        """
        Calculate inclusive and exclusive ratios for the entire surah.
        
        Args:
            surah_num: Surah number to analyze
            
        Returns:
            Dictionary with surah-level ratio analysis
        """
        if surah_num not in self.muqattaat_mapping:
            return {}
        
        # Get surah verses and Muqatta'at letters
        verses = self.get_surah_verses(surah_num)
        muqattaat_letters = self.get_muqattaat_letters_for_surah(surah_num)
        
        if not verses:
            return {}
        
        # Combine all verses in surah
        surah_text = " ".join(verses)
        
        # Count all letters
        letter_counts = self.count_letters_in_text(surah_text)
        total_letters = sum(letter_counts.values())
        
        # Count special letters (sum of all Muqatta'at letters)
        special_letters_count = sum(letter_counts.get(letter, 0) for letter in muqattaat_letters)
        
        # Count other letters (total - special)
        other_letters_count = total_letters - special_letters_count
        
        # Calculate ratios
        inclusive_ratio = special_letters_count / total_letters if total_letters > 0 else 0
        exclusive_ratio = special_letters_count / other_letters_count if other_letters_count > 0 else 0
        
        return {
            'surah': surah_num,
            'surah_name': self.get_surah_name(surah_num),
            'total_letters': total_letters,
            'special_letters_count': special_letters_count,
            'other_letters_count': other_letters_count,
            'inclusive_ratio': inclusive_ratio,  # special / total
            'exclusive_ratio': exclusive_ratio,  # special / other
            'muqattaat_letters': muqattaat_letters
        }
    
    def analyze_surah_sliding_window_ratios(self, surah_num: int, window_size: int = 10) -> List[Dict]:
        """
        Analyze a surah using sliding window approach with inclusive and exclusive ratios.
        
        Args:
            surah_num: Surah number to analyze
            window_size: Size of the sliding window (default: 10 verses)
            
        Returns:
            List of dictionaries with window analysis results
        """
        if surah_num not in self.muqattaat_mapping:
            return []
        
        # Get surah verses and Muqatta'at letters
        verses = self.get_surah_verses(surah_num)
        muqattaat_letters = self.get_muqattaat_letters_for_surah(surah_num)
        
        if len(verses) < window_size:
            print(f"Warning: Surah {surah_num} has only {len(verses)} verses, less than window size {window_size}")
            return []
        
        results = []
        
        # Create sliding windows
        for start_idx in range(len(verses) - window_size + 1):
            end_idx = start_idx + window_size
            
            # Get verses in current window
            window_verses = verses[start_idx:end_idx]
            window_text = " ".join(window_verses)
            
            # Count letters in window
            letter_counts = self.count_letters_in_text(window_text)
            total_letters = sum(letter_counts.values())
            
            # Count special letters (sum of all Muqatta'at letters in this window)
            special_letters_count = sum(letter_counts.get(letter, 0) for letter in muqattaat_letters)
            
            # Count other letters (total - special)
            other_letters_count = total_letters - special_letters_count
            
            # Calculate ratios
            inclusive_ratio = special_letters_count / total_letters if total_letters > 0 else 0
            exclusive_ratio = special_letters_count / other_letters_count if other_letters_count > 0 else 0
            
            window_result = {
                'surah': surah_num,
                'surah_name': self.get_surah_name(surah_num),
                'window_start': start_idx + 1,  # 1-indexed
                'window_end': end_idx,  # 1-indexed
                'window_size': window_size,
                'total_letters_in_window': total_letters,
                'special_letters_count': special_letters_count,
                'other_letters_count': other_letters_count,
                'inclusive_ratio': inclusive_ratio,  # special / total
                'exclusive_ratio': exclusive_ratio,  # special / other
                'verses_in_window': window_verses
            }
            
            # Also add individual letter counts for reference
            for letter in muqattaat_letters:
                window_result[f'count_{letter}'] = letter_counts.get(letter, 0)
            
            results.append(window_result)
        
        return results
    
    def analyze_first_n_surahs(self, n: int = 5, window_size: int = 10) -> Tuple[Dict, List[Dict]]:
        """
        Analyze the first N surahs with Muqatta'at letters.
        
        Args:
            n: Number of surahs to analyze
            window_size: Size of the sliding window
            
        Returns:
            Tuple of (surah_level_results, window_level_results)
        """
        # Get first N surahs with Muqatta'at
        surahs_with_muqattaat = sorted(self.muqattaat_mapping.keys())
        first_n_surahs = surahs_with_muqattaat[:n]
        
        print(f"Analyzing first {n} surahs with Muqatta'at: {first_n_surahs}")
        
        surah_level_results = {}
        all_window_results = []
        
        for surah_num in first_n_surahs:
            surah_name = self.get_surah_name(surah_num)
            print(f"Analyzing Surah {surah_num} ({surah_name})...")
            
            # Calculate surah-level ratios
            surah_ratios = self.calculate_surah_level_ratios(surah_num)
            surah_level_results[surah_num] = surah_ratios
            
            # Calculate window-level ratios
            window_results = self.analyze_surah_sliding_window_ratios(surah_num, window_size)
            all_window_results.extend(window_results)
            print(f"  Found {len(window_results)} windows")
            print(f"  Surah-level ratios - Inclusive: {surah_ratios['inclusive_ratio']:.6f}, Exclusive: {surah_ratios['exclusive_ratio']:.6f}")
        
        return surah_level_results, all_window_results
    
    def analyze_all_surahs(self, window_size: int = 10) -> Tuple[Dict, List[Dict]]:
        """
        Analyze ALL surahs with Muqatta'at letters.
        
        Args:
            window_size: Size of the sliding window
            
        Returns:
            Tuple of (surah_level_results, window_level_results)
        """
        # Get all surahs with Muqatta'at
        all_surahs = sorted(self.muqattaat_mapping.keys())
        
        print(f"Analyzing ALL {len(all_surahs)} surahs with Muqatta'at: {all_surahs}")
        
        surah_level_results = {}
        all_window_results = []
        
        for surah_num in all_surahs:
            surah_name = self.get_surah_name(surah_num)
            print(f"Analyzing Surah {surah_num} ({surah_name})...")
            
            # Calculate surah-level ratios
            surah_ratios = self.calculate_surah_level_ratios(surah_num)
            surah_level_results[surah_num] = surah_ratios
            
            # Calculate window-level ratios
            window_results = self.analyze_surah_sliding_window_ratios(surah_num, window_size)
            all_window_results.extend(window_results)
            print(f"  Found {len(window_results)} windows")
            print(f"  Surah-level ratios - Inclusive: {surah_ratios['inclusive_ratio']:.6f}, Exclusive: {surah_ratios['exclusive_ratio']:.6f}")
        
        return surah_level_results, all_window_results
    
    def print_surah_level_summary(self, surah_results: Dict):
        """Print a summary of surah-level ratio analysis."""
        print("\n" + "=" * 120)
        print("SURAH-LEVEL RATIO ANALYSIS")
        print("=" * 120)
        print(f"{'Surah':<8} {'Name':<20} {'Total Letters':<15} {'Special':<12} {'Other':<12} {'Inclusive Ratio':<18} {'Exclusive Ratio':<18}")
        print("-" * 120)
        
        for surah_num in sorted(surah_results.keys()):
            result = surah_results[surah_num]
            print(f"{surah_num:<8} "
                  f"{result['surah_name']:<20} "
                  f"{result['total_letters']:<15} "
                  f"{result['special_letters_count']:<12} "
                  f"{result['other_letters_count']:<12} "
                  f"{result['inclusive_ratio']:<18.6f} "
                  f"{result['exclusive_ratio']:<18.6f}")
        
        print("=" * 120)
    
    def print_window_level_summary(self, window_results: List[Dict], max_rows: int = 20):
        """Print a summary of window-level ratio analysis."""
        print("\n" + "=" * 140)
        print("WINDOW-LEVEL RATIO ANALYSIS (First 20 windows)")
        print("=" * 140)
        print(f"{'Surah':<8} {'Name':<20} {'Window':<12} {'Total':<12} {'Special':<12} {'Other':<12} {'Inclusive':<15} {'Exclusive':<15}")
        print("-" * 140)
        
        for result in window_results[:max_rows]:
            window_range = f"{result['window_start']}-{result['window_end']}"
            print(f"{result['surah']:<8} "
                  f"{result['surah_name']:<20} "
                  f"{window_range:<12} "
                  f"{result['total_letters_in_window']:<12} "
                  f"{result['special_letters_count']:<12} "
                  f"{result['other_letters_count']:<12} "
                  f"{result['inclusive_ratio']:<15.6f} "
                  f"{result['exclusive_ratio']:<15.6f}")
        
        if len(window_results) > max_rows:
            print(f"\n... and {len(window_results) - max_rows} more windows")
        
        print("=" * 140)
    
    def save_results_to_csv(self, surah_results: Dict, window_results: List[Dict], filename: str):
        """Save analysis results to CSV file."""
        # Save surah-level results
        surah_filename = filename.replace('.csv', '_surah_level.csv')
        with open(surah_filename, 'w', newline='', encoding='utf-8') as file:
            writer = csv.writer(file)
            
            header = ['surah', 'surah_name', 'inclusive_ratio', 'exclusive_ratio',
                     'total_letters', 'special_letters_count', 'other_letters_count', 
                     'muqattaat_letters']
            writer.writerow(header)
            
            for surah_num in sorted(surah_results.keys()):
                result = surah_results[surah_num]
                row = [
                    result['surah'],
                    result['surah_name'],
                    result['inclusive_ratio'],
                    result['exclusive_ratio'],
                    result['total_letters'],
                    result['special_letters_count'],
                    result['other_letters_count'],
                    ''.join(result['muqattaat_letters'])
                ]
                writer.writerow(row)
        
        print(f"Surah-level results saved to {surah_filename}")
        
        # Save window-level results
        window_filename = filename.replace('.csv', '_window_level.csv')
        with open(window_filename, 'w', newline='', encoding='utf-8') as file:
            writer = csv.writer(file)
            
            # Get all unique letters from results
            all_letters = set()
            for result in window_results:
                for key in result.keys():
                    if key.startswith('count_'):
                        letter = key.replace('count_', '')
                        all_letters.add(letter)
            
            all_letters = sorted(all_letters)
            
            # Create header - ratios come right after surah_name
            header = ['surah', 'surah_name', 'inclusive_ratio', 'exclusive_ratio',
                     'window_start', 'window_end', 'window_size', 
                     'total_letters_in_window', 'special_letters_count', 'other_letters_count']
            
            # Add individual letter counts
            for letter in all_letters:
                header.append(f'count_{letter}')
            
            writer.writerow(header)
            
            # Write data
            for result in window_results:
                row = [
                    result['surah'],
                    result['surah_name'],
                    result['inclusive_ratio'],
                    result['exclusive_ratio'],
                    result['window_start'],
                    result['window_end'],
                    result['window_size'],
                    result['total_letters_in_window'],
                    result['special_letters_count'],
                    result['other_letters_count']
                ]
                
                # Add individual letter counts
                for letter in all_letters:
                    row.append(result.get(f'count_{letter}', 0))
                
                writer.writerow(row)
        
        print(f"Window-level results saved to {window_filename}")


def main():
    """Main function to run the ratio analysis."""
    print("Starting Special Letters Ratio Analysis (Inclusive & Exclusive)...")
    print("=" * 80)
    print("This analysis calculates:")
    print("1. Surah-level ratios: special letters to other letters (entire surah)")
    print("2. Window-level ratios: special letters to other letters (10-verse windows)")
    print("For each, calculates both inclusive and exclusive ratios")
    print("=" * 80)
    
    # Load data
    analyzer = SpecialLettersRatioAnalyzer("datasets/quran-simple-clean.csv")
    
    # Analyze ALL surahs with Muqatta'at letters (29 total)
    print("\nAnalyzing ALL surahs with Muqatta'at letters...")
    surah_results, window_results = analyzer.analyze_all_surahs(window_size=10)
    
    print(f"\nAnalysis completed:")
    print(f"  - Analyzed {len(surah_results)} surahs at surah level")
    print(f"  - Analyzed {len(window_results)} windows at window level")
    
    # Print summaries
    analyzer.print_surah_level_summary(surah_results)
    analyzer.print_window_level_summary(window_results)
    
    # Save results
    analyzer.save_results_to_csv(surah_results, window_results, "09_sliding_window_ratios.csv")
    
    print(f"\nAnalysis complete! Results saved to:")
    print(f"  - 09_sliding_window_ratios_surah_level.csv")
    print(f"  - 09_sliding_window_ratios_window_level.csv")
    print("\nRatio definitions:")
    print("  - Inclusive ratio = special_letters / total_letters (special + other)")
    print("  - Exclusive ratio = special_letters / other_letters (excluding special from denominator)")


if __name__ == "__main__":
    main()

