package no.nav.svangerskapspenger.tjeneste.opprettperioder;


import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.junit.Test;

import no.nav.svangerskapspenger.domene.felles.Arbeidsforhold;
import no.nav.svangerskapspenger.domene.felles.LukketPeriode;
import no.nav.svangerskapspenger.domene.felles.arbeid.AktivitetIdentifikator;
import no.nav.svangerskapspenger.domene.felles.arbeid.Arbeid;
import no.nav.svangerskapspenger.domene.felles.arbeid.ArbeidTidslinje;
import no.nav.svangerskapspenger.domene.felles.arbeid.Arbeidsprosenter;
import no.nav.svangerskapspenger.domene.søknad.IngenTilretteligging;
import no.nav.svangerskapspenger.domene.søknad.Søknad;

public class UtbetalingsgradUtlederTest {

    private static final Arbeidsforhold ARBEIDSFORHOLD1 = Arbeidsforhold.virksomhet("123", "456");

    private static final LocalDate TERMINDATO = LocalDate.of(2019, Month.MAY, 1);
    private static final LocalDate BEHOVDATO = LocalDate.of(2019, Month.JANUARY, 1);

    @Test
    public void test_utbetalingsgrad_beregning() {
        utførTest(100, 50, new BigDecimal("50"));
        utførTest(100, 10, new BigDecimal("90"));
        utførTest(100, 0, new BigDecimal("100"));
        utførTest(100, 100, new BigDecimal("0"));
        utførTest(100, 110, new BigDecimal("0"));

        utførTest(80, 20, new BigDecimal("75"));
        utførTest(80, 10, new BigDecimal("87.5"));
        utførTest(80, 0, new BigDecimal("100"));
        utførTest(80, 100, new BigDecimal("0"));
        utførTest(80, 110, new BigDecimal("0"));

        utførTest(120, 30, new BigDecimal("75"));
        utførTest(120, 12, new BigDecimal("90"));
        utførTest(120, 0, new BigDecimal("100"));
        utførTest(120, 108, new BigDecimal("10"));

    }

    private void utførTest(long stillingsprosent, long tilretteleggingsgrad, BigDecimal fasit) {
        var resultat = UtbetalingsgradUtleder.beregnUtbetalingsgrad(lagArbeideprosenter(stillingsprosent), lagSøknad(), BEHOVDATO, TERMINDATO.minusWeeks(3).minusDays(1), BigDecimal.valueOf(tilretteleggingsgrad));
        assertThat(resultat.stripTrailingZeros()).isEqualTo(fasit.stripTrailingZeros());
    }


    private Arbeidsprosenter lagArbeideprosenter(long stillingsprosent) {
        var arbeidsprosenter = new Arbeidsprosenter();
        var aktivitetsindikator = new AktivitetIdentifikator(ARBEIDSFORHOLD1.getArbeidsgiverVirksomhetId(), ARBEIDSFORHOLD1.getArbeidsforholdId().get(), AktivitetIdentifikator.ArbeidsgiverType.VIRKSOMHET);
        var tidslinje = new ArbeidTidslinje.Builder()
            .medArbeid(new LukketPeriode(LocalDate.of(2017 , Month.JANUARY, 1), LocalDate.of(2020,Month.JANUARY, 1)), Arbeid.forOrdinærtArbeid(BigDecimal.valueOf(stillingsprosent)))
            .build();
        arbeidsprosenter.leggTil(aktivitetsindikator, tidslinje);
        return arbeidsprosenter;
    }

    private Søknad lagSøknad() {
        var ingenTilrettelegging = new IngenTilretteligging(BEHOVDATO);

        var søknad = new Søknad(
            ARBEIDSFORHOLD1,
            TERMINDATO,
            BEHOVDATO,
            List.of(ingenTilrettelegging));
        return søknad;
    }

}
