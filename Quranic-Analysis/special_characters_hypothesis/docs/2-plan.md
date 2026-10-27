# Muqatta'at Checksum Analysis Plan

## Overview

Build a comprehensive analysis system to test the hypothesis that Muqatta'at (المقطعات) - the disjointed letters at the start of certain surahs - function as checksums for validating Quranic text sequences.

## Implementation Approach

### 1. Shared Utilities Module

**File**: `src/data_utils.py`

Core data preparation functions:

- Load and clean Quran CSV dataset
- Remove Basmala (بسم الله الرحمن الرحيم) from first ayah of each surah
- Special handling: Remove entire Surah 1 (Al-Fatiha) as it only contains Basmala
- Extract and catalog Muqatta'at from 29 surahs
- Create mapping: `{surah_number: muqattaat_letters}`
- Provide clean text accessor functions for analysis

### 2. Basic Statistical Analysis

**File**: `src/01_basic_statistics.ipynb`

Calculate foundational metrics:

- Letter frequency distributions (character counts) per surah
- Mean, variance, standard deviation of letter occurrences
- Compare surahs WITH vs WITHOUT Muqatta'at
- Correlation between Muqatta'at letters and surah letter frequencies
- **Visualizations**: Bar charts, histograms, box plots, correlation heatmaps

### 3. Information Theory Analysis

**File**: `src/02_information_theory.ipynb`

Test the checksum hypothesis:

- Shannon entropy of letter distributions
- Compression ratios (gzip/zlib) before/after removing Muqatta'at
- Information content and predictive power analysis
- Redundancy calculation: redundancy = 1 - (H/H_max)
- **Visualizations**: Entropy trends, compression comparison plots, redundancy charts

### 4. Frequency Domain Analysis

**File**: `src/03_frequency_domain.ipynb`

Detect periodic patterns:

- Convert letter sequences to numeric representations
- Apply FFT to detect spectral patterns
- Identify dominant frequencies correlated with Muqatta'at
- Compare spectral signatures (with/without Muqatta'at)
- **Visualizations**: Spectrograms, power spectral density plots, frequency heatmaps

### 5. Autocorrelation Analysis

**File**: `src/04_autocorrelation.ipynb`

Pattern repetition detection:

- Calculate autocorrelation function for letter sequences
- Identify repeating structures at different lags
- Test if Muqatta'at predict periodic patterns
- Cross-correlation between Muqatta'at and surah body
- **Visualizations**: Autocorrelation plots, lag plots, cross-correlation matrices

## Key Files

- **Data source**: `datasets/quran-simple-clean.csv` (6238 verses)
- **Shared utilities**: `src/data_utils.py` (to be created)
- **Analysis notebooks**: 
- `src/01_basic_statistics.ipynb`
- `src/02_information_theory.ipynb`
- `src/03_frequency_domain.ipynb`
- `src/04_autocorrelation.ipynb`

## Technical Notes

- Use pandas for data manipulation
- NumPy/SciPy for numerical analysis (FFT, entropy)
- Matplotlib/Seaborn for visualizations
- Handle Arabic text properly with existing libraries (arabic-reshaper, python-bidi)
- Character-level analysis only (no morphological processing needed)
- Each notebook is self-contained with its own visualizations
- All notebooks import from shared `data_utils.py` module