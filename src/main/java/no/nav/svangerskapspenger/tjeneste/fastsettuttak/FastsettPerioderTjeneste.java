package no.nav.svangerskapspenger.tjeneste.fastsettuttak;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.core.JsonProcessingException;

import no.nav.svangerskapspenger.domene.felles.Arbeidsforhold;
import no.nav.svangerskapspenger.domene.resultat.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.svangerskapspenger.regler.fastsettperiode.FastsettePeriodeRegel;
import no.nav.svangerskapspenger.regler.fastsettperiode.Regelresultat;
import no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag.FastsettePeriodeGrunnlag;
import no.nav.svangerskapspenger.domene.søknad.AvklarteDatoer;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperiode;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperioder;
import no.nav.svangerskapspenger.tjeneste.fastsettuttak.feil.UttakRegelFeil;
import no.nav.svangerskapspenger.tjeneste.fastsettuttak.jackson.JacksonJsonConfig;
import no.nav.fpsak.nare.evaluation.summary.EvaluationSerializer;

public class FastsettPerioderTjeneste {

    private final JacksonJsonConfig jacksonJsonConfig = new JacksonJsonConfig();

    public void fastsettePerioder(AvklarteDatoer avklarteDatoer, Uttaksperioder uttaksperioder) {
        //Først knekk opp perioder på alle potensielle knekkpunkter
        var knekkpunkter = finnKnekkpunkter(avklarteDatoer);
        uttaksperioder.knekk(knekkpunkter);

        //Fastsett perioder
        uttaksperioder.alleArbeidsforhold().forEach(arbeidsforhold -> {
            if (erTilretteleggingBehovDatoFørTreUkerTermindato(avklarteDatoer, arbeidsforhold)) {
                fastsettPerioder(avklarteDatoer, uttaksperioder, arbeidsforhold);
            } else {
                uttaksperioder.avslåForArbeidsforhold(arbeidsforhold, ArbeidsforholdIkkeOppfyltÅrsak.LEGES_DATO_IKKE_FØR_TRE_UKER_FØR_TERMINDATO);
            }
        });
    }

    private void fastsettPerioder(AvklarteDatoer avklarteDatoer, Uttaksperioder uttaksperioder, Arbeidsforhold arbeidsforhold) {
        FastsettePeriodeRegel regel = new FastsettePeriodeRegel();
        var uttaksperioderPerArbeidsforhold = uttaksperioder.perioder(arbeidsforhold);
        uttaksperioderPerArbeidsforhold.getUttaksperioder().forEach(periode -> fastsettPeriode(regel, avklarteDatoer, periode));
        if (uttaksperioderPerArbeidsforhold.getUttaksperioder().isEmpty()) {
            uttaksperioderPerArbeidsforhold.avslå(ArbeidsforholdIkkeOppfyltÅrsak.UTTAK_KUN_PÅ_HELG);
        }
    }

    private boolean erTilretteleggingBehovDatoFørTreUkerTermindato(AvklarteDatoer avklarteDatoer, Arbeidsforhold arbeidsforhold) {
        var tilretteleggingBehovDato = avklarteDatoer.getTilretteleggingBehovDatoer().get(arbeidsforhold);
        if (tilretteleggingBehovDato != null) {
            return tilretteleggingBehovDato.isBefore(avklarteDatoer.getTerminsdato().minusWeeks(3));
        }
        return true;
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
        avklarteDatoer.getFørsteLovligeUttaksdag().ifPresent(knekkpunkter::add);
        knekkpunkter.add(avklarteDatoer.getTerminsdato().minusWeeks(3));
        avklarteDatoer.getFødselsdato().ifPresent(knekkpunkter::add);
        avklarteDatoer.getBrukersDødsdato().ifPresent(knekkpunkter::add);
        avklarteDatoer.getBarnetsDødsdato().ifPresent(knekkpunkter::add);
        avklarteDatoer.getFerier().forEach(ferie -> {
            knekkpunkter.add(ferie.getFom());
            knekkpunkter.add(ferie.getTom().plusDays(1));
        });

        return knekkpunkter;
    }

}
