package no.nav.svangerskapspenger.regler.fastsettperiode;

import no.nav.svangerskapspenger.domene.resultat.PeriodeIkkeOppfyltÅrsak;
import no.nav.svangerskapspenger.domene.resultat.PeriodeOppfyltÅrsak;

class Sluttpunkt {

    private Sluttpunkt() {
        // For å hindre instanser
    }

    static FastsettePeriodeUtfall oppfylt(String id, PeriodeOppfyltÅrsak årsak) {
        return new FastsettePeriodeUtfall(id, årsak);
    }

    static FastsettePeriodeUtfall ikkeOppfylt(String id, PeriodeIkkeOppfyltÅrsak årsak) {
        return new FastsettePeriodeUtfall(id, årsak);
    }

}
