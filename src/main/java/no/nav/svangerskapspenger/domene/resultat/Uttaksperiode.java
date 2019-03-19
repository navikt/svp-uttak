package no.nav.svangerskapspenger.domene.resultat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import no.nav.svangerskapspenger.domene.felles.Tuple;
import no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag.LukketPeriode;

public class Uttaksperiode extends LukketPeriode {

    private final BigDecimal utbetalingsgrad;
    private UtfallType utfallType = UtfallType.UAVKLART;
    private PeriodeÅrsak årsak;
    private String sporingGrunnlag;
    private String sporingRegel;

    public Uttaksperiode(LocalDate fom,
                         LocalDate tom,
                         BigDecimal utbetalingsgrad) {
        super(fom, tom);
        this.utbetalingsgrad = utbetalingsgrad;
    }

    /**
     * Kopi-constructor for knekking.
     * NB: Husk å oppdatere denne når ny felt legges til.
     *
     */
    private Uttaksperiode(Uttaksperiode uttaksperiode, LocalDate fom, LocalDate tom) {
        this(fom, tom, uttaksperiode.utbetalingsgrad);
        this.årsak = uttaksperiode.årsak;
        this.utfallType =uttaksperiode.utfallType;
        this.sporingGrunnlag = uttaksperiode.sporingGrunnlag;
        this.sporingRegel = uttaksperiode.sporingRegel;
    }

    public void avslå(PeriodeÅrsak årsak, String sporingGrunnlag, String sporingRegel) {
        this.utfallType = UtfallType.AVSLÅTT;
        this.årsak = årsak;
        this.sporingGrunnlag = sporingGrunnlag;
        this.sporingRegel = sporingRegel;
    }

    public void innvilg(PeriodeÅrsak årsak, String sporingGrunnlag, String sporingRegel) {
        this.utfallType = UtfallType.INNVILGET;
        this.årsak = årsak;
        this.sporingGrunnlag = sporingGrunnlag;
        this.sporingRegel = sporingRegel;
    }

    public UtfallType getUtfallType() {
        return utfallType;
    }

    public Årsak getÅrsak() {
        return årsak;
    }

    public String getSporingGrunnlag() {
        return sporingGrunnlag;
    }

    public String getSporingRegel() {
        return sporingRegel;
    }

    public BigDecimal getUtbetalingsgrad() {
        return utbetalingsgrad;
    }

    Optional<Tuple<Uttaksperiode, Uttaksperiode>> knekk(LocalDate knekkpunkt) {
        if (knekkpunkt.isAfter(getFom()) && !knekkpunkt.isAfter(getTom())) {
            var periode1 = new Uttaksperiode(this, this.getFom(), knekkpunkt.minusDays(1));
            var periode2 = new Uttaksperiode(this, knekkpunkt, this.getTom());
            return Optional.of(new Tuple<>(periode1, periode2));
        }
        return Optional.empty();
    }

}
