package no.nav.svangerskapspenger.domene.resultat;

public enum ArbeidsforholdInnvilgetÅrsak implements ArbeidsforholdÅrsak {

    ARBEIDSGIVER_KAN_TILRETTELEGGE(8303, "Abeidsgiver kan tilrettelegge");

    private final int id;
    private final String beskrivelse;

    ArbeidsforholdInnvilgetÅrsak(int id, String beskrivelse) {
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
