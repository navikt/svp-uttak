package no.nav.svangerskapspenger.domene.felles;

import java.util.Objects;
import java.util.Optional;

public class Arbeidsforhold {

    private final String arbeidsgiverVirksomhetId;
    private final String arbeidsgiverAktørId;
    private final String arbeidsforholdId;

    private Arbeidsforhold(String arbeidsgiverVirksomhetId, String arbeidsgiverAktørId, String arbeidsforholdId) {
        this.arbeidsgiverVirksomhetId = arbeidsgiverVirksomhetId;
        this.arbeidsgiverAktørId = arbeidsgiverAktørId;
        this.arbeidsforholdId = arbeidsforholdId;
    }

    public static Arbeidsforhold virksomhet(String arbeidsgiverVirksomhetId, String arbeidsforholdId) {
        Objects.requireNonNull(arbeidsgiverVirksomhetId);
        return new Arbeidsforhold(arbeidsgiverVirksomhetId, null, arbeidsforholdId);
    }

    public static Arbeidsforhold aktør(String arbeidsgiverAktørId, String arbeidsforholdId) {
        Objects.requireNonNull(arbeidsgiverAktørId);
        return new Arbeidsforhold(null, arbeidsgiverAktørId, arbeidsforholdId);
    }

    public String getArbeidsgiverVirksomhetId() {
        return arbeidsgiverVirksomhetId;
    }

    public String getArbeidsgiverAktørId() {
        return arbeidsgiverAktørId;
    }

    public Optional<String> getArbeidsforholdId() {
        return Optional.ofNullable(arbeidsforholdId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arbeidsforhold that = (Arbeidsforhold) o;
        return Objects.equals(arbeidsgiverVirksomhetId, that.arbeidsgiverVirksomhetId) &&
            Objects.equals(arbeidsgiverAktørId, that.arbeidsgiverAktørId) &&
            Objects.equals(arbeidsforholdId, that.arbeidsforholdId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arbeidsgiverVirksomhetId, arbeidsgiverAktørId, arbeidsforholdId);
    }
}
