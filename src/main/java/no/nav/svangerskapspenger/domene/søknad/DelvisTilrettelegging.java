package no.nav.svangerskapspenger.domene.søknad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public class DelvisTilrettelegging implements Tilrettelegging {

    private LocalDate tilretteleggingArbeidsgiverDato;
    private final BigDecimal tilretteleggingsprosent;
    private Optional<BigDecimal> overstyrtUtbetalingsgrad;


    public DelvisTilrettelegging(LocalDate tilretteleggingArbeidsgiverDato, BigDecimal tilretteleggingsprosent, BigDecimal overstyrtUtbetalingsgrad) {
        this.tilretteleggingArbeidsgiverDato = tilretteleggingArbeidsgiverDato;
        this.tilretteleggingsprosent = tilretteleggingsprosent;
        this.overstyrtUtbetalingsgrad = Optional.ofNullable(overstyrtUtbetalingsgrad);
    }
    public DelvisTilrettelegging(LocalDate tilretteleggingArbeidsgiverDato, BigDecimal tilretteleggingsprosent) {
        this(tilretteleggingArbeidsgiverDato, tilretteleggingsprosent, null);
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
    public void setArbeidsgiversDato(LocalDate arbeidsgiversDato) {
        this.tilretteleggingArbeidsgiverDato = arbeidsgiversDato;
    }

    public Optional<BigDecimal> getOverstyrtUtbetalingsgrad() {
        return overstyrtUtbetalingsgrad;
    }

}
