package no.nav.svangerskapspenger.domene.søknad;

import no.nav.svangerskapspenger.domene.resultat.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperiode;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperioder;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FullTilrettelegging implements Tilrettelegging {

    private static final BigDecimal FULL_UTBETALINGSGRAD = BigDecimal.valueOf(100L);

    private LocalDate tilretteleggingArbeidsgiverDato;

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
                        new Uttaksperiode(søknad.getTilretteliggingBehovDato(), søknad.sisteDagFørTermin(), FULL_UTBETALINGSGRAD));
            } else {
                uttaksperioder.leggTilPerioder(arbeidsforhold,
                        new Uttaksperiode(søknad.getTilretteliggingBehovDato(), tilretteleggingArbeidsgiverDato.minusDays(1), FULL_UTBETALINGSGRAD));
            }
        } else {
            uttaksperioder.leggTilPerioder(søknad.getArbeidsforhold());
            uttaksperioder.avslåForArbeidsforhold(arbeidsforhold, ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE);
        }
    }

    @Override
    public LocalDate getArbeidsgiversDato() {
        return tilretteleggingArbeidsgiverDato;
    }

    @Override
    public TilretteleggingKryss getTilretteleggingKryss() {
        return TilretteleggingKryss.A;
    }

    @Override
    public BigDecimal getTilretteleggingsprosent() {
        return BigDecimal.valueOf(100L);
    }

    @Override
    public void setArbeidsgiversDato(LocalDate arbeidsgiversDato) {
        this.tilretteleggingArbeidsgiverDato = arbeidsgiversDato;
    }

}
