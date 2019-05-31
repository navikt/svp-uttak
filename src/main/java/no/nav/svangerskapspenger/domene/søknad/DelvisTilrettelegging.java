package no.nav.svangerskapspenger.domene.søknad;

import no.nav.svangerskapspenger.domene.resultat.Uttaksperiode;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperioder;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DelvisTilrettelegging implements Tilrettelegging {

    private static final BigDecimal FULL_UTBETALINGSGRAD = BigDecimal.valueOf(100L);

    private final LocalDate tilretteleggingArbeidsgiverDato;
    private final BigDecimal tilretteleggingsprosent;

    public DelvisTilrettelegging(LocalDate tilretteleggingArbeidsgiverDato, BigDecimal tilretteleggingsprosent) {
        this.tilretteleggingArbeidsgiverDato = tilretteleggingArbeidsgiverDato;
        this.tilretteleggingsprosent = tilretteleggingsprosent;
    }

    @Override
    public void opprettPerioder(Uttaksperioder uttaksperioder, Søknad søknad ) {
        if (tilretteleggingArbeidsgiverDato.isAfter(søknad.getTilretteliggingBehovDato())) {
            if (tilretteleggingArbeidsgiverDato.isBefore(søknad.getTermindato().minusWeeks(3))) {
                uttaksperioder.leggTilPerioder(søknad.getArbeidsforhold(),
                        new Uttaksperiode(søknad.getTilretteliggingBehovDato(), tilretteleggingArbeidsgiverDato.minusDays(1), FULL_UTBETALINGSGRAD),
                        new Uttaksperiode(tilretteleggingArbeidsgiverDato, søknad.sisteDagFørTermin(), FULL_UTBETALINGSGRAD.subtract(tilretteleggingsprosent))
                );
            } else {
                uttaksperioder.leggTilPerioder(søknad.getArbeidsforhold(),
                        new Uttaksperiode(søknad.getTilretteliggingBehovDato(), søknad.sisteDagFørTermin(), FULL_UTBETALINGSGRAD)
                );
            }
        } else {
            uttaksperioder.leggTilPerioder(søknad.getArbeidsforhold(),
                    new Uttaksperiode(søknad.getTilretteliggingBehovDato(), søknad.sisteDagFørTermin(), FULL_UTBETALINGSGRAD.subtract(tilretteleggingsprosent))
            );
        }
    }

    @Override
    public LocalDate getArbeidsgiversDato() {
        return tilretteleggingArbeidsgiverDato;
    }

    @Override
    public TilretteleggingKryss getTilretteleggingKryss() {
        return TilretteleggingKryss.B;
    }

    @Override
    public BigDecimal getTilretteleggingsprosent() {
        return tilretteleggingsprosent;
    }
}
