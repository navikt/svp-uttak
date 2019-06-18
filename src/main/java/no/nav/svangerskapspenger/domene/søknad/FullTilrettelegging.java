package no.nav.svangerskapspenger.domene.søknad;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FullTilrettelegging implements Tilrettelegging {

    private LocalDate tilretteleggingArbeidsgiverDato;

    public FullTilrettelegging(LocalDate tilretteleggingArbeidsgiverDato) {
        this.tilretteleggingArbeidsgiverDato = tilretteleggingArbeidsgiverDato;
    }

    public LocalDate getTilretteleggingArbeidsgiverDato() {
        return tilretteleggingArbeidsgiverDato;
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
