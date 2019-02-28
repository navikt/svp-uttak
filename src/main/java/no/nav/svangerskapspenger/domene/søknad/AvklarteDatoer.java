package no.nav.svangerskapspenger.domene.søknad;

import java.time.LocalDate;
import java.util.Optional;

public class AvklarteDatoer {

    private final Optional<LocalDate> brukersDødsdato;
    private final Optional<LocalDate> barnetsDødsdato;
    private final Optional<LocalDate> opphørsdatoForMedlemskap;
    private final LocalDate førsteLovligeUttaksdato;
    private final LocalDate termindato;
    private final Optional<LocalDate> fødselsdato;

    public AvklarteDatoer(
            Optional<LocalDate> brukersDødsdato,
            Optional<LocalDate> barnetsDødsdato,
            Optional<LocalDate> opphørsdatoForMedlemskap,
            LocalDate førsteLovligeUttaksdato,
            LocalDate termindato,
            Optional<LocalDate> fødselsdato) {
        this.brukersDødsdato = brukersDødsdato;
        this.barnetsDødsdato = barnetsDødsdato;
        this.opphørsdatoForMedlemskap = opphørsdatoForMedlemskap;
        this.førsteLovligeUttaksdato = førsteLovligeUttaksdato;
        this.termindato = termindato;
        this.fødselsdato = fødselsdato;
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


    public static final class AvklarteDatoerBuilder {
        private Optional<LocalDate> brukersDødsdato;
        private Optional<LocalDate> barnetsDødsdato;
        private Optional<LocalDate> opphørsdatoForMedlemskap;
        private LocalDate førsteLovligeUttaksdato;
        private LocalDate termindato;
        private Optional<LocalDate> fødselsdato;

        private AvklarteDatoerBuilder() {
        }

        public static AvklarteDatoerBuilder anAvklarteDatoer() {
            return new AvklarteDatoerBuilder();
        }

        public AvklarteDatoerBuilder medBrukersDødsdato(Optional<LocalDate> brukersDødsdato) {
            this.brukersDødsdato = brukersDødsdato;
            return this;
        }

        public AvklarteDatoerBuilder medBarnetsDødsdato(Optional<LocalDate> barnetsDødsdato) {
            this.barnetsDødsdato = barnetsDødsdato;
            return this;
        }

        public AvklarteDatoerBuilder medOpphørsdatoForMedlemskap(Optional<LocalDate> opphørsdatoForMedlemskap) {
            this.opphørsdatoForMedlemskap = opphørsdatoForMedlemskap;
            return this;
        }

        public AvklarteDatoerBuilder medFørsteLovligeUttaksdato(LocalDate førsteLovligeUttaksdato) {
            this.førsteLovligeUttaksdato = førsteLovligeUttaksdato;
            return this;
        }

        public AvklarteDatoerBuilder medTermindato(LocalDate termindato) {
            this.termindato = termindato;
            return this;
        }

        public AvklarteDatoerBuilder medFødselsdato(Optional<LocalDate> fødselsdato) {
            this.fødselsdato = fødselsdato;
            return this;
        }

        public AvklarteDatoer build() {
            return new AvklarteDatoer(brukersDødsdato, barnetsDødsdato, opphørsdatoForMedlemskap, førsteLovligeUttaksdato, termindato, fødselsdato);
        }
    }
}
