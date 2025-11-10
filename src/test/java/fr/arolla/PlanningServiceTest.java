package fr.arolla;

import fr.arolla.domain.Absences;
import fr.arolla.domain.PlageHoraire;
import fr.arolla.domain.PlanningDeBase;
import fr.arolla.domain.SortiesAstreintes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanningServiceTest {

    @Mock
    private PlanningSQLRepository repository;

    @InjectMocks
    private PlanningService service;

    @Test
    void collecter_nullRequest_throws() {
        assertThrows(InvalidParameterException.class, () -> service.collecter(null));
        verifyNoInteractions(repository);
    }

    @Test
    void collecter_invalidIds_throwsWithMessages() {
        PlanningRequest request = new PlanningRequest(0, 0, null, null, null, null);
        InvalidParameterException ex = assertThrows(InvalidParameterException.class, () -> service.collecter(request));
        String msg = ex.getMessage();
        // vérifie que les deux messages d'erreur sur les ids sont présents
        assertTrue(msg.contains("idSemaine doit être supérieur à 0"));
        assertTrue(msg.contains("idTechnicien doit être supérieur à 0"));
        verifyNoInteractions(repository);
    }

    @Test
    void collecter_plageWithBadDates_throws() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 2, 12, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 2, 10, 0); // end before start
        PlageHoraire bad = new PlageHoraire(start, end);

        PlanningDeBase base = new PlanningDeBase(List.of(bad));

        PlanningRequest request = new PlanningRequest(1,
                                                      1,
                                                      base,
                                                      new Absences(List.of()),
                                                      new SortiesAstreintes(List.of()),
                                                      List.of());
        InvalidParameterException ex = assertThrows(InvalidParameterException.class, () -> service.collecter(request));
        String msg = ex.getMessage();
        assertTrue(msg.contains("planningDeBase[0] : dateDebut doit être avant dateFin") || msg.contains(
                "planningDeBase[0] :"));
        verify(repository, never()).findById(anyInt(), anyInt());
        verify(repository, never()).save(any());
    }

    @Test
    void collecter_evpsValidation_throws() {
        Evp nullEvp = null;
        Evp emptyType = new Evp("", LocalDateTime.now(), "");
        Evp noDate = new Evp("type", null, "");

        PlanningRequest request = new PlanningRequest(1,
                                                      1,
                                                      new PlanningDeBase(List.of()),
                                                      new Absences(List.of()),
                                                      new SortiesAstreintes(List.of()),
                                                      Arrays.asList(nullEvp, emptyType, noDate));

        InvalidParameterException ex = assertThrows(InvalidParameterException.class, () -> service.collecter(request));
        String msg = ex.getMessage();
        assertTrue(msg.contains("evps[0] est null"));
        assertTrue(msg.contains("evps[1].type est vide") || msg.contains("evps[1].type"));
        assertTrue(msg.contains("evps[2].date est null") || msg.contains("evps[2].date"));
        verifyNoInteractions(repository);
    }

    @Test
    void collecter_crossOverlap_throws() {
        LocalDateTime a1 = LocalDateTime.of(2025, 3, 3, 9, 0);
        LocalDateTime a2 = LocalDateTime.of(2025, 3, 3, 12, 0);
        LocalDateTime b1 = LocalDateTime.of(2025, 3, 3, 11, 0);
        LocalDateTime b2 = LocalDateTime.of(2025, 3, 3, 13, 0);
        PlageHoraire base = new PlageHoraire(a1, a2);
        PlageHoraire ast = new PlageHoraire(b1, b2);

        PlanningDeBase basePlage = new PlanningDeBase(List.of(base));
        SortiesAstreintes astPlage = new SortiesAstreintes(List.of(ast));

        PlanningRequest request = new PlanningRequest(1, 1, basePlage, new Absences(List.of()), astPlage, List.of());
        InvalidParameterException ex = assertThrows(InvalidParameterException.class, () -> service.collecter(request));
        String msg = ex.getMessage();
        assertTrue(msg.contains("Chevauchement entre planningDeBase[0] et sortiesAstreintes[0]") || msg.contains(
                "Chevauchement"));
        verifyNoInteractions(repository);
    }

    @Test
    void collecter_success_newPlanning_setsEnAttente_andCalculatesHours() {
        // planningDeBase: 08:00-12:00 (4h)
        // sortiesAstreintes: 13:00-15:00 (2h)
        // absences: none
        LocalDateTime d1 = LocalDateTime.of(2025, 5, 5, 8, 0);
        LocalDateTime d2 = LocalDateTime.of(2025, 5, 5, 12, 0);
        LocalDateTime d3 = LocalDateTime.of(2025, 5, 5, 13, 0);
        LocalDateTime d4 = LocalDateTime.of(2025, 5, 5, 15, 0);
        PlageHoraire base = new PlageHoraire(d1, d2);
        PlageHoraire ast = new PlageHoraire(d3, d4);

        PlanningDeBase basePlage = new PlanningDeBase(List.of(base));
        SortiesAstreintes astPlage = new SortiesAstreintes(List.of(ast));

        PlanningRequest request = new PlanningRequest(10, 42, basePlage, new Absences(List.of()), astPlage, List.of());

        when(repository.findById(10, 42)).thenReturn(Optional.empty());

        service.collecter(request);

        ArgumentCaptor<PlanningDTO> captor = ArgumentCaptor.forClass(PlanningDTO.class);
        verify(repository).save(captor.capture());
        PlanningDTO saved = captor.getValue();
        assertEquals(10, saved.idSemaine());
        assertEquals(42, saved.idTechnicien());
        assertEquals(6.0, saved.nbHeuresTravaillees());
        assertEquals("EN_ATTENTE", saved.statut());
    }

    @Test
    void collecter_existingPlanning_notEnAttente_setsAConfirmer() {
        LocalDateTime d1 = LocalDateTime.of(2025, 6, 6, 8, 0);
        LocalDateTime d2 = LocalDateTime.of(2025, 6, 6, 12, 0);
        PlageHoraire base = new PlageHoraire(d1, d2);

        PlanningDeBase basePlage = new PlanningDeBase(List.of(base));


        PlanningRequest request = new PlanningRequest(20,
                                                      7,
                                                      basePlage,
                                                      new Absences(List.of()),
                                                      new SortiesAstreintes(List.of()),
                                                      List.of());

        // existing planning has statut different from EN_ATTENTE
        PlanningDTO existing = new PlanningDTO(20, 7, null, null, null, null, 0.0, "VALIDE");
        when(repository.findById(20, 7)).thenReturn(Optional.of(existing));

        service.collecter(request);

        ArgumentCaptor<PlanningDTO> captor = ArgumentCaptor.forClass(PlanningDTO.class);
        verify(repository).save(captor.capture());
        PlanningDTO saved = captor.getValue();
        assertEquals("A_CONFIRMER", saved.statut());
    }

}
