package no.nav.svangerskapspenger.tjeneste.opprettperioder;

import java.math.BigDecimal;
import java.math.RoundingMode;

import no.nav.svangerskapspenger.domene.søknad.Søknad;

final class UtbetalingsgradUtleder {

    private static final BigDecimal NULL_PROSENT = new BigDecimal("0.00");
    private static final BigDecimal HUNDRE_PROSENT = new BigDecimal("100");

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
        var arbeidsprosent = stillingsprosent.subtract(tilretteleggingsprosent);
        var utbetalingsgrad = arbeidsprosent.multiply(HUNDRE_PROSENT).divide(stillingsprosent, 2, RoundingMode.HALF_UP);
        if (utbetalingsgrad.compareTo(BigDecimal.valueOf(100L)) > 0) {
            return HUNDRE_PROSENT;
        }
        return utbetalingsgrad;
    }

}
