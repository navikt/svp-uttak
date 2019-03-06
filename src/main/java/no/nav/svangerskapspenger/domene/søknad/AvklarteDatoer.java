package no.nav.svangerskapspenger.domene.søknad;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class AvklarteDatoer {

    private final LocalDate tilretteleggingBehovDato;
    private final LocalDate brukersDødsdato;
    private final LocalDate barnetsDødsdato;
    private final LocalDate opphørsdatoForMedlemskap;
    private final LocalDate førsteLovligeUttaksdato;
    private final LocalDate termindato;
    private final LocalDate fødselsdato;

    public AvklarteDatoer(
            LocalDate tilretteleggingBehovDato,
            LocalDate brukersDødsdato,
            LocalDate barnetsDødsdato,
            LocalDate opphørsdatoForMedlemskap,
            LocalDate førsteLovligeUttaksdato,
            LocalDate termindato,
            LocalDate fødselsdato) {
        Objects.requireNonNull(tilretteleggingBehovDato, "Tilrettelegging behov dato må være utfylt.");
        Objects.requireNonNull(førsteLovligeUttaksdato, "Første lovlige uttaksdato må være utfylt.");
        Objects.requireNonNull(termindato, "Termindato må være utfylt.");


        this.tilretteleggingBehovDato = tilretteleggingBehovDato;
        this.brukersDødsdato = brukersDødsdato;
        this.barnetsDødsdato = barnetsDødsdato;
        this.opphørsdatoForMedlemskap = opphørsdatoForMedlemskap;
        this.førsteLovligeUttaksdato = førsteLovligeUttaksdato;
        this.termindato = termindato;
        this.fødselsdato = fødselsdato;
    }

    public LocalDate getTilretteleggingBehovDato() {
        return tilretteleggingBehovDato;
    }

    public Optional<LocalDate> getBrukersDødsdato() {
        return Optional.ofNullable(brukersDødsdato);
    }

    public Optional<LocalDate> getBarnetsDødsdato() {
        return Optional.ofNullable(barnetsDødsdato);
    }

    public Optional<LocalDate> getOpphørsdatoForMedlemskap() {
        return Optional.ofNullable(opphørsdatoForMedlemskap);
    }

    public LocalDate getFørsteLovligeUttaksdag() {
        return førsteLovligeUttaksdato;
    }

    public LocalDate getTerminsdato() {
        return termindato;
    }

    public Optional<LocalDate> getFødselsdato() {
        return Optional.ofNullable(fødselsdato);
    }

}
