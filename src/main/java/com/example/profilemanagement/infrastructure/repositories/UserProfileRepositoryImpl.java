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
@Transactional
public class UserProfileRepositoryImpl implements UserProfileRepositoryPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UserProfileEntity save(UserProfileEntity userProfileEntity) {
        try {
            if (userProfileEntity.getId() == null) {
                entityManager.persist(userProfileEntity);
            } else {
                entityManager.merge(userProfileEntity);
            }
            return userProfileEntity;
        } catch (PersistenceException e) {
            throw new PersistenceException("Failed to save user profile entity", e);
        }
    }

    @Override
    public Optional<UserProfileEntity> findById(Long id) {
        UserProfileEntity userProfileEntity = entityManager.find(UserProfileEntity.class, id);
        return Optional.ofNullable(userProfileEntity);
    }

    @Override
    public void deleteById(Long id) {
        UserProfileEntity userProfileEntity = entityManager.find(UserProfileEntity.class, id);
        if (userProfileEntity != null) {
            entityManager.remove(userProfileEntity);
        } else {
            throw new EntityNotFoundException("Entity not found for id: " + id);
        }
    }

    @Override
    public List<UserProfileEntity> findAll() {
        try {
            return entityManager.createQuery("SELECT u FROM UserProfileEntity u", UserProfileEntity.class).getResultList();
        } catch (PersistenceException e) {
            throw new PersistenceException("Error occurred while retrieving all user profiles", e);
        }
    }
}
