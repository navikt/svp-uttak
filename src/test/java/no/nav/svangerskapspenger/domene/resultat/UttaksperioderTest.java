package no.nav.svangerskapspenger.domene.resultat;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

import org.junit.Test;

import no.nav.svangerskapspenger.domene.felles.AktivitetType;
import no.nav.svangerskapspenger.domene.felles.Arbeidsforhold;

public class UttaksperioderTest {

    private static final BigDecimal FULL_UTBETALING = BigDecimal.valueOf(100L);

    @Test
    public void riktig_start_og_slutt_dato_med_en_periode() {
        var arbeidsforhold = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "123", "456");
        var uttaksperioder = new Uttaksperioder();
        uttaksperioder.leggTilPerioder(arbeidsforhold,
            new Uttaksperiode(LocalDate.of(2019, Month.JANUARY, 1), LocalDate.of(2019, Month.JANUARY, 31), FULL_UTBETALING));

        assertThat(uttaksperioder.finnFørsteUttaksdato()).contains(LocalDate.of(2019, Month.JANUARY, 1));
        assertThat(uttaksperioder.finnSisteUttaksdato()).contains(LocalDate.of(2019, Month.JANUARY, 31));
    }

    @Test
    public void riktig_start_og_slutt_dato_med_perioder_på_forskjellige_arbeidsforhold() {
        var arbeidsforhold1 = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "123", "456");
        var arbeidsforhold2 = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "234", "567");
        var uttaksperioder = new Uttaksperioder();
        uttaksperioder.leggTilPerioder(arbeidsforhold1,
            new Uttaksperiode(LocalDate.of(2019, Month.JANUARY, 1), LocalDate.of(2019, Month.JANUARY, 31), FULL_UTBETALING));
        uttaksperioder.leggTilPerioder(arbeidsforhold2,
            new Uttaksperiode(LocalDate.of(2019, Month.JANUARY, 15), LocalDate.of(2019, Month.FEBRUARY, 20), FULL_UTBETALING));

        assertThat(uttaksperioder.finnFørsteUttaksdato()).contains(LocalDate.of(2019, Month.JANUARY, 1));
        assertThat(uttaksperioder.finnSisteUttaksdato()).contains(LocalDate.of(2019, Month.FEBRUARY, 20));
    }

    @Test
    public void ingen_start_og_slutt_dato_dersom_avslag_på_arbeidsforhold() {
        var arbeidsforhold = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "123", "456");
        var uttaksperioder = new Uttaksperioder();
        uttaksperioder.leggTilPerioder(arbeidsforhold,
            new Uttaksperiode(LocalDate.of(2019, Month.JANUARY, 1), LocalDate.of(2019, Month.JANUARY, 31), FULL_UTBETALING));
        uttaksperioder.avslåForArbeidsforhold(arbeidsforhold, ArbeidsforholdIkkeOppfyltÅrsak.UTTAK_KUN_PÅ_HELG);

        assertThat(uttaksperioder.finnFørsteUttaksdato()).isEmpty();
        assertThat(uttaksperioder.finnSisteUttaksdato()).isEmpty();
    }

    @Test
    public void ingen_perioder_gir_ingen_start_og_slutt_dato() {
        var uttaksperioder = new Uttaksperioder();

        assertThat(uttaksperioder.finnFørsteUttaksdato()).isEmpty();
        assertThat(uttaksperioder.finnSisteUttaksdato()).isEmpty();
    }

    @Test
    public void skal_være_mulig_å_legge_til_perioder() {
        var uttaksperioder = new Uttaksperioder();
        var arbeidsforhold = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "123", "456");

        uttaksperioder.leggTilPerioder(arbeidsforhold, new Uttaksperiode(LocalDate.of(2019, Month.JANUARY, 1), LocalDate.of(2019, Month.JANUARY, 31), FULL_UTBETALING));
        uttaksperioder.leggTilPerioder(arbeidsforhold,
            new Uttaksperiode(LocalDate.of(2019, Month.FEBRUARY, 1), LocalDate.of(2019, Month.FEBRUARY, 28), FULL_UTBETALING),
            new Uttaksperiode(LocalDate.of(2019, Month.MARCH, 1), LocalDate.of(2019, Month.MARCH, 31), FULL_UTBETALING));


        assertThat(uttaksperioder.perioder(arbeidsforhold).getUttaksperioder()).hasSize(3);
    }

}
