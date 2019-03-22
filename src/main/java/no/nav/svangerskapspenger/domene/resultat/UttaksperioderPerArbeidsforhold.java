package no.nav.svangerskapspenger.domene.resultat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.svangerskapspenger.utils.Virkedager;

public class UttaksperioderPerArbeidsforhold {

    private ArbeidsforholdIkkeOppfyltÅrsak arbeidsforholdIkkeOppfyltÅrsak;
    private List<Uttaksperiode> uttaksperioder;

    public UttaksperioderPerArbeidsforhold(List<Uttaksperiode> uttaksperioder) {
        this.uttaksperioder = uttaksperioder;
    }

    public ArbeidsforholdIkkeOppfyltÅrsak getArbeidsforholdIkkeOppfyltÅrsak() {
        return arbeidsforholdIkkeOppfyltÅrsak;
    }

    public List<Uttaksperiode> getUttaksperioder() {
        return uttaksperioder;
    }

    public void fjernPerioderUtenVirkedager() {
        uttaksperioder = uttaksperioder
            .stream()
            .filter(periode -> Virkedager.antallVirkedager(periode.getFom(), periode.getTom())>0)
            .collect(Collectors.toList());
    }


    public void avslå(ArbeidsforholdIkkeOppfyltÅrsak arbeidsforholdIkkeOppfyltÅrsak) {
        uttaksperioder = List.of();
        this.arbeidsforholdIkkeOppfyltÅrsak = arbeidsforholdIkkeOppfyltÅrsak;
    }

    void knekk(Set<LocalDate> knekkpunkter) {
        knekkpunkter.forEach(knekkpunkt -> {
            var nyePerioder = new ArrayList<Uttaksperiode>();
            uttaksperioder.forEach(periode -> {
                var knektePerioder = periode.knekk(knekkpunkt);
                if (knektePerioder.isPresent()) {
                    nyePerioder.add(knektePerioder.get().getElement1());
                    nyePerioder.add(knektePerioder.get().getElement2());
                } else {
                    nyePerioder.add(periode);
                }
            });
            this.uttaksperioder = nyePerioder;
        });
    }

}
