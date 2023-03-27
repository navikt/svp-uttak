package no.nav.svangerskapspenger.domene.resultat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import no.nav.svangerskapspenger.domene.felles.LukketPeriode;
import no.nav.svangerskapspenger.domene.felles.Tuple;

public class Uttaksperiode extends LukketPeriode {

    private BigDecimal utbetalingsgrad;
    private UtfallType utfallType = UtfallType.UAVKLART;
    private PeriodeÅrsak årsak;
    private String regelInput;
    private String regelSporing;

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
        this.regelInput = uttaksperiode.regelInput;
        this.regelSporing = uttaksperiode.regelSporing;
    }

    public void avslå(PeriodeÅrsak årsak, String sporingGrunnlag, String sporingRegel) {
        this.utfallType = UtfallType.IKKE_OPPFYLT;
        this.årsak = årsak;
        this.utbetalingsgrad = BigDecimal.ZERO;
        this.regelInput = sporingGrunnlag;
        this.regelSporing = sporingRegel;
    }

    public void innvilg(PeriodeÅrsak årsak, String sporingGrunnlag, String sporingRegel) {
        this.utfallType = UtfallType.OPPFYLT;
        this.årsak = årsak;
        this.regelInput = sporingGrunnlag;
        this.regelSporing = sporingRegel;
    }

    public UtfallType getUtfallType() {
        return utfallType;
    }

    public Årsak getÅrsak() {
        return årsak;
    }

    public String getRegelInput() {
        return regelInput;
    }

    public String getRegelSporing() {
        return regelSporing;
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
