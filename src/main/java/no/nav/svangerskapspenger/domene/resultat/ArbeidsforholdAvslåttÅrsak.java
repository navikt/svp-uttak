package no.nav.svangerskapspenger.domene.resultat;

public enum ArbeidsforholdAvslåttÅrsak implements ArbeidsforholdÅrsak {

    UTTAK_KUN_ETTER_TRE_UKER_FØR_TERMINDATO(8301, "Uttak kun etter tre uker før termindato"),
    UTTAK_KUN_PÅ_HELG(8302, "Uttak kun på helg"),
    ARBEIDSGIVER_KAN_TILRETTELEGGE(8303, "Abeidsgiver kan tilrettelegge");

    private final int id;
    private final String beskrivelse;

    ArbeidsforholdAvslåttÅrsak(int id, String beskrivelse) {
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
