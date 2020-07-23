package io.jmlim.taskagile.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;

import javax.persistence.EntityManager;

@RequiredArgsConstructor
abstract class HibernateSupport {
    final EntityManager entityManager;

    Session getSession() {
        return entityManager.unwrap(Session.class);
    }
}
