package com.dummy.myerp.consumer.dao.impl.cache;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dummy.myerp.consumer.ConsumerHelper;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;

/**
 * Classe permettant d'effectuer des tests unitaires sur la classe {@link JournalComptableDaoCache}
 * @author André Monnier
 *
 */
public class JournalComptableDaoCacheTest {

	private static DaoProxy daoProxyMock=mock(DaoProxy.class);
	private static ComptabiliteDao comptabiliteDaoMock=mock(ComptabiliteDao.class);
	private JournalComptableDaoCache journalComptableDaoCache = new JournalComptableDaoCache();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception{
		ConsumerHelper.configure(daoProxyMock);
		when(daoProxyMock.getComptabiliteDao()).thenReturn(comptabiliteDaoMock);
	}
	
	/**
	 * Test de la méthode getByCode(String pCode) dans le cas d'une liste de JournalComptable comportant des éléments.
	 */
	@Test
	public void getByCodeCase1() {
		
		List<JournalComptable> vListJournalComptable = new ArrayList<JournalComptable>();
		
		JournalComptable vJournalComptable1 = new JournalComptable("AC123", "JournalComptable 1");
		vListJournalComptable.add(vJournalComptable1);
		JournalComptable vJournalComptable2 = new JournalComptable("VE2", "JournalComptable 2");
		vListJournalComptable.add(vJournalComptable2);
		JournalComptable vJournalComptable3 = new JournalComptable("Autre", "JournalComptable 3");
		vListJournalComptable.add(vJournalComptable3);
		JournalComptable vJournalComptable4 = new JournalComptable("Teste", "JournalComptable 4");
		vListJournalComptable.add(vJournalComptable4);
		
		when(comptabiliteDaoMock.getListJournalComptable()).thenReturn(vListJournalComptable);
		
		//Test des journaux qui sont dans la liste.
		assertTrue(vListJournalComptable.toString(),journalComptableDaoCache.getByCode("AC123").equals(vJournalComptable1));
		assertTrue(vListJournalComptable.toString(),journalComptableDaoCache.getByCode("VE2").equals(vJournalComptable2));
		assertTrue(vListJournalComptable.toString(),journalComptableDaoCache.getByCode("Autre").equals(vJournalComptable3));
		assertTrue(vListJournalComptable.toString(),journalComptableDaoCache.getByCode("Teste").equals(vJournalComptable4));
		
		//Test des journaux qui ne sont pas dans la liste.
		assertNull(vListJournalComptable.toString(),journalComptableDaoCache.getByCode("ZXTYU"));
		assertNull(vListJournalComptable.toString(),journalComptableDaoCache.getByCode("ac123"));
		
		//Test d'un code nul
		assertNull(vListJournalComptable.toString(),journalComptableDaoCache.getByCode(""));
	}
	
	/**
	 * Test de la méthode getByCode(String pCode) dans le cas d'une liste de JournalComptable null.
	 */
	@Test
	public void getByCodeCase2() {
		when(comptabiliteDaoMock.getListJournalComptable()).thenReturn(null); 
		assertNull("Erreur : l'objet retourné doit être nul",journalComptableDaoCache.getByCode("AC123"));
		assertNull("Erreur : l'objet retourné doit être nul",journalComptableDaoCache.getByCode("ZXTYU"));
		assertNull("Erreur : l'objet retourné doit être nul",journalComptableDaoCache.getByCode(""));
		assertNull("Erreur : l'objet retourné doit être nul",journalComptableDaoCache.getByCode(null));
	}
	
	/**
	 * Test de la méthode getByCode(String pCode) dans le cas d'une liste de JournalComptable vide.
	 */
	@Test
	public void getByCodeCase3() {
		when(comptabiliteDaoMock.getListJournalComptable()).thenReturn(new ArrayList<JournalComptable>()); 
		assertNull("Erreur : l'objet retourné doit être nul",journalComptableDaoCache.getByCode("AC123"));
		assertNull("Erreur : l'objet retourné doit être nul",journalComptableDaoCache.getByCode("ZXTYU"));
		assertNull("Erreur : l'objet retourné doit être nul",journalComptableDaoCache.getByCode(""));
		assertNull("Erreur : l'objet retourné doit être nul",journalComptableDaoCache.getByCode(null));
	}
}