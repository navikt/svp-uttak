package no.nav.svangerskapspenger.domene.resultat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UttaksperioderPerArbeidsforhold {

    private ArbeidsforholdÅrsak arbeidsforholdÅrsak;
    private List<Uttaksperiode> uttaksperioder;

    public UttaksperioderPerArbeidsforhold(List<Uttaksperiode> uttaksperioder) {
        this.uttaksperioder = uttaksperioder;
    }

    public ArbeidsforholdÅrsak getArbeidsforholdÅrsak() {
        return arbeidsforholdÅrsak;
    }

    public List<Uttaksperiode> getUttaksperioder() {
        return uttaksperioder;
    }

    void avslå(ArbeidsforholdAvslåttÅrsak arbeidsforholdAvslåttÅrsak) {
        uttaksperioder.clear();
        this.arbeidsforholdÅrsak = arbeidsforholdAvslåttÅrsak;
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
