package no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag;

import no.nav.svangerskapspenger.domene.felles.arbeid.Arbeidsprosenter;
import no.nav.svangerskapspenger.domene.søknad.AvklarteDatoer;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperiode;

public class FastsettePeriodeGrunnlag {

    private final AvklarteDatoer avklarteDatoer;
    private final Arbeidsprosenter arbeidsprosenter;
    private final Uttaksperiode aktuellPeriode;

    public FastsettePeriodeGrunnlag(
            AvklarteDatoer avklarteDatoer,
            Arbeidsprosenter arbeidsprosenter,
            Uttaksperiode periode) {
        this.avklarteDatoer = avklarteDatoer;
        this.arbeidsprosenter = arbeidsprosenter;
        this.aktuellPeriode = periode;
    }

    public AvklarteDatoer getAvklarteDatoer() {
        return avklarteDatoer;
    }

    public Arbeidsprosenter getArbeidsprosenter() {
        return arbeidsprosenter;
    }

    public Uttaksperiode getAktuellPeriode() {
        return aktuellPeriode;
    }

}
