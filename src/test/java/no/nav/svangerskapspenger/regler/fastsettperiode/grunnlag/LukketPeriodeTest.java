package no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag;

import java.time.LocalDate;
import java.time.Month;

import org.assertj.core.api.Assertions;
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

    @Test
    public void ikke_overlapp_når_perioder2_er_etter_periode1() {
        var periode1 = new LukketPeriode(LocalDate.of(2019, Month.JANUARY, 1), LocalDate.of(2019, Month.JANUARY, 31)) {};
        var periode2 = new LukketPeriode(LocalDate.of(2019, Month.FEBRUARY, 1), LocalDate.of(2019, Month.FEBRUARY, 28)) {};

        Assertions.assertThat(periode1.overlapper(periode2)).isFalse();
    }

    @Test
    public void ikke_overlapp_når_perioder2_er_før_periode1() {
        var periode1 = new LukketPeriode(LocalDate.of(2019, Month.FEBRUARY, 1), LocalDate.of(2019, Month.FEBRUARY, 28)) {};
        var periode2 = new LukketPeriode(LocalDate.of(2019, Month.JANUARY, 1), LocalDate.of(2019, Month.JANUARY, 31)) {};

        Assertions.assertThat(periode1.overlapper(periode2)).isFalse();
    }

    @Test
    public void delvis_overlapp_tolkes_som_overlapp() {
        var periode1 = new LukketPeriode(LocalDate.of(2019, Month.JANUARY, 1), LocalDate.of(2019, Month.FEBRUARY, 10)) {};
        var periode2 = new LukketPeriode(LocalDate.of(2019, Month.FEBRUARY, 1), LocalDate.of(2019, Month.FEBRUARY, 28)) {};

        Assertions.assertThat(periode1.overlapper(periode2)).isTrue();
    }

    @Test
    public void helt_overlapp_tolkes_som_overlapp() {
        var periode1 = new LukketPeriode(LocalDate.of(2019, Month.JANUARY, 1), LocalDate.of(2019, Month.FEBRUARY, 10)) {};
        var periode2 = new LukketPeriode(LocalDate.of(2019, Month.JANUARY, 1), LocalDate.of(2019, Month.FEBRUARY, 10)) {};

        Assertions.assertThat(periode1.overlapper(periode2)).isTrue();
    }

}
