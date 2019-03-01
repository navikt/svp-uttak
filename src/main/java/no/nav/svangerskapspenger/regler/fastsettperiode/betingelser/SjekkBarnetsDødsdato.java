package no.nav.svangerskapspenger.regler.fastsettperiode.betingelser;

import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;
import no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag.FastsettePeriodeGrunnlag;

@RuleDocumentation(SjekkBarnetsDødsdato.ID)
public class SjekkBarnetsDødsdato extends LeafSpecification<FastsettePeriodeGrunnlag> {

    public static final String ID = "FP_VK x.x";

    public SjekkBarnetsDødsdato() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(FastsettePeriodeGrunnlag grunnlag) {
        if (grunnlag.getAvklarteDatoer().getBarnetsDødsdato().isPresent()) {
            var startUttaksperiode = grunnlag.getAktuellPeriode().getFom();
            var barnetsDødsdato = grunnlag.getAvklarteDatoer().getBarnetsDødsdato().get();
            if (startUttaksperiode.equals(barnetsDødsdato) || startUttaksperiode.isAfter(barnetsDødsdato)) {
                return ja();
            }
        }
        return nei();
    }

}
