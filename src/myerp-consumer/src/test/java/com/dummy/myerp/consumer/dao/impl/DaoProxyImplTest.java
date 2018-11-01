package com.dummy.myerp.consumer.dao.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Classe permettant d'effectuer des tests unitaires sur la classe {@link DaoProxyImpl}
 * @author André Monnier
 *
 */
public class DaoProxyImplTest {

	/**
	 * Test de la méthode getInstance().
	 * On vérifie que l'objet retourné est non nul et que le pattern Singleton est valide.
	 */
	@Test
	public void getInstance() {
		DaoProxyImpl daoProxyImpl1=DaoProxyImpl.getInstance();
		assertNotNull("DaoProxy non instancié",daoProxyImpl1);
		DaoProxyImpl daoProxyImpl2=DaoProxyImpl.getInstance();
		assertTrue("Erreur dans le pattern Singleton",daoProxyImpl2.equals(daoProxyImpl1));
	}
}