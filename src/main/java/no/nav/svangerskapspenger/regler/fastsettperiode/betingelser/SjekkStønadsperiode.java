package no.nav.svangerskapspenger.regler.fastsettperiode.betingelser;

import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;
import no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag.FastsettePeriodeGrunnlag;

@RuleDocumentation(SjekkStønadsperiode.ID)
public class SjekkStønadsperiode extends LeafSpecification<FastsettePeriodeGrunnlag> {

    public static final String ID = "SVP_VK 14.4.10";

    public SjekkStønadsperiode() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(FastsettePeriodeGrunnlag grunnlag) {

        if (grunnlag.getAvklarteDatoer().getStartdatoNesteSak().isPresent()) {
            var startUttaksperiode = grunnlag.getAktuellPeriode().getFom();
            var startNesteSak = grunnlag.getAvklarteDatoer().getStartdatoNesteSak().get();
            if (startUttaksperiode.equals(startNesteSak) || startUttaksperiode.isAfter(startNesteSak)) {
                return ja();
            }
        }
        return nei();
    }

}
