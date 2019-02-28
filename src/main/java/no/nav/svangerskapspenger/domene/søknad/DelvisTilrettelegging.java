package no.nav.svangerskapspenger.domene.søknad;

import java.math.BigDecimal;
import java.time.LocalDate;

import no.nav.svangerskapspenger.domene.resultat.Uttaksperiode;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperioder;

public class DelvisTilrettelegging implements Tilrettelegging {

    private static final BigDecimal FULL_YTELSESGRAD = BigDecimal.valueOf(100L);

    private final LocalDate tilretteleggingArbeidsgiverDato;
    private final BigDecimal tilretteleggingsprosent;

    public DelvisTilrettelegging(LocalDate tilretteleggingArbeidsgiverDato, BigDecimal tilretteleggingsprosent) {
        this.tilretteleggingArbeidsgiverDato = tilretteleggingArbeidsgiverDato;
        this.tilretteleggingsprosent = tilretteleggingsprosent;
    }

    public LocalDate getTilretteleggingArbeidsgiverDato() {
        return tilretteleggingArbeidsgiverDato;
    }

    public BigDecimal getTilretteleggingsprosent() {
        return tilretteleggingsprosent;
    }

    @Override
    public void opprettPerioder(Uttaksperioder uttaksperioder, Søknad søknad ) {
        if (tilretteleggingArbeidsgiverDato.isAfter(søknad.getTilretteliggingBehovDato())) {
            if (tilretteleggingArbeidsgiverDato.isBefore(søknad.getTermindato().minusWeeks(3))) {
                uttaksperioder.leggTilPerioder(søknad.getArbeidsforhold(),
                        new Uttaksperiode(søknad.getTilretteliggingBehovDato(), tilretteleggingArbeidsgiverDato.minusDays(1), FULL_YTELSESGRAD),
                        new Uttaksperiode(tilretteleggingArbeidsgiverDato, søknad.sisteDagFørTermin(), FULL_YTELSESGRAD.subtract(tilretteleggingsprosent))
                );
            } else {
                uttaksperioder.leggTilPerioder(søknad.getArbeidsforhold(),
                        new Uttaksperiode(søknad.getTilretteliggingBehovDato(), søknad.sisteDagFørTermin(), FULL_YTELSESGRAD)
                );
            }
        } else {
            uttaksperioder.leggTilPerioder(søknad.getArbeidsforhold(),
                    new Uttaksperiode(søknad.getTilretteliggingBehovDato(), søknad.sisteDagFørTermin(), FULL_YTELSESGRAD.subtract(tilretteleggingsprosent))
            );
        }
    }

}
