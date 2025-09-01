package sn.team.gestion_loc_immeubles.DAO;

import sn.team.gestion_loc_immeubles.Entities.Unite;

import java.util.List;

public interface UniteDAO extends GenericDAO<Unite> {
    List<Unite> findByImmeuble(Long immeubleId);
}
