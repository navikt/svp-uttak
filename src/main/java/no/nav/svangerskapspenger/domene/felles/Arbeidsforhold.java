package no.nav.svangerskapspenger.domene.felles;

import java.util.Objects;
import java.util.Optional;

public class Arbeidsforhold {

    private final AktivitetType aktivitetType;
    private final String arbeidsgiverVirksomhetId;
    private final String arbeidsgiverAktørId;
    private final String arbeidsforholdId;

    private Arbeidsforhold(AktivitetType aktivitetType, String arbeidsgiverVirksomhetId, String arbeidsgiverAktørId, String arbeidsforholdId) {
        this.aktivitetType = aktivitetType;
        this.arbeidsgiverVirksomhetId = arbeidsgiverVirksomhetId;
        this.arbeidsgiverAktørId = arbeidsgiverAktørId;
        this.arbeidsforholdId = arbeidsforholdId;
    }

    public static Arbeidsforhold virksomhet(AktivitetType aktivitetType, String arbeidsgiverVirksomhetId, String arbeidsforholdId) {
        Objects.requireNonNull(arbeidsgiverVirksomhetId);
        return new Arbeidsforhold(aktivitetType, arbeidsgiverVirksomhetId, null, arbeidsforholdId);
    }

    public static Arbeidsforhold aktør(AktivitetType aktivitetType, String arbeidsgiverAktørId, String arbeidsforholdId) {
        Objects.requireNonNull(arbeidsgiverAktørId);
        return new Arbeidsforhold(aktivitetType, null, arbeidsgiverAktørId, arbeidsforholdId);
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

    public AktivitetType getAktivitetType() {
        return aktivitetType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arbeidsforhold that = (Arbeidsforhold) o;
        return aktivitetType == that.aktivitetType &&
            Objects.equals(arbeidsgiverVirksomhetId, that.arbeidsgiverVirksomhetId) &&
            Objects.equals(arbeidsgiverAktørId, that.arbeidsgiverAktørId) &&
            Objects.equals(arbeidsforholdId, that.arbeidsforholdId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktivitetType, arbeidsgiverVirksomhetId, arbeidsgiverAktørId, arbeidsforholdId);
    }
}
