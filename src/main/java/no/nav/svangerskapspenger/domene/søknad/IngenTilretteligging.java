package no.nav.svangerskapspenger.domene.søknad;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

import no.nav.svangerskapspenger.domene.resultat.Uttaksperiode;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperioder;

public class IngenTilretteligging implements Tilrettelegging {

    private static final BigDecimal FULL_YTELSESGRAD = BigDecimal.valueOf(100L);

    private final LocalDate tilretteleggingOpphørerDato;

    public IngenTilretteligging(LocalDate tilretteleggingOpphørerDato) {
        this.tilretteleggingOpphørerDato = tilretteleggingOpphørerDato;
    }

    public LocalDate getTilretteleggingOpphørerDato() {
        return tilretteleggingOpphørerDato;
    }

    @Override
    public void opprettPerioder(Uttaksperioder uttaksperioder, Søknad søknad) {
        if (nesteUkedag(søknad.getTilretteliggingBehovDato()).equals(nesteUkedag(tilretteleggingOpphørerDato))) {
            uttaksperioder.leggTilPerioder(søknad.getArbeidsforhold(), new Uttaksperiode(søknad.getTilretteliggingBehovDato(), søknad.sisteDagFørTermin(), FULL_YTELSESGRAD));
        } else {
            uttaksperioder.leggTilPerioder(søknad.getArbeidsforhold(), new Uttaksperiode(tilretteleggingOpphørerDato, søknad.sisteDagFørTermin(), FULL_YTELSESGRAD));
        }
    }

    private LocalDate nesteUkedag(LocalDate dato) {
        if (dato.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
            return dato.plusDays(2);
        } else if(dato.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            return dato.plusDays(1);
        }
        return dato;
    }

}
