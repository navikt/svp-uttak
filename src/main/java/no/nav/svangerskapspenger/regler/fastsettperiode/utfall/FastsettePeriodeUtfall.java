package no.nav.svangerskapspenger.regler.fastsettperiode.utfall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.evaluation.RuleReasonRef;
import no.nav.fpsak.nare.evaluation.RuleReasonRefImpl;
import no.nav.fpsak.nare.evaluation.node.SingleEvaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;
import no.nav.svangerskapspenger.domene.resultat.PeriodeAvslåttÅrsak;
import no.nav.svangerskapspenger.domene.resultat.PeriodeInnvilgetÅrsak;
import no.nav.svangerskapspenger.domene.resultat.PeriodeÅrsak;
import no.nav.svangerskapspenger.domene.resultat.UtfallType;
import no.nav.svangerskapspenger.regler.fastsettperiode.FastsettePeriodeRegel;
import no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag.FastsettePeriodeGrunnlag;

public class FastsettePeriodeUtfall extends LeafSpecification<FastsettePeriodeGrunnlag> {

    private final PeriodeÅrsak periodeÅrsak;
    private final RuleReasonRef ruleReasonRef;
    private final List<BiConsumer<SingleEvaluation, FastsettePeriodeGrunnlag>> utfallSpesifiserere = new ArrayList<>();

    FastsettePeriodeUtfall(String id, PeriodeInnvilgetÅrsak periodeÅrsak) {
        this(id, periodeÅrsak, UtfallType.INNVILGET);
    }

    FastsettePeriodeUtfall(String id, PeriodeAvslåttÅrsak periodeÅrsak) {
        this(id, periodeÅrsak, UtfallType.AVSLÅTT);
    }

    private FastsettePeriodeUtfall(String id, PeriodeÅrsak periodeÅrsak, UtfallType utfallType) {
        super(id);
        if (periodeÅrsak == null) {
            throw new IllegalArgumentException("Årsak kan ikke være null.");
        }
        this.periodeÅrsak = periodeÅrsak;
        this.ruleReasonRef = new RuleReasonRefImpl(String.valueOf(periodeÅrsak.getId()), periodeÅrsak.getBeskrivelse());

        this.utfallSpesifiserere.add((singleEvaluation, grunnlag) -> {
            singleEvaluation.getEvaluationProperties().put(FastsettePeriodeRegel.FastsettePeriodePropertyType.UTFALL, utfallType);
            singleEvaluation.getEvaluationProperties().put(FastsettePeriodeRegel.FastsettePeriodePropertyType.ÅRSAK, periodeÅrsak);
        });
    }

    @Override
    public Evaluation evaluate(FastsettePeriodeGrunnlag grunnlag) {
        SingleEvaluation utfall = getHovedUtfall();
        spesifiserUtfall(utfall, grunnlag);
        return utfall;
    }

    private void spesifiserUtfall(SingleEvaluation utfall, FastsettePeriodeGrunnlag grunnlag) {
        if (utfallSpesifiserere.isEmpty()) {
            return;
        }
        utfall.setEvaluationProperties(new HashMap<>());
        utfallSpesifiserere.forEach(utfallSpesifiserer -> utfallSpesifiserer.accept(utfall, grunnlag));
    }

    private SingleEvaluation getHovedUtfall() {
        if (periodeÅrsak instanceof PeriodeInnvilgetÅrsak) {
            return ja(ruleReasonRef);
        }
        return nei(ruleReasonRef);
    }

}
