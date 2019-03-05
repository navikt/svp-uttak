package no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag;

import java.time.LocalDate;
import java.util.Objects;

/**
 * En periode som har definert både start- og slutt-tidpunkt
 */
public abstract class LukketPeriode {

    private final LocalDate fom;
    private final LocalDate tom;

    protected LukketPeriode(LocalDate fom, LocalDate tom) {
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

}
