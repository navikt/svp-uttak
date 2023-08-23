package no.nav.svangerskapspenger.domene.søknad;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DelvisTilrettelegging implements Tilrettelegging {

    private LocalDate tilretteleggingArbeidsgiverDato;
    private final BigDecimal tilretteleggingsprosent;
    private final BigDecimal overstyrtUtbetalingsgrad;


    public DelvisTilrettelegging(LocalDate tilretteleggingArbeidsgiverDato, BigDecimal tilretteleggingsprosent, BigDecimal overstyrtUtbetalingsgrad) {
        this.tilretteleggingArbeidsgiverDato = tilretteleggingArbeidsgiverDato;
        this.tilretteleggingsprosent = tilretteleggingsprosent;
        this.overstyrtUtbetalingsgrad = overstyrtUtbetalingsgrad;
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
    @Override
    public BigDecimal getOverstyrtUtbetalingsgrad() {
        return overstyrtUtbetalingsgrad;
    }

    @Override
    public void setArbeidsgiversDato(LocalDate arbeidsgiversDato) {
        this.tilretteleggingArbeidsgiverDato = arbeidsgiversDato;
    }
}
