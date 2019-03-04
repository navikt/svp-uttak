package no.nav.svangerskapspenger.tjeneste.fastsettuttak;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import no.nav.svangerskapspenger.domene.resultat.*;
import org.junit.Test;

import no.nav.svangerskapspenger.domene.felles.Arbeidsforhold;
import no.nav.svangerskapspenger.domene.søknad.AvklarteDatoer;

public class FastsettPerioderTjenesteTest {

    private static final Arbeidsforhold ARBEIDSFORHOLD1 = new Arbeidsforhold("123", "456");
    private static final BigDecimal FULL_YTELSESGRAD = BigDecimal.valueOf(100L);

    private static final LocalDate JORDMORS_DATO = LocalDate.of(2019, Month.JANUARY, 1);
    private static final LocalDate TERMINDATO = LocalDate.of(2019, Month.MAY,1);

    private final FastsettPerioderTjeneste fastsettPerioderTjeneste = new FastsettPerioderTjeneste();

    @Test
    public void lovlig_uttak_skal_bli_innvilget() {
        var avklarteDatoer = new AvklarteDatoer(
                JORDMORS_DATO,
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

        fastsettPerioderTjeneste.fastsettePerioder(avklarteDatoer, uttaksperioder);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
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
                JORDMORS_DATO,
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

        fastsettPerioderTjeneste.fastsettePerioder(avklarteDatoer, uttaksperioder);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
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
                JORDMORS_DATO,
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

        fastsettPerioderTjeneste.fastsettePerioder(avklarteDatoer, uttaksperioder);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
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
                JORDMORS_DATO,
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

        fastsettPerioderTjeneste.fastsettePerioder(avklarteDatoer, uttaksperioder);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
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

    @Test
    public void uttak_med_delvis_tilrettelegging_etter_en_måned_og_opphør_av_medlemskap_gir_tre_perioder() {
        var opphørAvMedlemskap = LocalDate.of(2019, Month.APRIL, 1);
        var avklarteDatoer = new AvklarteDatoer(
                JORDMORS_DATO,
                Optional.empty(),
                Optional.empty(),
                Optional.of(opphørAvMedlemskap),
                JORDMORS_DATO,
                TERMINDATO,
                Optional.empty()
        );
        var uttaksperioder = new Uttaksperioder();
        var startTilpassing = LocalDate.of(2019, Month.FEBRUARY, 1);
        uttaksperioder.leggTilPerioder(ARBEIDSFORHOLD1,
                new Uttaksperiode(JORDMORS_DATO, startTilpassing.minusDays(1), FULL_YTELSESGRAD),
                new Uttaksperiode(startTilpassing, TERMINDATO.minusWeeks(3).minusDays(1), BigDecimal.valueOf(40L)));

        fastsettPerioderTjeneste.fastsettePerioder(avklarteDatoer, uttaksperioder);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
        assertThat(perioder).hasSize(3);

        var periode0 = perioder.get(0);
        assertThat(periode0.getFom()).isEqualTo(JORDMORS_DATO);
        assertThat(periode0.getTom()).isEqualTo(startTilpassing.minusDays(1));
        assertThat(periode0.getYtelsesgrad()).isEqualTo(FULL_YTELSESGRAD);
        assertThat(periode0.getUtfallType()).isEqualTo(UtfallType.INNVILGET);
        assertThat(periode0.getÅrsak()).isEqualTo(PeriodeInnvilgetÅrsak.UTTAK_ER_INNVILGET);

        var periode1 = perioder.get(1);
        assertThat(periode1.getFom()).isEqualTo(startTilpassing);
        assertThat(periode1.getTom()).isEqualTo(opphørAvMedlemskap.minusDays(1));
        assertThat(periode1.getYtelsesgrad()).isEqualTo(BigDecimal.valueOf(40L));
        assertThat(periode1.getUtfallType()).isEqualTo(UtfallType.INNVILGET);
        assertThat(periode1.getÅrsak()).isEqualTo(PeriodeInnvilgetÅrsak.UTTAK_ER_INNVILGET);

        var periode2 = perioder.get(2);
        assertThat(periode2.getFom()).isEqualTo(opphørAvMedlemskap);
        assertThat(periode2.getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(periode2.getYtelsesgrad()).isEqualTo(BigDecimal.valueOf(40L));
        assertThat(periode2.getUtfallType()).isEqualTo(UtfallType.AVSLÅTT);
        assertThat(periode2.getÅrsak()).isEqualTo(PeriodeAvslåttÅrsak.BRUKER_ER_IKKE_MEDLEM);
    }

    @Test
    public void dersom_leges_dato_er_etter_tre_uker_før_termimdato_så_skal_hele_arbeidsforholdet_avslås() {
        var opphørAvMedlemskap = LocalDate.of(2019, Month.APRIL, 1);
        var avklarteDatoer = new AvklarteDatoer(
            TERMINDATO.minusWeeks(2),
            Optional.empty(),
            Optional.empty(),
            Optional.of(opphørAvMedlemskap),
            JORDMORS_DATO,
            TERMINDATO,
            Optional.empty()
        );
        var uttaksperioder = new Uttaksperioder();
        uttaksperioder.leggTilPerioder(ARBEIDSFORHOLD1,
            new Uttaksperiode(TERMINDATO.minusWeeks(2), TERMINDATO.minusDays(1), FULL_YTELSESGRAD));

        fastsettPerioderTjeneste.fastsettePerioder(avklarteDatoer, uttaksperioder);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next());
        assertThat(perioder.getUttaksperioder()).hasSize(0);
        assertThat(perioder.getArbeidsforholdÅrsak()).isEqualTo(ArbeidsforholdAvslåttÅrsak.LEGES_DATO_IKKE_FØR_TRE_UKER_FØR_TERMINDATO);
    }

}
