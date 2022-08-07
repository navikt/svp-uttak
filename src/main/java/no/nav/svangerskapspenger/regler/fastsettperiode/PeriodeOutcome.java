package no.nav.svangerskapspenger.regler.fastsettperiode;

import no.nav.fpsak.nare.evaluation.RuleReasonRef;
import no.nav.svangerskapspenger.domene.resultat.PeriodeÅrsak;
import no.nav.svangerskapspenger.domene.resultat.UtfallType;

public record PeriodeOutcome(PeriodeÅrsak periodeÅrsak, UtfallType utfallType) implements RuleReasonRef {

    @Override
    public String getReasonCode() {
        return String.valueOf(periodeÅrsak.getId());
    }

    @Override
    public String getReasonTextTemplate() {
        return periodeÅrsak.getBeskrivelse();
    }
}
