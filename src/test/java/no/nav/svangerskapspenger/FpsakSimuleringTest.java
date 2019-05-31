package no.nav.svangerskapspenger;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import no.nav.svangerskapspenger.domene.felles.Arbeidsforhold;
import no.nav.svangerskapspenger.domene.resultat.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperioder;
import no.nav.svangerskapspenger.domene.søknad.AvklarteDatoer;
import no.nav.svangerskapspenger.domene.søknad.IngenTilretteligging;
import no.nav.svangerskapspenger.domene.søknad.Søknad;
import no.nav.svangerskapspenger.tjeneste.fastsettuttak.FastsettPerioderTjeneste;
import no.nav.svangerskapspenger.tjeneste.opprettperioder.ManuellBehandling;
import no.nav.svangerskapspenger.tjeneste.opprettperioder.UttaksperioderTjeneste;
import no.nav.svangerskapspenger.tjeneste.opprettperioder.UttaksperioderTjenesteV2;

class Resultat {
    private Set<ManuellBehandling> manuellBehandlingSet = new HashSet<>();
    private Uttaksperioder uttaksperioder;

    public Resultat(Set<ManuellBehandling> manuellBehandlingSet) {
        this.manuellBehandlingSet = manuellBehandlingSet;
    }

    public Resultat(Uttaksperioder uttaksperioder) {
        this.uttaksperioder = uttaksperioder;
    }

    public Set<ManuellBehandling> getManuellBehandlingSet() {
        return manuellBehandlingSet;
    }

    public Uttaksperioder getUttaksperioder() {
        return uttaksperioder;
    }
}

/**
 * Tester uttakstjenester på samme måte som de brukes i Fpsak.
 */
public class FpsakSimuleringTest {

    private static final Arbeidsforhold ARBEIDSFORHOLD1 = Arbeidsforhold.virksomhet("123", "456");

    private UttaksperioderTjeneste uttaksperioderTjeneste = new UttaksperioderTjenesteV2();
    private FastsettPerioderTjeneste fastsettPerioderTjeneste = new FastsettPerioderTjeneste();

    @Test
    public void fullt_uttak_ved_ingen_tilpasning_fra_første_dag() {
        var termindato = LocalDate.of(2019, Month.MAY, 1);
        var tilretteleggingsbehovdato = LocalDate.of(2019, Month.JANUARY, 1);
        var ingenTilrettelegging = new IngenTilretteligging(tilretteleggingsbehovdato);
        var søknad = new Søknad(ARBEIDSFORHOLD1, termindato, tilretteleggingsbehovdato, List.of(ingenTilrettelegging));
        var avklartedatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(tilretteleggingsbehovdato)
            .medTermindato(termindato)
            .medTilretteleggingBehovDato(ARBEIDSFORHOLD1, tilretteleggingsbehovdato)
            .build();

        var resultat = opprettOgFastsettPerioder(søknad, avklartedatoer);

        assertThat(resultat.getManuellBehandlingSet()).isEmpty();
        var uttaksresultat = resultat.getUttaksperioder();
        assertThat(uttaksresultat.alleArbeidsforhold()).hasSize(1);
        var uttakPerArbeidsforhold = uttaksresultat.perioder(ARBEIDSFORHOLD1);
        assertThat(uttakPerArbeidsforhold).isNotNull();
        assertThat(uttakPerArbeidsforhold.getArbeidsforholdIkkeOppfyltÅrsak()).isNull();
        var perioder = uttakPerArbeidsforhold.getUttaksperioder();
        assertThat(perioder).hasSize(1);
        assertThat(perioder.get(0).getFom()).isEqualTo(tilretteleggingsbehovdato);
        assertThat(perioder.get(0).getTom()).isEqualTo(termindato.minusWeeks(3).minusDays(1));
    }

    @Test
    public void kun_uttak_i_helg_gir_ikke_oppfylt_på_arbeidsforhold() {
        var termindato = LocalDate.of(2019, Month.MAY, 6); //mandag
        var tilretteleggingsbehovdato = LocalDate.of(2019, Month.APRIL, 13); //lørdag
        var ingenTilrettelegging = new IngenTilretteligging(tilretteleggingsbehovdato);
        var søknad = new Søknad(ARBEIDSFORHOLD1, termindato, tilretteleggingsbehovdato, List.of(ingenTilrettelegging));
        var avklartedatoer = new AvklarteDatoer.Builder()
            .medFørsteLovligeUttaksdato(tilretteleggingsbehovdato)
            .medTermindato(termindato)
            .medTilretteleggingBehovDato(ARBEIDSFORHOLD1, tilretteleggingsbehovdato)
            .build();

        var resultat = opprettOgFastsettPerioder(søknad, avklartedatoer);

        assertThat(resultat.getManuellBehandlingSet()).isEmpty();
        var uttaksresultat = resultat.getUttaksperioder();
        assertThat(uttaksresultat.alleArbeidsforhold()).hasSize(1);
        var uttakPerArbeidsforhold = uttaksresultat.perioder(ARBEIDSFORHOLD1);
        assertThat(uttakPerArbeidsforhold).isNotNull();
        assertThat(uttakPerArbeidsforhold.getArbeidsforholdIkkeOppfyltÅrsak()).isEqualTo(ArbeidsforholdIkkeOppfyltÅrsak.UTTAK_KUN_PÅ_HELG);
        var perioder = uttakPerArbeidsforhold.getUttaksperioder();
        assertThat(perioder).hasSize(0);
    }


    private Resultat opprettOgFastsettPerioder(Søknad søknad, AvklarteDatoer avklarteDatoer) {
        return opprettOgFastsettPerioder(List.of(søknad), avklarteDatoer);
    }

    private Resultat opprettOgFastsettPerioder(List<Søknad> søknader, AvklarteDatoer avklarteDatoer) {
        var uttaksperioder = new Uttaksperioder();
        var manuellBehandlingSet = uttaksperioderTjeneste.opprett(søknader, uttaksperioder);
        if (!manuellBehandlingSet.isEmpty()) {
            return new Resultat(manuellBehandlingSet);
        }
        fastsettPerioderTjeneste.fastsettePerioder(avklarteDatoer, uttaksperioder);
        return new Resultat(uttaksperioder);
    }

}
