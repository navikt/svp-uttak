package no.nav.svangerskapspenger.domene.resultat;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

import org.junit.Test;

import no.nav.svangerskapspenger.domene.felles.Arbeidsforhold;

public class UttaksperioderTest {

    @Test
    public void riktig_start_og_slutt_dato_med_en_periode() {
        var arbeidsforhold = new Arbeidsforhold("123","456");
        var uttaksperioder = new Uttaksperioder();
        uttaksperioder.leggTilPerioder(arbeidsforhold,
            new Uttaksperiode(LocalDate.of(2019, Month.JANUARY, 1), LocalDate.of(2019, Month.JANUARY, 31), BigDecimal.valueOf(100L)));

        assertThat(uttaksperioder.finnFørsteUttaksdato().get()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 1));
        assertThat(uttaksperioder.finnSisteUttaksdato().get()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 31));
    }

    @Test
    public void riktig_start_og_slutt_dato_med_perioder_på_forskjellige_arbeidsforhold() {
        var arbeidsforhold1 = new Arbeidsforhold("123","456");
        var arbeidsforhold2 = new Arbeidsforhold("234","567");
        var uttaksperioder = new Uttaksperioder();
        uttaksperioder.leggTilPerioder(arbeidsforhold1,
            new Uttaksperiode(LocalDate.of(2019, Month.JANUARY, 1), LocalDate.of(2019, Month.JANUARY, 31), BigDecimal.valueOf(100L)));
        uttaksperioder.leggTilPerioder(arbeidsforhold2,
            new Uttaksperiode(LocalDate.of(2019, Month.JANUARY, 15), LocalDate.of(2019, Month.FEBRUARY, 20), BigDecimal.valueOf(100L)));

        assertThat(uttaksperioder.finnFørsteUttaksdato().get()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 1));
        assertThat(uttaksperioder.finnSisteUttaksdato().get()).isEqualTo(LocalDate.of(2019, Month.FEBRUARY, 20));
    }

    @Test
    public void ingen_start_og_slutt_dato_dersom_avslag_på_arbeidsforhold() {
        var arbeidsforhold = new Arbeidsforhold("123","456");
        var uttaksperioder = new Uttaksperioder();
        uttaksperioder.leggTilPerioder(arbeidsforhold,
            new Uttaksperiode(LocalDate.of(2019, Month.JANUARY, 1), LocalDate.of(2019, Month.JANUARY, 31), BigDecimal.valueOf(100L)));
        uttaksperioder.avslåForArbeidsforhold(arbeidsforhold, ArbeidsforholdAvslåttÅrsak.UTTAK_KUN_PÅ_HELG);

        assertThat(uttaksperioder.finnFørsteUttaksdato().isPresent()).isFalse();
        assertThat(uttaksperioder.finnSisteUttaksdato().isPresent()).isFalse();
    }

    @Test
    public void ingen_perioder_gir_ingen_start_og_slutt_dato() {
        var uttaksperioder = new Uttaksperioder();

        assertThat(uttaksperioder.finnFørsteUttaksdato().isPresent()).isFalse();
        assertThat(uttaksperioder.finnSisteUttaksdato().isPresent()).isFalse();
    }

}
