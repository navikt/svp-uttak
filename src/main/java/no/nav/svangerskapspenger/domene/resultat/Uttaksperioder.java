package no.nav.svangerskapspenger.domene.resultat;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import no.nav.svangerskapspenger.domene.felles.Arbeidsforhold;
import no.nav.svangerskapspenger.domene.felles.LukketPeriode;

public class Uttaksperioder {

    private final Map<Arbeidsforhold, UttaksperioderPerArbeidsforhold> perioderPerArbeidsforholdMap;

    public Uttaksperioder() {
        this.perioderPerArbeidsforholdMap = new HashMap<>();
    }

    public void leggTilPerioder(Arbeidsforhold arbeidsforhold, Uttaksperiode... perioder) {
        var uttaksperioderPerArbeidsforhold = perioderPerArbeidsforholdMap.get(arbeidsforhold);
        if (uttaksperioderPerArbeidsforhold == null) {
            perioderPerArbeidsforholdMap.put(arbeidsforhold, new UttaksperioderPerArbeidsforhold(List.of(perioder)));
            return;
        }
        uttaksperioderPerArbeidsforhold.leggTilPerioder(perioder);
    }

    public Set<Arbeidsforhold> alleArbeidsforhold() {
        return perioderPerArbeidsforholdMap.keySet();
    }

    public UttaksperioderPerArbeidsforhold perioder(Arbeidsforhold arbeidsforhold) {
        return perioderPerArbeidsforholdMap.get(arbeidsforhold);
    }

    public void avslåForArbeidsforhold(Arbeidsforhold arbeidsforhold, ArbeidsforholdIkkeOppfyltÅrsak arbeidsforholdIkkeOppfyltÅrsak) {
        var perioderPerArbeidsforhold = perioderPerArbeidsforholdMap.get(arbeidsforhold);
        if (perioderPerArbeidsforhold == null) {
            perioderPerArbeidsforhold = new UttaksperioderPerArbeidsforhold(List.of());
            perioderPerArbeidsforholdMap.put(arbeidsforhold, perioderPerArbeidsforhold);
        }
        perioderPerArbeidsforhold.avslå(arbeidsforholdIkkeOppfyltÅrsak);
    }


    public void knekk(Set<LocalDate> knekkpunkter) {
        alleArbeidsforhold().forEach(arbeidsforhold -> perioderPerArbeidsforholdMap.get(arbeidsforhold).knekk(knekkpunkter));
    }

    Optional<LocalDate> finnSisteUttaksdato() {
        Optional<LocalDate> sisteUttaksdato = Optional.empty();
        for (var entry: perioderPerArbeidsforholdMap.entrySet()) {
            var maxDatoForArbeidsforhold = entry.getValue().getUttaksperioder().stream().map(LukketPeriode::getTom).max(LocalDate::compareTo);
            if (!sisteUttaksdato.isPresent() || (maxDatoForArbeidsforhold.isPresent() && maxDatoForArbeidsforhold.get().isAfter(sisteUttaksdato.get()))) {
                sisteUttaksdato = maxDatoForArbeidsforhold;
            }
        }
        return sisteUttaksdato;
    }

    public Optional<LocalDate> finnFørsteUttaksdato() {
        Optional<LocalDate> førsteUttaksdato = Optional.empty();
        for (var entry: perioderPerArbeidsforholdMap.entrySet()) {
            var minDatoForArbeidsforhold = entry.getValue().getUttaksperioder().stream().map(LukketPeriode::getFom).min(LocalDate::compareTo);
            if (!førsteUttaksdato.isPresent() || (minDatoForArbeidsforhold.isPresent() && minDatoForArbeidsforhold.get().isBefore(førsteUttaksdato.get()))) {
                førsteUttaksdato = minDatoForArbeidsforhold;
            }
        }
        return førsteUttaksdato;
    }

}
