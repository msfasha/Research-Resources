# noise_robustness_experiment.py
"""
Low-effort experiment: Test robustness to sensor noise
Addresses reviewer concern: "Does this fall apart with imperfect data?"
"""

import epyt
import numpy as np
import json

NETWORK_FILE = "/home/me/Downloads/sample_network.inp"

def add_noise_to_data(data, noise_std, noise_type="absolute"):
    """Add Gaussian noise to sensor data"""
    noisy_data = []
    for value in data:
        if noise_type == "absolute":
            noise = np.random.normal(0, noise_std)
            noisy_value = value + noise
        else:  # relative percentage
            noise = np.random.normal(0, noise_std * abs(value))
            noisy_value = value + noise
        noisy_data.append(noisy_value)
    return noisy_data

def count_anomalies(pressures, flows, pressure_low=20, pressure_high=60):
    """Count nodes flagged as anomalous"""
    low_pressure = sum(1 for p in pressures if 0 < p < pressure_low)
    high_pressure = sum(1 for p in pressures if p > pressure_high)
    return low_pressure + high_pressure

def run_noise_experiment(network_file, burst_junction_idx=None):
    """Run noise robustness experiment"""
    
    print("\n" + "="*70)
    print("NOISE ROBUSTNESS EXPERIMENT")
    print("="*70)
    
    results = []
    
    # Define noise levels
    noise_configs = [
        {"name": "No Noise (Baseline)", "pressure": 0, "flow": 0},
        {"name": "Low Noise (±2m, ±5% flow)", "pressure": 2, "flow": 0.05},
        {"name": "Medium Noise (±5m, ±10% flow)", "pressure": 5, "flow": 0.10},
        {"name": "High Noise (±8m, ±15% flow)", "pressure": 8, "flow": 0.15},
    ]
    
    for config in noise_configs:
        print(f"\n{'='*70}")
        print(f"Testing: {config['name']}")
        print(f"{'='*70}")
        
        # Load network
        d = epyt.epanet(network_file)
        
        # Add burst if specified
        if burst_junction_idx:
            d.setNodeEmitterCoeff(burst_junction_idx, 4.7)
            scenario = "burst"
        else:
            scenario = "baseline"
        
        # Run simulation
        H = d.getComputedHydraulicTimeSeries()
        
        # Get data
        hour = 0
        pressures = H.Pressure[hour]
        flows = H.Flow[hour]
        
        # Add noise
        if config['pressure'] > 0:
            pressures = add_noise_to_data(pressures, config['pressure'], "absolute")
        if config['flow'] > 0:
            flows = add_noise_to_data(flows, config['flow'], "relative")
        
        # Count anomalies
        total_anomalies = count_anomalies(pressures, flows)
        low_pressure = sum(1 for p in pressures if 0 < p < 20)
        critical = sum(1 for p in pressures if 0 < p < 15)
        
        # Check if burst still detected (look for increase in low pressure nodes)
        burst_detected = "N/A"
        if burst_junction_idx:
            # Simple heuristic: if >35% nodes have low pressure, burst is "detected"
            if low_pressure > 400:  # More than baseline 399
                burst_detected = "Yes"
            else:
                burst_detected = "No"
        
        result = {
            "config": config['name'],
            "pressure_noise_m": config['pressure'],
            "flow_noise_pct": config['flow'] * 100,
            "total_anomalies": total_anomalies,
            "low_pressure_nodes": low_pressure,
            "critical_nodes": critical,
            "burst_detected": burst_detected,
            "scenario": scenario
        }
        
        results.append(result)
        
        print(f"  Total anomalies flagged: {total_anomalies}")
        print(f"  Low pressure nodes (<20m): {low_pressure}")
        print(f"  Critical nodes (<15m): {critical}")
        if burst_junction_idx:
            print(f"  Burst detected: {burst_detected}")
        
        d.unload()
    
    return results

def run_both_scenarios():
    """Run noise experiment for both baseline and burst"""
    
    print("\n" + "="*70)
    print("EXPERIMENT 1: BASELINE NETWORK (NO BURST)")
    print("="*70)
    
    baseline_results = run_noise_experiment(NETWORK_FILE, burst_junction_idx=None)
    
    print("\n" + "="*70)
    print("EXPERIMENT 2: PIPE BURST SCENARIO")
    print("="*70)
    
    # Get junction count and pick middle junction for burst
    d_temp = epyt.epanet(NETWORK_FILE)
    junction_count = d_temp.getNodeJunctionCount()
    burst_junction = junction_count // 2
    d_temp.unload()
    
    burst_results = run_noise_experiment(NETWORK_FILE, burst_junction_idx=burst_junction)
    
    # Save results
    all_results = {
        "baseline": baseline_results,
        "burst": burst_results
    }
    
    with open("noise_robustness_results.json", 'w') as f:
        json.dump(all_results, f, indent=2)
    
    print("\n" + "="*70)
    print("EXPERIMENT COMPLETED")
    print("="*70)
    print("Results saved to: noise_robustness_results.json")
    
    # Print summary table
    print("\n" + "="*70)
    print("SUMMARY TABLE: BURST SCENARIO")
    print("="*70)
    print(f"{'Noise Level':<30} {'Anomalies':<12} {'Burst Det.':<12} {'False Alarms':<15}")
    print("-"*70)
    
    baseline_anomalies = burst_results[0]['total_anomalies']
    
    for result in burst_results:
        false_alarm_increase = ((result['total_anomalies'] - baseline_anomalies) / baseline_anomalies * 100)
        print(f"{result['config']:<30} {result['total_anomalies']:<12} {result['burst_detected']:<12} {false_alarm_increase:>+6.1f}%")
    
    return all_results

if __name__ == "__main__":
    results = run_both_scenarios()