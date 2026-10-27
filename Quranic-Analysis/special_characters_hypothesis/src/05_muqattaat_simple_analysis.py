"""
Simplified Muqatta'at Letter Ratio Analysis

This module analyzes the frequency of Muqatta'at letters in surahs that contain them.
The analysis compares the frequency of Muqatta'at letters in the current surah against:

BEFORE ANALYSIS:
1. The surah directly before the surah with Muqatta'at
2. All surahs before the surah with Muqatta'at

AFTER ANALYSIS:
3. The surah directly after the surah with Muqatta'at
4. All surahs after the surah with Muqatta'at (excluding the current surah itself)

This comprehensive analysis tests the hypothesis that Muqatta'at letters serve as checksums
by examining their frequency patterns in relation to both previous and subsequent content.
"""

import csv
import re
from typing import Dict, List, Tuple, Optional
from collections import Counter


class SimpleQuranAnalyzer:
    """Simple analyzer for Quran Muqatta'at analysis using only standard library."""
    
    def __init__(self, csv_path: str):
        """
        Initialize the analyzer with Quran CSV data.
        
        Args:
            csv_path: Path to the Quran CSV file
        """
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
    
    def get_surah_text(self, surah_num: int, include_muqattaat: bool = True) -> str:
        """Get complete text of a surah."""
        surah_verses = [row['text'] for row in self.data if row['surah'] == surah_num]
        
        if not surah_verses:
            return ""
        
        # Remove Basmala from first verse
        if surah_verses:
            surah_verses[0] = self.remove_basmala(surah_verses[0])
        
        # Filter out empty verses
        surah_verses = [verse for verse in surah_verses if verse.strip()]
        
        # If this surah has Muqatta'at and we want to include them
        if include_muqattaat and surah_num in self.muqattaat_mapping:
            muqattaat = self.muqattaat_mapping[surah_num]
            return muqattaat + " " + " ".join(surah_verses)
        else:
            return " ".join(surah_verses)
    
    def count_letters_in_text(self, text: str) -> Dict[str, int]:
        """Count individual letters in Arabic text."""
        # Remove spaces, punctuation, and keep only Arabic letters
        clean_text = re.sub(r'[^\u0600-\u06FF]', '', text)
        return Counter(clean_text)
    
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
    
    def calculate_letter_ratios(self, text: str, target_letters: List[str]) -> Dict[str, float]:
        """Calculate the ratio of target letters to total letters in text."""
        letter_counts = self.count_letters_in_text(text)
        total_letters = sum(letter_counts.values())
        
        if total_letters == 0:
            return {letter: 0.0 for letter in target_letters}
        
        ratios = {}
        for letter in target_letters:
            count = letter_counts.get(letter, 0)
            ratios[letter] = count / total_letters
            
        return ratios
    
    def analyze_surah_muqattaat_ratios(self, surah_num: int) -> Dict:
        """Analyze Muqatta'at letter ratios for a specific surah."""
        if surah_num not in self.muqattaat_mapping:
            return {"error": f"Surah {surah_num} does not have Muqatta'at"}
        
        # Get Muqatta'at letters for this surah
        muqattaat_letters = self.get_muqattaat_letters_for_surah(surah_num)
        
        # Get text of current surah (with Muqatta'at)
        current_surah_text = self.get_surah_text(surah_num, include_muqattaat=True)
        
        # Get text of previous surah
        previous_surah_num = surah_num - 1
        previous_surah_text = ""
        if previous_surah_num > 0:
            previous_surah_text = self.get_surah_text(previous_surah_num, include_muqattaat=True)
        
        # Get text of all previous surahs
        all_previous_text = ""
        for prev_surah in range(1, surah_num):
            all_previous_text += " " + self.get_surah_text(prev_surah, include_muqattaat=True)
        
        # Get text of next surah (after current surah)
        next_surah_num = surah_num + 1
        next_surah_text = ""
        max_surah = max(row['surah'] for row in self.data)
        if next_surah_num <= max_surah:
            next_surah_text = self.get_surah_text(next_surah_num, include_muqattaat=True)
        
        # Get text of all surahs after current surah (excluding current surah)
        all_after_text = ""
        for after_surah in range(surah_num + 1, max_surah + 1):
            all_after_text += " " + self.get_surah_text(after_surah, include_muqattaat=True)
        
        # Calculate ratios
        current_ratios = self.calculate_letter_ratios(current_surah_text, muqattaat_letters)
        previous_ratios = self.calculate_letter_ratios(previous_surah_text, muqattaat_letters)
        all_previous_ratios = self.calculate_letter_ratios(all_previous_text, muqattaat_letters)
        next_ratios = self.calculate_letter_ratios(next_surah_text, muqattaat_letters)
        all_after_ratios = self.calculate_letter_ratios(all_after_text, muqattaat_letters)
        
        # Calculate differences
        vs_previous_diff = {}
        vs_all_previous_diff = {}
        vs_next_diff = {}
        vs_all_after_diff = {}
        
        for letter in muqattaat_letters:
            vs_previous_diff[letter] = current_ratios[letter] - previous_ratios[letter]
            vs_all_previous_diff[letter] = current_ratios[letter] - all_previous_ratios[letter]
            vs_next_diff[letter] = current_ratios[letter] - next_ratios[letter]
            vs_all_after_diff[letter] = current_ratios[letter] - all_after_ratios[letter]
        
        # Calculate actual letter counts
        current_counts = self.count_letters_in_text(current_surah_text)
        previous_counts = self.count_letters_in_text(previous_surah_text)
        all_previous_counts = self.count_letters_in_text(all_previous_text)
        next_counts = self.count_letters_in_text(next_surah_text)
        all_after_counts = self.count_letters_in_text(all_after_text)
        
        # Get counts for Muqatta'at letters specifically
        current_letter_counts = {}
        previous_letter_counts = {}
        all_previous_letter_counts = {}
        next_letter_counts = {}
        all_after_letter_counts = {}
        
        for letter in muqattaat_letters:
            current_letter_counts[letter] = current_counts.get(letter, 0)
            previous_letter_counts[letter] = previous_counts.get(letter, 0)
            all_previous_letter_counts[letter] = all_previous_counts.get(letter, 0)
            next_letter_counts[letter] = next_counts.get(letter, 0)
            all_after_letter_counts[letter] = all_after_counts.get(letter, 0)
        
        return {
            "surah": surah_num,
            "muqattaat_letters": muqattaat_letters,
            "current_ratios": current_ratios,
            "previous_ratios": previous_ratios,
            "all_previous_ratios": all_previous_ratios,
            "next_ratios": next_ratios,
            "all_after_ratios": all_after_ratios,
            "vs_previous_difference": vs_previous_diff,
            "vs_all_previous_difference": vs_all_previous_diff,
            "vs_next_difference": vs_next_diff,
            "vs_all_after_difference": vs_all_after_diff,
            "current_total_letters": sum(current_counts.values()),
            "previous_total_letters": sum(previous_counts.values()),
            "all_previous_total_letters": sum(all_previous_counts.values()),
            "next_total_letters": sum(next_counts.values()),
            "all_after_total_letters": sum(all_after_counts.values()),
            "current_letter_counts": current_letter_counts,
            "previous_letter_counts": previous_letter_counts,
            "all_previous_letter_counts": all_previous_letter_counts,
            "next_letter_counts": next_letter_counts,
            "all_after_letter_counts": all_after_letter_counts
        }
    
    def analyze_all_muqattaat_surahs(self) -> List[Dict]:
        """Analyze all surahs with Muqatta'at."""
        results = []
        
        for surah_num in sorted(self.muqattaat_mapping.keys()):
            analysis = self.analyze_surah_muqattaat_ratios(surah_num)
            if "error" not in analysis:
                results.append(analysis)
        
        return results
    
    def create_results_table(self, results: List[Dict]):
        """Create a table with the analysis results."""
        # Create list of rows for table
        rows = []
        
        for result in results:
            surah = result['surah']
            muqattaat = ''.join(result['muqattaat_letters'])
            
            for letter in result['muqattaat_letters']:
                current_count = result['current_letter_counts'][letter]
                prev_count = result['previous_letter_counts'][letter]
                all_prev_count = result['all_previous_letter_counts'][letter]
                next_count = result['next_letter_counts'][letter]
                all_after_count = result['all_after_letter_counts'][letter]
                
                rows.append({
                    'Surah': surah,
                    'Muqattaat': muqattaat,
                    'Letter': letter,
                    'Current_Count': current_count,
                    'Previous_Count': prev_count,
                    'All_Previous_Count': all_prev_count,
                    'Next_Count': next_count,
                    'All_After_Count': all_after_count,
                    'Diff_vs_Previous': current_count - prev_count,
                    'Diff_vs_All_Previous': current_count - all_prev_count,
                    'Diff_vs_Next': current_count - next_count,
                    'Diff_vs_All_After': current_count - all_after_count,
                    'Current_Total_Letters': result['current_total_letters'],
                    'Previous_Total_Letters': result['previous_total_letters'],
                    'All_Previous_Total_Letters': result['all_previous_total_letters'],
                    'Next_Total_Letters': result['next_total_letters'],
                    'All_After_Total_Letters': result['all_after_total_letters']
                })
        
        return rows
    
    def print_analysis_results(self, results: List[Dict]):
        """Print analysis results as a formatted table."""
        rows = self.create_results_table(results)
        
        print("=" * 160)
        print("MUQATTA'AT LETTER COUNT ANALYSIS RESULTS")
        print("=" * 160)
        
        # Print header
        header = f"{'Surah':<5} {'Muqattaat':<10} {'Letter':<6} {'Current':<8} {'Previous':<9} {'All_Prev':<9} {'Next':<6} {'All_After':<10} {'Diff_Prev':<10} {'Diff_All':<9} {'Diff_Next':<10} {'Diff_After':<10} {'Curr_Tot':<8} {'Prev_Tot':<8} {'All_Tot':<8} {'Next_Tot':<8} {'After_Tot':<8}"
        print(header)
        print("-" * 160)
        
        # Print data rows
        for row in rows:
            line = f"{row['Surah']:<5} {row['Muqattaat']:<10} {row['Letter']:<6} {row['Current_Count']:<8} {row['Previous_Count']:<9} {row['All_Previous_Count']:<9} {row['Next_Count']:<6} {row['All_After_Count']:<10} {row['Diff_vs_Previous']:<10} {row['Diff_vs_All_Previous']:<9} {row['Diff_vs_Next']:<10} {row['Diff_vs_All_After']:<10} {row['Current_Total_Letters']:<8} {row['Previous_Total_Letters']:<8} {row['All_Previous_Total_Letters']:<8} {row['Next_Total_Letters']:<8} {row['All_After_Total_Letters']:<8}"
            print(line)
        
        print("=" * 160)
        
        return rows
    
    def save_results_to_csv_wide(self, results: List[Dict], filename: str):
        """Save analysis results to CSV file in wide format."""
        with open(filename, 'w', newline='', encoding='utf-8') as file:
            writer = csv.writer(file)
            
            # Write header
            header = ['surah', 'muqattaat_letters', 'current_total_letters', 
                     'previous_total_letters', 'all_previous_total_letters',
                     'next_total_letters', 'all_after_total_letters']
            
            # Add letter-specific columns
            all_letters = set()
            for result in results:
                all_letters.update(result['muqattaat_letters'])
            
            for letter in sorted(all_letters):
                header.extend([
                    f'current_count_{letter}',
                    f'previous_count_{letter}',
                    f'all_previous_count_{letter}',
                    f'next_count_{letter}',
                    f'all_after_count_{letter}',
                    f'current_ratio_{letter}',
                    f'previous_ratio_{letter}',
                    f'all_previous_ratio_{letter}',
                    f'next_ratio_{letter}',
                    f'all_after_ratio_{letter}',
                    f'vs_previous_diff_{letter}',
                    f'vs_all_previous_diff_{letter}',
                    f'vs_next_diff_{letter}',
                    f'vs_all_after_diff_{letter}'
                ])
            
            writer.writerow(header)
            
            # Write data
            for result in results:
                row = [
                    result['surah'],
                    ' '.join(result['muqattaat_letters']),
                    result['current_total_letters'],
                    result['previous_total_letters'],
                    result['all_previous_total_letters'],
                    result['next_total_letters'],
                    result['all_after_total_letters']
                ]
                
                for letter in sorted(all_letters):
                    if letter in result['muqattaat_letters']:
                        row.extend([
                            result['current_letter_counts'][letter],
                            result['previous_letter_counts'][letter],
                            result['all_previous_letter_counts'][letter],
                            result['next_letter_counts'][letter],
                            result['all_after_letter_counts'][letter],
                            result['current_ratios'][letter],
                            result['previous_ratios'][letter],
                            result['all_previous_ratios'][letter],
                            result['next_ratios'][letter],
                            result['all_after_ratios'][letter],
                            result['vs_previous_difference'][letter],
                            result['vs_all_previous_difference'][letter],
                            result['vs_next_difference'][letter],
                            result['vs_all_after_difference'][letter]
                        ])
                    else:
                        row.extend([0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0])
                
                writer.writerow(row)
        
        print(f"Wide format results saved to {filename}")
    
    def save_results_to_csv_long(self, results: List[Dict], filename: str):
        """Save analysis results to CSV file in long format with descriptive calculation types."""
        with open(filename, 'w', newline='', encoding='utf-8') as file:
            writer = csv.writer(file)
            
            # Write header for long format
            header = [
                'surah', 'muqattaat_letters', 'letter', 'calculation_type', 
                'calculation_description', 'value', 'comparison_context'
            ]
            writer.writerow(header)
            
            # Define calculation types and their descriptions
            calculation_types = {
                'count_current': 'Letter count in current surah',
                'count_previous': 'Letter count in previous surah',
                'count_all_previous': 'Letter count in all previous surahs',
                'count_next': 'Letter count in next surah',
                'count_all_after': 'Letter count in all subsequent surahs',
                'ratio_current': 'Letter ratio in current surah (count/total)',
                'ratio_previous': 'Letter ratio in previous surah (count/total)',
                'ratio_all_previous': 'Letter ratio in all previous surahs (count/total)',
                'ratio_next': 'Letter ratio in next surah (count/total)',
                'ratio_all_after': 'Letter ratio in all subsequent surahs (count/total)',
                'diff_vs_previous': 'Difference between current and previous surah ratios',
                'diff_vs_all_previous': 'Difference between current and all previous surahs ratios',
                'diff_vs_next': 'Difference between current and next surah ratios',
                'diff_vs_all_after': 'Difference between current and all subsequent surahs ratios'
            }
            
            # Write data rows
            for result in results:
                surah = result['surah']
                muqattaat = ' '.join(result['muqattaat_letters'])
                
                for letter in result['muqattaat_letters']:
                    # Count data
                    writer.writerow([
                        surah, muqattaat, letter, 'count_current',
                        calculation_types['count_current'],
                        result['current_letter_counts'][letter],
                        'Current surah with Muqatta\'at'
                    ])
                    
                    writer.writerow([
                        surah, muqattaat, letter, 'count_previous',
                        calculation_types['count_previous'],
                        result['previous_letter_counts'][letter],
                        'Previous surah (surah ' + str(surah-1) + ')'
                    ])
                    
                    writer.writerow([
                        surah, muqattaat, letter, 'count_all_previous',
                        calculation_types['count_all_previous'],
                        result['all_previous_letter_counts'][letter],
                        'All surahs before current (surahs 1-' + str(surah-1) + ')'
                    ])
                    
                    writer.writerow([
                        surah, muqattaat, letter, 'count_next',
                        calculation_types['count_next'],
                        result['next_letter_counts'][letter],
                        'Next surah (surah ' + str(surah+1) + ')'
                    ])
                    
                    writer.writerow([
                        surah, muqattaat, letter, 'count_all_after',
                        calculation_types['count_all_after'],
                        result['all_after_letter_counts'][letter],
                        'All surahs after current (surahs ' + str(surah+1) + '-114)'
                    ])
                    
                    # Ratio data
                    writer.writerow([
                        surah, muqattaat, letter, 'ratio_current',
                        calculation_types['ratio_current'],
                        result['current_ratios'][letter],
                        'Current surah with Muqatta\'at'
                    ])
                    
                    writer.writerow([
                        surah, muqattaat, letter, 'ratio_previous',
                        calculation_types['ratio_previous'],
                        result['previous_ratios'][letter],
                        'Previous surah (surah ' + str(surah-1) + ')'
                    ])
                    
                    writer.writerow([
                        surah, muqattaat, letter, 'ratio_all_previous',
                        calculation_types['ratio_all_previous'],
                        result['all_previous_ratios'][letter],
                        'All surahs before current (surahs 1-' + str(surah-1) + ')'
                    ])
                    
                    writer.writerow([
                        surah, muqattaat, letter, 'ratio_next',
                        calculation_types['ratio_next'],
                        result['next_ratios'][letter],
                        'Next surah (surah ' + str(surah+1) + ')'
                    ])
                    
                    writer.writerow([
                        surah, muqattaat, letter, 'ratio_all_after',
                        calculation_types['ratio_all_after'],
                        result['all_after_ratios'][letter],
                        'All surahs after current (surahs ' + str(surah+1) + '-114)'
                    ])
                    
                    # Difference data
                    writer.writerow([
                        surah, muqattaat, letter, 'diff_vs_previous',
                        calculation_types['diff_vs_previous'],
                        result['vs_previous_difference'][letter],
                        'Current vs Previous surah ratio difference'
                    ])
                    
                    writer.writerow([
                        surah, muqattaat, letter, 'diff_vs_all_previous',
                        calculation_types['diff_vs_all_previous'],
                        result['vs_all_previous_difference'][letter],
                        'Current vs All Previous surahs ratio difference'
                    ])
                    
                    writer.writerow([
                        surah, muqattaat, letter, 'diff_vs_next',
                        calculation_types['diff_vs_next'],
                        result['vs_next_difference'][letter],
                        'Current vs Next surah ratio difference'
                    ])
                    
                    writer.writerow([
                        surah, muqattaat, letter, 'diff_vs_all_after',
                        calculation_types['diff_vs_all_after'],
                        result['vs_all_after_difference'][letter],
                        'Current vs All Subsequent surahs ratio difference'
                    ])
        
        print(f"Long format results saved to {filename}")
    
    def save_results_to_csv(self, results: List[Dict], filename: str):
        """Save analysis results to CSV file (legacy method for backward compatibility)."""
        self.save_results_to_csv_wide(results, filename)
    
    def get_summary_statistics(self, results: List[Dict]) -> Dict:
        """Get summary statistics for the analysis."""
        summary = {}
        
        # Collect all letters that appear in any surah
        all_letters = set()
        for result in results:
            all_letters.update(result['muqattaat_letters'])
        
        for letter in sorted(all_letters):
            # Find surahs that have this letter
            letter_results = [r for r in results if letter in r['muqattaat_letters']]
            
            if letter_results:
                vs_prev_diffs = [r['vs_previous_difference'][letter] for r in letter_results]
                vs_all_prev_diffs = [r['vs_all_previous_difference'][letter] for r in letter_results]
                vs_next_diffs = [r['vs_next_difference'][letter] for r in letter_results]
                vs_all_after_diffs = [r['vs_all_after_difference'][letter] for r in letter_results]
                
                summary[f'{letter}_vs_previous_avg'] = sum(vs_prev_diffs) / len(vs_prev_diffs)
                summary[f'{letter}_vs_previous_std'] = (sum((x - summary[f'{letter}_vs_previous_avg'])**2 for x in vs_prev_diffs) / len(vs_prev_diffs))**0.5
                summary[f'{letter}_vs_all_previous_avg'] = sum(vs_all_prev_diffs) / len(vs_all_prev_diffs)
                summary[f'{letter}_vs_all_previous_std'] = (sum((x - summary[f'{letter}_vs_all_previous_avg'])**2 for x in vs_all_prev_diffs) / len(vs_all_prev_diffs))**0.5
                summary[f'{letter}_vs_next_avg'] = sum(vs_next_diffs) / len(vs_next_diffs)
                summary[f'{letter}_vs_next_std'] = (sum((x - summary[f'{letter}_vs_next_avg'])**2 for x in vs_next_diffs) / len(vs_next_diffs))**0.5
                summary[f'{letter}_vs_all_after_avg'] = sum(vs_all_after_diffs) / len(vs_all_after_diffs)
                summary[f'{letter}_vs_all_after_std'] = (sum((x - summary[f'{letter}_vs_all_after_avg'])**2 for x in vs_all_after_diffs) / len(vs_all_after_diffs))**0.5
        
        return summary


def main():
    """Main function to run the analysis."""
    print("Starting Muqatta'at Letter Ratio Analysis...")
    print("=" * 50)
    
    # Load data
    analyzer = SimpleQuranAnalyzer("datasets/quran-simple-clean.csv")
    
    # Run analysis
    print("\nAnalyzing Muqatta'at letter ratios...")
    results = analyzer.analyze_all_muqattaat_surahs()
    
    print(f"\nAnalysis completed for {len(results)} surahs")
    
    # Print results as table
    table_rows = analyzer.print_analysis_results(results)
    
    # Save results in both formats
    analyzer.save_results_to_csv_wide(results, "05_wide.csv")
    analyzer.save_results_to_csv_long(results, "05_long.csv")
    
    print(f"\nAnalysis complete! Results saved to:")
    print(f"  - Wide format: '05_wide.csv'")
    print(f"  - Long format: '05_long.csv'")
    print(f"Total rows in wide format: {len(table_rows)}")
    
    # Calculate total rows for long format
    total_long_rows = 0
    for result in results:
        total_long_rows += len(result['muqattaat_letters']) * 14  # 14 calculation types per letter
    print(f"Total rows in long format: {total_long_rows}")


if __name__ == "__main__":
    main()
