package no.nav.svangerskapspenger.tjeneste.opprettperioder;


import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.junit.Test;

import no.nav.svangerskapspenger.domene.felles.AktivitetType;
import no.nav.svangerskapspenger.domene.felles.Arbeidsforhold;
import no.nav.svangerskapspenger.domene.søknad.IngenTilretteligging;
import no.nav.svangerskapspenger.domene.søknad.Søknad;

public class UtbetalingsgradUtlederTest {

    private static final Arbeidsforhold ARBEIDSFORHOLD1 = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "123", "456");

    private static final LocalDate TERMINDATO = LocalDate.of(2019, Month.MAY, 1);
    private static final LocalDate BEHOVDATO = LocalDate.of(2019, Month.JANUARY, 1);

    @Test
    public void test_utbetalingsgrad_beregning() {
        utførTest(new BigDecimal("100"), new BigDecimal("50"), new BigDecimal("50.00"));
        utførTest(new BigDecimal("100"), new BigDecimal("10"), new BigDecimal("90.00"));
        utførTest(new BigDecimal("100"), new BigDecimal("0"), new BigDecimal("100.00"));
        utførTest(new BigDecimal("100"), new BigDecimal("100"), new BigDecimal("0.00"));
        utførTest(new BigDecimal("100"), new BigDecimal("110"), new BigDecimal("0.00"));

        utførTest(new BigDecimal("80"), new BigDecimal("20"), new BigDecimal("75.00"));
        utførTest(new BigDecimal("80"), new BigDecimal("10"), new BigDecimal("87.50"));
        utførTest(new BigDecimal("80"), new BigDecimal("0"), new BigDecimal("100.00"));
        utførTest(new BigDecimal("80"), new BigDecimal("100"), new BigDecimal("0.00"));
        utførTest(new BigDecimal("80"), new BigDecimal("110"), new BigDecimal("0.00"));
        utførTest(new BigDecimal("80"), new BigDecimal("40"), new BigDecimal("50.00"));

        utførTest(new BigDecimal("50"), new BigDecimal("30"), new BigDecimal("40.00"));
        utførTest(new BigDecimal("95"), new BigDecimal("5"), new BigDecimal("94.74"));

        utførTest(new BigDecimal("120"), new BigDecimal("30"), new BigDecimal("75.00"));
        utførTest(new BigDecimal("120"), new BigDecimal("12"), new BigDecimal("90.00"));
        utførTest(new BigDecimal("120"), new BigDecimal("0"), new BigDecimal("100.00"));
        utførTest(new BigDecimal("120"), new BigDecimal("108"), new BigDecimal("10.00"));



    }

    private void utførTest(BigDecimal stillingsprosent, BigDecimal tilretteleggingsgrad, BigDecimal fasit) {
        var resultat = UtbetalingsgradUtleder.beregnUtbetalingsgrad(lagSøknad(stillingsprosent), tilretteleggingsgrad);
        assertThat(resultat).isEqualTo(fasit);
    }



    private Søknad lagSøknad(BigDecimal stillingsprosent) {
        var ingenTilrettelegging = new IngenTilretteligging(BEHOVDATO);

        var søknad = new Søknad(
            ARBEIDSFORHOLD1,
            stillingsprosent,
            TERMINDATO,
            BEHOVDATO,
            List.of(ingenTilrettelegging));
        return søknad;
    }

}

