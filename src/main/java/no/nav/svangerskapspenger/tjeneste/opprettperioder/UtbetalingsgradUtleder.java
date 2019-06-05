package no.nav.svangerskapspenger.tjeneste.opprettperioder;

import java.math.BigDecimal;
import java.time.LocalDate;

import no.nav.svangerskapspenger.domene.felles.Arbeidsforhold;
import no.nav.svangerskapspenger.domene.felles.LukketPeriode;
import no.nav.svangerskapspenger.domene.felles.arbeid.AktivitetIdentifikator;
import no.nav.svangerskapspenger.domene.felles.arbeid.Arbeidsprosenter;
import no.nav.svangerskapspenger.domene.søknad.Søknad;

final class UtbetalingsgradUtleder {

    private static final BigDecimal NULL_PROSENT = BigDecimal.ZERO;
    private static final BigDecimal HUNDRE_PROSENT = BigDecimal.valueOf(100L);

    private UtbetalingsgradUtleder() {
        //static class
    }

    static BigDecimal beregnUtbetalingsgrad(Arbeidsprosenter arbeidsprosenter, Søknad søknad, LocalDate fom, LocalDate tom, BigDecimal tilretteleggingsprosent) {
        BigDecimal stillingsprosent = arbeidsprosenter.getStillingsprosent(tilAktivitetIdentifikator(søknad.getArbeidsforhold()), new LukketPeriode(fom, tom));
        if (stillingsprosent == null || stillingsprosent.equals(NULL_PROSENT)) {
            stillingsprosent = HUNDRE_PROSENT;
        }
        if (tilretteleggingsprosent.compareTo(stillingsprosent) > 0) {
            return NULL_PROSENT;
        }
        var utbetalingsgrad = HUNDRE_PROSENT.subtract(tilretteleggingsprosent.divide(stillingsprosent).multiply(HUNDRE_PROSENT));
        if (utbetalingsgrad.compareTo(BigDecimal.valueOf(100L)) > 0) {
            return HUNDRE_PROSENT;
        }
        return utbetalingsgrad;
    }

    private static AktivitetIdentifikator tilAktivitetIdentifikator(Arbeidsforhold arbeidsforhold) {
        if (arbeidsforhold.getArbeidsgiverVirksomhetId() != null) {
            return new AktivitetIdentifikator(arbeidsforhold.getArbeidsgiverVirksomhetId(), arbeidsforhold.getArbeidsforholdId().orElse(null), AktivitetIdentifikator.ArbeidsgiverType.VIRKSOMHET);
        }
        return new AktivitetIdentifikator(arbeidsforhold.getArbeidsgiverVirksomhetId(), arbeidsforhold.getArbeidsforholdId().orElse(null), AktivitetIdentifikator.ArbeidsgiverType.PERSON);
    }

}
