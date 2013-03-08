package org.abstractj.scarecrow.util;

import org.hibernate.ejb.Ejb3Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.Properties;

public class PersistenceUtil {

    private static EntityManagerFactory entityManagerFactory = null;
    private static EntityManager entityManager = null;

    static {
        try {
            if (entityManagerFactory == null) {
                Properties properties = configuration();
                Ejb3Configuration cfg = new Ejb3Configuration();
                cfg.addProperties(properties);
                cfg.addAnnotatedClass(org.picketlink.idm.jpa.schema.IdentityObject.class);
                cfg.addAnnotatedClass(org.picketlink.idm.jpa.schema.PartitionObject.class);
                cfg.addAnnotatedClass(org.picketlink.idm.jpa.schema.RelationshipObject.class);
                cfg.addAnnotatedClass(org.picketlink.idm.jpa.schema.RelationshipIdentityObject.class);
                cfg.addAnnotatedClass(org.picketlink.idm.jpa.schema.RelationshipObjectAttribute.class);
                cfg.addAnnotatedClass(org.picketlink.idm.jpa.schema.IdentityObjectAttribute.class);
                cfg.addAnnotatedClass(org.picketlink.idm.jpa.schema.CredentialObject.class);
                cfg.addAnnotatedClass(org.picketlink.idm.jpa.schema.CredentialObjectAttribute.class);
                entityManagerFactory = cfg.buildEntityManagerFactory();
            }
        } catch (Throwable e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public static EntityManager getEntityManager() {
        entityManager = entityManagerFactory.createEntityManager();
        return entityManager;
    }

    public static EntityTransaction getTransaction() {
        return entityManager.getTransaction();
    }

    public void close() {
        if (entityManager.isOpen() && !entityManager.getTransaction().isActive()) {
            entityManager.close();
            entityManagerFactory.close();
        }
    }

    private static Properties configuration() {
        Properties properties = new Properties();
        properties.put("javax.persistence.provider", "org.hibernate.ejb.HibernatePersistence");
        properties.put("javax.persistence.transactionType", "RESOURCE_LOCAL");
        properties.put("hibernate.connection.username", "sa");
        properties.put("hibernate.connection.password", "");
        properties.put("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
        properties.put("hibernate.connection.url", "jdbc:hsqldb:.");
        properties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");
        return properties;
    }
}
