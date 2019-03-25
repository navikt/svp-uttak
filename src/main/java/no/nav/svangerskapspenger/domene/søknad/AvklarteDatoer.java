package no.nav.svangerskapspenger.domene.søknad;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AvklarteDatoer {

    private LocalDate tilretteleggingBehovDato;
    private LocalDate brukersDødsdato;
    private LocalDate barnetsDødsdato;
    private LocalDate opphørsdatoForMedlemskap;
    private LocalDate førsteLovligeUttaksdato;
    private LocalDate termindato;
    private LocalDate fødselsdato;
    private List<Ferie> ferier = new ArrayList<>();

    private AvklarteDatoer() {
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

    public List<Ferie> getFerier() {
        return Collections.unmodifiableList(ferier);
    }

    public static final class Builder {
        private AvklarteDatoer kladd;

        public Builder() {
            kladd = new AvklarteDatoer();
        }

        public Builder medTilretteleggingBehovDato(LocalDate tilretteleggingBehovDato) {
            kladd.tilretteleggingBehovDato = tilretteleggingBehovDato;
            return this;
        }

        public Builder medBrukersDødsdato(LocalDate brukersDødsdato) {
            kladd.brukersDødsdato = brukersDødsdato;
            return this;
        }

        public Builder medBarnetsDødsdato(LocalDate barnetsDødsdato) {
            kladd.barnetsDødsdato = barnetsDødsdato;
            return this;
        }

        public Builder medOpphørsdatoForMedlemskap(LocalDate opphørsdatoForMedlemskap) {
            kladd.opphørsdatoForMedlemskap = opphørsdatoForMedlemskap;
            return this;
        }

        public Builder medFørsteLovligeUttaksdato(LocalDate førsteLovligeUttaksdato) {
            kladd.førsteLovligeUttaksdato = førsteLovligeUttaksdato;
            return this;
        }

        public Builder medTermindato(LocalDate termindato) {
            kladd.termindato = termindato;
            return this;
        }

        public Builder medFødselsdato(LocalDate fødselsdato) {
            kladd.fødselsdato = fødselsdato;
            return this;
        }

        public Builder medFerie(Ferie ferie) {
            kladd.ferier.add(ferie);
            return this;
        }

        public AvklarteDatoer build() {
            Objects.requireNonNull(kladd.tilretteleggingBehovDato, "Tilrettelegging behov dato må være utfylt.");
            Objects.requireNonNull(kladd.førsteLovligeUttaksdato, "Første lovlige uttaksdato må være utfylt.");
            Objects.requireNonNull(kladd.termindato, "Termindato må være utfylt.");
            return kladd;
        }
    }
}
