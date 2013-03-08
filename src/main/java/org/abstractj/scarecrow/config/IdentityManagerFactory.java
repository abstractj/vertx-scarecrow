package org.abstractj.scarecrow.config;

import org.abstractj.scarecrow.util.PersistenceUtil;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.SecurityConfigurationException;
import org.picketlink.idm.config.IdentityConfiguration;
import org.picketlink.idm.internal.DefaultIdentityManager;
import org.picketlink.idm.internal.DefaultIdentityStoreInvocationContextFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class IdentityManagerFactory {

    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;

    public IdentityManagerFactory() {
        this.entityManagerFactory = PersistenceUtil.getEntityManagerFactory();
        this.entityManager = PersistenceUtil.getEntityManager();
    }

    public IdentityManager create() {
        IdentityManager identityManager = null;
        try {

            entityManager.getTransaction().begin();

            identityManager = new DefaultIdentityManager();
            IdentityConfiguration config = new IdentityConfiguration();
            config.addStoreConfiguration(PicketLinkIdentityStore.configuration());

            DefaultIdentityStoreInvocationContextFactory icf = new DefaultIdentityStoreInvocationContextFactory(entityManagerFactory);

            icf.setEntityManager(entityManager);
            identityManager.bootstrap(config, icf);

            entityManager.getTransaction().commit();

        } catch (SecurityConfigurationException e) {
            e.printStackTrace();
        }

        return identityManager;
    }
}
