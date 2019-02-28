package no.nav.svangerskapspenger.regler.uttak.fastsetteperiode.betingelser;

import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;
import no.nav.svangerskapspenger.regler.uttak.fastsetteperiode.grunnlag.FastsettePeriodeGrunnlag;


@RuleDocumentation(SjekkFødselsdato.ID)
public class SjekkFødselsdato extends LeafSpecification<FastsettePeriodeGrunnlag> {

    public static final String ID = "FP_VK x.x";

    public SjekkFødselsdato() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(FastsettePeriodeGrunnlag grunnlag) {
        if (grunnlag.getAvklarteDatoer().getFødselsdato().isPresent()) {
            var startUttaksperiode = grunnlag.getAktuellPeriode().getFom();
            var fødsslsdato = grunnlag.getAvklarteDatoer().getFødselsdato().get();
            if (fødsslsdato.isBefore(grunnlag.getAvklarteDatoer().getTerminsdato().minusWeeks(3))) {
                if (startUttaksperiode.equals(fødsslsdato) || startUttaksperiode.isAfter(grunnlag.getAvklarteDatoer().getFørsteLovligeUttaksdag())) {
                    return ja();
                }
            }
        }
        return nei();
    }

}
