package no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag;

import no.nav.svangerskapspenger.domene.resultat.Uttaksperiode;
import no.nav.svangerskapspenger.domene.søknad.AvklarteDatoer;

public class FastsettePeriodeGrunnlag {

    private final AvklarteDatoer avklarteDatoer;
    private final Uttaksperiode aktuellPeriode;

    public FastsettePeriodeGrunnlag(
            AvklarteDatoer avklarteDatoer,
            Uttaksperiode periode) {
        this.avklarteDatoer = avklarteDatoer;
        this.aktuellPeriode = periode;
    }

    public AvklarteDatoer getAvklarteDatoer() {
        return avklarteDatoer;
    }

    public Uttaksperiode getAktuellPeriode() {
        return aktuellPeriode;
    }

}
