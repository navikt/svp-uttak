package no.nav.svangerskapspenger.regler.fastsettperiode.betingelser;

import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;
import no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag.FastsettePeriodeGrunnlag;
import no.nav.svangerskapspenger.tjeneste.fastsettuttak.SvpOppholdÅrsak;
@RuleDocumentation(SjekkSykepenger.ID)
public class SjekkSykepenger extends LeafSpecification<FastsettePeriodeGrunnlag> {
    public static final String ID = "SVP_VK 14.4.11";

    public SjekkSykepenger() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(FastsettePeriodeGrunnlag grunnlag) {
        var overlappendeSykepenger = grunnlag.getOppholdPerArbeidsforhold().stream()
            .filter(opphold -> opphold.getÅrsak().equals(SvpOppholdÅrsak.SYKEPENGER))
            .filter(opphold -> opphold.overlapper(grunnlag.getAktuellPeriode())).findAny();

        if (overlappendeSykepenger.isPresent()) {
            return ja();
        }

        return nei();
    }
}
