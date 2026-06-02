package no.nav.svangerskapspenger.domene.felles;

import java.util.Objects;
import java.util.Optional;

public class Arbeidsforhold {

    private final AktivitetType aktivitetType;
    private final String arbeidsgiverVirksomhetId;
    private final String arbeidsgiverAktørId;
    private final String arbeidsforholdId;
    private final boolean arbeidsforholdErSplittet;

    private Arbeidsforhold(AktivitetType aktivitetType, String arbeidsgiverVirksomhetId, String arbeidsgiverAktørId, String arbeidsforholdId,
                           boolean arbeidsforholdErSplittet) {
        this.aktivitetType = aktivitetType;
        this.arbeidsgiverVirksomhetId = arbeidsgiverVirksomhetId;
        this.arbeidsgiverAktørId = arbeidsgiverAktørId;
        this.arbeidsforholdId = arbeidsforholdId;
        this.arbeidsforholdErSplittet = arbeidsforholdErSplittet;
    }

    public static Arbeidsforhold virksomhet(AktivitetType aktivitetType, String arbeidsgiverVirksomhetId, String arbeidsforholdId,
                                            boolean arbeidsforholdErSplittet) {
        Objects.requireNonNull(arbeidsgiverVirksomhetId);
        return new Arbeidsforhold(aktivitetType, arbeidsgiverVirksomhetId, null, arbeidsforholdId, arbeidsforholdErSplittet);
    }

    public static Arbeidsforhold aktør(AktivitetType aktivitetType, String arbeidsgiverAktørId, String arbeidsforholdId) {
        Objects.requireNonNull(arbeidsgiverAktørId);
        return new Arbeidsforhold(aktivitetType, null, arbeidsgiverAktørId, arbeidsforholdId, false);
    }

    public static Arbeidsforhold annet(AktivitetType aktivitetType) {
        return new Arbeidsforhold(aktivitetType, null, null, null, false);
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

    public boolean getArbeidsforholdErSplittet() {
        return arbeidsforholdErSplittet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arbeidsforhold that = (Arbeidsforhold) o;
        return aktivitetType == that.aktivitetType &&
            Objects.equals(arbeidsgiverVirksomhetId, that.arbeidsgiverVirksomhetId) &&
            Objects.equals(arbeidsgiverAktørId, that.arbeidsgiverAktørId) &&
            Objects.equals(arbeidsforholdId, that.arbeidsforholdId) &&
            Objects.equals(arbeidsforholdErSplittet, that.arbeidsforholdErSplittet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktivitetType, arbeidsgiverVirksomhetId, arbeidsgiverAktørId, arbeidsforholdId,  arbeidsforholdErSplittet);
    }
}
