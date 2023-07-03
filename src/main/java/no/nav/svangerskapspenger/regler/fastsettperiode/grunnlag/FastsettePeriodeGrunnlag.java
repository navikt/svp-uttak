package no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag;

import java.util.List;

import no.nav.svangerskapspenger.domene.resultat.Uttaksperiode;
import no.nav.svangerskapspenger.domene.søknad.AvklarteDatoer;
import no.nav.svangerskapspenger.domene.søknad.Opphold;

public class FastsettePeriodeGrunnlag {

    private final AvklarteDatoer avklarteDatoer;
    private final Uttaksperiode aktuellPeriode;
    private final Inngangsvilkår inngangsvilkår;
    private final List<Opphold> oppholdPerArbeidsforhold;


    public FastsettePeriodeGrunnlag(
        AvklarteDatoer avklarteDatoer,
        Uttaksperiode periode,
        Inngangsvilkår inngangsvilkår,
        List<Opphold> oppholdPerArbeidsforhold) {
        this.avklarteDatoer = avklarteDatoer;
        this.aktuellPeriode = periode;
        this.inngangsvilkår = inngangsvilkår;
        this.oppholdPerArbeidsforhold = oppholdPerArbeidsforhold;
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

    public List<Opphold> getOppholdPerArbeidsforhold() {
        return oppholdPerArbeidsforhold;
    }
}
