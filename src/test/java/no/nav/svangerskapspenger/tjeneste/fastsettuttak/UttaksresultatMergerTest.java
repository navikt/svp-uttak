package no.nav.svangerskapspenger.tjeneste.fastsettuttak;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import org.junit.Test;

import no.nav.svangerskapspenger.domene.felles.AktivitetType;
import no.nav.svangerskapspenger.domene.felles.Arbeidsforhold;
import no.nav.svangerskapspenger.domene.resultat.PeriodeOppfyltÅrsak;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperiode;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperioder;

public class UttaksresultatMergerTest {

    private static final BigDecimal FULL_UTBETALING = BigDecimal.valueOf(100L);
    private static final BigDecimal HALV_UTBETALING = BigDecimal.valueOf(50L);

    private UttaksresultatMerger uttaksresultatMerger = new UttaksresultatMerger();

    @Test
    public void dersom_det_ikke_finnes_opprinnelig_uttaksplan_så_skal_ny_returneres() {
        var fom = LocalDate.of(2019, Month.JANUARY, 1);
        var tom = LocalDate.of(2019, Month.JANUARY, 31);

        var nyePerioder = new Uttaksperioder();
        var arbeidsforhold = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "123", null);
        var periode = new Uttaksperiode(fom, tom, FULL_UTBETALING);
        nyePerioder.leggTilPerioder(arbeidsforhold, periode);

        var resultat = uttaksresultatMerger.merge(Optional.empty(), nyePerioder);

        assertThat(resultat).isNotNull();
        assertThat(resultat.alleArbeidsforhold()).hasSize(1);
        var resultatPerioder = resultat.perioder(resultat.alleArbeidsforhold().iterator().next());
        assertThat(resultatPerioder.getUttaksperioder()).hasSize(1);
        var resultatPeriode = resultatPerioder.getUttaksperioder().get(0);
        assertThat(resultatPeriode.getFom()).isEqualTo(fom);
        assertThat(resultatPeriode.getTom()).isEqualTo(tom);
        assertThat(resultatPeriode.getUtbetalingsgrad()).isEqualTo(FULL_UTBETALING);
    }

    @Test
    public void dersom_det_finnes_en_opprinnelig_uttaksplan_og_ikke_en_ny_så_skal_sammenslått_plan_være_lik_opprinnelig_plan() {
        var fom = LocalDate.of(2019, Month.JANUARY, 1);
        var tom = LocalDate.of(2019, Month.JANUARY, 31);

        var tidligerePerioder = new Uttaksperioder();
        var arbeidsforhold = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "123", null);
        var periode = new Uttaksperiode(fom, tom, FULL_UTBETALING);
        tidligerePerioder.leggTilPerioder(arbeidsforhold, periode);

        var nyePerioder = new Uttaksperioder();

        var resultat = uttaksresultatMerger.merge(Optional.of(tidligerePerioder), nyePerioder);

        assertThat(resultat).isNotNull();
        assertThat(resultat.alleArbeidsforhold()).hasSize(1);
        var resultatPerioder = resultat.perioder(resultat.alleArbeidsforhold().iterator().next());
        assertThat(resultatPerioder.getUttaksperioder()).hasSize(1);
        var resultatPeriode = resultatPerioder.getUttaksperioder().get(0);
        assertThat(resultatPeriode.getFom()).isEqualTo(fom);
        assertThat(resultatPeriode.getTom()).isEqualTo(tom);
        assertThat(resultatPeriode.getUtbetalingsgrad()).isEqualTo(FULL_UTBETALING);
    }

    @Test
    public void helt_overlappende_perioder_skal_føre_til_at_ny_periode_gjelder() {
        var fom = LocalDate.of(2019, Month.JANUARY, 1);
        var tom = LocalDate.of(2019, Month.JANUARY, 31);

        var tidligerePerioder = new Uttaksperioder();
        var tidligereArbeidsforhold = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "123", null);
        var tidligerePeriode = new Uttaksperiode(fom, tom, FULL_UTBETALING);
        tidligerePerioder.leggTilPerioder(tidligereArbeidsforhold, tidligerePeriode);

        var nyePerioder = new Uttaksperioder();
        var nyeArbeidsforhold = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "123", null);
        var nyePeriode = new Uttaksperiode(fom, tom, HALV_UTBETALING);
        nyePerioder.leggTilPerioder(nyeArbeidsforhold, nyePeriode);

        var resultat = uttaksresultatMerger.merge(Optional.of(tidligerePerioder), nyePerioder);

        assertThat(resultat).isNotNull();
        assertThat(resultat.alleArbeidsforhold()).hasSize(1);
        var resultatPerioder = resultat.perioder(resultat.alleArbeidsforhold().iterator().next());
        assertThat(resultatPerioder.getUttaksperioder()).hasSize(1);
        var resultatPeriode = resultatPerioder.getUttaksperioder().get(0);
        assertThat(resultatPeriode.getFom()).isEqualTo(fom);
        assertThat(resultatPeriode.getTom()).isEqualTo(tom);
        assertThat(resultatPeriode.getUtbetalingsgrad()).isEqualTo(HALV_UTBETALING);

    }

    @Test
    public void delvis_overlappende_perioder_fører_til_knekk() {
        var fom1 = LocalDate.of(2019, Month.JANUARY, 1);
        var tom1 = LocalDate.of(2019, Month.JANUARY, 31);
        var fom2 = LocalDate.of(2019, Month.JANUARY, 15);
        var tom2 = LocalDate.of(2019, Month.FEBRUARY, 20);

        var tidligerePerioder = new Uttaksperioder();
        var tidligereArbeidsforhold = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "123", null);
        var tidligerePeriode = new Uttaksperiode(fom1, tom1, FULL_UTBETALING);
        tidligerePeriode.innvilg(PeriodeOppfyltÅrsak.UTTAK_ER_INNVILGET, "{}", "{}");
        tidligerePerioder.leggTilPerioder(tidligereArbeidsforhold, tidligerePeriode);

        var nyePerioder = new Uttaksperioder();
        var nyeArbeidsforhold = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "123", null);
        var nyePeriode = new Uttaksperiode(fom2, tom2, HALV_UTBETALING);
        nyePeriode.innvilg(PeriodeOppfyltÅrsak.UTTAK_ER_INNVILGET, "{}", "{}");
        nyePerioder.leggTilPerioder(nyeArbeidsforhold, nyePeriode);

        var resultat = uttaksresultatMerger.merge(Optional.of(tidligerePerioder), nyePerioder);

        assertThat(resultat).isNotNull();
        assertThat(resultat.alleArbeidsforhold()).hasSize(1);
        var resultatPerioder = resultat.perioder(resultat.alleArbeidsforhold().iterator().next());
        assertThat(resultatPerioder.getUttaksperioder()).hasSize(2);

        var resultatPeriode0 = resultatPerioder.getUttaksperioder().get(0);
        assertThat(resultatPeriode0.getFom()).isEqualTo(fom1);
        assertThat(resultatPeriode0.getTom()).isEqualTo(fom2.minusDays(1));
        assertThat(resultatPeriode0.getUtbetalingsgrad()).isEqualTo(FULL_UTBETALING);
        sjekkRegelSporing(resultatPeriode0);

        var resultatPeriode1 = resultatPerioder.getUttaksperioder().get(1);
        assertThat(resultatPeriode1.getFom()).isEqualTo(fom2);
        assertThat(resultatPeriode1.getTom()).isEqualTo(tom2);
        assertThat(resultatPeriode1.getUtbetalingsgrad()).isEqualTo(HALV_UTBETALING);
        sjekkRegelSporing(resultatPeriode1);
    }

    private void sjekkRegelSporing(Uttaksperiode uttaksperiode) {
        assertThat(uttaksperiode.getRegelInput()).isEqualTo("{}");
        assertThat(uttaksperiode.getRegelSporing()).isEqualTo("{}");
    }

}
