package ru.kata.spring.boot_security.demo.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;

@Repository
public class RoleDaoImp implements RoleDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Role findByName(String name) {
        try {
            return entityManager.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void save(Role role) {
        entityManager.persist(role);
    }

    @Override
    public List<Role> findAll() {
        return entityManager.createQuery("SELECT r FROM Role r", Role.class)
                .getResultList();
    }

    @Override
    @Transactional
    public void delete(Role role) {
        // 1. Сначала удаляем все связи этой роли с пользователями
        entityManager.createNativeQuery("DELETE FROM users_roles WHERE role_id = :roleId")
                .setParameter("roleId", role.getId())
                .executeUpdate();

        // 2. Затем удаляем саму роль
        Role mergedRole = entityManager.merge(role); // Прикрепляем сущность к контексту
        entityManager.remove(mergedRole);
    }

    // Дополнительный полезный метод
    @Override
    public Role findById(Long id) {
        return entityManager.find(Role.class, id);
    }
}