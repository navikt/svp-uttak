package no.nav.svangerskapspenger.domene.resultat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import no.nav.svangerskapspenger.utils.Virkedager;

public class UttaksperioderPerArbeidsforhold {

    private ArbeidsforholdIkkeOppfyltÅrsak arbeidsforholdIkkeOppfyltÅrsak;
    private List<Uttaksperiode> uttaksperioder;

    UttaksperioderPerArbeidsforhold(List<Uttaksperiode> uttaksperioder) {
        this.uttaksperioder = uttaksperioder;
    }

    public ArbeidsforholdIkkeOppfyltÅrsak getArbeidsforholdIkkeOppfyltÅrsak() {
        return arbeidsforholdIkkeOppfyltÅrsak;
    }

    public List<Uttaksperiode> getUttaksperioder() {
        //Fjerner perioder med bare helg.
        return uttaksperioder
            .stream()
            .filter(periode -> Virkedager.antallVirkedager(periode.getFom(), periode.getTom())>0)
            .toList();
    }

    public void avslå(ArbeidsforholdIkkeOppfyltÅrsak arbeidsforholdIkkeOppfyltÅrsak) {
        uttaksperioder = List.of();
        this.arbeidsforholdIkkeOppfyltÅrsak = arbeidsforholdIkkeOppfyltÅrsak;
    }

    public void knekk(Set<LocalDate> knekkpunkter) {
        knekkpunkter.forEach(knekkpunkt -> {
            var nyePerioder = new ArrayList<Uttaksperiode>();
            uttaksperioder.forEach(periode -> {
                var knektePerioder = periode.knekk(knekkpunkt);
                if (knektePerioder.isPresent()) {
                    nyePerioder.add(knektePerioder.get().periode1());
                    nyePerioder.add(knektePerioder.get().periode2());
                } else {
                    nyePerioder.add(periode);
                }
            });
            this.uttaksperioder = nyePerioder;
        });
    }

    void leggTilPerioder(Uttaksperiode... perioder) {
        var tmpList = new ArrayList<Uttaksperiode>();
        tmpList.addAll(uttaksperioder);
        tmpList.addAll(List.of(perioder));
        uttaksperioder = Collections.unmodifiableList(tmpList);
    }

}
