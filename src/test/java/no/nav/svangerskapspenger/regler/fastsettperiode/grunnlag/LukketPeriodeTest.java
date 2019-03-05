package no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag;


import java.time.LocalDate;
import java.time.Month;

import org.junit.Test;

public class LukketPeriodeTest {

    @Test(expected = NullPointerException.class)
    public void manglene_fom_skal_kaste_exception() {
        new LukketPeriode(null, LocalDate.of(2019, Month.FEBRUARY, 1)) {};
    }

    @Test(expected = NullPointerException.class)
    public void mnaglende_tom_skal_kaste_exception() {
        new LukketPeriode(LocalDate.of(2019, Month.JANUARY, 1), null) {};
    }

    @Test(expected = IllegalArgumentException.class)
    public void fom_etter_tom_skal_kaste_exceptions() {
        new LukketPeriode(LocalDate.of(2019, Month.FEBRUARY, 1), LocalDate.of(2019, Month.JANUARY, 1)) {};
    }


}
