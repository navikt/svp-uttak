package no.nav.svangerskapspenger.domene.søknad;

import java.time.LocalDate;
import java.util.Optional;

public class AvklarteDatoer {

    private final LocalDate legesDato;
    private final Optional<LocalDate> brukersDødsdato;
    private final Optional<LocalDate> barnetsDødsdato;
    private final Optional<LocalDate> opphørsdatoForMedlemskap;
    private final LocalDate førsteLovligeUttaksdato;
    private final LocalDate termindato;
    private final Optional<LocalDate> fødselsdato;

    public AvklarteDatoer(
            LocalDate legesDato,
            Optional<LocalDate> brukersDødsdato,
            Optional<LocalDate> barnetsDødsdato,
            Optional<LocalDate> opphørsdatoForMedlemskap,
            LocalDate førsteLovligeUttaksdato,
            LocalDate termindato,
            Optional<LocalDate> fødselsdato) {
        this.legesDato = legesDato;
        this.brukersDødsdato = brukersDødsdato;
        this.barnetsDødsdato = barnetsDødsdato;
        this.opphørsdatoForMedlemskap = opphørsdatoForMedlemskap;
        this.førsteLovligeUttaksdato = førsteLovligeUttaksdato;
        this.termindato = termindato;
        this.fødselsdato = fødselsdato;
    }

    public LocalDate getLegesDato() {
        return legesDato;
    }

    public Optional<LocalDate> getBrukersDødsdato() {
        return brukersDødsdato;
    }

    public Optional<LocalDate> getBarnetsDødsdato() {
        return barnetsDødsdato;
    }

    public Optional<LocalDate> getOpphørsdatoForMedlemskap() {
        return opphørsdatoForMedlemskap;
    }

    public LocalDate getFørsteLovligeUttaksdag() {
        return førsteLovligeUttaksdato;
    }

    public LocalDate getTerminsdato() {
        return termindato;
    }

    public Optional<LocalDate> getFødselsdato() {
        return fødselsdato;
    }

}
