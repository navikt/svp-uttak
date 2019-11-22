package no.nav.svangerskapspenger.domene.søknad;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AvklarteDatoer {

    private LocalDate brukersDødsdato;
    private LocalDate barnetsDødsdato;
    private LocalDate opphørsdatoForMedlemskap;
    private LocalDate førsteLovligeUttaksdato;
    private LocalDate termindato;
    private LocalDate fødselsdato;
    private List<Ferie> ferier = new ArrayList<>();
    private LocalDate startOppholdUttak;

    private AvklarteDatoer() {
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

    public Optional<LocalDate> getFørsteLovligeUttaksdato() {
        return Optional.ofNullable(førsteLovligeUttaksdato);
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

    public Optional<LocalDate> getStartOppholdUttak() {
        return Optional.ofNullable(startOppholdUttak);
    }

    /**
     * Setter en eventuelt start for hull i uttak som skal føre til at alle perioder etterpå skal avlås.
     * (Ikke en del av builder siden den må settes på et senere tidspunkt enn når objektet bygges i utgangspunktet)
     *
     * @param startOppholdUttak start for opphold i uttak
     */
    public void setStartOppholdUttak(LocalDate startOppholdUttak) {
        this.startOppholdUttak = startOppholdUttak;
    }

    public static final class Builder {
        private AvklarteDatoer kladd;

        public Builder() {
            kladd = new AvklarteDatoer();
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

        public Builder medFerie(List<Ferie> ferie) {
            kladd.ferier.addAll(ferie);
            return this;
        }

        public AvklarteDatoer build() {
            Objects.requireNonNull(kladd.termindato, "Termindato må være utfylt.");
            return kladd;
        }
    }
}
