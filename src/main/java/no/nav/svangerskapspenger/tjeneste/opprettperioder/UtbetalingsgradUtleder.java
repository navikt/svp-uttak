package no.nav.svangerskapspenger.tjeneste.opprettperioder;

import java.math.BigDecimal;

import no.nav.svangerskapspenger.domene.søknad.Søknad;

final class UtbetalingsgradUtleder {

    private static final BigDecimal NULL_PROSENT = BigDecimal.ZERO;
    private static final BigDecimal HUNDRE_PROSENT = BigDecimal.valueOf(100L);

    private UtbetalingsgradUtleder() {
        //static class
    }

    static BigDecimal beregnUtbetalingsgrad(Søknad søknad, BigDecimal tilretteleggingsprosent) {
        BigDecimal stillingsprosent = søknad.getStillingsprosentForArbeidsforhold();
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

}
