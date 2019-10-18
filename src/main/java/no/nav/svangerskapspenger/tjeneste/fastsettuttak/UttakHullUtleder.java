package no.nav.svangerskapspenger.tjeneste.fastsettuttak;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.fpsak.tidsserie.LocalDateInterval;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperioder;
import no.nav.svangerskapspenger.domene.søknad.Ferie;
import no.nav.svangerskapspenger.utils.Virkedager;

class UttakHullUtleder {

    Optional<LocalDate> finnStartHull(Uttaksperioder nyeUttaksperioder, List<Ferie> ferier) {
        List<LocalDateInterval> sorterteIntervaler = tilSorterteIntervaler(nyeUttaksperioder, ferier);
        if (sorterteIntervaler.isEmpty()) {
            return Optional.empty();
        }
        var forrigeSluttPlusEn = sorterteIntervaler.get(0).getTomDato().plusDays(1);
        for (int i = 1; i < sorterteIntervaler.size(); i++) {
            var nyForrigeSluttPlusEn = sorterteIntervaler.get(i - 1).getTomDato().plusDays(1);
            if (nyForrigeSluttPlusEn.isAfter(forrigeSluttPlusEn)) {
                forrigeSluttPlusEn = nyForrigeSluttPlusEn;
            }
            var startNesteMinusEn = sorterteIntervaler.get(i).getFomDato().minusDays(1);
            if (forrigeSluttPlusEn.isBefore(startNesteMinusEn)) {
                if (Virkedager.antallVirkedager(forrigeSluttPlusEn, startNesteMinusEn) > 0) {
                    return Optional.of(forrigeSluttPlusEn);
                }
            }

        }
        return Optional.empty();
    }

    private List<LocalDateInterval> tilSorterteIntervaler(Uttaksperioder nyeUttaksperioder, List<Ferie> ferier) {
        var perioder = new ArrayList<>(tilIntervaler(nyeUttaksperioder));
        var førsteuttaksdato = nyeUttaksperioder.finnFørsteUttaksdato();
        førsteuttaksdato.ifPresent(localDate -> perioder.addAll(tilIntervaler(ferier, localDate)));
        perioder.sort(LocalDateInterval::compareTo);
        return perioder;
    }

    private List<LocalDateInterval> tilIntervaler(List<Ferie> ferier, LocalDate førsteuttaksdato) {
        return ferier
            .stream()
            .filter(ferie -> ferie.getFom().isAfter(førsteuttaksdato)) //se bort fra ferieperioder som er før førsteuttaksdato
            .map(ferie -> new LocalDateInterval(ferie.getFom(), ferie.getTom()))
            .collect(Collectors.toList());
    }

    private List<LocalDateInterval> tilIntervaler(Uttaksperioder uttaksperioder) {
        return uttaksperioder.alleArbeidsforhold()
            .stream()
            .flatMap(arbeidsforhold -> uttaksperioder.perioder(arbeidsforhold).getUttaksperioder().stream())
            .filter(periode -> !BigDecimal.ZERO.equals(periode.getUtbetalingsgrad()))
            .map(periode -> new LocalDateInterval(periode.getFom(), periode.getTom()))
            .collect(Collectors.toList());
    }

}
