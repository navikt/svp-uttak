package no.nav.svangerskapspenger.regler.fastsettperiode;

import no.nav.svangerskapspenger.domene.resultat.PeriodeAvslåttÅrsak;
import no.nav.svangerskapspenger.domene.resultat.PeriodeInnvilgetÅrsak;

class Sluttpunkt {

    private Sluttpunkt() {
        // For å hindre instanser
    }

    static FastsettePeriodeUtfall innvilgelse(String id, PeriodeInnvilgetÅrsak årsak) {
        return new FastsettePeriodeUtfall(id, årsak);
    }

    static FastsettePeriodeUtfall avslag(String id, PeriodeAvslåttÅrsak årsak) {
        return new FastsettePeriodeUtfall(id, årsak);
    }

}
