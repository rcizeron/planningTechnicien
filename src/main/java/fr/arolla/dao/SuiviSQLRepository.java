package fr.arolla.dao;

import fr.arolla.domain.SuiviDeTempsRepository;
import fr.arolla.domain.entities.SuiviDeTemps;

import java.util.Optional;

public class SuiviSQLRepository implements SuiviDeTempsRepository {

    @Override
    public void save(SuiviDeTemps suiviDeTemps) {
        // Sauvegarde en base de données (simulée)
        SuiviEntite entite = new SuiviEntite(
                suiviDeTemps.idSemaine(),
                suiviDeTemps.idTechnicien(),
                suiviDeTemps.planningDeBase().getPlagesHoraire(),
                suiviDeTemps.absences().getPlagesHoraire(),
                suiviDeTemps.sortiesAstreintes().getPlagesHoraire(),
                suiviDeTemps.evps(),
                suiviDeTemps.nbHeuresTravaillees(),
                suiviDeTemps.statut()
        );
        // Code pour insérer 'entite' dans la base de données irait ici
    }

    @Override
    public Optional<SuiviDeTemps> findById(int idSemaine, int idTechnicien) {
        // Récupération depuis la base de données (simulée)
        return Optional.empty();
    }
}
