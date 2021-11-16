**10.2 GLOBAL OCEAN OBSERVING SYSTEM (GOOS)**

**10.2.1 Introduction**

This chapter provides guidance on WIGOS Station Identifiers (WSIs) to be used by operators of all GOOS Observation Coordination Group (OCG) Observing Networks and allocated by OceanOPS (formerly known as JCOMMOPS) including:

- Ship observations (VOS, SOOP, ASAP) under the Ship Observations Team (SOT), and other ship-based systems (fishing vessels, ferry boxes, etc).
- Data Buoy observations (global drifter array, tropical moored buoy array, national/coastal moored buoy networks, high latitude (Arctic/Antarctic) buoys, tsunami buoys, unmanned surface vehicles misc. fixed platforms) under the Data Buoy Cooperation Panel (DBCP),
- Global tide gauge network under the Global Sea Level Observing System (GLOSS),
- Global profiling float arrayunder the Argo,
- Fixed-point time series reference sitesunder theOceanSITES,
- Repeat hydrography programme under theGO-SHIP,
- Underwater gliders under the OceanGliders,
- Animal Borne Instruments under the AniBOS,
- Global High Frequency Radar Network under the HFRNet,
- a number of other ocean observing networks emerging at a regional level, or at the boundary of existing OCG observing networks;

The WSI scheme for the observing platforms of GOOS OCG Networks presented here builds on the legacy of existing WMO-identifiers (WMO ID) traditionally used to distribute observing platform/station data on the GTS. The WMO ID allocation scheme [add reference for WMO ID rules?] has not always ensured the uniqueness of identifiers, and was based on a human and manual allocation system using a set of complex rules. Some Members are also using sporadically some random identifiers that are not meeting any defined rule.

The new proposed scheme will harmonize the allocation of station identifiers, ensure their uniqueness, and develop a robust and operational workflow through a machine-based allocation system.

As WMO decentralized office (in France), OceanOPS has the delegated authority to issue WSIs for ocean observing platforms operated by Members through its operational Information System. However, if any Member decides to issue the WSIs for some of its ocean observing platforms it must use a different &quot;Issuer of Identifier Number&quot; (see further below - 22000 is reserved for OceanOPS). It is recommended as well for the Member to notify OceanOPS in such case, so that the overall data and metadata flow is properly monitored by WMO.

**10.2.2**  **WIGOS STATION IDENTIFIERS**

The structure of a WIGOS Station Identifier is described below:

| WIGOS Identifier Series
 (number) | Issuer of Identifier
 (number) | Issue Number
 (number) | Local Identifier
 (characters) |
| --- | --- | --- | --- |

**10.2.2.1 WIGOS Identifier Series**

The WIGOS Identifier Series for OceanOPS platforms is a constant:0.

**10.2.2.2 Issuer of Identifier**

Issuer of identifier allocated for GOOS observing platforms administered through OceanOPS, is a constant: 22000 (see Chapter 2, Table 2.4 Issuer of identifier values in the range 22000–22999).

**10.2.2.3 Issue Number**

This Issue number allows values between 0 to 65534.
This number will not have any meaning and will be allocated by the machine to secure uniqueness amongst exceptions, observing system workflows and practices, and also to resolve historical reuse of same identifiers.
In practice for OceanOPS platforms Issue number will be used to monitor the different redeployments of the same platform (e.g.: a new mission for a glider), or a new installation of an existing platform/station on a new ship or at a fixed site (e.g. moored buoys, OceanSITES).

**10.2.2.4 Local Identifier**
The Local Identifier is based generally on the historical WMO-identifier or other identifiers defined by Observing Networks.
The general form of a WMO identifier is based on 7 digits:

WMO-ID = A1Bwnnnnn.

where A1= WMO Regional Association area and Bw is the sub-area.

Bw is designated against the sub-area (1-7) for moored and drifting buoys, where fixed values 8 or 9 are assigned for other platform types.

Legacy 5-digit WMO-IDs (A1BWnnn) were converted into 7 digits by adding 00 after the A1Bw (e.g. A1Bw00nnn).

However, there are instances when these rules of identifier allocation according to geographical criteria have not been followed (e.g. some floats or drifter deployments). In practice it is often impossible to set a WMO-ID in advance of the deployments as a batch of platforms can be deployed in different areas.

While this geographical distinction makes sense for fixed or anchored instrumentation it is less appropriate for mobile platforms moving autonomously within different areas, continuously or not.

It is highly recommended that:

1. Data users do not base their data extraction or assimilation schemes on the A1 orA1Bw and
2. A1Bw is to be defined only for fixed platforms.

As the WMO-ID (and WSI) allocation system is machine based, OceanOPS will use the platform deployment location and the WMO area polygons4 to defineA1 and Bw as appropriate.

Table 10.2.1 below illustrates the new rules that simplify the current WMO-IDlocal identifier allocation based on platform types and geographical areas for a robust implementation.

While in the best practices of WSI management the WSI content should have no meaning and be simply unique, Table 10.2.1 proposal preserved some legacy on the readability of local identifiers by platform types.

**Table 10.2.1: Rules for new local identifiers allocation**

| **Platform Type** | **old local identifier** | **new local identifier** |
| --- | --- | --- |
| Profiling floats, micro floats, Ice tethered profilers, polar ocean profiling systems, deep floats, etc. | A19nnnnnA1= [1-7] | A19nnnnn A1= [1-7] random |
| Marine Animals | 99nnnnn | 99nnnnn |
| Subsurface autonomous platforms, gliders | A18xxnnn If A1= 4 nnn = 900-999 Else Nnn = 500-999 | 89nnnnn |
| any ship based instruments | ship call sign | Following up on SOT relevant decision, a **7 digits reference** (also called SOT-ID) is created from scratch and randomly by the machine with characters 2, 3 and 7 being letters, the others being letters or numbers. This ensures that this new identifier will not overlap with any radio call sign used in the past, or any other WMO-IDs. |
| Surface drifters, ice drifters | A1Bwxxnnn nnn=500-899 xx=00-99 A1= [1-7], Bw =[1-7] | A18nnnnn A1= [1-7] random |
| Other autonomous surface instruments (saildrones, wave gliders, etc.) | A18xxnnn If A1= 4 nnn = 900-999 Else Nnn = 500-999 | A10nnnnn A1= [1-7] random |
| Fixed systems, moored buoys, mooring SITES, HF Radars, tide gauges, etc. | moored buoys: A1Bwxxnnn, nnn=000-499 xx=00-99 A1= [1-7], Bw =[1-7] Fixed platforms: A1Bwxxnnn, nnn=000-499 xx=00-99 A1= [1-7], Bw =[1-7] | A1Bnnnnn, nnnnn random, A1= [1-7], Bw = [1-7] matching WMO areas/subareas |
| Reserve of free blocks for future platform types 81nnnnn, 82nnnnn, 83nnnnn, 84nnnnn, 85nnnnn, 86nnnnn, 87nnnnn | | |

The WIGOS identifiers allocated by OceanOPS will have then the following form:

| WIGOS Identifier Series (number) | Issuer of Identifier (number) | Issue Number (number) | Local Identifier (characters) |
| --- | --- | --- | --- |
| 0 | 22000 | 0 to 65534 | 7 digits string |

**10.2.3 WSI, WIGOS metadata and GTS data**

OceanOPS allocates WSI but also gather, quality control and harmonize related platform metadata. These metadata are served through various web services and API, including a routine submission to the WIGOS /OSCAR system.

OceanOPS captures the different local identifiers effectively used for GTS distribution, even if non unique, incorrect, or changed along a platform lifetime.

This is stored in the WIGOS metadata record with a defined time window (see 4.3.3.2 ProgramAffiliation \&gt; ProgramSpecificFacilityId) so that the link is preserved between metadata records and data available to users.

The uniqueness of the local identifier (and WSI) is checked at the applicative level (web and API) and through a database constraint.
 The allocation of a WSI by OceanOPS triggers the operational submission of metadata to WIGOS/OSCAR.

[1](#sdfootnote1anc) _OceanOPS uses commonly &quot;observing platform&quot; and other synonyms are used by different communities such as &quot;station&quot; or &quot;observing facility&quot;._

[2](#sdfootnote2anc) _Decision 25 (JCOMM-5) on_ [https://library.wmo.int/doc\_num.php?explnum\_id=4528](https://library.wmo.int/doc_num.php?explnum_id=4528)

[3](#sdfootnote3anc) _See table on_ [_https://community.wmo.int/rules-allocating-wmo-numbers_](https://community.wmo.int/rules-allocating-wmo-numbers)

[4](#sdfootnote4anc) _See map on_ [_https://community.wmo.int/rules-allocating-wmo-numbers_](https://community.wmo.int/rules-allocating-wmo-numbers)

[5](#sdfootnote5anc) _Decision 32 (JCOMM-5) on_ [_https://library.wmo.int/doc\_num.php?explnum\_id=4528_](https://library.wmo.int/doc_num.php?explnum_id=4528)