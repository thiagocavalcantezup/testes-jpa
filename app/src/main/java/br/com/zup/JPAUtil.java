package br.com.zup;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {

    private static final EntityManagerFactory EMFACTORY = Persistence.createEntityManagerFactory(
            "testes-jpa");

    public static EntityManager getEntityManager() {
        return EMFACTORY.createEntityManager();
    }

}
