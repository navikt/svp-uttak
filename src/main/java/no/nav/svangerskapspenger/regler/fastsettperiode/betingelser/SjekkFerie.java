package no.nav.svangerskapspenger.regler.fastsettperiode.betingelser;

import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;
import no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag.FastsettePeriodeGrunnlag;

@RuleDocumentation(SjekkFerie.ID)
public class SjekkFerie extends LeafSpecification<FastsettePeriodeGrunnlag> {

    public static final String ID = "SVP_VK 14.4.8";

    public SjekkFerie() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(FastsettePeriodeGrunnlag grunnlag) {
        var overlappendeFerie = grunnlag.getAvklarteDatoer().getFerier().stream()
            .filter(ferie -> ferie.overlapper(grunnlag.getAktuellPeriode())).findAny();

        if (overlappendeFerie.isPresent()) {
            return ja();
        }

        return nei();
    }

}
