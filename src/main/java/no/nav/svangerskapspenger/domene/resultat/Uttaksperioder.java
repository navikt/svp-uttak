package no.nav.svangerskapspenger.domene.resultat;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.nav.svangerskapspenger.domene.felles.Arbeidsforhold;

public class Uttaksperioder {

    private final Map<Arbeidsforhold, UttaksperioderPerArbeidsforhold> perioderPerArbeidsforholdMap;

    public Uttaksperioder() {
        this.perioderPerArbeidsforholdMap = new HashMap<>();
    }

    public void leggTilPerioder(Arbeidsforhold arbeidsforhold, Uttaksperiode... perioder) {
        perioderPerArbeidsforholdMap.put(arbeidsforhold, new UttaksperioderPerArbeidsforhold(List.of(perioder)));
    }

    public Set<Arbeidsforhold> alleArbeidsforhold() {
        return perioderPerArbeidsforholdMap.keySet();
    }

    public UttaksperioderPerArbeidsforhold perioder(Arbeidsforhold arbeidsforhold) {
        return perioderPerArbeidsforholdMap.get(arbeidsforhold);
    }

    public void avslåForArbeidsforhold(Arbeidsforhold arbeidsforhold, ArbeidsforholdAvslåttÅrsak arbeidsforholdAvslåttÅrsak) {
        var perioderPerArbeidsforhold = perioderPerArbeidsforholdMap.get(arbeidsforhold);
        perioderPerArbeidsforhold.avslå(arbeidsforholdAvslåttÅrsak);
    }


    public void knekk(Set<LocalDate> knekkpunkter) {
        alleArbeidsforhold().forEach(arbeidsforhold -> perioderPerArbeidsforholdMap.get(arbeidsforhold).knekk(knekkpunkter));
    }


}
