package io.jmlim.taskagile.infrastructure.repository;

import io.jmlim.taskagile.domain.model.user.User;
import io.jmlim.taskagile.domain.model.user.UserRepository;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
public class HibernateUserRepository extends HibernateSupport implements UserRepository {

    public HibernateUserRepository(EntityManager entityManager) {
        super(entityManager);
    }

    /**
     * Find user by a name
     *
     * @param username
     * @return
     */
    @Override
    public Optional<User> findByUsername(String username) {
        Query<User> query = getSession().createQuery("from User where username = :username", User.class);
        query.setParameter("username", username);
        return query.uniqueResultOptional();
    }

    /**
     * Find user by an email address
     *
     * @param emailAddress
     * @return
     */
    @Override
    public Optional<User> findByEmailAddress(String emailAddress) {
        Query<User> query = getSession().createQuery("from User where emailAddress = :emailAddress", User.class);
        query.setParameter("emailAddress", emailAddress);
        return query.uniqueResultOptional();
    }

    /**
     * Save a new user or an existing user
     *
     * @param user
     */
    @Override
    public void save(User user) {
        entityManager.persist(user);
        entityManager.flush();
    }
}
