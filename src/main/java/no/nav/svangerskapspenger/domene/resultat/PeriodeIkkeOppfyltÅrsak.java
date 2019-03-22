package no.nav.svangerskapspenger.domene.resultat;

public enum PeriodeIkkeOppfyltÅrsak implements PeriodeÅrsak {

    BRUKER_ER_DØD(8304, "Bruker er død"),
    BARN_ER_DØDT(8305, "Barnet/barna er dødt"),
    BRUKER_ER_IKKE_MEDLEM(8306, "Bruker er ikke medlem"),
    SØKT_FOR_SENT(8308, "Søkt for sent"),
    PERIODEN_ER_IKKE_FØR_FØDSEL(8309, "Perioden er ikke før fødsel"),
    PERIODEN_MÅ_SLUTTE_SENEST_TRE_UKER_FØR_TERMIN(8310, "Perioden må slutte senest tre uker før termin");

    private final int id;
    private final String beskrivelse;

    PeriodeIkkeOppfyltÅrsak(int id, String beskrivelse) {
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
