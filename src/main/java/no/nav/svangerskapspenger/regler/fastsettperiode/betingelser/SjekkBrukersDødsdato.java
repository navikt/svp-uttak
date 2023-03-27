package no.nav.svangerskapspenger.regler.fastsettperiode.betingelser;

import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;
import no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag.FastsettePeriodeGrunnlag;

@RuleDocumentation(SjekkBrukersDødsdato.ID)
public class SjekkBrukersDødsdato extends LeafSpecification<FastsettePeriodeGrunnlag> {

    public static final String ID = "SVP_VK 14.4.1";

    public SjekkBrukersDødsdato() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(FastsettePeriodeGrunnlag grunnlag) {
        return grunnlag.getAvklarteDatoer().getBrukersDødsdato().map(brukersdødsdato -> {
            var startUttaksperiode = grunnlag.getAktuellPeriode().getFom();
            return startUttaksperiode.equals(brukersdødsdato) || startUttaksperiode.isAfter(brukersdødsdato) ? ja() : nei();
        }).orElse(nei());
    }

}
