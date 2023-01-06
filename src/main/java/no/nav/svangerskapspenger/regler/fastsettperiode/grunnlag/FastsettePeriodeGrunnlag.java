package no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag;

import no.nav.svangerskapspenger.domene.resultat.Uttaksperiode;
import no.nav.svangerskapspenger.domene.søknad.AvklarteDatoer;

public class FastsettePeriodeGrunnlag {

    private final AvklarteDatoer avklarteDatoer;
    private final Uttaksperiode aktuellPeriode;
    private final Inngangsvilkår inngangsvilkår;


    public FastsettePeriodeGrunnlag(
            AvklarteDatoer avklarteDatoer,
            Uttaksperiode periode,
            Inngangsvilkår inngangsvilkår) {
        this.avklarteDatoer = avklarteDatoer;
        this.aktuellPeriode = periode;
        this.inngangsvilkår = inngangsvilkår;
    }

    public AvklarteDatoer getAvklarteDatoer() {
        return avklarteDatoer;
    }

    public Uttaksperiode getAktuellPeriode() {
        return aktuellPeriode;
    }

    public Inngangsvilkår getInngangsvilkår() {
        return inngangsvilkår;
    }
}
