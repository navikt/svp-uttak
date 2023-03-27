package no.nav.svangerskapspenger.tjeneste.opprettperioder;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import no.nav.svangerskapspenger.domene.felles.AktivitetType;
import no.nav.svangerskapspenger.domene.felles.Arbeidsforhold;
import no.nav.svangerskapspenger.domene.resultat.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.svangerskapspenger.domene.søknad.DelvisTilrettelegging;
import no.nav.svangerskapspenger.domene.søknad.FullTilrettelegging;
import no.nav.svangerskapspenger.domene.søknad.IngenTilrettelegging;
import no.nav.svangerskapspenger.domene.søknad.Søknad;

public class UttaksperioderTjenesteTest {

    private static final BigDecimal HUNDRE_PROSENT = BigDecimal.valueOf(100L);

    private static final Arbeidsforhold ARBEIDSFORHOLD1 = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "123", "456");
    private static final LocalDate TERMINDATO = LocalDate.of(2019, Month.MAY, 1);

    private UttaksperioderTjeneste uttaksperioderTjeneste = new UttaksperioderTjeneste();

    @Test
    public void først_delvis_tilrettelegging_så_full_tilrettelegging_fører_til_to_perioder() {
        var delvisTilrettelegging = new DelvisTilrettelegging(LocalDate.of(2019, Month.JANUARY, 1), BigDecimal.valueOf(50L));
        var fullTilrettelegging = new FullTilrettelegging(LocalDate.of(2019, Month.FEBRUARY, 1));
        var søknad = new Søknad(
            ARBEIDSFORHOLD1,
            HUNDRE_PROSENT,
            TERMINDATO,
            LocalDate.of(2019, Month.JANUARY, 1),
            List.of(delvisTilrettelegging, fullTilrettelegging));

        var uttaksperioder = uttaksperioderTjeneste.opprett(List.of(søknad));

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
        assertThat(perioder).hasSize(2);

        assertThat(perioder.get(0).getFom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 1));
        assertThat(perioder.get(0).getTom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 31));
        assertThat(perioder.get(0).getUtbetalingsgrad()).isEqualTo(new BigDecimal("50.00"));

        assertThat(perioder.get(1).getFom()).isEqualTo(LocalDate.of(2019, Month.FEBRUARY, 1));
        assertThat(perioder.get(1).getTom()).isEqualTo(LocalDate.of(2019, Month.APRIL, 9));
        assertThat(perioder.get(1).getUtbetalingsgrad()).isEqualTo(BigDecimal.ZERO);
    }


    @Test
    public void først_ikke_søkt_deretter_delvis_tilrettelegging_så_full_tilrettelegging_fører_til_tre_perioder() {
        var delvisTilrettelegging = new DelvisTilrettelegging(LocalDate.of(2019, Month.JANUARY, 15), BigDecimal.valueOf(50L));
        var fullTilrettelegging = new FullTilrettelegging(LocalDate.of(2019, Month.FEBRUARY, 1));
        var søknad = new Søknad(
            ARBEIDSFORHOLD1,
            HUNDRE_PROSENT,
            TERMINDATO,
            LocalDate.of(2019, Month.JANUARY, 1),
            List.of(delvisTilrettelegging, fullTilrettelegging));

        var uttaksperioder = uttaksperioderTjeneste.opprett(List.of(søknad));

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
        assertThat(perioder).hasSize(3);

        assertThat(perioder.get(0).getFom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 1));
        assertThat(perioder.get(0).getTom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 14));
        assertThat(perioder.get(0).getUtbetalingsgrad()).isEqualTo(HUNDRE_PROSENT);

        assertThat(perioder.get(1).getFom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 15));
        assertThat(perioder.get(1).getTom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 31));
        assertThat(perioder.get(1).getUtbetalingsgrad()).isEqualTo(new BigDecimal("50.00"));

        assertThat(perioder.get(2).getFom()).isEqualTo(LocalDate.of(2019, Month.FEBRUARY, 1));
        assertThat(perioder.get(2).getTom()).isEqualTo(LocalDate.of(2019, Month.APRIL, 9));
        assertThat(perioder.get(2).getUtbetalingsgrad()).isEqualTo(BigDecimal.ZERO);

    }

    @Test
    public void først_ingen_tilrettelegging_før_behov_dato_så_full_tilrettelegging_fører_til_to_perioder() {
        var ingenTilrettelegging = new IngenTilrettelegging(LocalDate.of(2018, Month.DECEMBER, 15));
        var fullTilrettelegging = new FullTilrettelegging(LocalDate.of(2019, Month.FEBRUARY, 1));
        var søknad = new Søknad(
            ARBEIDSFORHOLD1,
            HUNDRE_PROSENT,
            TERMINDATO,
            LocalDate.of(2019, Month.JANUARY, 1),
            List.of(ingenTilrettelegging, fullTilrettelegging));

        var uttaksperioder = uttaksperioderTjeneste.opprett(List.of(søknad));

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
        assertThat(perioder).hasSize(2);

        assertThat(perioder.get(0).getFom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 1));
        assertThat(perioder.get(0).getTom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 31));
        assertThat(perioder.get(0).getUtbetalingsgrad()).isEqualTo(HUNDRE_PROSENT);

        assertThat(perioder.get(1).getFom()).isEqualTo(LocalDate.of(2019, Month.FEBRUARY, 1));
        assertThat(perioder.get(1).getTom()).isEqualTo(LocalDate.of(2019, Month.APRIL, 9));
        assertThat(perioder.get(1).getUtbetalingsgrad()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void alt_før_behovsdato_fjernes_dersom_det_finnes_en_arbeidsgiversdato_link_behovsdato() {

        var ingenTilrettelegging = new IngenTilrettelegging(LocalDate.of(2018, Month.DECEMBER, 15));
        var delvisTilrettelegging = new DelvisTilrettelegging(LocalDate.of(2019, Month.JANUARY, 1), new BigDecimal("40.0"));
        var fullTilrettelegging = new FullTilrettelegging(LocalDate.of(2019, Month.FEBRUARY, 1));
        var søknad = new Søknad(
            ARBEIDSFORHOLD1,
            HUNDRE_PROSENT,
            TERMINDATO,
            LocalDate.of(2019, Month.JANUARY, 1),
            List.of(ingenTilrettelegging, delvisTilrettelegging, fullTilrettelegging));

        var uttaksperioder = uttaksperioderTjeneste.opprett(List.of(søknad));

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
        assertThat(perioder).hasSize(2);

        assertThat(perioder.get(0).getFom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 1));
        assertThat(perioder.get(0).getTom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 31));
        assertThat(perioder.get(0).getUtbetalingsgrad()).isEqualTo(new BigDecimal("60.00"));

        assertThat(perioder.get(1).getFom()).isEqualTo(LocalDate.of(2019, Month.FEBRUARY, 1));
        assertThat(perioder.get(1).getTom()).isEqualTo(LocalDate.of(2019, Month.APRIL, 9));
        assertThat(perioder.get(1).getUtbetalingsgrad()).isEqualTo(BigDecimal.ZERO);


    }


    @Test
    public void full_tilrettelegging_fra_start() {
        var tilrettelegging = new FullTilrettelegging(LocalDate.of(2019, Month.JANUARY, 1));
        var søknad = new Søknad(
            ARBEIDSFORHOLD1,
            HUNDRE_PROSENT,
            TERMINDATO,
            LocalDate.of(2019, Month.JANUARY, 1),
            Collections.singletonList(tilrettelegging));

        var uttaksperioder = uttaksperioderTjeneste.opprett(List.of(søknad));

        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1);
        assertThat(perioder.getUttaksperioder()).isEmpty();
        assertThat(perioder.getArbeidsforholdIkkeOppfyltÅrsak()).isEqualTo(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE);
    }


    @Test
    public void full_tilrettelegging_etter_en_måned() {
        var tilrettelegging = new FullTilrettelegging(LocalDate.of(2019, Month.FEBRUARY, 1));
        var søknad = new Søknad(
            ARBEIDSFORHOLD1,
            HUNDRE_PROSENT,
            TERMINDATO,
            LocalDate.of(2019, Month.JANUARY, 1),
            Collections.singletonList(tilrettelegging));

        var uttaksperioder = uttaksperioderTjeneste.opprett(List.of(søknad));

        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1).getUttaksperioder();
        assertThat(perioder).hasSize(2);

        assertThat(perioder.get(0).getFom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 1));
        assertThat(perioder.get(0).getTom()).isEqualTo(tilrettelegging.getTilretteleggingArbeidsgiverDato().minusDays(1));
        assertThat(perioder.get(0).getUtbetalingsgrad()).isEqualTo(HUNDRE_PROSENT);

        assertThat(perioder.get(1).getFom()).isEqualTo(tilrettelegging.getTilretteleggingArbeidsgiverDato());
        assertThat(perioder.get(1).getTom()).isEqualTo(LocalDate.of(2019, Month.APRIL, 9));
        assertThat(perioder.get(1).getUtbetalingsgrad()).isEqualTo(BigDecimal.ZERO);

    }


    @Test
    public void full_tilrettelegging_på_termin() {
        var tilrettelegging = new FullTilrettelegging(LocalDate.of(2019, Month.MAY, 1));
        var søknad = new Søknad(
            ARBEIDSFORHOLD1,
            HUNDRE_PROSENT,
            TERMINDATO,
            LocalDate.of(2019, Month.JANUARY, 1),
            List.of(tilrettelegging));

        var uttaksperioder = uttaksperioderTjeneste.opprett(List.of(søknad));

        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1).getUttaksperioder();
        assertThat(perioder).hasSize(1);
        assertThat(perioder.get(0).getFom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 1));
        assertThat(perioder.get(0).getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(perioder.get(0).getUtbetalingsgrad()).isEqualTo(HUNDRE_PROSENT);
    }

    @Test
    public void delvis_tilrettelegging_fra_februar_fører_til_to_uttaksperioder() {
        var delvisTilrettelegging = new DelvisTilrettelegging(LocalDate.of(2019, Month.FEBRUARY, 1), BigDecimal.valueOf(20L));

        var søknad = new Søknad(
            ARBEIDSFORHOLD1,
            HUNDRE_PROSENT,
            TERMINDATO,
            LocalDate.of(2019, Month.JANUARY, 1),
            List.of(delvisTilrettelegging));

        var uttaksperioder = uttaksperioderTjeneste.opprett(List.of(søknad));

        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1).getUttaksperioder();
        assertThat(perioder).hasSize(2);
        assertThat(perioder.get(0).getFom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 1));
        assertThat(perioder.get(0).getTom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 31));
        assertThat(perioder.get(0).getUtbetalingsgrad()).isEqualTo(HUNDRE_PROSENT);

        assertThat(perioder.get(1).getFom()).isEqualTo(LocalDate.of(2019, Month.FEBRUARY, 1));
        assertThat(perioder.get(1).getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(perioder.get(1).getUtbetalingsgrad()).isEqualTo(new BigDecimal("80.00"));

    }

    @Test
    public void delvis_tilrettelegging_fra_første_dag_fører_til_en_uttaksperioder() {
        var delvisTilrettelegging = new DelvisTilrettelegging(LocalDate.of(2019, Month.JANUARY, 1), BigDecimal.valueOf(20L));

        var søknad = new Søknad(
            ARBEIDSFORHOLD1,
            HUNDRE_PROSENT,
            TERMINDATO,
            LocalDate.of(2019, Month.JANUARY, 1),
            List.of(delvisTilrettelegging));

        var uttaksperioder = uttaksperioderTjeneste.opprett(List.of(søknad));

        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1).getUttaksperioder();
        assertThat(perioder).hasSize(1);
        assertThat(perioder.get(0).getFom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 1));
        assertThat(perioder.get(0).getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(perioder.get(0).getUtbetalingsgrad()).isEqualTo(new BigDecimal("80.00"));
    }

    @Test
    public void delvis_tilrettelegging_for_sent_fører_til_en_uttaksperioder_med_full_utbetaling() {
        var delvisTilrettelegging = new DelvisTilrettelegging(LocalDate.of(2019, Month.MAY, 1), BigDecimal.valueOf(20L));
        var søknad = new Søknad(
            ARBEIDSFORHOLD1,
            HUNDRE_PROSENT,
            TERMINDATO,
            LocalDate.of(2019, Month.JANUARY, 1),
            List.of(delvisTilrettelegging));

        var uttaksperioder = uttaksperioderTjeneste.opprett(List.of(søknad));

        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1).getUttaksperioder();
        assertThat(perioder).hasSize(1);
        assertThat(perioder.get(0).getFom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 1));
        assertThat(perioder.get(0).getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(perioder.get(0).getUtbetalingsgrad()).isEqualTo(HUNDRE_PROSENT);
    }

    @Test
    public void ingen_tilrettelegging_fra_første_dag_gir_en_periode() {
        var ingenTilrettelegging = new IngenTilrettelegging(LocalDate.of(2019, Month.JANUARY, 1));
        var søknad = new Søknad(
            ARBEIDSFORHOLD1,
            HUNDRE_PROSENT,
            TERMINDATO,
            LocalDate.of(2019, Month.JANUARY, 1),
            List.of(ingenTilrettelegging));

        var uttaksperioder = uttaksperioderTjeneste.opprett(List.of(søknad));

        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1).getUttaksperioder();
        assertThat(perioder).hasSize(1);
        assertThat(perioder.get(0).getFom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 1));
        assertThat(perioder.get(0).getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(perioder.get(0).getUtbetalingsgrad()).isEqualTo(HUNDRE_PROSENT);
    }


    @Test
    public void ingen_tilrettelegging_før_etter_en_uke_gir_to_perioder() {
        var ingenTilrettelegging = new IngenTilrettelegging(LocalDate.of(2019, Month.JANUARY, 1).plusWeeks(1));
        var søknad = new Søknad(
            ARBEIDSFORHOLD1,
            HUNDRE_PROSENT,
            TERMINDATO,
            LocalDate.of(2019, Month.JANUARY, 1),
            List.of(ingenTilrettelegging));

        var uttaksperioder = uttaksperioderTjeneste.opprett(List.of(søknad));

        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1).getUttaksperioder();

        assertThat(perioder).hasSize(2);

        assertThat(perioder.get(0).getFom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 1));
        assertThat(perioder.get(0).getTom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 1).plusWeeks(1).minusDays(1));
        assertThat(perioder.get(0).getUtbetalingsgrad()).isEqualTo(BigDecimal.ZERO);

        assertThat(perioder.get(1).getFom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 1).plusWeeks(1));
        assertThat(perioder.get(1).getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(perioder.get(1).getUtbetalingsgrad()).isEqualTo(HUNDRE_PROSENT);
    }

    @Test
    public void ingen_tilrettelegging_etter_3_uker_før_termin_gir_avslag_på_arbeidsforhold() {
        var ingenTilrettelegging = new IngenTilrettelegging(LocalDate.of(2019, Month.APRIL, 20).plusWeeks(1));
        var søknad = new Søknad(
            ARBEIDSFORHOLD1,
            HUNDRE_PROSENT,
            TERMINDATO,
            LocalDate.of(2019, Month.JANUARY, 1),
            List.of(ingenTilrettelegging));

        var uttaksperioder = uttaksperioderTjeneste.opprett(List.of(søknad));

        assertThat(uttaksperioder.perioder(ARBEIDSFORHOLD1).getArbeidsforholdIkkeOppfyltÅrsak())
            .isEqualTo(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN);
        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1).getUttaksperioder();
        assertThat(perioder).isEmpty();
    }

    @Test
    public void dersom_leges_dato_er_etter_tre_uker_før_termimdato_så_skal_hele_arbeidsforholdet_ikke_oppfylles() {
        var ingenTilrettelegging = new IngenTilrettelegging(LocalDate.of(2019, Month.APRIL, 20));
        var søknad = new Søknad(
            ARBEIDSFORHOLD1,
            HUNDRE_PROSENT,
            TERMINDATO,
            LocalDate.of(2019, Month.APRIL, 20),
            List.of(ingenTilrettelegging));

        var uttaksperioder = uttaksperioderTjeneste.opprett(List.of(søknad));

        assertThat(uttaksperioder.perioder(ARBEIDSFORHOLD1).getArbeidsforholdIkkeOppfyltÅrsak())
            .isEqualTo(ArbeidsforholdIkkeOppfyltÅrsak.LEGES_DATO_IKKE_FØR_TRE_UKER_FØR_TERMINDATO);
        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1).getUttaksperioder();
        assertThat(perioder).isEmpty();
    }
}
