package no.nav.svangerskapspenger.domene.søknad;

import java.math.BigDecimal;
import java.time.LocalDate;

import no.nav.svangerskapspenger.domene.resultat.ArbeidsforholdAvslåttÅrsak;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperiode;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperioder;

public class FullTilrettelegging implements Tilrettelegging {

    private static final BigDecimal FULL_YTELSESGRAD = BigDecimal.valueOf(100L);

    private final LocalDate tilretteleggingArbeidsgiverDato;

    public FullTilrettelegging(LocalDate tilretteleggingArbeidsgiverDato) {
        this.tilretteleggingArbeidsgiverDato = tilretteleggingArbeidsgiverDato;
    }

    public LocalDate getTilretteleggingArbeidsgiverDato() {
        return tilretteleggingArbeidsgiverDato;
    }

    @Override
    public void opprettPerioder(Uttaksperioder uttaksperioder, Søknad søknad) {
        var arbeidsforhold = søknad.getArbeidsforhold();
        if(tilretteleggingArbeidsgiverDato.isAfter(søknad.getTilretteliggingBehovDato())) {
            if (tilretteleggingArbeidsgiverDato.isAfter(søknad.getTermindato().minusWeeks(3))) {
                uttaksperioder.leggTilPerioder(arbeidsforhold,
                        new Uttaksperiode(søknad.getTilretteliggingBehovDato(), søknad.getTermindato().minusWeeks(3).minusDays(1), FULL_YTELSESGRAD));
            } else {
                uttaksperioder.leggTilPerioder(arbeidsforhold,
                        new Uttaksperiode(søknad.getTilretteliggingBehovDato(), tilretteleggingArbeidsgiverDato.minusDays(1), FULL_YTELSESGRAD));
            }
        } else {
            uttaksperioder.leggTilPerioder(søknad.getArbeidsforhold());
            uttaksperioder.avslåForArbeidsforhold(arbeidsforhold, ArbeidsforholdAvslåttÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE);
        }
    }



}
