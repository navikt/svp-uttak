package no.nav.svangerskapspenger.tjeneste.fastsettuttak;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
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

    private static final BigDecimal FULL_UTBETALINGSGRAD = BigDecimal.valueOf(100L);

    private static final LocalDate TILRETTELEGGING_BEHOV_DATO = LocalDate.of(2019, Month.JANUARY, 1);
    private static final LocalDate TERMINDATO = LocalDate.of(2019, Month.MAY,1);
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
        var tidligereSøknader = new ArrayList<Søknad>();

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, tidligereSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
        assertThat(perioder).hasSize(1);
        var periode0 = perioder.get(0);
        assertThat(periode0.getFom()).isEqualTo(TILRETTELEGGING_BEHOV_DATO);
        assertThat(periode0.getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(periode0.getUtbetalingsgrad()).isEqualTo(FULL_UTBETALINGSGRAD);
        assertThat(periode0.getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
        assertThat(periode0.getÅrsak()).isEqualTo(PeriodeOppfyltÅrsak.UTTAK_ER_INNVILGET);
        assertThat(periode0.getRegelInput()).isNotEmpty();
        assertThat(periode0.getRegelSporing()).isNotEmpty();
    }


    @Test
    public void ingen_tilrettelegging_i_to_arbeidsforhold() {
        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(FØRSTE_LOVLIGE_UTTAKSDATO)
            .medTermindato(TERMINDATO)
            .build();


        var søknad1 = new Søknad(ARBEIDSFORHOLD1, ARBEIDSFORHOLD1_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO,
            List.of(new IngenTilrettelegging(TILRETTELEGGING_BEHOV_DATO)));
        var søknad2 = new Søknad(ARBEIDSFORHOLD2, ARBEIDSFORHOLD2_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO.plusDays(10),
            List.of(new IngenTilrettelegging(TILRETTELEGGING_BEHOV_DATO.plusDays(10))));
        var nyeSøknader = List.of(søknad1, søknad2);
        var tidligereSøknader = new ArrayList<Søknad>();

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, tidligereSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(2);

        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1).getUttaksperioder();
        assertThat(perioder).hasSize(1);
        var periode0 = perioder.get(0);
        assertThat(periode0.getFom()).isEqualTo(TILRETTELEGGING_BEHOV_DATO);
        assertThat(periode0.getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(periode0.getUtbetalingsgrad()).isEqualTo(ARBEIDSFORHOLD1_PROSENT);
        assertThat(periode0.getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
        assertThat(periode0.getÅrsak()).isEqualTo(PeriodeOppfyltÅrsak.UTTAK_ER_INNVILGET);
        assertThat(periode0.getRegelInput()).isNotEmpty();
        assertThat(periode0.getRegelSporing()).isNotEmpty();

        perioder = uttaksperioder.perioder(ARBEIDSFORHOLD2).getUttaksperioder();
        assertThat(perioder).hasSize(1);
        periode0 = perioder.get(0);
        assertThat(periode0.getFom()).isEqualTo(TILRETTELEGGING_BEHOV_DATO.plusDays(10));
        assertThat(periode0.getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(periode0.getUtbetalingsgrad()).isEqualTo(ARBEIDSFORHOLD2_PROSENT);
        assertThat(periode0.getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
        assertThat(periode0.getÅrsak()).isEqualTo(PeriodeOppfyltÅrsak.UTTAK_ER_INNVILGET);
        assertThat(periode0.getRegelInput()).isNotEmpty();
        assertThat(periode0.getRegelSporing()).isNotEmpty();
    }


    @Test
    public void uttak_skal_avslås_pga_søknadsfrist_dersom_første_lovlige_uttaksdato_ikke_er_satt() {
        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medTermindato(TERMINDATO)
            .build();

        var nyeSøknader = List.of(new Søknad(ARBEIDSFORHOLD1, ARBEIDSFORHOLD1_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO,
            List.of(new IngenTilrettelegging(TILRETTELEGGING_BEHOV_DATO))));
        var tidligereSøknader = new ArrayList<Søknad>();

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, tidligereSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
        assertThat(perioder).hasSize(1);
        var periode0 = perioder.get(0);
        assertThat(periode0.getFom()).isEqualTo(TILRETTELEGGING_BEHOV_DATO);
        assertThat(periode0.getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(periode0.getUtbetalingsgrad()).isEqualTo(BigDecimal.ZERO);
        assertThat(periode0.getUtfallType()).isEqualTo(UtfallType.IKKE_OPPFYLT);
        assertThat(periode0.getÅrsak()).isEqualTo(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT);
        assertThat(periode0.getRegelInput()).isNotEmpty();
        assertThat(periode0.getRegelSporing()).isNotEmpty();
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
        var tidligereSøknader = new ArrayList<Søknad>();

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, tidligereSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
        assertThat(perioder).hasSize(2);

        var periode0 = perioder.get(0);
        assertThat(periode0.getFom()).isEqualTo(TILRETTELEGGING_BEHOV_DATO);
        assertThat(periode0.getTom()).isEqualTo(brukersdødsdato.minusDays(1));
        assertThat(periode0.getUtbetalingsgrad()).isEqualTo(FULL_UTBETALINGSGRAD);
        assertThat(periode0.getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
        assertThat(periode0.getÅrsak()).isEqualTo(PeriodeOppfyltÅrsak.UTTAK_ER_INNVILGET);

        var periode1 = perioder.get(1);
        assertThat(periode1.getFom()).isEqualTo(brukersdødsdato);
        assertThat(periode1.getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(periode1.getUtbetalingsgrad()).isEqualTo(BigDecimal.ZERO);
        assertThat(periode1.getUtfallType()).isEqualTo(UtfallType.IKKE_OPPFYLT);
        assertThat(periode1.getÅrsak()).isEqualTo(PeriodeIkkeOppfyltÅrsak.BRUKER_ER_DØD);
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
        var tidligereSøknader = new ArrayList<Søknad>();

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, tidligereSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
        assertThat(perioder).hasSize(2);

        var periode0 = perioder.get(0);
        assertThat(periode0.getFom()).isEqualTo(TILRETTELEGGING_BEHOV_DATO);
        assertThat(periode0.getTom()).isEqualTo(barnetsDødsdato.minusDays(1));
        assertThat(periode0.getUtbetalingsgrad()).isEqualTo(FULL_UTBETALINGSGRAD);
        assertThat(periode0.getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
        assertThat(periode0.getÅrsak()).isEqualTo(PeriodeOppfyltÅrsak.UTTAK_ER_INNVILGET);

        var periode1 = perioder.get(1);
        assertThat(periode1.getFom()).isEqualTo(barnetsDødsdato);
        assertThat(periode1.getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(periode1.getUtbetalingsgrad()).isEqualTo(BigDecimal.ZERO);
        assertThat(periode1.getUtfallType()).isEqualTo(UtfallType.IKKE_OPPFYLT);
        assertThat(periode1.getÅrsak()).isEqualTo(PeriodeIkkeOppfyltÅrsak.BARN_ER_DØDT);
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
        var tidligereSøknader = new ArrayList<Søknad>();

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, tidligereSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
        assertThat(perioder).hasSize(2);

        var periode0 = perioder.get(0);
        assertThat(periode0.getFom()).isEqualTo(TILRETTELEGGING_BEHOV_DATO);
        assertThat(periode0.getTom()).isEqualTo(opphørAvMedlemskap.minusDays(1));
        assertThat(periode0.getUtbetalingsgrad()).isEqualTo(FULL_UTBETALINGSGRAD);
        assertThat(periode0.getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
        assertThat(periode0.getÅrsak()).isEqualTo(PeriodeOppfyltÅrsak.UTTAK_ER_INNVILGET);

        var periode1 = perioder.get(1);
        assertThat(periode1.getFom()).isEqualTo(opphørAvMedlemskap);
        assertThat(periode1.getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(periode1.getUtbetalingsgrad()).isEqualTo(BigDecimal.ZERO);
        assertThat(periode1.getUtfallType()).isEqualTo(UtfallType.IKKE_OPPFYLT);
        assertThat(periode1.getÅrsak()).isEqualTo(PeriodeIkkeOppfyltÅrsak.BRUKER_ER_IKKE_MEDLEM);

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
        var tidligereSøknader = new ArrayList<Søknad>();

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, tidligereSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
        assertThat(perioder).hasSize(3);

        var periode0 = perioder.get(0);
        assertThat(periode0.getFom()).isEqualTo(TILRETTELEGGING_BEHOV_DATO);
        assertThat(periode0.getTom()).isEqualTo(startTilpassing.minusDays(1));
        assertThat(periode0.getUtbetalingsgrad()).isEqualTo(FULL_UTBETALINGSGRAD);
        assertThat(periode0.getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
        assertThat(periode0.getÅrsak()).isEqualTo(PeriodeOppfyltÅrsak.UTTAK_ER_INNVILGET);

        var periode1 = perioder.get(1);
        assertThat(periode1.getFom()).isEqualTo(startTilpassing);
        assertThat(periode1.getTom()).isEqualTo(opphørAvMedlemskap.minusDays(1));
        assertThat(periode1.getUtbetalingsgrad()).isEqualTo(new BigDecimal("40.00"));
        assertThat(periode1.getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
        assertThat(periode1.getÅrsak()).isEqualTo(PeriodeOppfyltÅrsak.UTTAK_ER_INNVILGET);

        var periode2 = perioder.get(2);
        assertThat(periode2.getFom()).isEqualTo(opphørAvMedlemskap);
        assertThat(periode2.getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(periode2.getUtbetalingsgrad()).isEqualTo(BigDecimal.ZERO);
        assertThat(periode2.getUtfallType()).isEqualTo(UtfallType.IKKE_OPPFYLT);
        assertThat(periode2.getÅrsak()).isEqualTo(PeriodeIkkeOppfyltÅrsak.BRUKER_ER_IKKE_MEDLEM);
    }

    @Test
    public void dersom_arbeidsgiver_kan_tilrettelegge_fra_start_så_skal_hele_arbeidsforholdet_avslås() {
        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(FØRSTE_LOVLIGE_UTTAKSDATO)
            .medTermindato(TERMINDATO)
            .build();

        var nyeSøknader = List.of(new Søknad(ARBEIDSFORHOLD1, ARBEIDSFORHOLD1_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO,
            List.of(new FullTilrettelegging(TILRETTELEGGING_BEHOV_DATO))));
        var tidligereSøknader = new ArrayList<Søknad>();

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, tidligereSøknader, avklarteDatoer);

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
        var tidligereSøknader = new ArrayList<Søknad>();

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, tidligereSøknader, avklarteDatoer);

        var perioder = uttaksperioder.perioder(ARBEIDSFORHOLD1).getUttaksperioder();
        assertThat(perioder).hasSize(2);

        var periode0 = perioder.get(0);
        assertThat(periode0.getFom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 1));
        assertThat(periode0.getTom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 4));
        assertThat(periode0.getUtbetalingsgrad()).isEqualTo(new BigDecimal("90.00"));

        var periode1 = perioder.get(1);
        assertThat(periode1.getFom()).isEqualTo(LocalDate.of(2019, Month.JANUARY, 7));
        assertThat(periode1.getTom()).isEqualTo(LocalDate.of(2019, Month.APRIL, 9));
        assertThat(periode1.getUtbetalingsgrad()).isEqualTo(new BigDecimal("70.00"));
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
        var tidligereSøknader = new ArrayList<Søknad>();

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, tidligereSøknader, avklarteDatoer);

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
        var tidligereSøknader = new ArrayList<Søknad>();

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, tidligereSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
        assertThat(perioder).hasSize(2);

        var periode0 = perioder.get(0);
        assertThat(periode0.getFom()).isEqualTo(TILRETTELEGGING_BEHOV_DATO);
        assertThat(periode0.getTom()).isEqualTo(fødseldatoTidligFødsel.minusDays(1));
        assertThat(periode0.getUtbetalingsgrad()).isEqualTo(FULL_UTBETALINGSGRAD);
        assertThat(periode0.getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
        assertThat(periode0.getÅrsak()).isEqualTo(PeriodeOppfyltÅrsak.UTTAK_ER_INNVILGET);

        var periode1 = perioder.get(1);
        assertThat(periode1.getFom()).isEqualTo(fødseldatoTidligFødsel);
        assertThat(periode1.getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(periode1.getUtbetalingsgrad()).isEqualTo(BigDecimal.ZERO);
        assertThat(periode1.getUtfallType()).isEqualTo(UtfallType.IKKE_OPPFYLT);
        assertThat(periode1.getÅrsak()).isEqualTo(PeriodeIkkeOppfyltÅrsak.PERIODEN_ER_IKKE_FØR_FØDSEL);
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
        var tidligereSøknader = new ArrayList<Søknad>();

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, tidligereSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();
        assertThat(perioder).hasSize(2);

        var periode0 = perioder.get(0);
        assertThat(periode0.getFom()).isEqualTo(TILRETTELEGGING_BEHOV_DATO);
        assertThat(periode0.getTom()).isEqualTo(førsteLovligeUttaksdato.minusDays(1));
        assertThat(periode0.getUtbetalingsgrad()).isEqualTo(BigDecimal.ZERO);
        assertThat(periode0.getUtfallType()).isEqualTo(UtfallType.IKKE_OPPFYLT);
        assertThat(periode0.getÅrsak()).isEqualTo(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT);

        var periode1 = perioder.get(1);
        assertThat(periode1.getFom()).isEqualTo(førsteLovligeUttaksdato);
        assertThat(periode1.getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(periode1.getUtbetalingsgrad()).isEqualTo(FULL_UTBETALINGSGRAD);
        assertThat(periode1.getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
        assertThat(periode1.getÅrsak()).isEqualTo(PeriodeOppfyltÅrsak.UTTAK_ER_INNVILGET);
    }


    @Test
    public void ferie_skal_avslås() {
        var avklarteDatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(FØRSTE_LOVLIGE_UTTAKSDATO)
            .medTermindato(TERMINDATO)
            .medFerie(new Ferie(TILRETTELEGGING_BEHOV_DATO.plusWeeks(1), TILRETTELEGGING_BEHOV_DATO.plusWeeks(2).minusDays(1)))
            .build();


        var nyeSøknader = List.of(new Søknad(ARBEIDSFORHOLD1, ARBEIDSFORHOLD1_PROSENT, TERMINDATO, TILRETTELEGGING_BEHOV_DATO,
            List.of(new IngenTilrettelegging(TILRETTELEGGING_BEHOV_DATO))));
        var tidligereSøknader = new ArrayList<Søknad>();

        var uttaksperioder = fastsettPerioderTjeneste.fastsettePerioder(nyeSøknader, tidligereSøknader, avklarteDatoer);

        assertThat(uttaksperioder.alleArbeidsforhold()).hasSize(1);
        var perioder = uttaksperioder.perioder(uttaksperioder.alleArbeidsforhold().iterator().next()).getUttaksperioder();

        assertThat(perioder).hasSize(3);

        var periode0 = perioder.get(0);
        assertThat(periode0.getFom()).isEqualTo(TILRETTELEGGING_BEHOV_DATO);
        assertThat(periode0.getTom()).isEqualTo(TILRETTELEGGING_BEHOV_DATO.plusWeeks(1).minusDays(1));
        assertThat(periode0.getUtbetalingsgrad()).isEqualTo(FULL_UTBETALINGSGRAD);
        assertThat(periode0.getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
        assertThat(periode0.getÅrsak()).isEqualTo(PeriodeOppfyltÅrsak.UTTAK_ER_INNVILGET);
        assertThat(periode0.getRegelInput()).isNotEmpty();
        assertThat(periode0.getRegelSporing()).isNotEmpty();

        var periode1 = perioder.get(1);
        assertThat(periode1.getFom()).isEqualTo(TILRETTELEGGING_BEHOV_DATO.plusWeeks(1));
        assertThat(periode1.getTom()).isEqualTo(TILRETTELEGGING_BEHOV_DATO.plusWeeks(2).minusDays(1));
        assertThat(periode1.getUtbetalingsgrad()).isEqualTo(BigDecimal.ZERO);
        assertThat(periode1.getUtfallType()).isEqualTo(UtfallType.IKKE_OPPFYLT);
        assertThat(periode1.getÅrsak()).isEqualTo(PeriodeIkkeOppfyltÅrsak.PERIODEN_ER_SAMTIDIG_SOM_EN_FERIE);
        assertThat(periode1.getRegelInput()).isNotEmpty();
        assertThat(periode1.getRegelSporing()).isNotEmpty();

        var periode2 = perioder.get(2);
        assertThat(periode2.getFom()).isEqualTo(TILRETTELEGGING_BEHOV_DATO.plusWeeks(2));
        assertThat(periode2.getTom()).isEqualTo(TERMINDATO.minusWeeks(3).minusDays(1));
        assertThat(periode2.getUtbetalingsgrad()).isEqualTo(FULL_UTBETALINGSGRAD);
        assertThat(periode2.getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
        assertThat(periode2.getÅrsak()).isEqualTo(PeriodeOppfyltÅrsak.UTTAK_ER_INNVILGET);
        assertThat(periode2.getRegelInput()).isNotEmpty();
        assertThat(periode2.getRegelSporing()).isNotEmpty();
    }

}
