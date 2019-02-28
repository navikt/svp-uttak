package no.nav.svangerskapspenger.domene.søknad;

import java.time.LocalDate;
import java.util.List;

import no.nav.svangerskapspenger.domene.felles.Arbeidsforhold;

public class Søknad {

    private final Arbeidsforhold arbeidsforhold;
    private final LocalDate termindato;
    private final List<Tilrettelegging> tilrettelegginger;
    private final LocalDate tilretteliggingBehovDato;

    public Søknad(Arbeidsforhold arbeidsforhold, LocalDate termindato, LocalDate tilretteliggingBehovDato, List<Tilrettelegging> tilrettelegginger) {
        this.arbeidsforhold = arbeidsforhold;
        this.termindato = termindato;
        this.tilrettelegginger = tilrettelegginger;
        this.tilretteliggingBehovDato = tilretteliggingBehovDato;
    }

    public Arbeidsforhold getArbeidsforhold() {
        return arbeidsforhold;
    }

    public LocalDate getTermindato() {
        return termindato;
    }

    public List<Tilrettelegging> getTilrettelegginger() {
        return tilrettelegginger;
    }

    public LocalDate getTilretteliggingBehovDato() {
        return tilretteliggingBehovDato;
    }


    public LocalDate sisteDagFørTermin() {
        return getTermindato().minusWeeks(3).minusDays(1);
    }


}
