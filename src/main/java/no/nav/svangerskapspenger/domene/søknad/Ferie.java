package no.nav.svangerskapspenger.domene.søknad;

import java.time.LocalDate;

import no.nav.svangerskapspenger.domene.felles.LukketPeriode;

public class Ferie extends LukketPeriode {

    public Ferie(LocalDate fom, LocalDate tom) {
        super(fom, tom);
    }

}
