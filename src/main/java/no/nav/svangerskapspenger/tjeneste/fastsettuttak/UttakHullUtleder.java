package no.nav.svangerskapspenger.tjeneste.fastsettuttak;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import no.nav.fpsak.tidsserie.LocalDateInterval;
import no.nav.fpsak.tidsserie.LocalDateSegment;
import no.nav.fpsak.tidsserie.LocalDateTimeline;
import no.nav.fpsak.tidsserie.StandardCombinators;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperioder;
import no.nav.svangerskapspenger.domene.søknad.Opphold;

class UttakHullUtleder {

    Optional<LocalDate> finnStartHull(Uttaksperioder nyeUttaksperioder, List<Opphold> opphold) {
        var intervaller = intervallerFraTidslinje(nyeUttaksperioder, opphold);
        // Velg dagen etter tidligste tom dersom disjoint etter tidslinje-compress
        return intervaller.size() < 2 ? Optional.empty() :
            intervaller.stream().map(LocalDateInterval::getTomDato).min(Comparator.naturalOrder()).map(d -> d.plusDays(1));
    }

    private Collection<LocalDateInterval> intervallerFraTidslinje(Uttaksperioder nyeUttaksperioder, List<Opphold> opphold) {
        var perioder = new ArrayList<>(tilSegmenter(nyeUttaksperioder));
        if (!perioder.isEmpty() && !opphold.isEmpty()) {
            var førsteuttaksdato = perioder.stream().map(LocalDateSegment::getFom).min(Comparator.naturalOrder()).orElseThrow();
            var sisteuttaksdato = perioder.stream().map(LocalDateSegment::getTom).max(Comparator.naturalOrder()).orElseThrow();
            perioder.addAll(tilSegmenter(opphold, førsteuttaksdato, sisteuttaksdato));
        }
        return new LocalDateTimeline<>(perioder, StandardCombinators::alwaysTrueForMatch).compress().getLocalDateIntervals();
    }

    private List<LocalDateSegment<Boolean>> tilSegmenter(List<Opphold> oppholdListe, LocalDate førsteuttaksdato, LocalDate sisteuttaksdato) {
        return oppholdListe.stream()
            .filter(opphold -> !opphold.getTom().isBefore(førsteuttaksdato) &&  !opphold.getFom().isAfter(sisteuttaksdato)) //se bort fra ferieperioder som er før eller etter uttak
            .map(opphold -> new LocalDateSegment<>(opphold.getFom(), utvidFredagTilSøndag(opphold.getTom()), Boolean.TRUE))
            .toList();
    }

    private List<LocalDateSegment<Boolean>> tilSegmenter(Uttaksperioder uttaksperioder) {
        return uttaksperioder.alleArbeidsforhold().stream()
            .flatMap(arbeidsforhold -> uttaksperioder.perioder(arbeidsforhold).getUttaksperioder().stream())
            .filter(periode -> !BigDecimal.ZERO.equals(periode.getUtbetalingsgrad()))
            .map(periode -> new LocalDateSegment<>(periode.getFom(), utvidFredagTilSøndag(periode.getTom()), Boolean.TRUE))
            .toList();
    }

    private LocalDate utvidFredagTilSøndag(LocalDate dato) {
        if (DayOfWeek.FRIDAY.equals(DayOfWeek.from(DayOfWeek.from(dato)))) return dato.plusDays(2);
        if (DayOfWeek.SATURDAY.equals(DayOfWeek.from(DayOfWeek.from(dato)))) return dato.plusDays(1);
        return dato;
    }

}
