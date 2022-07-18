package br.com.zup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AppTest {

    EntityManager entityManager;
    Entidade entidade1;
    Entidade entidade2;

    @BeforeEach
    void setUp() {
        entityManager = JPAUtil.getEntityManager();
        entityManager.getTransaction().begin();
        entityManager.createQuery("DELETE FROM Entidade").executeUpdate();
        entityManager.getTransaction().commit();

        entidade1 = new Entidade("Entidade1");
    }

    @AfterEach
    void cleanUp() {
        entityManager.close();
    }

    @Test
    @DisplayName("Entidade 'transient' não está no Contexto de Persistência")
    void entidadeTransientNaoEstaNoContextoDePersistencia() {
        assertNull(entidade1.getId());
        assertFalse(entityManager.contains(entidade1));
    }

    @Test
    @DisplayName("Entidade 'transient' se torna 'managed' após 'persist()', está no Contexto de Persistencia e não recebe 'id' fora da transação")
    void entidadeTransientSeTornaManagedAposPersistEstaNoContextoDePersistenciaENaoRecebeIdForaDaTransacao() {
        entityManager.persist(entidade1);

        assertNull(entidade1.getId());
        assertTrue(entityManager.contains(entidade1));
    }

    @Test
    @DisplayName("Entidade 'transient' se torna 'managed' após 'persist()', está no Contexto de Persistencia e recebe 'id' dentro da transação")
    void entidadeTransientSeTornaManagedAposPersistEstaNoContextoDePersistenciaERecebeIdDentroDaTransacao() {
        entityManager.getTransaction().begin();
        entityManager.persist(entidade1);
        entityManager.getTransaction().commit();

        assertNotNull(entidade1.getId());
        assertTrue(entityManager.contains(entidade1));
    }

    @Test
    @DisplayName("Entidade 'detached' com 'detach()' não está no Contexto de Persistência")
    void entidadeDetachedComDetachNaoEstaNoContextoDePersistencia() {
        entityManager.getTransaction().begin();
        entityManager.persist(entidade1);
        entityManager.getTransaction().commit();

        entityManager.detach(entidade1);

        assertNotNull(entidade1.getId());
        assertFalse(entityManager.contains(entidade1));
    }

    @Test
    @DisplayName("Entidade 'detached' com 'clear()' não está no Contexto de Persistência")
    void entidadeDetachedComClearNaoEstaNoContextoDePersistencia() {
        entityManager.getTransaction().begin();
        entityManager.persist(entidade1);
        entityManager.getTransaction().commit();

        entityManager.clear();

        assertNotNull(entidade1.getId());
        assertFalse(entityManager.contains(entidade1));
    }

    @Test
    @DisplayName("Entidade 'detached' com 'close()' não está no Contexto de Persistência")
    void entidadeDetachedComCloseNaoEstaNoContextoDePersistencia() {
        entityManager.getTransaction().begin();
        entityManager.persist(entidade1);
        entityManager.getTransaction().commit();

        entityManager.close();
        entityManager = JPAUtil.getEntityManager();

        assertNotNull(entidade1.getId());
        assertFalse(entityManager.contains(entidade1));
    }

    @Test
    @DisplayName("Entidade 'detached' retorna como 'managed' na chamada do 'merge()' e está no Contexto de Persistência")
    void entidadeDetachedRetornaComoManagedNaChamadaDoMergeEEstaNoContextoDePersistencia() {
        entityManager.getTransaction().begin();
        entityManager.persist(entidade1);
        entityManager.getTransaction().commit();

        entityManager.detach(entidade1);
        Entidade entidadeRetorno = entityManager.merge(entidade1);

        assertNotNull(entidadeRetorno.getId());
        assertTrue(entityManager.contains(entidadeRetorno));
    }

    @Test
    @DisplayName("Entidade 'removed' não está no Contexto de Persistência")
    void entidadeRemovedNaoEstaNoContextoDePersistencia() {
        entityManager.getTransaction().begin();
        entityManager.persist(entidade1);
        entityManager.remove(entidade1);
        entityManager.getTransaction().commit();

        assertNotNull(entidade1.getId());
        assertFalse(entityManager.contains(entidade1));
    }

    @Test
    @DisplayName("Entidade 'removed' se torna 'managed' após 'persist()' e está no Contexto de Persistência")
    void entidadeRemovedSeTornaManagedAposPersistEEstaNoContextoDePersistencia() {
        entityManager.getTransaction().begin();
        entityManager.persist(entidade1);
        entityManager.remove(entidade1);
        entityManager.persist(entidade1);
        entityManager.getTransaction().commit();

        assertNotNull(entidade1.getId());
        assertTrue(entityManager.contains(entidade1));
    }

    @Test
    @DisplayName("Entidade 'managed' é salva no banco após 'flush()'")
    void entidadeManagedESalvaNoBancoAposFlush() {
        entityManager.persist(entidade1);

        assertEquals(0L, count());

        entityManager.getTransaction().begin();
        entityManager.flush();
        assertEquals(1L, count());
        entityManager.getTransaction().commit();
    }

    @Test
    @DisplayName("Entidade extraída do banco com 'getReference()' está 'managed'")
    void entidadeExtraidaDoBancoComGetReferenceEstaManaged() {
        entityManager.getTransaction().begin();
        entityManager.persist(entidade1);
        entityManager.getTransaction().commit();

        Entidade entidadeSalva = entityManager.getReference(Entidade.class, entidade1.getId());

        assertNotNull(entidadeSalva.getId());
        assertEquals(entidade1.getId(), entidadeSalva.getId());
        assertTrue(entityManager.contains(entidadeSalva));
    }

    @Test
    @DisplayName("Entidade extraída do banco com 'find()' está 'managed'")
    void entidadeExtraidaDoBancoComFindEstaManaged() {
        entityManager.getTransaction().begin();
        entityManager.persist(entidade1);
        entityManager.getTransaction().commit();

        Entidade entidadeSalva = entityManager.find(Entidade.class, entidade1.getId());

        assertNotNull(entidadeSalva.getId());
        assertEquals(entidade1.getId(), entidadeSalva.getId());
        assertTrue(entityManager.contains(entidadeSalva));
    }

    @Test
    @DisplayName("Entidade extraída do banco com 'getSingleResult()' está 'managed'")
    void entidadeExtraidaDoBancoComGetSingleResultEstaManaged() {
        entityManager.getTransaction().begin();
        entityManager.persist(entidade1);
        entityManager.getTransaction().commit();

        Entidade entidadeSalva = entityManager.createQuery("SELECT e FROM Entidade e WHERE e.id=:id", Entidade.class)
                .setParameter("id", entidade1.getId()).getSingleResult();

        assertNotNull(entidadeSalva.getId());
        assertEquals(entidade1.getId(), entidadeSalva.getId());
        assertTrue(entityManager.contains(entidadeSalva));
    }

    @Test
    @DisplayName("Entidades extraídas do banco com 'getResultList()' estão 'managed'")
    void entidadesExtraidasDoBancoComGetResultListEstaoManaged() {
        entidade2 = new Entidade("Entidade2");

        entityManager.getTransaction().begin();
        entityManager.persist(entidade1);
        entityManager.persist(entidade2);
        entityManager.getTransaction().commit();

        List<Entidade> entidadesSalvas = entityManager.createQuery("SELECT e FROM Entidade e", Entidade.class)
                .getResultList();

        assertEquals(2, entidadesSalvas.size());
        assertTrue(entityManager.contains(entidadesSalvas.get(0)));
        assertTrue(entityManager.contains(entidadesSalvas.get(1)));
    }

    @Test
    @DisplayName("Entidade 'removed' é deletada do banco após 'flush()'")
    void entidadeRemovedEDeletadaDoBancoAposFlush() {
        entityManager.getTransaction().begin();
        entityManager.persist(entidade1);
        entityManager.getTransaction().commit();

        Entidade entidadeSalva = entityManager.getReference(Entidade.class, entidade1.getId());
        assertEquals(1L, count());

        entityManager.remove(entidadeSalva);

        entityManager.getTransaction().begin();
        entityManager.flush();
        assertEquals(0L, count());
        entityManager.getTransaction().commit();
    }

    private Long count() {
        String countQuery = "SELECT COUNT(*) FROM Entidade";
        return entityManager.createQuery(countQuery, Long.class).getSingleResult();
    }
}
