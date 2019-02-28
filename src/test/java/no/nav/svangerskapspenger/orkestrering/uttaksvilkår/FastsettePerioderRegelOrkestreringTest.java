package no.nav.svangerskapspenger.orkestrering.uttaksvilkår;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import org.junit.Test;

import no.nav.svangerskapspenger.domene.felles.Arbeidsforhold;
import no.nav.svangerskapspenger.domene.resultat.PeriodeAvslåttÅrsak;
import no.nav.svangerskapspenger.domene.resultat.PeriodeInnvilgetÅrsak;
import no.nav.svangerskapspenger.domene.resultat.UtfallType;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperiode;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperioder;
import no.nav.svangerskapspenger.domene.søknad.AvklarteDatoer;

public class FastsettePerioderRegelOrkestreringTest {

    private static final Arbeidsforhold ARBEIDSFORHOLD1 = new Arbeidsforhold("123", "456");
    private static final BigDecimal FULL_YTELSESGRAD = BigDecimal.valueOf(100L);

    private static final LocalDate JORDMORS_DATO = LocalDate.of(2019, Month.JANUARY, 1);
    private static final LocalDate TERMINDATO = LocalDate.of(2019, Month.MAY,1);

    private final FastsettePerioderRegelOrkestrering fastsettePerioderRegelOrkestrering = new FastsettePerioderRegelOrkestrering();

    @Test
    public void lovlig_uttak_skal_bli_innvilget() {
        var avklarteDatoer = new AvklarteDatoer(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                JORDMORS_DATO,
                TERMINDATO,
                Optional.empty()
        );
        var uttaksperioder = new Uttaksperioder();
        uttaksperioder.leggTilPerioder(ARBEIDSFORHOLD1,
                new Uttaksperiode(JORDMORS_DATO, TERMINDATO.minusWeeks(3).minusDays(1), FULL_YTELSESGRAD));

        fastsettePerioderRegelOrkestrering.fastsettePerioder(avklarteDatoer, uttaksperioder);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next());
        assertThat(perioder).hasSize(1);
        var periode0 = perioder.get(0);
        assertThat(periode0.getFom()).isEqualTo(JORDMORS_DATO);
        assertThat(periode0.getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(periode0.getYtelsesgrad()).isEqualTo(FULL_YTELSESGRAD);
        assertThat(periode0.getUtfallType()).isEqualTo(UtfallType.INNVILGET);
        assertThat(periode0.getÅrsak()).isEqualTo(PeriodeInnvilgetÅrsak.UTTAK_ER_INNVILGET);
    }


    @Test
    public void uttak_avslås_ved_brukers_død() {
        var brukersdødsdato = LocalDate.of(2019, Month.MARCH, 1);
        var avklarteDatoer = new AvklarteDatoer(
                Optional.of(brukersdødsdato),
                Optional.empty(),
                Optional.empty(),
                JORDMORS_DATO,
                TERMINDATO,
                Optional.empty()
        );
        var uttaksperioder = new Uttaksperioder();
        uttaksperioder.leggTilPerioder(ARBEIDSFORHOLD1,
                new Uttaksperiode(JORDMORS_DATO, TERMINDATO.minusWeeks(3).minusDays(1), FULL_YTELSESGRAD));

        fastsettePerioderRegelOrkestrering.fastsettePerioder(avklarteDatoer, uttaksperioder);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next());
        assertThat(perioder).hasSize(2);

        var periode0 = perioder.get(0);
        assertThat(periode0.getFom()).isEqualTo(JORDMORS_DATO);
        assertThat(periode0.getTom()).isEqualTo(brukersdødsdato.minusDays(1));
        assertThat(periode0.getYtelsesgrad()).isEqualTo(FULL_YTELSESGRAD);
        assertThat(periode0.getUtfallType()).isEqualTo(UtfallType.INNVILGET);
        assertThat(periode0.getÅrsak()).isEqualTo(PeriodeInnvilgetÅrsak.UTTAK_ER_INNVILGET);

        var periode1 = perioder.get(1);
        assertThat(periode1.getFom()).isEqualTo(brukersdødsdato);
        assertThat(periode1.getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(periode1.getYtelsesgrad()).isEqualTo(FULL_YTELSESGRAD);
        assertThat(periode1.getUtfallType()).isEqualTo(UtfallType.AVSLÅTT);
        assertThat(periode1.getÅrsak()).isEqualTo(PeriodeAvslåttÅrsak.BRUKER_ER_DØD);
    }


    @Test
    public void uttak_avslås_ved_barnets_død() {
        var barnetsDødsdato = LocalDate.of(2019, Month.APRIL, 1);
        var avklarteDatoer = new AvklarteDatoer(
                Optional.empty(),
                Optional.of(barnetsDødsdato),
                Optional.empty(),
                JORDMORS_DATO,
                TERMINDATO,
                Optional.empty()
        );
        var uttaksperioder = new Uttaksperioder();
        uttaksperioder.leggTilPerioder(ARBEIDSFORHOLD1,
                new Uttaksperiode(JORDMORS_DATO, TERMINDATO.minusWeeks(3).minusDays(1), FULL_YTELSESGRAD));

        fastsettePerioderRegelOrkestrering.fastsettePerioder(avklarteDatoer, uttaksperioder);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next());
        assertThat(perioder).hasSize(2);

        var periode0 = perioder.get(0);
        assertThat(periode0.getFom()).isEqualTo(JORDMORS_DATO);
        assertThat(periode0.getTom()).isEqualTo(barnetsDødsdato.minusDays(1));
        assertThat(periode0.getYtelsesgrad()).isEqualTo(FULL_YTELSESGRAD);
        assertThat(periode0.getUtfallType()).isEqualTo(UtfallType.INNVILGET);
        assertThat(periode0.getÅrsak()).isEqualTo(PeriodeInnvilgetÅrsak.UTTAK_ER_INNVILGET);

        var periode1 = perioder.get(1);
        assertThat(periode1.getFom()).isEqualTo(barnetsDødsdato);
        assertThat(periode1.getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(periode1.getYtelsesgrad()).isEqualTo(FULL_YTELSESGRAD);
        assertThat(periode1.getUtfallType()).isEqualTo(UtfallType.AVSLÅTT);
        assertThat(periode1.getÅrsak()).isEqualTo(PeriodeAvslåttÅrsak.BARN_ER_DØDT);
    }


    @Test
    public void uttak_etter_opphør_av_medlemskap_avslås() {
        var opphørAvMedlemskap = LocalDate.of(2019, Month.APRIL, 1);
        var avklarteDatoer = new AvklarteDatoer(
                Optional.empty(),
                Optional.empty(),
                Optional.of(opphørAvMedlemskap),
                JORDMORS_DATO,
                TERMINDATO,
                Optional.empty()
        );
        var uttaksperioder = new Uttaksperioder();
        uttaksperioder.leggTilPerioder(ARBEIDSFORHOLD1,
                new Uttaksperiode(JORDMORS_DATO, TERMINDATO.minusWeeks(3).minusDays(1), FULL_YTELSESGRAD));

        fastsettePerioderRegelOrkestrering.fastsettePerioder(avklarteDatoer, uttaksperioder);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next());
        assertThat(perioder).hasSize(2);

        var periode0 = perioder.get(0);
        assertThat(periode0.getFom()).isEqualTo(JORDMORS_DATO);
        assertThat(periode0.getTom()).isEqualTo(opphørAvMedlemskap.minusDays(1));
        assertThat(periode0.getYtelsesgrad()).isEqualTo(FULL_YTELSESGRAD);
        assertThat(periode0.getUtfallType()).isEqualTo(UtfallType.INNVILGET);
        assertThat(periode0.getÅrsak()).isEqualTo(PeriodeInnvilgetÅrsak.UTTAK_ER_INNVILGET);

        var periode1 = perioder.get(1);
        assertThat(periode1.getFom()).isEqualTo(opphørAvMedlemskap);
        assertThat(periode1.getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(periode1.getYtelsesgrad()).isEqualTo(FULL_YTELSESGRAD);
        assertThat(periode1.getUtfallType()).isEqualTo(UtfallType.AVSLÅTT);
        assertThat(periode1.getÅrsak()).isEqualTo(PeriodeAvslåttÅrsak.BRUKER_ER_IKKE_MEDLEM);

    }

}