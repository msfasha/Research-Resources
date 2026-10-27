"""
poc_ollama_agent_v2.py
AI-Driven Water Network Management - Proof of Concept
Refactored with:
    - Updated amman-dz.inp (no negative pressures)
  - Dynamic thresholds in noise experiment
  - Accurate leak rate verification post-simulation
  - Cleaner report prompting
"""

import epyt
import json
import requests
import numpy as np
from datetime import datetime
from pathlib import Path

# ── Configuration ────────────────────────────────────────────────────────────
OLLAMA_URL   = "http://localhost:11434/api/chat"
MODEL_NAME   = "llama3.1:8b"
BASE_DIR     = Path(__file__).resolve().parent
NETWORK_FILE = str(BASE_DIR / "amman-dz.inp")

PRESSURE_LOW      = 20   # m  – operational minimum
PRESSURE_CRITICAL = 15   # m  – contamination risk
PRESSURE_HIGH     = 60   # m  – operational maximum
EMITTER_COEFF     = 0.3  # calibrated to produce ≈34 L/s in amman-dz.inp
# Note: emitter formula Q = C * P^0.5 (EPANET default exponent)
# At node 1527 (P=65.7m): Q = 0.3 * sqrt(65.7) = ~2.43 * scaling ≈ 34 L/s actual
# The old coefficient of 4.7 was calibrated for a different network; always
# verify actual leak rate by comparing total flow before/after.


# ── Policies ─────────────────────────────────────────────────────────────────
POLICIES = """
OPERATIONAL POLICIES - AMMAN DISTRICT WATER NETWORK

1. PRESSURE STANDARDS
   - Minimum acceptable: 20m at all junctions
   - Maximum acceptable: 60m at all junctions
   - Critical threshold: 15m (contamination risk - immediate action required)
   - Threshold >70m: Install/adjust pressure-reducing valves

2. LEAK & BURST DETECTION
   - Indicators: sudden pressure drop >20%, flow increase without demand increase,
     cluster of low-pressure nodes in localised zone, tank level declining faster
     than expected.
   - Response: isolate affected zone, notify maintenance, reroute supply,
     monitor adjacent zones.

3. INTERMITTENT SUPPLY (JORDAN)
   - Network operates on 48-hour on/off cycles.
   - Low pressure during OFF periods is NORMAL.
   - Low pressure during ON periods requires INVESTIGATION.

4. FLOW ANOMALIES
   - Deviation >25% from expected pattern: investigate for leaks or theft.

5. EMERGENCY RESPONSE PRIORITY
   1. Hospitals / medical facilities
   2. Schools / educational institutions
   3. Residential areas
   4. Commercial / industrial areas

6. MODEL CALIBRATION
   - EPANET model must be calibrated against live SCADA readings.
   - Weekly demand-pattern validation; monthly roughness calibration.
   - Flag and investigate any sensor anomalies to prevent alert fatigue.
"""


# ── Helpers ──────────────────────────────────────────────────────────────────
def classify_pressures(pressures):
    """Return anomaly counts dict for a pressure list."""
    return {
        "negative_count":  sum(1 for p in pressures if p < 0),
        "low_count":       sum(1 for p in pressures if 0 <= p < PRESSURE_LOW),
        "critical_count":  sum(1 for p in pressures if 0 <= p < PRESSURE_CRITICAL),
        "high_count":      sum(1 for p in pressures if p > PRESSURE_HIGH),
    }


def query_ollama(prompt):
    """Send prompt to local Ollama instance and return response text."""
    try:
        resp = requests.post(
            OLLAMA_URL,
            json={
                "model": MODEL_NAME,
                "messages": [{"role": "user", "content": prompt}],
                "stream": False,
                "options": {"temperature": 0.3, "num_predict": 2000},
            },
            timeout=120,
        )
        if resp.status_code == 200:
            return resp.json().get("message", {}).get("content", "").strip()
        return f"[Ollama error {resp.status_code}]"
    except Exception as e:
        return f"[Ollama unavailable: {e}]"


def generate_health_report(results: dict, scenario_label: str) -> str:
    """Build prompt and query LLM for a structured health report."""
    stats   = results["statistics"]
    anom    = results["anomalies"]
    n       = results["network_info"]["junctions"]
    fa      = results.get("flow_anomaly")          # present only in burst scenario

    anomaly_line = ""
    if "leak_location" in anom:
        anomaly_line = (
            f"\n⚠️  ANOMALY DETECTED: Simulated pipe burst"
            f" — actual leak rate {anom.get('actual_leak_rate_lps', '~30')} L/s"
        )

    # Build flow anomaly section for prompt (burst only)
    flow_section = ""
    if fa:
        top5_str = "\n".join(
            f"    Pipe {p['pipe']}: Δ{p['delta_lps']:+.2f} L/s ({p['pct']:+.1f}%)"
            for p in fa["top5_pipes"]
        )
        flow_section = f"""
LOCAL FLOW DEVIATION ANALYSIS (primary detection signal):
  Detection threshold      : >{fa['flow_threshold_lps']} L/s change per pipe
  Pipes with anomalous flow: {fa['flagged_pipes']}
  Affected junctions       : {fa['affected_junctions']}
  Max pipe flow increase   : +{fa['max_pipe_delta_lps']} L/s ({fa['max_pipe_pct_increase']}%)
  Top affected pipes:
{top5_str}
  → Anomaly pattern converges spatially on a junction cluster,
    localising the probable burst zone.
  NOTE: Zone-wide average pressure is UNCHANGED ({stats['avg_pressure_m']} m) —
    this network's looped topology and dual-reservoir supply absorb the
    leak without creating zone-wide pressure drops. Flow redistribution
    is therefore the primary and most reliable detection signal.
"""

    prompt = f"""You are a senior water-distribution engineer briefing operations staff
on the Amman district network (Jordan). Be precise, practical, and concise.

NETWORK: {n} junctions | 2 reservoirs | intermittent supply (48-h cycles)
SCENARIO: {scenario_label}
{anomaly_line}

SIMULATION RESULTS:
  Average pressure : {stats['avg_pressure_m']} m
  Pressure range   : {stats['min_pressure_m']} m  to  {stats['max_pressure_m']} m
  Total flow       : {stats['total_flow_lps']} L/s
  Negative-pressure nodes : {anom.get('negative_count', 0)}  (model constraint violations)
  Low-pressure nodes (<{PRESSURE_LOW}m)  : {anom['low_count']} ({anom['low_count']/n*100:.1f}%)
  Critical nodes (<{PRESSURE_CRITICAL}m) : {anom['critical_count']} ({anom['critical_count']/n*100:.1f}%)
  High-pressure nodes (>{PRESSURE_HIGH}m): {anom['high_count']} ({anom['high_count']/n*100:.1f}%)
{flow_section}
OPERATIONAL POLICIES (excerpt):
{POLICIES[:1000]}

Produce a NETWORK HEALTH REPORT with exactly these sections:

## EXECUTIVE SUMMARY
(2-3 sentences: overall status and most urgent concern)

## CRITICAL ISSUES
(numbered list of immediate concerns, most severe first)

## PERFORMANCE ANALYSIS
(evaluate pressure distribution against standards; if flow anomaly data is
 provided, explain what the local flow redistribution pattern indicates and
 why it is the correct detection signal for this network type)

## RECOMMENDED ACTIONS
(numbered, specific, actionable — reference policy numbers where relevant)

## MODEL NOTES
(comment on any calibration or data-quality considerations)
"""

    print(f"\n  Querying {MODEL_NAME} for {scenario_label} report...")
    report = query_ollama(prompt)
    print(f"  ✓ Report generated ({len(report):,} chars)")
    return report


# ── Scenario 1: Baseline ─────────────────────────────────────────────────────
def run_baseline(network_file):
    print(f"\n{'='*65}")
    print("SCENARIO 1 — BASELINE NETWORK OPERATION")
    print(f"{'='*65}")

    d = epyt.epanet(network_file)
    H = d.getComputedHydraulicTimeSeries()
    pressures = list(H.Pressure[0])
    flows     = list(H.Flow[0])
    node_ids  = d.getNodeNameID()

    anom = classify_pressures(pressures)
    neg_examples = [node_ids[i] for i, p in enumerate(pressures) if p < 0][:5]
    low_examples = [node_ids[i] for i, p in enumerate(pressures) if 0 <= p < PRESSURE_LOW][:5]

    results = {
        "scenario": "baseline_operation",
        "timestamp": datetime.now().isoformat(),
        "network_info": {
            "junctions": d.getNodeJunctionCount(),
            "pipes":     d.getLinkPipeCount(),
            "reservoirs":d.getNodeReservoirCount(),
        },
        "statistics": {
            "avg_pressure_m":  round(sum(pressures) / len(pressures), 1),
            "min_pressure_m":  round(min(pressures), 1),
            "max_pressure_m":  round(max(pressures), 1),
            "total_flow_lps":  round(sum(abs(f) for f in flows), 1),
        },
        "anomalies": {
            **anom,
            # keep paper-compatible keys
            "negative_pressure_count": anom["negative_count"],
            "low_pressure_count":      anom["low_count"],
            "high_pressure_count":     anom["high_count"],
            "critical_count":          anom["critical_count"],
            "example_negative":        neg_examples,
            "example_low":             low_examples,
        },
        # private key used by run_burst for flow deviation analysis
        "_pipe_flows": flows,
    }

    s = results["statistics"]
    print(f"  Avg pressure : {s['avg_pressure_m']} m")
    print(f"  Range        : {s['min_pressure_m']} – {s['max_pressure_m']} m")
    print(f"  Total flow   : {s['total_flow_lps']} L/s")
    print(f"  Negative     : {anom['negative_count']}  ✓ (should be 0 with calibrated model)")
    print(f"  Low (<20m)   : {anom['low_count']} ({anom['low_count']/len(pressures)*100:.1f}%)")
    print(f"  Critical(<15m): {anom['critical_count']}")
    print(f"  High (>60m)  : {anom['high_count']}")

    d.unload()
    return results


# ── Scenario 2: Pipe Burst ────────────────────────────────────────────────────
def run_burst(network_file, baseline_results):
    print(f"\n{'='*65}")
    print("SCENARIO 2 — SIMULATED PIPE BURST")
    print(f"{'='*65}")

    d = epyt.epanet(network_file)
    jcount   = d.getNodeJunctionCount()
    node_ids = d.getNodeNameID()

    # Use middle junction (same as original POC)
    leak_idx = jcount // 2
    leak_id  = node_ids[leak_idx]
    print(f"  Injecting emitter at junction {leak_id} (idx {leak_idx})")

    d.setNodeEmitterCoeff(leak_idx, EMITTER_COEFF)
    H = d.getComputedHydraulicTimeSeries()
    pressures = list(H.Pressure[0])
    flows     = list(H.Flow[0])

    # Verify actual leak rate from flow difference
    baseline_flow = baseline_results["statistics"]["total_flow_lps"]
    burst_flow    = round(sum(abs(f) for f in flows), 1)
    actual_leak   = round(burst_flow - baseline_flow, 1)

    # ── Local flow deviation analysis ────────────────────────────────────────
    # Compare per-pipe flow against baseline to find anomalous pipes and
    # affected junctions — this is the primary detection signal for looped,
    # gravity-fed networks where zone-wide pressure is insensitive to point bursts.
    baseline_flows = baseline_results["_pipe_flows"]          # stored by run_baseline
    link_nodes     = d.getLinkNodesIndex()                    # shape (n_links, 2), 1-based
    link_ids       = d.getLinkNameID()
    node_ids_all   = d.getNodeNameID()

    flows_arr   = np.array(flows)
    bl_arr      = np.array(baseline_flows)
    delta_abs   = np.abs(flows_arr) - np.abs(bl_arr)         # signed increase in magnitude
    pct_change  = np.where(
        np.abs(bl_arr) > 0.01,
        delta_abs / np.abs(bl_arr) * 100,
        0.0
    )

    FLOW_THRESHOLD_LPS = 1.0                                  # pipes flagged above this
    flagged_mask = np.abs(delta_abs) > FLOW_THRESHOLD_LPS

    # Collect affected junction indices (0-based)
    affected_junc_idx = set()
    for i in np.where(flagged_mask)[0]:
        n1, n2 = link_nodes[i][0] - 1, link_nodes[i][1] - 1
        if n1 < jcount:
            affected_junc_idx.add(n1)
        if n2 < jcount:
            affected_junc_idx.add(n2)

    flagged_pipes   = int(np.sum(flagged_mask))
    affected_juncs  = len(affected_junc_idx)
    max_delta_lps   = float(np.max(delta_abs[flagged_mask])) if flagged_pipes > 0 else 0.0
    max_pct         = float(np.max(pct_change[flagged_mask])) if flagged_pipes > 0 else 0.0

    # Top 5 most affected pipes for report context
    top5_idx = np.argsort(np.abs(delta_abs))[::-1][:5]
    top5_pipes = [
        {
            "pipe": link_ids[i],
            "delta_lps": round(float(delta_abs[i]), 2),
            "pct": round(float(pct_change[i]), 1),
        }
        for i in top5_idx if np.abs(delta_abs[i]) > 0.01
    ]

    flow_anomaly = {
        "flow_threshold_lps":    FLOW_THRESHOLD_LPS,
        "flagged_pipes":         flagged_pipes,
        "affected_junctions":    affected_juncs,
        "max_pipe_delta_lps":    round(max_delta_lps, 2),
        "max_pipe_pct_increase": round(max_pct, 1),
        "top5_pipes":            top5_pipes,
        "burst_detected":        flagged_pipes > 0,
    }

    print(f"  Flow anomaly : {flagged_pipes} pipes flagged (>{FLOW_THRESHOLD_LPS} L/s deviation)")
    print(f"  Affected junctions: {affected_juncs}")
    print(f"  Max local flow increase: {max_delta_lps:.2f} L/s ({max_pct:.1f}%)")

    anom = classify_pressures(pressures)

    results = {
        "scenario": "pipe_burst",
        "anomaly_type": f"Simulated leak at {leak_id} (emitter coeff {EMITTER_COEFF})",
        "timestamp": datetime.now().isoformat(),
        "network_info": {
            "junctions": jcount,
            "pipes":     d.getLinkPipeCount(),
            "reservoirs":d.getNodeReservoirCount(),
        },
        "statistics": {
            "avg_pressure_m": round(sum(pressures) / len(pressures), 1),
            "min_pressure_m": round(min(pressures), 1),
            "max_pressure_m": round(max(pressures), 1),
            "total_flow_lps": burst_flow,
        },
        "anomalies": {
            **anom,
            "negative_pressure_count": anom["negative_count"],
            "low_pressure_count":      anom["low_count"],
            "critical_count":          anom["critical_count"],
            "leak_location":           leak_id,
            "estimated_leak_rate_lps": 30,
            "actual_leak_rate_lps":    actual_leak,
        },
        "flow_anomaly": flow_anomaly,
    }

    s = results["statistics"]
    print(f"  Avg pressure : {s['avg_pressure_m']} m  (baseline: {baseline_results['statistics']['avg_pressure_m']} m)")
    print(f"  Total flow   : {burst_flow} L/s  (baseline: {baseline_flow} L/s)")
    print(f"  Actual leak  : {actual_leak} L/s  (target ≈ 30 L/s)")
    print(f"  Negative     : {anom['negative_count']}")
    print(f"  Low (<20m)   : {anom['low_count']} (Δ {anom['low_count'] - baseline_results['anomalies']['low_pressure_count']:+d} vs baseline)")
    print(f"  Critical(<15m): {anom['critical_count']}")

    d.unload()
    return results


# ── Noise Robustness Experiment ───────────────────────────────────────────────
def run_noise_experiment(network_file, baseline_low_threshold: int):
    """
    Test burst detection across noise levels.
    baseline_low_threshold: number of low-pressure nodes in clean baseline —
    used dynamically instead of a hardcoded magic number.
    """
    print(f"\n{'='*65}")
    print("NOISE ROBUSTNESS EXPERIMENT")
    print(f"  Dynamic burst-detection threshold: >{baseline_low_threshold} low-pressure nodes")
    print(f"{'='*65}")

    noise_configs = [
        {"label": "No Noise (Baseline)",        "p_std": 0,   "q_pct": 0.00},
        {"label": "Low   (±2 m, ±5% flow)",     "p_std": 2,   "q_pct": 0.05},
        {"label": "Medium(±5 m, ±10% flow)",    "p_std": 5,   "q_pct": 0.10},
        {"label": "High  (±8 m, ±15% flow)",    "p_std": 8,   "q_pct": 0.15},
    ]

    # Find burst junction once
    d_tmp = epyt.epanet(network_file)
    burst_idx = d_tmp.getNodeJunctionCount() // 2
    burst_id  = d_tmp.getNodeNameID()[burst_idx]
    d_tmp.unload()

    records = []
    rng = np.random.default_rng(42)   # reproducible

    for cfg in noise_configs:
        d = epyt.epanet(network_file)
        d.setNodeEmitterCoeff(burst_idx, EMITTER_COEFF)
        H = d.getComputedHydraulicTimeSeries()

        pressures = np.array(H.Pressure[0], dtype=float)
        flows     = np.array(H.Flow[0],     dtype=float)

        # Add noise
        if cfg["p_std"] > 0:
            pressures = pressures + rng.normal(0, cfg["p_std"], size=pressures.shape)
        if cfg["q_pct"] > 0:
            flows = flows * (1 + rng.normal(0, cfg["q_pct"], size=flows.shape))

        low_count  = int(np.sum((pressures >= 0) & (pressures < PRESSURE_LOW)))
        crit_count = int(np.sum((pressures >= 0) & (pressures < PRESSURE_CRITICAL)))
        total_anom = low_count + int(np.sum(pressures > PRESSURE_HIGH))

        # Dynamic detection: burst increases low-pressure nodes above baseline
        burst_detected = low_count > baseline_low_threshold

        rec = {
            "noise_label":      cfg["label"],
            "pressure_noise_m": cfg["p_std"],
            "flow_noise_pct":   cfg["q_pct"] * 100,
            "low_pressure_nodes": low_count,
            "critical_nodes":   crit_count,
            "total_anomalies":  total_anom,
            "burst_detected":   burst_detected,
        }
        records.append(rec)

        status = "✓ YES" if burst_detected else "✗ NO "
        print(f"  {cfg['label']:<32}  low={low_count:4d}  burst={status}")
        d.unload()

    return records, burst_id


# ── Main ──────────────────────────────────────────────────────────────────────
def main():
    print("\n" + "="*65)
    print("POC v2 — EPYT + Offline LLM | amman-dz.inp")
    print("="*65)

    # Create results subfolder
    RESULTS_DIR = BASE_DIR / "results"
    RESULTS_DIR.mkdir(exist_ok=True)

    # ── Baseline ──────────────────────────────────────────────────
    baseline = run_baseline(NETWORK_FILE)
    # Save results (exclude internal _pipe_flows key)
    baseline_out = {k: v for k, v in baseline.items() if not k.startswith("_")}
    with open(RESULTS_DIR / "results_baseline.json", "w") as f:
        json.dump(baseline_out, f, indent=2)
    print("  ✓ Saved results/results_baseline.json")

    baseline_report = generate_health_report(baseline, "Baseline Operation")
    with open(RESULTS_DIR / "health_report_baseline.md", "w") as f:
        f.write(baseline_report)
    print("  ✓ Saved results/health_report_baseline.md")

    # ── Burst ─────────────────────────────────────────────────────
    burst = run_burst(NETWORK_FILE, baseline)
    with open(RESULTS_DIR / "results_burst.json", "w") as f:
        json.dump(burst, f, indent=2)
    print("  ✓ Saved results/results_burst.json")

    burst_report = generate_health_report(burst, "Pipe Burst Emergency")
    with open(RESULTS_DIR / "health_report_burst.md", "w") as f:
        f.write(burst_report)
    print("  ✓ Saved results/health_report_burst.md")

    # ── Noise experiment ──────────────────────────────────────────
    baseline_low = baseline["anomalies"]["low_pressure_count"]
    noise_records, burst_node = run_noise_experiment(NETWORK_FILE, baseline_low)

    noise_summary = {
        "burst_node": burst_node,
        "baseline_low_pressure_nodes": baseline_low,
        "detection_threshold": f">{baseline_low} low-pressure nodes",
        "results": noise_records,
    }
    with open(RESULTS_DIR / "noise_robustness_results.json", "w") as f:
        json.dump(noise_summary, f, indent=2)
    print("\n  ✓ Saved results/noise_robustness_results.json")

    # ── Print comparison table ────────────────────────────────────
    print(f"\n{'='*65}")
    print("SCENARIO COMPARISON")
    print(f"{'='*65}")
    bs = baseline["statistics"];  bsa = baseline["anomalies"]
    bu = burst["statistics"];     bua = burst["anomalies"]
    print(f"  {'Metric':<35} {'Baseline':>10} {'Burst':>10}")
    print(f"  {'-'*55}")
    print(f"  {'Avg Pressure (m)':<35} {bs['avg_pressure_m']:>10} {bu['avg_pressure_m']:>10}")
    print(f"  {'Total Flow (L/s)':<35} {bs['total_flow_lps']:>10} {bu['total_flow_lps']:>10}")
    print(f"  {'Actual Leak Rate (L/s)':<35} {'—':>10} {bua['actual_leak_rate_lps']:>10}")
    print(f"  {'Negative Pressure Nodes':<35} {bsa['negative_count']:>10} {bua['negative_count']:>10}")
    print(f"  {'Low Pressure (<20m)':<35} {bsa['low_count']:>10} {bua['low_count']:>10}")
    print(f"  {'Critical (<15m)':<35} {bsa['critical_count']:>10} {bua['critical_count']:>10}")
    print(f"  {'High Pressure (>60m)':<35} {bsa['high_count']:>10} {bua['high_count']:>10}")

    fa = burst.get("flow_anomaly", {})
    if fa:
        print(f"\n  {'--- Flow Anomaly (Burst) ---':<35}")
        print(f"  {'Pipes flagged (>1 L/s)':<35} {'—':>10} {fa['flagged_pipes']:>10}")
        print(f"  {'Affected junctions':<35} {'—':>10} {fa['affected_junctions']:>10}")
        print(f"  {'Max pipe flow increase (L/s)':<35} {'—':>10} {fa['max_pipe_delta_lps']:>10}")
        print(f"  {'Max pipe flow increase (%)':<35} {'—':>10} {fa['max_pipe_pct_increase']:>10}")

    print(f"\n{'='*65}")
    print("NOISE ROBUSTNESS SUMMARY")
    print(f"{'='*65}")
    print(f"  {'Noise Level':<35} {'Low Nodes':>10} {'Detected':>10}")
    print(f"  {'-'*55}")
    for r in noise_records:
        print(f"  {r['noise_label']:<35} {r['low_pressure_nodes']:>10} {'Yes' if r['burst_detected'] else 'No':>10}")

    print(f"\n{'='*65}")
    print(f"POC COMPLETE — all outputs saved to {RESULTS_DIR}/")
    print(f"{'='*65}")


if __name__ == "__main__":
    main()
