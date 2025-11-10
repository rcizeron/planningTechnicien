package fr.arolla.domain;

import fr.arolla.domain.entities.SuiviDeTemps;

import java.util.Optional;

public interface SuiviDeTempsRepository {
    void save(SuiviDeTemps planningDTO);

    Optional<SuiviDeTemps> findById(int idSemaine, int idTechnicien);
}
