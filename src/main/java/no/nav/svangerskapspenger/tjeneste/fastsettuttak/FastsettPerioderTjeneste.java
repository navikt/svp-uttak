package no.nav.svangerskapspenger.tjeneste.fastsettuttak;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.core.JsonProcessingException;

import no.nav.svangerskapspenger.domene.resultat.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.svangerskapspenger.domene.resultat.UttaksperioderPerArbeidsforhold;
import no.nav.svangerskapspenger.domene.søknad.Søknad;
import no.nav.svangerskapspenger.regler.fastsettperiode.FastsettePeriodeRegel;
import no.nav.svangerskapspenger.regler.fastsettperiode.Regelresultat;
import no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag.FastsettePeriodeGrunnlag;
import no.nav.svangerskapspenger.domene.søknad.AvklarteDatoer;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperiode;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperioder;
import no.nav.svangerskapspenger.tjeneste.fastsettuttak.feil.UttakRegelFeil;
import no.nav.svangerskapspenger.tjeneste.fastsettuttak.jackson.JacksonJsonConfig;
import no.nav.fpsak.nare.evaluation.summary.EvaluationSerializer;
import no.nav.svangerskapspenger.tjeneste.opprettperioder.UttaksperioderTjeneste;

public class FastsettPerioderTjeneste {

    private final JacksonJsonConfig jacksonJsonConfig = new JacksonJsonConfig();

    private UttaksperioderTjeneste uttaksperioderTjeneste = new UttaksperioderTjeneste();
    private UttaksresultatMerger uttaksresultatMerger = new UttaksresultatMerger();
    private UttakHullUtleder uttakHullUtleder = new UttakHullUtleder();

    public Uttaksperioder fastsettePerioder(List<Søknad> nyeSøknader, List<Søknad> tidligereSøknader, AvklarteDatoer avklarteDatoer) {
        var nyeUttaksperioder = new Uttaksperioder();
        uttaksperioderTjeneste.opprett(nyeSøknader, nyeUttaksperioder);
        Optional<Uttaksperioder> tidligereUttaksperioder = Optional.empty();
        if (!tidligereSøknader.isEmpty()) {
            var perioder = new Uttaksperioder();
            uttaksperioderTjeneste.opprett(tidligereSøknader, perioder);
            tidligereUttaksperioder = Optional.of(perioder);
        }

        var eventueltStartHullUttak = uttakHullUtleder.finnStartHull(tidligereUttaksperioder, nyeUttaksperioder, avklarteDatoer.getFerier());
        eventueltStartHullUttak.ifPresent(startHullUttak -> avklarteDatoer.setStartOppholdUttak(startHullUttak));

        tidligereUttaksperioder.ifPresent(perioder -> fastsettePerioder(avklarteDatoer, perioder));
        if (!nyeSøknader.isEmpty()) {
            //Kjør reglene bare dersom det er søkt om noe nytt
            fastsettePerioder(avklarteDatoer, nyeUttaksperioder);
        }
        return uttaksresultatMerger.merge(tidligereUttaksperioder, nyeUttaksperioder);
    }



    private void fastsettePerioder(AvklarteDatoer avklarteDatoer, Uttaksperioder uttaksperioder) {
        //Først knekk opp perioder på alle potensielle knekkpunkter
        var knekkpunkter = finnKnekkpunkter(avklarteDatoer);
        uttaksperioder.knekk(knekkpunkter);

        //Fastsett perioder
        uttaksperioder.alleArbeidsforhold().forEach(arbeidsforhold -> {
            var uttaksperioderPerArbeidsforhold = uttaksperioder.perioder(arbeidsforhold);
            if (uttaksperioderPerArbeidsforhold.getArbeidsforholdIkkeOppfyltÅrsak() == null) {
                fastsettPerioder(avklarteDatoer, uttaksperioderPerArbeidsforhold);
            }
        });
    }

    private void fastsettPerioder(AvklarteDatoer avklarteDatoer, UttaksperioderPerArbeidsforhold uttaksperioderPerArbeidsforhold) {
        FastsettePeriodeRegel regel = new FastsettePeriodeRegel();
        uttaksperioderPerArbeidsforhold.getUttaksperioder().forEach(periode -> fastsettPeriode(regel, avklarteDatoer, periode));
        if (uttaksperioderPerArbeidsforhold.getUttaksperioder().isEmpty()) {
            uttaksperioderPerArbeidsforhold.avslå(ArbeidsforholdIkkeOppfyltÅrsak.UTTAK_KUN_PÅ_HELG);
        }
    }

    private void fastsettPeriode(FastsettePeriodeRegel regel, AvklarteDatoer avklarteDatoer, Uttaksperiode periode) {
        var grunnlag = new FastsettePeriodeGrunnlag(avklarteDatoer, periode);
        var evaluering = regel.evaluer(grunnlag);
        var inputJson = toJson(grunnlag);
        var regelJson = EvaluationSerializer.asJson(evaluering);
        var regelresultat = new Regelresultat(evaluering);
        var utfallType = regelresultat.getUtfallType();
        var årsak = regelresultat.getPeriodeÅrsak();

        switch (utfallType) {
            case IKKE_OPPFYLT:
                periode.avslå(årsak, inputJson, regelJson);
                break;
            case OPPFYLT:
                periode.innvilg(årsak, inputJson, regelJson);
                break;
            default:
                throw new UnsupportedOperationException(String.format("Ukjent utfalltype: %s", utfallType.name()));
        }
    }

    private String toJson(FastsettePeriodeGrunnlag grunnlag) {
        try {
            return jacksonJsonConfig.toJson(grunnlag);
        } catch (JsonProcessingException e) {
            throw new UttakRegelFeil("Kunne ikke serialisere regelinput for avklaring av uttaksperioder.", e);
        }
    }

    private Set<LocalDate> finnKnekkpunkter(AvklarteDatoer avklarteDatoer) {
        var knekkpunkter = new TreeSet<LocalDate>();

        avklarteDatoer.getOpphørsdatoForMedlemskap().ifPresent(knekkpunkter::add);
        avklarteDatoer.getFørsteLovligeUttaksdato().ifPresent(knekkpunkter::add);
        knekkpunkter.add(avklarteDatoer.getTerminsdato().minusWeeks(3));
        avklarteDatoer.getFødselsdato().ifPresent(knekkpunkter::add);
        avklarteDatoer.getBrukersDødsdato().ifPresent(knekkpunkter::add);
        avklarteDatoer.getBarnetsDødsdato().ifPresent(knekkpunkter::add);
        avklarteDatoer.getStartOppholdUttak().ifPresent(knekkpunkter::add);
        avklarteDatoer.getFerier().forEach(ferie -> {
            knekkpunkter.add(ferie.getFom());
            knekkpunkter.add(ferie.getTom().plusDays(1));
        });

        return knekkpunkter;
    }

}
