package no.nav.svangerskapspenger.regler.fastsettperiode.betingelser;

import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;
import no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag.FastsettePeriodeGrunnlag;

@RuleDocumentation(SjekkBrukersDødsdato.ID)
public class SjekkBrukersDødsdato extends LeafSpecification<FastsettePeriodeGrunnlag> {

    public static final String ID = "FP_VK x.x";

    public SjekkBrukersDødsdato() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(FastsettePeriodeGrunnlag grunnlag) {
        if (grunnlag.getAvklarteDatoer().getBrukersDødsdato().isPresent()) {
            var startUttaksperiode = grunnlag.getAktuellPeriode().getFom();
            var brukersdødsdato = grunnlag.getAvklarteDatoer().getBrukersDødsdato().get();
            if (startUttaksperiode.equals(brukersdødsdato) || startUttaksperiode.isAfter(brukersdødsdato)) {
                return ja();
            }
        }
        return nei();
    }

}
