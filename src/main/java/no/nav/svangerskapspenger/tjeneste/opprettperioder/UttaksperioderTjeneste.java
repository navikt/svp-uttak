package no.nav.svangerskapspenger.tjeneste.opprettperioder;

import no.nav.svangerskapspenger.domene.resultat.Uttaksperioder;
import no.nav.svangerskapspenger.domene.søknad.Søknad;

import java.util.List;
import java.util.Set;

public interface UttaksperioderTjeneste {

    Set<ManuellBehandling> opprett(List<Søknad> søknader, Uttaksperioder uttaksperioder);

}
