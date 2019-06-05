package no.nav.svangerskapspenger.domene.felles;

import java.time.LocalDate;
import java.util.Objects;

/**
 * En periode som har definert både start- og slutt-tidpunkt
 */
public class LukketPeriode {

    private final LocalDate fom;
    private final LocalDate tom;

    public LukketPeriode(LocalDate fom, LocalDate tom) {
        Objects.requireNonNull(tom);
        Objects.requireNonNull(fom);
        if (tom.isBefore(fom)) {
            throw new IllegalArgumentException("Til og med dato før fra og med dato: " + fom + ">" + tom);
        }
        this.fom = fom;
        this.tom = tom;
    }

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public boolean overlapper(LukketPeriode annenPeriode) {
        return !this.tom.isBefore(annenPeriode.fom) && !this.fom.isAfter(annenPeriode.tom);
    }

}
