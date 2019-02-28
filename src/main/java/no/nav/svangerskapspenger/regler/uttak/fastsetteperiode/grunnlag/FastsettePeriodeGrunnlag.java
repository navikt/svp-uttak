package no.nav.svangerskapspenger.regler.uttak.fastsetteperiode.grunnlag;

import no.nav.svangerskapspenger.domene.søknad.AvklarteDatoer;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperiode;

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
