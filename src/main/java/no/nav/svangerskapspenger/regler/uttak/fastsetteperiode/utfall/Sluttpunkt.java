package no.nav.svangerskapspenger.regler.uttak.fastsetteperiode.utfall;

import no.nav.svangerskapspenger.domene.resultat.PeriodeAvslåttÅrsak;
import no.nav.svangerskapspenger.domene.resultat.PeriodeInnvilgetÅrsak;

public class Sluttpunkt {

    private Sluttpunkt() {
        // For å hindre instanser
    }

    public static FastsettePeriodeUtfall innvilgelse(String id, PeriodeInnvilgetÅrsak årsak) {
        return new FastsettePeriodeUtfall(id, årsak);
    }

    public static FastsettePeriodeUtfall avslag(String id, PeriodeAvslåttÅrsak årsak) {
        return new FastsettePeriodeUtfall(id, årsak);
    }

}
