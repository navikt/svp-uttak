package no.nav.svangerskapspenger.domene.felles;

import java.util.Objects;
import java.util.Optional;

public class Arbeidsforhold {

    private final String virksomhetId;
    private final String arbeidsforholdId;

    public Arbeidsforhold(String virksomhetId, String arbeidsforholdId) {
        this.virksomhetId = virksomhetId;
        this.arbeidsforholdId = arbeidsforholdId;
    }

    public String getVirksomhetId() {
        return virksomhetId;
    }

    public Optional<String> getArbeidsforholdId() {
        return Optional.ofNullable(arbeidsforholdId);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arbeidsforhold that = (Arbeidsforhold) o;
        return Objects.equals(virksomhetId, that.virksomhetId) &&
                Objects.equals(arbeidsforholdId, that.arbeidsforholdId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(virksomhetId, arbeidsforholdId);
    }
}
