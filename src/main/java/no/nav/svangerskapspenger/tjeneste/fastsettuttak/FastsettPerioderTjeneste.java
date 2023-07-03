package no.nav.svangerskapspenger.tjeneste.fastsettuttak;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import no.nav.fpsak.nare.evaluation.summary.EvaluationSerializer;
import no.nav.fpsak.nare.evaluation.summary.EvaluationSummary;
import no.nav.fpsak.nare.evaluation.summary.EvaluationVersion;
import no.nav.fpsak.nare.evaluation.summary.NareVersion;
import no.nav.fpsak.nare.json.JsonOutput;
import no.nav.fpsak.nare.json.NareJsonException;
import no.nav.svangerskapspenger.domene.felles.Arbeidsforhold;
import no.nav.svangerskapspenger.domene.resultat.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperiode;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperioder;
import no.nav.svangerskapspenger.domene.resultat.UttaksperioderPerArbeidsforhold;
import no.nav.svangerskapspenger.domene.søknad.AvklarteDatoer;
import no.nav.svangerskapspenger.domene.søknad.Opphold;
import no.nav.svangerskapspenger.domene.søknad.Søknad;
import no.nav.svangerskapspenger.regler.fastsettperiode.FastsettePeriodeRegel;
import no.nav.svangerskapspenger.regler.fastsettperiode.PeriodeOutcome;
import no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag.FastsettePeriodeGrunnlag;
import no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag.Inngangsvilkår;
import no.nav.svangerskapspenger.tjeneste.fastsettuttak.feil.UttakRegelFeil;
import no.nav.svangerskapspenger.tjeneste.opprettperioder.UttaksperioderTjeneste;

public class FastsettPerioderTjeneste {

    private static final EvaluationVersion REGEL_VERSION = NareVersion.readVersionPropertyFor("svp-uttak", "nare/svp-uttak-version.properties");

    private UttaksperioderTjeneste uttaksperioderTjeneste = new UttaksperioderTjeneste();
    private UttakHullUtleder uttakHullUtleder = new UttakHullUtleder();

    public Uttaksperioder fastsettePerioder(List<Søknad> nyeSøknader, AvklarteDatoer avklarteDatoer, Inngangsvilkår inngangsvilkår,
                                            Map<Arbeidsforhold, List<Opphold>> oppholdPerArbeidsforholdMap) {
        var nyeUttaksperioder = uttaksperioderTjeneste.opprett(nyeSøknader);

        var oppholdListe = oppholdPerArbeidsforholdMap.values().stream().flatMap(Collection::stream).toList();

        var eventueltStartHullUttak = uttakHullUtleder.finnStartHull(nyeUttaksperioder, oppholdListe);
        eventueltStartHullUttak.ifPresent(avklarteDatoer::setStartOppholdUttak);

        if (!nyeSøknader.isEmpty()) {
            //Kjør reglene bare dersom det er søkt om noe nytt
            fastsettePerioder(avklarteDatoer, nyeUttaksperioder, inngangsvilkår, oppholdPerArbeidsforholdMap);
        }
        return nyeUttaksperioder;
    }



    private void fastsettePerioder(AvklarteDatoer avklarteDatoer, Uttaksperioder uttaksperioder, Inngangsvilkår inngangsvilkår,
                                   Map<Arbeidsforhold, List<Opphold>> oppholdPerArbeidsforholdMap) {
        //Først knekk opp perioder for potensielle knekkpunkter som gjelder alle arbeidsforhold
        var knekkpunkter = finnKnekkpunkter(avklarteDatoer);
        uttaksperioder.knekk(knekkpunkter);

        //Fastsett perioder
        uttaksperioder.alleArbeidsforhold().forEach(arbeidsforhold -> {
            var uttaksperioderPerArbeidsforhold = uttaksperioder.perioder(arbeidsforhold);
            if (uttaksperioderPerArbeidsforhold.getArbeidsforholdIkkeOppfyltÅrsak() == null) {
                //Knekker opp perioder for et eventuelt opphold per arbeidsforhold
                var oppholdPerArbeidsforhold = oppholdPerArbeidsforholdMap.getOrDefault(arbeidsforhold, Collections.emptyList());
                var knekkpunkterOpphold = finnKnekkpunkterOpphold(oppholdPerArbeidsforhold);
                uttaksperioderPerArbeidsforhold.knekk(knekkpunkterOpphold);

                fastsettPerioder(avklarteDatoer, uttaksperioderPerArbeidsforhold, inngangsvilkår, oppholdPerArbeidsforhold);
            }
        });
    }

    private void fastsettPerioder(AvklarteDatoer avklarteDatoer, UttaksperioderPerArbeidsforhold uttaksperioderPerArbeidsforhold, Inngangsvilkår inngangsvilkår,
                                  List<Opphold> oppholdPerArbeidsforhold) {
        FastsettePeriodeRegel regel = new FastsettePeriodeRegel();
        uttaksperioderPerArbeidsforhold.getUttaksperioder().forEach(periode -> fastsettPeriode(regel, avklarteDatoer, periode, inngangsvilkår, oppholdPerArbeidsforhold));
        if (uttaksperioderPerArbeidsforhold.getUttaksperioder().isEmpty()) {
            uttaksperioderPerArbeidsforhold.avslå(ArbeidsforholdIkkeOppfyltÅrsak.UTTAK_KUN_PÅ_HELG);
        }
    }

    private void fastsettPeriode(FastsettePeriodeRegel regel, AvklarteDatoer avklarteDatoer, Uttaksperiode periode, Inngangsvilkår inngangsvilkår,
                                 List<Opphold> oppholdPerArbeidsforhold) {
        var grunnlag = new FastsettePeriodeGrunnlag(avklarteDatoer, periode, inngangsvilkår, oppholdPerArbeidsforhold);
        var evaluering = regel.evaluer(grunnlag);
        var inputJson = toJson(grunnlag);
        var regelJson = EvaluationSerializer.asJson(evaluering, REGEL_VERSION, NareVersion.NARE_VERSION);
        var regelresultat = new EvaluationSummary(evaluering).allOutcomes().stream()
            .filter(PeriodeOutcome.class::isInstance)
            .findFirst().map(PeriodeOutcome.class::cast).orElseThrow();
        var utfallType = regelresultat.utfallType();
        var årsak = regelresultat.periodeÅrsak();

        switch (utfallType) {
            case IKKE_OPPFYLT -> periode.avslå(årsak, inputJson, regelJson);
            case OPPFYLT -> periode.innvilg(årsak, inputJson, regelJson);
            default -> throw new UnsupportedOperationException(String.format("Ukjent utfalltype: %s", utfallType.name()));
        }
    }

    private String toJson(FastsettePeriodeGrunnlag grunnlag) {
        try {
            return JsonOutput.asJson(grunnlag);
        } catch (NareJsonException e) {
            throw new UttakRegelFeil("Kunne ikke serialisere regelinput for avklaring av uttaksperioder.", e);
        }
    }

    private Set<LocalDate> finnKnekkpunkter(AvklarteDatoer avklarteDatoer) {
        var knekkpunkter = new TreeSet<LocalDate>();

        avklarteDatoer.getOpphørsdatoForMedlemskap().ifPresent(knekkpunkter::add);
        avklarteDatoer.getFørsteLovligeUttaksdato().ifPresent(knekkpunkter::add);
        knekkpunkter.add(avklarteDatoer.getTerminsdato().minusWeeks(3));
        avklarteDatoer.getStartdatoNesteSak().ifPresent(knekkpunkter::add);
        avklarteDatoer.getFødselsdato().ifPresent(knekkpunkter::add);
        avklarteDatoer.getBrukersDødsdato().ifPresent(knekkpunkter::add);
        avklarteDatoer.getBarnetsDødsdato().ifPresent(knekkpunkter::add);
        avklarteDatoer.getStartOppholdUttak().ifPresent(knekkpunkter::add);
        return knekkpunkter;
    }

    private Set<LocalDate> finnKnekkpunkterOpphold(List<Opphold> oppholdListe) {
        var knekkpunkterOpphold = new TreeSet<LocalDate>();
        oppholdListe.forEach(opphold -> {
            knekkpunkterOpphold.add(opphold.getFom());
            knekkpunkterOpphold.add(opphold.getTom().plusDays(1));
        });
        return knekkpunkterOpphold;
    }

}
