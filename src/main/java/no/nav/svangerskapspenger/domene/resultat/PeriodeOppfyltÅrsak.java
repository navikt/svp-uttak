package no.nav.svangerskapspenger.domene.resultat;

public enum PeriodeOppfyltÅrsak implements PeriodeÅrsak {

    UTTAK_ER_INNVILGET(8601, "Uttak er innvilget");

    private final int id;
    private final String beskrivelse;

    PeriodeOppfyltÅrsak(int id, String beskrivelse) {
        this.id = id;
        this.beskrivelse = beskrivelse;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getBeskrivelse() {
        return beskrivelse;
    }
}
