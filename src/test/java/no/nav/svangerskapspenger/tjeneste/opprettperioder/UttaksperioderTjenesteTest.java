package no.nav.svangerskapspenger.tjeneste.opprettperioder;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import no.nav.svangerskapspenger.domene.felles.Arbeidsforhold;
import no.nav.svangerskapspenger.domene.søknad.DelvisTilrettelegging;
import no.nav.svangerskapspenger.domene.søknad.FullTilrettelegging;
import no.nav.svangerskapspenger.domene.søknad.IngenTilretteligging;
import no.nav.svangerskapspenger.domene.søknad.Søknad;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperioder;

public class UttaksperioderTjenesteTest {

    private static final BigDecimal FULL_YTELSESGRAD = BigDecimal.valueOf(100L);

    private static final Arbeidsforhold ARBEIDSFORHOLD1 = new Arbeidsforhold("123", "456");
    private static final LocalDate TERMINDATO = LocalDate.of(2019, Month.MAY, 1);

    @Test
    public void manuell_behandling_pga_tvetydig_søknad_med_flere_tilrettelegginger() {
        var delvisTilrettelegging = new DelvisTilrettelegging(LocalDate.of(2019, Month.JANUARY, 1), BigDecimal.valueOf(50L));
        var fullTilrettelegging = new FullTilrettelegging(LocalDate.of(2019, Month.FEBRUARY, 1));
        var søknad = new Søknad(
                ARBEIDSFORHOLD1,
                TERMINDATO,
                LocalDate.of(2019, Month.JANUARY, 1),
                List.of(delvisTilrettelegging, fullTilrettelegging));

        var uttaksperioder = new Uttaksperioder();
        var manuellBehandlingSet = new UttaksperioderTjeneste().opprett(List.of(søknad), uttaksperioder);

        assertThat(manuellBehandlingSet).hasSize(1);
        assertThat(manuellBehandlingSet.iterator().next()).isEqualTo(ManuellBehandling.AVKLAR_TILRETTELIGGING);
    }

    @Test
    public void full_tilrettelegging_fra_start() {
        var tilrettelegging = new FullTilrettelegging(LocalDate.of(2019, Month.JANUARY, 1));
        var søknad = new Søknad(
                ARBEIDSFORHOLD1,
                TERMINDATO,
                LocalDate.of(2019, Month.JANUARY, 1),
                Collections.singletonList(tilrettelegging));

        var uttaksperioder = new Uttaksperioder();
        var manuellBehandlingSet = new UttaksperioderTjeneste().opprett(List.of(søknad), uttaksperioder);

        assertThat(manuellBehandlingSet).hasSize(0);
        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1);
        assertThat(perioder).hasSize(0);
    }


    @Test
    public void full_tilrettelegging_etter_en_måned() {
        var tilrettelegging = new FullTilrettelegging(LocalDate.of(2019, Month.FEBRUARY, 1));
        var søknad = new Søknad(
                ARBEIDSFORHOLD1,
                TERMINDATO,
                LocalDate.of(2019, Month.JANUARY, 1),
                Collections.singletonList(tilrettelegging));

        var uttaksperioder = new Uttaksperioder();
        var manuellBehandlingSet = new UttaksperioderTjeneste().opprett(List.of(søknad), uttaksperioder);

        assertThat(manuellBehandlingSet).hasSize(0);
        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1);
        assertThat(perioder).hasSize(1);
        assertThat(perioder.get(0).getFom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 1));
        assertThat(perioder.get(0).getTom()).isEqualTo(tilrettelegging.getTilretteleggingArbeidsgiverDato().minusDays(1));
        assertThat(perioder.get(0).getYtelsesgrad()).isEqualTo(FULL_YTELSESGRAD);
    }


    @Test
    public void full_tilrettelegging_på_termin() {
        var tilrettelegging = new FullTilrettelegging(LocalDate.of(2019, Month.MAY, 1));
        var søknad = new Søknad(
                ARBEIDSFORHOLD1,
                TERMINDATO,
                LocalDate.of(2019, Month.JANUARY, 1),
                List.of(tilrettelegging));

        var uttaksperioder = new Uttaksperioder();
        var manuellBehandlingSet = new UttaksperioderTjeneste().opprett(List.of(søknad), uttaksperioder);

        assertThat(manuellBehandlingSet).hasSize(0);
        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1);
        assertThat(perioder).hasSize(1);
        assertThat(perioder.get(0).getFom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 1));
        assertThat(perioder.get(0).getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(perioder.get(0).getYtelsesgrad()).isEqualTo(FULL_YTELSESGRAD);
    }

    @Test
    public void delvis_tilrettelegging_fra_februar_fører_til_to_uttaksperioder() {
        var delvisTilrettelegging = new DelvisTilrettelegging(LocalDate.of(2019, Month.FEBRUARY, 1), BigDecimal.valueOf(20L));

        var søknad = new Søknad(
                ARBEIDSFORHOLD1,
                TERMINDATO,
                LocalDate.of(2019, Month.JANUARY, 1),
                List.of(delvisTilrettelegging));

        var uttaksperioder = new Uttaksperioder();
        var manuellBehandlingSet = new UttaksperioderTjeneste().opprett(List.of(søknad), uttaksperioder);

        assertThat(manuellBehandlingSet).hasSize(0);
        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1);
        assertThat(perioder).hasSize(2);
        assertThat(perioder.get(0).getFom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 1));
        assertThat(perioder.get(0).getTom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 31));
        assertThat(perioder.get(0).getYtelsesgrad()).isEqualTo(FULL_YTELSESGRAD);

        assertThat(perioder.get(1).getFom()).isEqualTo(LocalDate.of(2019, Month.FEBRUARY, 1));
        assertThat(perioder.get(1).getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(perioder.get(1).getYtelsesgrad()).isEqualTo(BigDecimal.valueOf(80L));

    }

    @Test
    public void delvis_tilrettelegging_fra_første_dag_fører_til_en_uttaksperioder() {
        var delvisTilrettelegging = new DelvisTilrettelegging(LocalDate.of(2019, Month.JANUARY, 1), BigDecimal.valueOf(20L));

        var søknad = new Søknad(
                ARBEIDSFORHOLD1,
                TERMINDATO,
                LocalDate.of(2019, Month.JANUARY, 1),
                List.of(delvisTilrettelegging));

        var uttaksperioder = new Uttaksperioder();
        var manuellBehandlingSet = new UttaksperioderTjeneste().opprett(List.of(søknad), uttaksperioder);

        assertThat(manuellBehandlingSet).hasSize(0);
        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1);
        assertThat(perioder).hasSize(1);
        assertThat(perioder.get(0).getFom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 1));
        assertThat(perioder.get(0).getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(perioder.get(0).getYtelsesgrad()).isEqualTo(BigDecimal.valueOf(80L));
    }

    @Test
    public void delvis_tilrettelegging_for_sent_fører_til_en_uttaksperioder_med_full_ytelse() {
        var delvisTilrettelegging = new DelvisTilrettelegging(LocalDate.of(2019, Month.MAY, 1), BigDecimal.valueOf(20L));
        var søknad = new Søknad(
                ARBEIDSFORHOLD1,
                TERMINDATO,
                LocalDate.of(2019, Month.JANUARY, 1),
                List.of(delvisTilrettelegging));

        var uttaksperioder = new Uttaksperioder();
        var manuellBehandlingSet = new UttaksperioderTjeneste().opprett(List.of(søknad), uttaksperioder);

        assertThat(manuellBehandlingSet).hasSize(0);
        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1);
        assertThat(perioder).hasSize(1);
        assertThat(perioder.get(0).getFom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 1));
        assertThat(perioder.get(0).getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(perioder.get(0).getYtelsesgrad()).isEqualTo(FULL_YTELSESGRAD);
    }

    @Test
    public void ingen_tilrettelegging_fra_første_dag_gir_en_periode() {
        var ingenTilrettelegging = new IngenTilretteligging(LocalDate.of(2019, Month.JANUARY, 1));
        var søknad = new Søknad(
                ARBEIDSFORHOLD1,
                TERMINDATO,
                LocalDate.of(2019, Month.JANUARY, 1),
                List.of(ingenTilrettelegging));

        var uttaksperioder = new Uttaksperioder();
        var manuellBehandlingSet = new UttaksperioderTjeneste().opprett(List.of(søknad), uttaksperioder);

        assertThat(manuellBehandlingSet).hasSize(0);
        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1);
        assertThat(perioder).hasSize(1);
        assertThat(perioder.get(0).getFom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 1));
        assertThat(perioder.get(0).getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(perioder.get(0).getYtelsesgrad()).isEqualTo(FULL_YTELSESGRAD);
    }

}