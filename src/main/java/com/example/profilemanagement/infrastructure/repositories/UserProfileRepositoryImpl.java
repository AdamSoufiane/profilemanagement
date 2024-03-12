package com.example.profilemanagement.infrastructure.repositories;

import com.example.profilemanagement.domain.entities.UserProfileEntity;
import com.example.profilemanagement.domain.ports.UserProfileRepositoryPort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

@Repository
public class UserProfileRepositoryImpl implements UserProfileRepositoryPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public UserProfileEntity save(UserProfileEntity userProfileEntity) {
        try {
            if (userProfileEntity.getId() == null) {
                entityManager.persist(userProfileEntity);
            } else {
                entityManager.merge(userProfileEntity);
            }
            return userProfileEntity;
        } catch (Exception e) {
            throw new PersistenceException("Failed to save user profile entity", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserProfileEntity> findById(Long id) {
        UserProfileEntity userProfileEntity = entityManager.find(UserProfileEntity.class, id);
        return Optional.ofNullable(userProfileEntity);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        UserProfileEntity userProfileEntity = entityManager.find(UserProfileEntity.class, id);
        if (userProfileEntity != null) {
            entityManager.remove(userProfileEntity);
        } else {
            throw new EntityNotFoundException("Entity not found for id: " + id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileEntity> findAll() {
        return entityManager.createQuery("SELECT u FROM UserProfileEntity u", UserProfileEntity.class).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserProfileEntity> findByEmail(String email) {
        List<UserProfileEntity> users = entityManager.createQuery("SELECT u FROM UserProfileEntity u WHERE u.email = :email", UserProfileEntity.class)
                .setParameter("email", email)
                .getResultList();
        return users.stream().findFirst();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
