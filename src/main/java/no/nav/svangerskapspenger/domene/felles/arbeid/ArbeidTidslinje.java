package no.nav.svangerskapspenger.domene.felles.arbeid;

import static no.nav.fpsak.tidsserie.LocalDateInterval.TIDENES_BEGYNNELSE;
import static no.nav.fpsak.tidsserie.LocalDateInterval.TIDENES_ENDE;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import no.nav.fpsak.tidsserie.LocalDateInterval;
import no.nav.fpsak.tidsserie.LocalDateSegment;
import no.nav.fpsak.tidsserie.LocalDateTimeline;
import no.nav.svangerskapspenger.domene.felles.LukketPeriode;

public class ArbeidTidslinje {

    private LocalDateTimeline<Arbeid> arbeid;

    private ArbeidTidslinje(LocalDateTimeline<Arbeid> arbeid) {
        this.arbeid = arbeid;
    }

    public LocalDateTimeline<Arbeid> getArbeid() {
        return arbeid;
    }

    private Optional<Arbeid> getArbeid(LukketPeriode periode) {
        LocalDateInterval periodeMedHelg = medHelg(periode);
        LocalDateSegment<Arbeid> overlappendeVerdi = arbeid.getSegment(periodeMedHelg);
        if (overlappendeVerdi == null) {
            return Optional.empty();
        }
        if (arbeid.isContinuous(periodeMedHelg)) {
            return Optional.of(overlappendeVerdi.getValue());
        }
        throw new IllegalArgumentException("Utvikler-feil: intervallet " + periode + " har ikke bare en verdi");
    }

    public Optional<Arbeid> getArbeid(LocalDate fom, LocalDate tom) {
        return getArbeid(new LukketPeriode(fom, tom));
    }

    public Optional<BigDecimal> getStillingsprosent(LukketPeriode periode) {
        return getArbeid(periode).map(Arbeid::getStillingsprosent);
    }

    public static class Builder {
        private List<LocalDateSegment<Arbeid>> kladd = new ArrayList<>();

        public Builder() {
            Arbeid arbeid = new Arbeid(BigDecimal.valueOf(100));
            this.kladd.add(new LocalDateSegment<>(TIDENES_BEGYNNELSE, TIDENES_ENDE, arbeid));
        }

        public Builder medArbeid(LocalDate fom, LocalDate tom, Arbeid arbeid) {
            return medArbeid(new LukketPeriode(fom, tom), arbeid);
        }

        public Builder medArbeid(LukketPeriode periode, Arbeid arbeid) {
            //helger spiller ikke noen rolle, justerer slik at periode kan bli sammenhende
            this.kladd.add(new LocalDateSegment<>(medHelg(periode), arbeid));
            return this;
        }

        public ArbeidTidslinje build() {
            LocalDateTimeline<Arbeid> timeline = new LocalDateTimeline<>(kladd, (datoInterval, datoSegment1, datoSegment2) -> {
                Arbeid arbeid2 = datoSegment2.getValue();
                Arbeid sumArbeid = new Arbeid(arbeid2.getStillingsprosent());
                return new LocalDateSegment<>(datoInterval, sumArbeid);
            }).compress();
            return new ArbeidTidslinje(timeline);
        }
    }

    static LocalDateInterval medHelg(LukketPeriode periode) {
        LocalDate fom = PerioderUtenHelgUtil.helgBlirMandag(periode.getFom());
        LocalDate tom = PerioderUtenHelgUtil.fredagLørdagBlirSøndag(periode.getTom());

        boolean helePeriodeErIEnHelg = fom.isAfter(tom);
        if (helePeriodeErIEnHelg) {
            LocalDateInterval helg = new LocalDateInterval(tom.minusDays(1), tom);
            return helg;
        } else {
            return new LocalDateInterval(fom, tom);
        }
    }
}