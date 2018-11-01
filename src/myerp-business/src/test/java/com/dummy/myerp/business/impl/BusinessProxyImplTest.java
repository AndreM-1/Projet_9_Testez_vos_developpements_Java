package com.dummy.myerp.business.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import com.dummy.myerp.consumer.dao.contrat.DaoProxy;


/**
 * Classe permettant d'effectuer des tests unitaires sur la classe {@link BusinessProxyImpl}
 * @author André Monnier
 */
public class BusinessProxyImplTest {
	
	private static DaoProxy daoProxyMock=mock(DaoProxy.class);
	
	private TransactionManager transactionManagerMock=mock(TransactionManager.class);

	/**
	 * Test de la méthode getInstance() avec un daoProxy null. On s'attend à une exception de type UnsatisfiedLinkError.
	 */
	@Test(expected = UnsatisfiedLinkError.class)
	public void getInstanceCase1() {
		BusinessProxyImpl.getInstance();
	}
	
	/**
	 * Test de la méthode getInstance(DaoProxy pDaoProxy, TransactionManager pTransactionManager) avec un daoProxy null.
	 * On s'attend à une exception de type UnsatisfiedLinkError.
	 */
	@Test(expected = UnsatisfiedLinkError.class)
	public void getInstanceCase2() {
		BusinessProxyImpl.getInstance(null, transactionManagerMock);	
	}
	
	/**
	 * Test de la méthode getInstance(DaoProxy pDaoProxy, TransactionManager pTransactionManager).
	 * On vérifie que l'objet retourné est non nul et que le pattern Singleton est valide.
	 */
	@Test
	public void getInstanceCase3() {

		BusinessProxyImpl businessProxyImpl1=BusinessProxyImpl.getInstance(daoProxyMock, transactionManagerMock);	
		
		assertTrue("Erreur : l'objet de type BusinessProxyImpl n'a pas été instancié",businessProxyImpl1!=null);
		
		BusinessProxyImpl businessProxyImpl2=BusinessProxyImpl.getInstance(daoProxyMock, transactionManagerMock);	
		assertTrue("Erreur dans le pattern Singleton",businessProxyImpl2.equals(businessProxyImpl1));
		
		BusinessProxyImpl businessProxyImpl3=BusinessProxyImpl.getInstance();	
		assertTrue("Erreur dans le pattern Singleton",businessProxyImpl3.equals(businessProxyImpl1));
	}
	
}