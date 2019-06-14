package no.nav.svangerskapspenger.tjeneste.fastsettuttak;

import java.time.LocalDate;
import java.util.Optional;

import no.nav.svangerskapspenger.domene.felles.Arbeidsforhold;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperiode;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperioder;

class UttaksresultatMerger {

    Uttaksperioder merge(Optional<Uttaksperioder> tidligereUttaksperioder,
                                                 Uttaksperioder nyeUttaksperioder) {
        if(!tidligereUttaksperioder.isPresent()) {
            return nyeUttaksperioder;
        }

        var mergetUttaksperioder = mergePerioder(tidligereUttaksperioder, nyeUttaksperioder);

        nyeUttaksperioder.alleArbeidsforhold().forEach(nyttArbeidsforhold-> {
            nyeUttaksperioder.perioder(nyttArbeidsforhold).getUttaksperioder().forEach(periode -> {
                mergetUttaksperioder.leggTilPerioder(nyttArbeidsforhold, periode);
            });
        });

        return mergetUttaksperioder;
    }

    private Uttaksperioder mergePerioder(Optional<Uttaksperioder> tidligereUttaksperioder, Uttaksperioder nyeUttaksperioder) {
        var mergetUttaksperioder = new Uttaksperioder();

        tidligereUttaksperioder.get().alleArbeidsforhold().forEach(tidligereArbeidsforhold -> {
            var førsteUttaksdagForNyeUttaksperioder = finnFørsteUttaksdag(tidligereArbeidsforhold, nyeUttaksperioder);
            for (var periode : tidligereUttaksperioder.get().perioder(tidligereArbeidsforhold).getUttaksperioder()) {
                var nyPeriode = periode;
                if (førsteUttaksdagForNyeUttaksperioder.isPresent()) {
                    if (skalPeriodenFjernes(førsteUttaksdagForNyeUttaksperioder.get(), periode)) {
                        continue; //hopp over dersom nye perioder overlapper
                    }
                    var knekkpunkt = skalPeriodenKnekkes(førsteUttaksdagForNyeUttaksperioder.get(), periode);
                    if (knekkpunkt.isPresent() ) {
                        var knektePerioder = periode.knekk(knekkpunkt.get());
                        if (knektePerioder.isPresent()) {
                            nyPeriode = knektePerioder.get().getElement1();
                        }
                    }
                }
                mergetUttaksperioder.leggTilPerioder(tidligereArbeidsforhold, nyPeriode);
            }
        });
        return mergetUttaksperioder;
    }

    private boolean skalPeriodenFjernes(LocalDate førsteUttaksdagForNyeUttaksperioder, Uttaksperiode periode) {
        return periode.getFom().equals(førsteUttaksdagForNyeUttaksperioder) || periode.getFom().isAfter(førsteUttaksdagForNyeUttaksperioder);
    }

    private Optional<LocalDate> skalPeriodenKnekkes(LocalDate førsteUttaksdagForNyeUttaksperioder, Uttaksperiode periode) {
        if (periode.getFom().isBefore(førsteUttaksdagForNyeUttaksperioder) &&
            (periode.getTom().isAfter(førsteUttaksdagForNyeUttaksperioder) || periode.getTom().isEqual(førsteUttaksdagForNyeUttaksperioder))) {
            return Optional.of(førsteUttaksdagForNyeUttaksperioder);
        }
        return Optional.empty();
    }

    private Optional<LocalDate> finnFørsteUttaksdag(Arbeidsforhold arbeidsforhold,
                                                    Uttaksperioder uttaksperioder) {
        var uttaksperioderPerArbeidsforhold = uttaksperioder.perioder(arbeidsforhold);
        if (uttaksperioderPerArbeidsforhold != null && !uttaksperioderPerArbeidsforhold.getUttaksperioder().isEmpty()) {
            return Optional.of(uttaksperioderPerArbeidsforhold.getUttaksperioder().get(0).getFom());
        }
        return Optional.empty();
    }

}
