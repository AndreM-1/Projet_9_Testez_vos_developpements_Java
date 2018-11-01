package com.dummy.myerp.consumer;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import com.dummy.myerp.consumer.dao.contrat.DaoProxy;

/**
 * Classe permettant d'effectuer des tests unitaires sur la classe {@link ConsumerHelper}
 * @author André Monnier
 *
 */
public class ConsumerHelperTest {

	private DaoProxy daoProxyMock=mock(DaoProxy.class);
	
	/**
	 * Test de la méthode configure(DaoProxy pDaoProxy)
	 */
	@Test
    public void configure() {
    	ConsumerHelper.configure(daoProxyMock);
    	assertTrue("Erreur lors de la configuration du DaoProxy ",ConsumerHelper.getDaoProxy().equals(daoProxyMock));
    }
}