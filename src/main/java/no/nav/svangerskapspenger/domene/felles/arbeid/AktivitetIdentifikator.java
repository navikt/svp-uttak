package no.nav.svangerskapspenger.domene.felles.arbeid;

import java.util.Objects;

public class AktivitetIdentifikator {

    public enum ArbeidsgiverType {
        PERSON, VIRKSOMHET
    }

    private final String arbeidsforholdId;
    private final String arbeidsgiverIdentifikator;
    private final ArbeidsgiverType arbeidsgiverType;

    public AktivitetIdentifikator(String arbeidsgiverIdentifikator, String arbeidsforholdId, ArbeidsgiverType arbeidsgiverType) {
        this.arbeidsgiverType = arbeidsgiverType;
        this.arbeidsforholdId = arbeidsforholdId;
        this.arbeidsgiverIdentifikator = arbeidsgiverIdentifikator;
    }



    public String getArbeidsgiverIdentifikator() {
        return arbeidsgiverIdentifikator;
    }

    public String getArbeidsforholdId() {
        return arbeidsforholdId;
    }

    public ArbeidsgiverType getArbeidsgiverType() {
        return arbeidsgiverType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AktivitetIdentifikator that = (AktivitetIdentifikator) o;
        return arbeidsgiverType == that.arbeidsgiverType &&
                Objects.equals(arbeidsgiverIdentifikator, that.arbeidsgiverIdentifikator) &&
                Objects.equals(arbeidsforholdId, that.arbeidsforholdId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arbeidsgiverIdentifikator, arbeidsforholdId, arbeidsgiverType);
    }

    @Override
    public String toString() {
        return "AktivitetIdentifikator{" +
                "arbeidsgiverIdentifikator='" + arbeidsgiverIdentifikator + '\'' +
                ", arbeidsforholdId='" + arbeidsforholdId + '\'' +
                ", arbeidsgiverType=" + arbeidsgiverType +
                '}';
    }
}
