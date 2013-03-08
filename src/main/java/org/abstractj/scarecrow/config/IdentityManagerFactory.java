/*
 * Taken from PicketLink with small modifications
 *
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
