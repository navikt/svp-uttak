package no.nav.svangerskapspenger.domene.felles.arbeid;

import java.math.BigDecimal;
import java.util.Objects;

public class Arbeid {

    private static final BigDecimal STILLINGSPROSENT_100_PROSENT = BigDecimal.valueOf(100L);

    private final BigDecimal stillingsprosent;

    Arbeid(BigDecimal stillingsprosent) {
        this.stillingsprosent = stillingsprosent;
    }

    public BigDecimal getStillingsprosent() {
        return stillingsprosent;
    }

    public static Arbeid forFrilans() {
        return new Arbeid(STILLINGSPROSENT_100_PROSENT);
    }

    public static Arbeid forSelvstendigNæringsdrivende() {
        return new Arbeid(STILLINGSPROSENT_100_PROSENT);
    }

    public static Arbeid forOrdinærtArbeid(BigDecimal stillingsprosent) {
        return new Arbeid(stillingsprosent);
    }

    public static Arbeid forAnnet() {
        return new Arbeid(STILLINGSPROSENT_100_PROSENT);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arbeid arbeid = (Arbeid) o;
        return Objects.equals(stillingsprosent, arbeid.stillingsprosent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stillingsprosent);
    }
}
