package no.nav.svangerskapspenger.tjeneste.fastsettuttak;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.core.JsonProcessingException;

import no.nav.svangerskapspenger.domene.resultat.ArbeidsforholdAvslåttÅrsak;
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
        if (erLegesDatoErFørTreUkerTermindato(avklarteDatoer)) {
            FastsettePeriodeRegel regel = new FastsettePeriodeRegel();
            uttaksperioder.alleArbeidsforhold().forEach(arbeidsforhold -> uttaksperioder.perioder(arbeidsforhold).getUttaksperioder().forEach(periode -> fastsettPeriode(regel, avklarteDatoer, periode)));
        } else {
            //avslå for alle arbeidsforhold
            uttaksperioder.alleArbeidsforhold().forEach(arbeidsforhold -> {
                uttaksperioder.avslåForArbeidsforhold(arbeidsforhold, ArbeidsforholdAvslåttÅrsak.LEGES_DATO_IKKE_FØR_TRE_UKER_FØR_TERMINDATO);
            });
        }
    }

    private boolean erLegesDatoErFørTreUkerTermindato(AvklarteDatoer avklarteDatoer) {
        return avklarteDatoer.getLegesDato().isBefore(avklarteDatoer.getTerminsdato().minusWeeks(3));
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
            case AVSLÅTT:
                periode.avslå(årsak, inputJson, regelJson);
                break;
            case INNVILGET:
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
        knekkpunkter.add(avklarteDatoer.getFørsteLovligeUttaksdag());
        knekkpunkter.add(avklarteDatoer.getTerminsdato().minusWeeks(3));
        avklarteDatoer.getFødselsdato().ifPresent(knekkpunkter::add);
        avklarteDatoer.getBrukersDødsdato().ifPresent(knekkpunkter::add);
        avklarteDatoer.getBarnetsDødsdato().ifPresent(knekkpunkter::add);

        return knekkpunkter;
    }



}
