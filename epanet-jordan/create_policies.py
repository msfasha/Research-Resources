# create_policies.py
"""Create operational policies for AI agent to reference"""

def create_operational_policies():
    """Generate operational policy document"""
    
    policies = """
OPERATIONAL POLICIES - AMMAN DISTRICT WATER NETWORK
===================================================

1. PRESSURE MANAGEMENT STANDARDS
---------------------------------
- Minimum acceptable pressure: 20 meters at all junctions
- Maximum acceptable pressure: 60 meters at all junctions
- Pressure below 15m is CRITICAL - immediate investigation required
- Pressure above 70m requires immediate pressure reduction

Action: If pressure drops below 20m at multiple nodes:
  → Check for pipe bursts or major leaks
  → Verify tank levels and reservoir supply
  → Consider pump activation or pressure zone adjustment

Action: If pressure exceeds 60m at multiple nodes:
  → Risk of pipe bursts and joint failures
  → Install or adjust pressure-reducing valves (PRVs)
  → Reduce pump output or tank elevation setpoints


2. LEAK AND BURST DETECTION
----------------------------
Indicators of pipe burst:
  - Sudden pressure drop (>20% in affected zone)
  - Flow increase without corresponding demand increase
  - Multiple low-pressure nodes in localized area
  - Tank level declining faster than expected

Action: Upon detecting likely burst:
  → Isolate affected zone by closing boundary valves
  → Notify maintenance crews with location estimate
  → Reroute supply through alternative pipes if available
  → Monitor adjacent zones for pressure impacts


3. INTERMITTENT SUPPLY OPERATIONS (JORDAN SPECIFIC)
----------------------------------------------------
- Network operates 48-hour on/off cycles
- During OFF periods: pressures should be near-zero (expected)
- During ON periods: all standards apply

Action: Distinguish between scheduled shutoff and anomalies:
  → Low pressure during supply hours = PROBLEM
  → Low pressure during shutoff hours = NORMAL
  → Sudden pressure loss during supply period = INVESTIGATE


4. FLOW ANOMALIES
-----------------
- Unexpected flow increases suggest leaks or theft
- Flow decreases may indicate blockage or unauthorized valve closure

Action: If flow deviates >25% from expected:
  → Compare against historical patterns
  → Check for unauthorized connections (theft)
  → Investigate for leaks if flow is higher than expected
  → Check for closed valves if flow is lower than expected


5. TANK LEVEL MANAGEMENT
-------------------------
- Tanks should maintain levels between 30-90% capacity
- Rapid drawdown indicates high demand or leakage
- Failure to refill indicates supply issues

Action: Tank level management:
  → Below 30%: Increase supply or reduce outflow
  → Above 90%: Risk of overflow - reduce inflow
  → Rapid changes: Investigate cause immediately


6. PRIORITY DURING EMERGENCIES
-------------------------------
Priority order for service restoration:
  1. Hospitals and medical facilities
  2. Schools and educational institutions  
  3. Residential areas
  4. Commercial and industrial areas

Action: During water shortage or major failure:
  → Maintain supply to priority facilities first
  → Communicate outage duration to affected customers
  → Implement rotational supply if needed


7. ENERGY EFFICIENCY
--------------------
- Pumps should operate in most efficient range
- Avoid excessive pressures (wastes energy)
- Nighttime operations should use lower pressures

Action: Optimize energy consumption:
  → Reduce pump speeds during low-demand periods
  → Use gravity flow where possible
  → Maintain pressures just above minimum required


8. WATER QUALITY CONCERNS
--------------------------
- Low pressures (<10m) risk backflow contamination
- Stagnant zones (low velocities) degrade water quality
- After repairs: flushing required before service restoration

Action: Water quality protection:
  → Never allow pressures below 10m
  → Flush mains after repairs (minimum 2 hours)
  → Monitor chlorine residuals in low-flow areas
  → Increase flow velocities in stagnant zones


9. CUSTOMER COMMUNICATION
--------------------------
- Notify customers of planned outages >24 hours in advance
- Provide estimated restoration times for emergencies
- Explain causes of service disruptions when safe to disclose

Action: Communication protocols:
  → High-priority alerts for >4 hour outages
  → SMS/social media for widespread impacts
  → Direct calls to affected facilities for critical outages


10. DATA QUALITY AND CALIBRATION
---------------------------------
- EPANET model must be calibrated against actual readings
- Sensor failures can cause false alarms
- Regular validation prevents alert fatigue

Action: Maintain model accuracy:
  → Weekly calibration of demand patterns
  → Monthly validation of pipe roughness coefficients
  → Flag and investigate sensor anomalies
  → Update model after network modifications

===================================================
Last Updated: January 2026
Authority: Amman Water Utility Operations Manual
"""
    
    with open("operational_policies.txt", 'w') as f:
        f.write(policies)
    
    print("✓ Created operational_policies.txt")
    print(f"  Total policies: 10 sections")
    print(f"  Document length: {len(policies.split())} words")
    
    return "operational_policies.txt"

if __name__ == "__main__":
    create_operational_policies()