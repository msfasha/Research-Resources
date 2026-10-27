"""
Sliding Window Muqatta'at Letter Ratio Analysis

This module analyzes the RATIO distribution of Muqatta'at letters within surahs using a sliding window approach.
For each surah that contains Muqatta'at letters, it calculates the RATIO of these letters to total letters
in consecutive 10-verse windows, sliding the window by one verse at a time.

FOCUS: RATIO ANALYSIS (letter frequency / total letters in window)

The analysis helps understand:
1. How Muqatta'at letter RATIOS are distributed throughout each surah
2. Whether there are consistent ratio patterns or clusters
3. If the letter ratios appear more frequently in certain sections of the surah
4. The relationship between Muqatta'at letter ratios and the overall structure of the surah

Methodology:
- For each surah with Muqatta'at letters, extract all verses
- Create sliding windows of 10 consecutive verses
- Calculate RATIO of each Muqatta'at letter to total letters in each window
- Slide the window by 1 verse and repeat
- Store results in a structured format for analysis

This approach provides insights into the internal RATIO distribution patterns of Muqatta'at letters
and may reveal whether they serve as structural markers within the surahs.

NOTE: This file focuses on RATIO analysis. For COUNT analysis, see 07_muqattaat_sliding_window_counts.py
"""

import csv
import re
from typing import Dict, List, Tuple, Optional
from collections import Counter


class SlidingWindowMuqattaatAnalyzer:
    """Analyzer for Muqatta'at letters using sliding window approach."""
    
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
        self._load_data()
        self._extract_muqattaat()
    
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
    
    def analyze_surah_sliding_window(self, surah_num: int, window_size: int = 10) -> List[Dict]:
        """
        Analyze a surah using sliding window approach.
        
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
            
            # Count Muqatta'at letters specifically
            window_result = {
                'surah': surah_num,
                'window_start': start_idx + 1,  # 1-indexed
                'window_end': end_idx,  # 1-indexed
                'window_size': window_size,
                'total_letters_in_window': total_letters,
                'verses_in_window': window_verses
            }
            
            # Add counts for each Muqatta'at letter
            for letter in muqattaat_letters:
                count = letter_counts.get(letter, 0)
                window_result[f'count_{letter}'] = count
                window_result[f'ratio_{letter}'] = count / total_letters if total_letters > 0 else 0
            
            results.append(window_result)
        
        return results
    
    def analyze_first_n_surahs(self, n: int = 5, window_size: int = 10) -> List[Dict]:
        """
        Analyze the first N surahs with Muqatta'at letters.
        
        Args:
            n: Number of surahs to analyze
            window_size: Size of the sliding window
            
        Returns:
            List of all window analysis results
        """
        # Get first N surahs with Muqatta'at
        surahs_with_muqattaat = sorted(self.muqattaat_mapping.keys())
        first_n_surahs = surahs_with_muqattaat[:n]
        
        print(f"Analyzing first {n} surahs with Muqatta'at: {first_n_surahs}")
        
        all_results = []
        
        for surah_num in first_n_surahs:
            print(f"Analyzing Surah {surah_num}...")
            surah_results = self.analyze_surah_sliding_window(surah_num, window_size)
            all_results.extend(surah_results)
            print(f"  Found {len(surah_results)} windows")
        
        return all_results
    
    def create_dataframe_table(self, results: List[Dict]) -> None:
        """Create and display a formatted table of results."""
        if not results:
            print("No results to display")
            return
        
        print("=" * 150)
        print("SLIDING WINDOW MUQATTA'AT LETTER ANALYSIS")
        print("=" * 150)
        
        # Get all unique letters from results
        all_letters = set()
        for result in results:
            for key in result.keys():
                if key.startswith('count_'):
                    letter = key.replace('count_', '')
                    all_letters.add(letter)
        
        all_letters = sorted(all_letters)
        
        # Create header
        header_parts = ['Surah', 'Start', 'End', 'Total_Letters']
        for letter in all_letters:
            header_parts.append(f'Count_{letter}')
        for letter in all_letters:
            header_parts.append(f'Ratio_{letter}')
        
        header = " | ".join(f"{part:<12}" for part in header_parts)
        print(header)
        print("-" * len(header))
        
        # Print data rows
        for result in results:
            row_parts = [
                str(result['surah']),
                str(result['window_start']),
                str(result['window_end']),
                str(result['total_letters_in_window'])
            ]
            
            for letter in all_letters:
                count = result.get(f'count_{letter}', 0)
                row_parts.append(str(count))
            
            for letter in all_letters:
                ratio = result.get(f'ratio_{letter}', 0)
                row_parts.append(f"{ratio:.4f}")
            
            row = " | ".join(f"{part:<12}" for part in row_parts)
            print(row)
        
        print("=" * 150)
        print(f"Total windows analyzed: {len(results)}")
    
    def save_results_to_csv(self, results: List[Dict], filename: str):
        """Save analysis results to CSV file."""
        if not results:
            print("No results to save")
            return
        
        with open(filename, 'w', newline='', encoding='utf-8') as file:
            writer = csv.writer(file)
            
            # Get all unique letters
            all_letters = set()
            for result in results:
                for key in result.keys():
                    if key.startswith('count_'):
                        letter = key.replace('count_', '')
                        all_letters.add(letter)
            
            all_letters = sorted(all_letters)
            
            # Create header
            header = ['surah', 'window_start', 'window_end', 'window_size', 'total_letters_in_window']
            for letter in all_letters:
                header.extend([f'count_{letter}', f'ratio_{letter}'])
            
            writer.writerow(header)
            
            # Write data
            for result in results:
                row = [
                    result['surah'],
                    result['window_start'],
                    result['window_end'],
                    result['window_size'],
                    result['total_letters_in_window']
                ]
                
                for letter in all_letters:
                    row.extend([
                        result.get(f'count_{letter}', 0),
                        result.get(f'ratio_{letter}', 0)
                    ])
                
                writer.writerow(row)
        
        print(f"Results saved to {filename}")


def main():
    """Main function to run the sliding window analysis."""
    print("Starting Sliding Window Muqatta'at Letter Analysis...")
    print("=" * 60)
    
    # Load data
    analyzer = SlidingWindowMuqattaatAnalyzer("datasets/quran-simple-clean.csv")
    
    # Analyze first 5 surahs with Muqatta'at letters
    print("\nAnalyzing first 5 surahs with Muqatta'at letters...")
    results = analyzer.analyze_first_n_surahs(n=5, window_size=10)
    
    print(f"\nAnalysis completed. Found {len(results)} windows across 5 surahs")
    
    # Display results as table
    analyzer.create_dataframe_table(results)
    
    # Save results
    analyzer.save_results_to_csv(results, "06_sliding_window_ratio.csv")
    
    print(f"\nAnalysis complete! Results saved to '06_sliding_window_ratio.csv'")


if __name__ == "__main__":
    main()
