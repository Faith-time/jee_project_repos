package sn.team.gestion_loc_immeubles.DAO;

import jakarta.persistence.EntityManager;
import sn.team.gestion_loc_immeubles.Util.JPAUtil;

import java.util.List;

public abstract class GenericDAOImpl<T> implements GenericDAO<T> {

    protected final Class<T> entityClass;

    public GenericDAOImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected EntityManager getEntityManager() {
        return JPAUtil.getEntityManager();
    }

    @Override
    public void save(T entity) {
        EntityManager em = getEntityManager();
        try {
            JPAUtil.beginTransaction();
            em.persist(entity);
            JPAUtil.commitTransaction();
        } catch (Exception e) {
            JPAUtil.rollbackTransaction();
            throw new RuntimeException("Erreur lors de la sauvegarde: " + e.getMessage(), e);
        }
    }

    @Override
    public T findById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(entityClass, id);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche par ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<T> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("FROM " + entityClass.getSimpleName(), entityClass)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération de tous les éléments: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(T entity) {
        EntityManager em = getEntityManager();
        try {
            JPAUtil.beginTransaction();
            em.merge(entity);
            JPAUtil.commitTransaction();
        } catch (Exception e) {
            JPAUtil.rollbackTransaction();
            throw new RuntimeException("Erreur lors de la mise à jour: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) {
        EntityManager em = getEntityManager();
        try {
            JPAUtil.beginTransaction();
            T entity = findById(id);
            if (entity != null) {
                em.remove(em.contains(entity) ? entity : em.merge(entity));
            }
            JPAUtil.commitTransaction();
        } catch (Exception e) {
            JPAUtil.rollbackTransaction();
            throw new RuntimeException("Erreur lors de la suppression: " + e.getMessage(), e);
        }
    }
}