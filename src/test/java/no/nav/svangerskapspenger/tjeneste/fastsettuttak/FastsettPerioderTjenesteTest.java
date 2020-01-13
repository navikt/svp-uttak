package no.nav.svangerskapspenger.tjeneste.fastsettuttak;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import no.nav.svangerskapspenger.domene.felles.AktivitetType;
import no.nav.svangerskapspenger.domene.resultat.*;
import org.junit.Test;

import no.nav.svangerskapspenger.domene.felles.Arbeidsforhold;
import no.nav.svangerskapspenger.domene.søknad.AvklarteDatoer;
import no.nav.svangerskapspenger.domene.søknad.DelvisTilrettelegging;
import no.nav.svangerskapspenger.domene.søknad.Ferie;
import no.nav.svangerskapspenger.domene.søknad.FullTilrettelegging;
import no.nav.svangerskapspenger.domene.søknad.IngenTilrettelegging;
import no.nav.svangerskapspenger.domene.søknad.Søknad;

public class FastsettPerioderTjenesteTest {

    private static final Arbeidsforhold ARBEIDSFORHOLD1 = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "123", "456");
    private static final BigDecimal ARBEIDSFORHOLD1_PROSENT = BigDecimal.valueOf(100L);
    private static final Arbeidsforhold ARBEIDSFORHOLD2 = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "234", "567");
    private static final BigDecimal ARBEIDSFORHOLD2_PROSENT = BigDecimal.valueOf(100L);

    private static final Arbeidsforhold ARBEIDSFORHOLD3 = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "345", "1");
    private static final BigDecimal ARBEIDSFORHOLD3_PROSENT = BigDecimal.valueOf(60L);
    private static final Arbeidsforhold ARBEIDSFORHOLD4 = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "345", "2");
    private static final BigDecimal ARBEIDSFORHOLD4_PROSENT = BigDecimal.valueOf(40L);

    private static final BigDecimal FULL_UTBETALINGSGRAD = BigDecimal.valueOf(100L);

    private static final LocalDate TILRETTELEGGING_BEHOV_DATO = LocalDate.of(2019, Month.JANUARY, 1);
    private static final LocalDate TERMINDATO = LocalDate.of(2019, Month.MAY, 1);
    private static final LocalDate SØKNAD_MOTTAT_DATO = LocalDate.of(2019, Month.JANUARY, 3);
    private static final LocalDate FØRSTE_LOVLIGE_UTTAKSDATO = SØKNAD_MOTTAT_DATO.withDayOfMonth(1).minusMonths(3);

    private final FastsettPerioderTjeneste fastsettPerioderTjeneste = new FastsettPerioderTjeneste();

    @Test
    public void ingen_tilrettelegging_fra_behovsdato() {
        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(FØRSTE_LOVLIGE_UTTAKSDATO)
            .medTermindato(TERMINDATO)
            .build();

        var nyeSøknader = List.of(new Søknad(ARBEIDSFORHOLD1, ARBEIDSFORHOLD1_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO,
            List.of(new IngenTilrettelegging(TILRETTELEGGING_BEHOV_DATO))));

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
        assertThat(perioder).hasSize(1);
        sjekkInnvilgetPeriode(perioder.get(0), TILRETTELEGGING_BEHOV_DATO, TERMINDATO.minusWeeks(3).minusDays(1));
    }


    @Test
    public void ingen_tilrettelegging_i_to_arbeidsforhold_hos_forskjellige_arbeidsgivere() {
        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(FØRSTE_LOVLIGE_UTTAKSDATO)
            .medTermindato(TERMINDATO)
            .build();


        var søknad1 = new Søknad(ARBEIDSFORHOLD1, ARBEIDSFORHOLD1_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO,
            List.of(new IngenTilrettelegging(TILRETTELEGGING_BEHOV_DATO)));
        var søknad2 = new Søknad(ARBEIDSFORHOLD2, ARBEIDSFORHOLD2_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO.plusDays(10),
            List.of(new IngenTilrettelegging(TILRETTELEGGING_BEHOV_DATO.plusDays(10))));
        var nyeSøknader = List.of(søknad1, søknad2);

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(2);

        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1).getUttaksperioder();
        assertThat(perioder).hasSize(1);
        sjekkInnvilgetPeriode(perioder.get(0), TILRETTELEGGING_BEHOV_DATO, TERMINDATO.minusWeeks(3).minusDays(1));

        perioder = uttaksperioder.perioder(ARBEIDSFORHOLD2).getUttaksperioder();
        assertThat(perioder).hasSize(1);
        sjekkInnvilgetPeriode(perioder.get(0), TILRETTELEGGING_BEHOV_DATO.plusDays(10), TERMINDATO.minusWeeks(3).minusDays(1));
    }

    @Test
    public void ingen_tilrettelegging_i_to_arbeidsforhold_hos_samme_arbeidsgiver() {
        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(FØRSTE_LOVLIGE_UTTAKSDATO)
            .medTermindato(TERMINDATO)
            .build();


        var søknad1 = new Søknad(ARBEIDSFORHOLD3, ARBEIDSFORHOLD3_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO,
            List.of(new IngenTilrettelegging(TILRETTELEGGING_BEHOV_DATO)));
        var søknad2 = new Søknad(ARBEIDSFORHOLD4, ARBEIDSFORHOLD4_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO.plusDays(10),
            List.of(new IngenTilrettelegging(TILRETTELEGGING_BEHOV_DATO.plusDays(10))));
        var nyeSøknader = List.of(søknad1, søknad2);

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(2);

        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD3).getUttaksperioder();
        assertThat(perioder).hasSize(1);
        sjekkInnvilgetPeriode(perioder.get(0), TILRETTELEGGING_BEHOV_DATO, TERMINDATO.minusWeeks(3).minusDays(1));

        perioder = uttaksperioder.perioder(ARBEIDSFORHOLD4).getUttaksperioder();
        assertThat(perioder).hasSize(1);
        sjekkInnvilgetPeriode(perioder.get(0), TILRETTELEGGING_BEHOV_DATO.plusDays(10), TERMINDATO.minusWeeks(3).minusDays(1));
    }

    @Test
    public void uttak_skal_avslås_pga_søknadsfrist_dersom_første_lovlige_uttaksdato_ikke_er_satt() {
        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medTermindato(TERMINDATO)
            .build();

        var nyeSøknader = List.of(new Søknad(ARBEIDSFORHOLD1, ARBEIDSFORHOLD1_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO,
            List.of(new IngenTilrettelegging(TILRETTELEGGING_BEHOV_DATO))));

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
        assertThat(perioder).hasSize(1);

        sjekkAvslåttPeriode(perioder.get(0), TILRETTELEGGING_BEHOV_DATO, TERMINDATO.minusWeeks(3).minusDays(1), PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT);
    }

    @Test
    public void uttak_ikke_oppfylt_ved_brukers_død() {
        var brukersdødsdato = LocalDate.of(2019, Month.MARCH, 1);
        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(FØRSTE_LOVLIGE_UTTAKSDATO)
            .medTermindato(TERMINDATO)
            .medBrukersDødsdato(brukersdødsdato)
            .build();

        var nyeSøknader = List.of(new Søknad(ARBEIDSFORHOLD1, ARBEIDSFORHOLD1_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO,
            List.of(new IngenTilrettelegging(TILRETTELEGGING_BEHOV_DATO))));

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
        assertThat(perioder).hasSize(2);

        sjekkInnvilgetPeriode(perioder.get(0), TILRETTELEGGING_BEHOV_DATO, brukersdødsdato.minusDays(1));
        sjekkAvslåttPeriode(perioder.get(1), brukersdødsdato, TERMINDATO.minusWeeks(3).minusDays(1), PeriodeIkkeOppfyltÅrsak.BRUKER_ER_DØD);
    }

    @Test
    public void uttak_ikke_oppfylt_ved_barnets_død() {
        var barnetsDødsdato = LocalDate.of(2019, Month.APRIL, 1);
        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(FØRSTE_LOVLIGE_UTTAKSDATO)
            .medTermindato(TERMINDATO)
            .medBarnetsDødsdato(barnetsDødsdato)
            .build();

        var nyeSøknader = List.of(new Søknad(ARBEIDSFORHOLD1, ARBEIDSFORHOLD1_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO,
            List.of(new IngenTilrettelegging(TILRETTELEGGING_BEHOV_DATO))));

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
        assertThat(perioder).hasSize(2);

        sjekkInnvilgetPeriode(perioder.get(0), TILRETTELEGGING_BEHOV_DATO, barnetsDødsdato.minusDays(1));
        sjekkAvslåttPeriode(perioder.get(1), barnetsDødsdato, TERMINDATO.minusWeeks(3).minusDays(1), PeriodeIkkeOppfyltÅrsak.BARN_ER_DØDT);
    }


    @Test
    public void uttak_etter_opphør_av_medlemskap_avslås() {
        var opphørAvMedlemskap = LocalDate.of(2019, Month.APRIL, 1);
        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(FØRSTE_LOVLIGE_UTTAKSDATO)
            .medTermindato(TERMINDATO)
            .medOpphørsdatoForMedlemskap(opphørAvMedlemskap)
            .build();

        var nyeSøknader = List.of(new Søknad(ARBEIDSFORHOLD1, ARBEIDSFORHOLD1_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO,
            List.of(new IngenTilrettelegging(TILRETTELEGGING_BEHOV_DATO))));

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
        assertThat(perioder).hasSize(2);

        sjekkInnvilgetPeriode(perioder.get(0), TILRETTELEGGING_BEHOV_DATO, opphørAvMedlemskap.minusDays(1));
        sjekkAvslåttPeriode(perioder.get(1), opphørAvMedlemskap, TERMINDATO.minusWeeks(3).minusDays(1), PeriodeIkkeOppfyltÅrsak.BRUKER_ER_IKKE_MEDLEM);
    }

    @Test
    public void uttak_med_delvis_tilrettelegging_etter_en_måned_og_opphør_av_medlemskap_gir_tre_perioder() {
        var opphørAvMedlemskap = LocalDate.of(2019, Month.APRIL, 1);
        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(FØRSTE_LOVLIGE_UTTAKSDATO)
            .medTermindato(TERMINDATO)
            .medOpphørsdatoForMedlemskap(opphørAvMedlemskap)
            .build();


        var startTilpassing = LocalDate.of(2019, Month.FEBRUARY, 1);
        var nyeSøknader = List.of(new Søknad(ARBEIDSFORHOLD1, ARBEIDSFORHOLD1_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO,
            List.of(new DelvisTilrettelegging(startTilpassing, BigDecimal.valueOf(60L)))));

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
        assertThat(perioder).hasSize(3);

        sjekkInnvilgetPeriode(perioder.get(0), TILRETTELEGGING_BEHOV_DATO, startTilpassing.minusDays(1));
        sjekkInnvilgetPeriode(perioder.get(1), startTilpassing, opphørAvMedlemskap.minusDays(1), new BigDecimal("40.00"));
        sjekkAvslåttPeriode(perioder.get(2), opphørAvMedlemskap, TERMINDATO.minusWeeks(3).minusDays(1), PeriodeIkkeOppfyltÅrsak.BRUKER_ER_IKKE_MEDLEM);
    }

    @Test
    public void dersom_arbeidsgiver_kan_tilrettelegge_fra_start_så_skal_hele_arbeidsforholdet_avslås() {
        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(FØRSTE_LOVLIGE_UTTAKSDATO)
            .medTermindato(TERMINDATO)
            .build();

        var nyeSøknader = List.of(new Søknad(ARBEIDSFORHOLD1, ARBEIDSFORHOLD1_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO,
            List.of(new FullTilrettelegging(TILRETTELEGGING_BEHOV_DATO))));

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next());
        assertThat(perioder.getUttaksperioder()).hasSize(0);
        assertThat(perioder.getArbeidsforholdIkkeOppfyltÅrsak()).isEqualTo(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE);
    }

    @Test
    public void skal_fjerne_uttaksperiode_dersom_den_kun_inneholder_helg() {
        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(FØRSTE_LOVLIGE_UTTAKSDATO)
            .medTermindato(TERMINDATO)
            .build();

        var nyeSøknader = List.of(new Søknad(ARBEIDSFORHOLD1, ARBEIDSFORHOLD1_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO,
            List.of(
                new DelvisTilrettelegging(LocalDate.of(2019, Month.JANUARY, 1), new BigDecimal("10.00")),
                new DelvisTilrettelegging(LocalDate.of(2019, Month.JANUARY, 5), new BigDecimal("20.00")),
                new DelvisTilrettelegging(LocalDate.of(2019, Month.JANUARY, 7), new BigDecimal("30.00"))
        )));

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, avklarteDatoer);

        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1).getUttaksperioder();
        assertThat(perioder).hasSize(2);

        sjekkInnvilgetPeriode(perioder.get(0), LocalDate.of(2019, Month.JANUARY, 1), LocalDate.of(2019, Month.JANUARY, 4), new BigDecimal("90.00"));
        sjekkInnvilgetPeriode(perioder.get(1), LocalDate.of(2019, Month.JANUARY, 7), LocalDate.of(2019, Month.APRIL, 9), new BigDecimal("70.00"));
    }

    @Test
    public void skal_sette_ikke_oppfylt_på_hele_arbeidsforholdet_dersom_periodene_kun_inneholder_helg() {
        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(FØRSTE_LOVLIGE_UTTAKSDATO)
            .medTermindato(TERMINDATO)
            .build();

        var nyeSøknader = List.of(new Søknad(ARBEIDSFORHOLD1, ARBEIDSFORHOLD1_PROSENT, LocalDate.of(2019, 1, 27), LocalDate.of(2019, 1, 5),
            List.of(
                new IngenTilrettelegging(LocalDate.of(2019, Month.JANUARY, 5))
            )));

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, avklarteDatoer);

        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1);
        assertThat(perioder.getUttaksperioder()).hasSize(0);
        assertThat(perioder.getArbeidsforholdIkkeOppfyltÅrsak()).isEqualTo(ArbeidsforholdIkkeOppfyltÅrsak.UTTAK_KUN_PÅ_HELG);
    }

    @Test
    public void lovlig_uttak_med_tidlig_fødsel_skal_ikke_oppfylles_fra_fødselsdato() {
        var fødseldatoTidligFødsel = TERMINDATO.minusWeeks(4);
        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(FØRSTE_LOVLIGE_UTTAKSDATO)
            .medTermindato(TERMINDATO)
            .medFødselsdato(fødseldatoTidligFødsel)
            .build();

        var nyeSøknader = List.of(new Søknad(ARBEIDSFORHOLD1, ARBEIDSFORHOLD1_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO,
            List.of(new IngenTilrettelegging(TILRETTELEGGING_BEHOV_DATO))));

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
        assertThat(perioder).hasSize(2);

        sjekkInnvilgetPeriode(perioder.get(0), TILRETTELEGGING_BEHOV_DATO, fødseldatoTidligFødsel.minusDays(1));
        sjekkAvslåttPeriode(perioder.get(1), fødseldatoTidligFødsel, TERMINDATO.minusWeeks(3).minusDays(1), PeriodeIkkeOppfyltÅrsak.PERIODEN_ER_IKKE_FØR_FØDSEL);
    }

    @Test
    public void søkt_for_sent_gir_ikke_oppfylte_perioder_frem_til_første_lovlige_uttaksperiode() {
        var førsteLovligeUttaksdato = TILRETTELEGGING_BEHOV_DATO.plusWeeks(1);
        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(førsteLovligeUttaksdato)
            .medTermindato(TERMINDATO)
            .build();

        var nyeSøknader = List.of(new Søknad(ARBEIDSFORHOLD1, ARBEIDSFORHOLD1_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO,
            List.of(new IngenTilrettelegging(TILRETTELEGGING_BEHOV_DATO))));

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
        assertThat(perioder).hasSize(2);

        sjekkAvslåttPeriode(perioder.get(0), TILRETTELEGGING_BEHOV_DATO, førsteLovligeUttaksdato.minusDays(1), PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT);
        sjekkInnvilgetPeriode(perioder.get(1), førsteLovligeUttaksdato, TERMINDATO.minusWeeks(3).minusDays(1));
    }

    @Test
    public void ferie_skal_avslås() {
        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(FØRSTE_LOVLIGE_UTTAKSDATO)
            .medTermindato(TERMINDATO)
            .medFerie(Ferie.opprett(TILRETTELEGGING_BEHOV_DATO.plusWeeks(1), TILRETTELEGGING_BEHOV_DATO.plusWeeks(2).minusDays(1)))
            .build();


        var nyeSøknader = List.of(new Søknad(ARBEIDSFORHOLD1, ARBEIDSFORHOLD1_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO,
            List.of(new IngenTilrettelegging(TILRETTELEGGING_BEHOV_DATO))));

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();

        assertThat(perioder).hasSize(3);

        sjekkInnvilgetPeriode(perioder.get(0), TILRETTELEGGING_BEHOV_DATO, TILRETTELEGGING_BEHOV_DATO.plusWeeks(1).minusDays(1));
        sjekkAvslåttPeriode(perioder.get(1), TILRETTELEGGING_BEHOV_DATO.plusWeeks(1), TILRETTELEGGING_BEHOV_DATO.plusWeeks(2).minusDays(1), PeriodeIkkeOppfyltÅrsak.PERIODEN_ER_SAMTIDIG_SOM_EN_FERIE);
        sjekkInnvilgetPeriode(perioder.get(2), TILRETTELEGGING_BEHOV_DATO.plusWeeks(2), TERMINDATO.minusWeeks(3).minusDays(1));
    }

    @Test
    public void perioder_etter_hull_i_uttak_skal_avslås() {
        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(FØRSTE_LOVLIGE_UTTAKSDATO)
            .medTermindato(TERMINDATO)
            .build();

        var nyeSøknader = List.of(new Søknad(ARBEIDSFORHOLD1, ARBEIDSFORHOLD1_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO,
            List.of(
                new DelvisTilrettelegging(LocalDate.of(2019, Month.JANUARY, 1), new BigDecimal("10.00")),
                new FullTilrettelegging(LocalDate.of(2019, Month.FEBRUARY, 1)),
                new DelvisTilrettelegging(LocalDate.of(2019, Month.MARCH, 1), new BigDecimal("30.00"))
            )));

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, avklarteDatoer);

        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1).getUttaksperioder();
        assertThat(perioder).hasSize(3);

        sjekkInnvilgetPeriode(perioder.get(0), LocalDate.of(2019, Month.JANUARY, 1), LocalDate.of(2019, Month.JANUARY, 31), new BigDecimal("90.00"));
        sjekkInnvilgetPeriode(perioder.get(1), LocalDate.of(2019, Month.FEBRUARY, 1), LocalDate.of(2019, Month.FEBRUARY, 28), BigDecimal.ZERO);
        sjekkAvslåttPeriode(perioder.get(2), LocalDate.of(2019, Month.MARCH, 1), LocalDate.of(2019, Month.APRIL, 9), PeriodeIkkeOppfyltÅrsak.PERIODEN_ER_ETTER_ET_OPPHOLD_I_UTTAK);
    }

    @Test
    public void perioder_etter_hull_i_uttak_skal_avslås_og_overstyring_av_utbetalingsgrad_skal_ignoreres() {
        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(FØRSTE_LOVLIGE_UTTAKSDATO)
            .medTermindato(TERMINDATO)
            .build();

        var nyeSøknader = List.of(new Søknad(ARBEIDSFORHOLD1, ARBEIDSFORHOLD1_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO,
            List.of(
                new DelvisTilrettelegging(LocalDate.of(2019, Month.JANUARY, 1), new BigDecimal("10.00")),
                new FullTilrettelegging(LocalDate.of(2019, Month.FEBRUARY, 1)),
                new DelvisTilrettelegging(LocalDate.of(2019, Month.MARCH, 1), new BigDecimal("30.00"), new BigDecimal("20.00"))
            )));

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, avklarteDatoer);

        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1).getUttaksperioder();
        assertThat(perioder).hasSize(3);

        sjekkInnvilgetPeriode(perioder.get(0), LocalDate.of(2019, Month.JANUARY, 1), LocalDate.of(2019, Month.JANUARY, 31), new BigDecimal("90.00"));
        sjekkInnvilgetPeriode(perioder.get(1), LocalDate.of(2019, Month.FEBRUARY, 1), LocalDate.of(2019, Month.FEBRUARY, 28), BigDecimal.ZERO);
        sjekkAvslåttPeriode(perioder.get(2), LocalDate.of(2019, Month.MARCH, 1), LocalDate.of(2019, Month.APRIL, 9), PeriodeIkkeOppfyltÅrsak.PERIODEN_ER_ETTER_ET_OPPHOLD_I_UTTAK);
    }

    @Test
    public void hull_som_dekkes_av_ferie_skal_ikke_føre_til_avslag_på_etterfølgende_perioder() {
        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(FØRSTE_LOVLIGE_UTTAKSDATO)
            .medTermindato(TERMINDATO)
            .medFerie(Ferie.opprett(LocalDate.of(2019, Month.FEBRUARY, 1), LocalDate.of(2019, Month.FEBRUARY, 28)))
            .build();

        var nyeSøknader = List.of(new Søknad(ARBEIDSFORHOLD1, ARBEIDSFORHOLD1_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO,
            List.of(
                new DelvisTilrettelegging(LocalDate.of(2019, Month.JANUARY, 1), new BigDecimal("10.00")),
                new FullTilrettelegging(LocalDate.of(2019, Month.FEBRUARY, 1)),
                new DelvisTilrettelegging(LocalDate.of(2019, Month.MARCH, 1), new BigDecimal("30.00"))
            )));

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, avklarteDatoer);

        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1).getUttaksperioder();
        assertThat(perioder).hasSize(3);

        sjekkInnvilgetPeriode(perioder.get(0), LocalDate.of(2019, Month.JANUARY, 1), LocalDate.of(2019, Month.JANUARY, 31), new BigDecimal("90.00"));
        sjekkAvslåttPeriode(perioder.get(1), LocalDate.of(2019, Month.FEBRUARY, 1), LocalDate.of(2019, Month.FEBRUARY, 28), PeriodeIkkeOppfyltÅrsak.PERIODEN_ER_SAMTIDIG_SOM_EN_FERIE);
        sjekkInnvilgetPeriode(perioder.get(2), LocalDate.of(2019, Month.MARCH, 1), LocalDate.of(2019, Month.APRIL, 9), new BigDecimal("70.00"));
    }

    @Test
    public void ferie_periode_før_uttak_skal_ikke_påvirke_resultatet() {
        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(FØRSTE_LOVLIGE_UTTAKSDATO)
            .medTermindato(TERMINDATO)
            .medFerie(Ferie.opprett(LocalDate.of(2018, Month.DECEMBER, 15), LocalDate.of(2018, Month.DECEMBER, 28)))
            .build();

        var nyeSøknader = List.of(new Søknad(ARBEIDSFORHOLD1, ARBEIDSFORHOLD1_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO,
            List.of(
                new IngenTilrettelegging(LocalDate.of(2019, Month.JANUARY, 1))
            )));

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, avklarteDatoer);

        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1).getUttaksperioder();
        assertThat(perioder).hasSize(1);

        sjekkInnvilgetPeriode(perioder.get(0), LocalDate.of(2019, Month.JANUARY, 1), LocalDate.of(2019, Month.APRIL, 9));
    }

    @Test
    public void uttak_med_delvis_tilrettelegging_og_overstyrt_utbetalingsgrad_får_riktig_uttaksresultat() {
        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(FØRSTE_LOVLIGE_UTTAKSDATO)
            .medTermindato(TERMINDATO)
            .build();


        var nyeSøknader = List.of(new Søknad(ARBEIDSFORHOLD1, ARBEIDSFORHOLD1_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO,
            List.of(new DelvisTilrettelegging(TILRETTELEGGING_BEHOV_DATO, BigDecimal.valueOf(60L), new BigDecimal("20.00")))));

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
        assertThat(perioder).hasSize(1);

        sjekkInnvilgetPeriodeMedOverstyrtUtbetalingsgrad(perioder.get(0), TILRETTELEGGING_BEHOV_DATO, TERMINDATO.minusWeeks(3).minusDays(1), new BigDecimal("20.00"));
    }


    @Test
    public void ferie_overlappende_med_periode_mellom_behovsdato_og_oppstart_av_ingen_tilrettelegging_skal_ikke_føre_til_avslag_pga_hull_mellom_uttak() {
        var behovsdato = LocalDate.of(2019, Month.SEPTEMBER, 30);
        var startFerie = LocalDate.of(2019, Month.NOVEMBER, 10);
        var sluttFerie =  LocalDate.of(2019, Month.NOVEMBER, 19);
        var startIngenTilrettelegging = LocalDate.of(2019, Month.NOVEMBER, 22);
        var termindato = LocalDate.of(2020, Month.JUNE, 14);

        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(behovsdato.minusMonths(3))
            .medTermindato(termindato)
            .medFerie(Ferie.opprett(startFerie, sluttFerie))
            .build();

        var nyeSøknader = List.of(new Søknad(ARBEIDSFORHOLD1, ARBEIDSFORHOLD1_PROSENT, termindato, behovsdato,
            List.of(new IngenTilrettelegging(startIngenTilrettelegging))));

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
        assertThat(perioder).hasSize(4);

        sjekkInnvilgetPeriode(perioder.get(0), behovsdato, startFerie.minusDays(1), BigDecimal.ZERO);
        sjekkAvslåttPeriode(perioder.get(1), startFerie, sluttFerie, PeriodeIkkeOppfyltÅrsak.PERIODEN_ER_SAMTIDIG_SOM_EN_FERIE);
        sjekkInnvilgetPeriode(perioder.get(2), sluttFerie.plusDays(1), startIngenTilrettelegging.minusDays(1), BigDecimal.ZERO);
        sjekkInnvilgetPeriode(perioder.get(3), startIngenTilrettelegging, termindato.minusWeeks(3).minusDays(1), FULL_UTBETALINGSGRAD);
    }

    private void sjekkInnvilgetPeriode(Uttaksperiode uttaksperiode, LocalDate fom, LocalDate tom) {
        sjekkPeriode(uttaksperiode, fom, tom, FULL_UTBETALINGSGRAD, UtfallType.OPPFYLT, PeriodeOppfyltÅrsak.UTTAK_ER_INNVILGET, false);
    }

    private void sjekkInnvilgetPeriode(Uttaksperiode uttaksperiode, LocalDate fom, LocalDate tom, BigDecimal utbetalingsgrad) {
        sjekkPeriode(uttaksperiode, fom, tom, utbetalingsgrad, UtfallType.OPPFYLT, PeriodeOppfyltÅrsak.UTTAK_ER_INNVILGET, false);
    }
    private void sjekkInnvilgetPeriodeMedOverstyrtUtbetalingsgrad(Uttaksperiode uttaksperiode, LocalDate fom, LocalDate tom, BigDecimal utbetalingsgrad) {
        sjekkPeriode(uttaksperiode, fom, tom, utbetalingsgrad, UtfallType.OPPFYLT, PeriodeOppfyltÅrsak.UTTAK_ER_INNVILGET, true);
    }

    private void sjekkAvslåttPeriode(Uttaksperiode uttaksperiode, LocalDate fom, LocalDate tom, PeriodeIkkeOppfyltÅrsak årsak) {
        sjekkPeriode(uttaksperiode, fom, tom, BigDecimal.ZERO, UtfallType.IKKE_OPPFYLT, årsak, false);
    }

    private void sjekkPeriode(Uttaksperiode uttaksperiode, LocalDate fom, LocalDate tom, BigDecimal utbetalingsgrad, UtfallType utfallType, Årsak årsak, boolean overstyrtUtbetalingsgrad) {
        assertThat(uttaksperiode.getFom()).isEqualTo(fom);
        assertThat(uttaksperiode.getTom()).isEqualTo(tom);
        assertThat(uttaksperiode.getUtbetalingsgrad()).isEqualByComparingTo(utbetalingsgrad);
        assertThat(uttaksperiode.getUtfallType()).isEqualTo(utfallType);
        assertThat(uttaksperiode.getÅrsak()).isEqualTo(årsak);
        assertThat(uttaksperiode.getRegelInput()).isNotEmpty();
        assertThat(uttaksperiode.getRegelSporing()).isNotEmpty();
        assertThat(uttaksperiode.isUtbetalingsgradOverstyrt()).isEqualTo(overstyrtUtbetalingsgrad);
    }

}
