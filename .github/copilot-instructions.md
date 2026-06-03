# svp-uttak

Business-rule library for svangerskapspenger uttak calculation.

## Shared context

- Source of truth for shared domain, architecture, and conventions: `navikt/fp-context`
- Copilot Space: `navikt/TeamForeldrepenger`

## Repo-specific context

| Topic      | Details                                                        |
|------------|----------------------------------------------------------------|
| Role       | Calculates svangerskapspenger benefit periods and uttaksvilkar |
| Consumers  | `fp-sak` module `uttak` svangerskapspenger flow                |
| Tech stack | Java SemVer library using `fp-nare` rule framework             |
| API        | `FastsettPerioderTjeneste` with input / output objects         |

The resulting structure is used downstream in `fp-beregning-ytelse`, letters and statistics.

## Verification

- Verify behavior changes through `fp-sak` and relevant `navikt/fp-autotest` suites `fpsak` or `verdikjede` limited to svangerskapspenger.

