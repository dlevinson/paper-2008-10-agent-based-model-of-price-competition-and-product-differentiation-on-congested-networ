# Agent-Based Model Of Price Competition And Product Differentiation On Congested Networks

## Bibliographic Information

- Row ID: `paper-2008-10`
- Year: 2008
- Authors: Zhang, Levinson, Zhu
- Venue: Journal of Transport Economics and Policy 42(3):435–461 (2008)
- DOI/URL: —
- Citation: Zhang, Levinson, Zhu. (2008). Agent-Based Model Of Price Competition And Product Differentiation On Congested Networks. Journal of Transport Economics and Policy 42(3):435–461 (2008)

## Archive Status

- Pipeline: `READY-TO-UPLOAD/PUBLIC`
- Audit upload action: `upload_candidate`
- Rights status: `likely_clear_with_provenance`
- Controlled access status: `none`
- Human subjects status: `no`
- Asset match status: `exact_match`
- Audit timestamp: 2026-05-17 12:54:07
- Package decision: The ARC/Zhang `VOT.zip` source matches the published price-competition/product-differentiation model and includes the Sioux Falls network and OD inputs used by the Java model.

## Public Archive Or Source Pointers

- No separate public source-code archive was identified in the paper.

## Local Workbench Contents

- `paper/AgentPriceCompetition.pdf`: local article reference for audit validation.
- `code/network_vot13_price_competition_source/`: curated source-only extraction from `/Users/dlev2617/Documents/Code/ARC - Zhang/VOT.zip`.
- `code/network_vot13_price_competition_source/SiouxFallsNet.txt` and `SiouxFallsOD.txt`: benchmark network and OD inputs included in the historical source archive.
- `documentation/PAPER_FIRST_VALIDATION.md`: paper-first validation notes.
- `documentation/SOURCE_BOUNDARY.md`: included/excluded files and runtime caveats.

## Exclusions And Non-Copied Evidence

- Compiled `.class` files, generated Javadoc, `.DS_Store`, and the generated `Out-NetworkGrowth.txt` output are excluded.
- A duplicate `/Users/dlev2617/Documents/Software/ARC - Zhang/VOT.zip` exists and is byte-identical to the Code copy; it is not duplicated here.

## Remaining Work

- No blocking public-upload work remains for the historical source/input package.
- Runtime use may require editing hard-coded Windows paths in `NetworkVOT13.java`; this is documented as legacy setup context, not a reason to withhold the source package.

Generated: 2026-05-17 12:54:07 AEST

<!-- package-hardening-status:start -->
## Package Hardening Status

Generated: 2026-05-20 14:46:37 AEST

- Pipeline: `READY-TO-UPLOAD/PUBLIC`
- Sidecars added/updated: `PACKAGE_STATUS.md`, `PACKAGE_MANIFEST.csv`, `LICENSE_STATUS.md`.
- Paper reference copies are for local audit convenience and are not public-upload assets without rights review.
- Final GitHub upload should use the manifest include statuses and the license-status note.
<!-- package-hardening-status:end -->
