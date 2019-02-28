package no.nav.svangerskapspenger.domene.resultat;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.nav.svangerskapspenger.domene.felles.Arbeidsforhold;

public class Uttaksperioder {

    private final Map<Arbeidsforhold, UttaksperioderPerArbeidsforhold> perioderPerArbeidsforhold;

    public Uttaksperioder() {
        this.perioderPerArbeidsforhold = new HashMap<>();
    }

    public void leggTilPerioder(Arbeidsforhold arbeidsforhold, Uttaksperiode... perioder) {
        perioderPerArbeidsforhold.put(arbeidsforhold, new UttaksperioderPerArbeidsforhold(List.of(perioder)));
    }

    public Set<Arbeidsforhold> alleArbeidsforhold() {
        return perioderPerArbeidsforhold.keySet();
    }

    public List<Uttaksperiode> perioder(Arbeidsforhold arbeidsforhold) {
        var perioder = perioderPerArbeidsforhold.get(arbeidsforhold);
        if (perioder == null) {
            return List.of();
        }
        return perioderPerArbeidsforhold.get(arbeidsforhold).getUttaksperioder();
    }

    public void knekk(Set<LocalDate> knekkpunkter) {
        alleArbeidsforhold().forEach(arbeidsforhold -> perioderPerArbeidsforhold.get(arbeidsforhold).knekk(knekkpunkter));
    }


}
