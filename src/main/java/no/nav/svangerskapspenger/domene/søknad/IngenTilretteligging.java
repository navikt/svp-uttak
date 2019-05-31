package no.nav.svangerskapspenger.domene.søknad;

import no.nav.svangerskapspenger.domene.resultat.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperiode;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperioder;

import java.math.BigDecimal;
import java.time.LocalDate;

public class IngenTilretteligging implements Tilrettelegging {

    private static final BigDecimal FULL_UTBETALINGSGRAD = BigDecimal.valueOf(100L);

    private final LocalDate tilretteleggingOpphørerDato;

    public IngenTilretteligging(LocalDate tilretteleggingOpphørerDato) {
        this.tilretteleggingOpphørerDato = tilretteleggingOpphørerDato;
    }

    @Override
    public void opprettPerioder(Uttaksperioder uttaksperioder, Søknad søknad) {
        var arbeidsforhold = søknad.getArbeidsforhold();
        if (tilretteleggingOpphørerDato.isAfter(søknad.getTilretteliggingBehovDato()) || tilretteleggingOpphørerDato.equals(søknad.getTilretteliggingBehovDato())) {
            if (tilretteleggingOpphørerDato.isBefore(søknad.sisteDagFørTermin().plusDays(1))) {
                uttaksperioder.leggTilPerioder(arbeidsforhold, new Uttaksperiode(tilretteleggingOpphørerDato, søknad.sisteDagFørTermin(), FULL_UTBETALINGSGRAD));
            } else {
                uttaksperioder.avslåForArbeidsforhold(arbeidsforhold, ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN);
            }
        } else {
            uttaksperioder.leggTilPerioder(arbeidsforhold, new Uttaksperiode(søknad.getTilretteliggingBehovDato(), søknad.sisteDagFørTermin(), FULL_UTBETALINGSGRAD));
        }
    }

    @Override
    public LocalDate getArbeidsgiversDato() {
        return tilretteleggingOpphørerDato;
    }

    @Override
    public TilretteleggingKryss getTilretteleggingKryss() {
        return TilretteleggingKryss.C;
    }

    @Override
    public BigDecimal getTilretteleggingsprosent() {
        return BigDecimal.ZERO;
    }


}
