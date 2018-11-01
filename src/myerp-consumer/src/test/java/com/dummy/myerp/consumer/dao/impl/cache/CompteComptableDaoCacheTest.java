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
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;

/**
 * Classe permettant d'effectuer des tests unitaires sur la classe {@link CompteComptableDaoCache}
 * @author André Monnier
 *
 */
public class CompteComptableDaoCacheTest {
	
	private static DaoProxy daoProxyMock=mock(DaoProxy.class);
	private static ComptabiliteDao comptabiliteDaoMock=mock(ComptabiliteDao.class);
	private CompteComptableDaoCache compteComptableDaoCache = new CompteComptableDaoCache();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception{
		ConsumerHelper.configure(daoProxyMock);
		when(daoProxyMock.getComptabiliteDao()).thenReturn(comptabiliteDaoMock);
	}

	/**
	 * Test de la méthode getByNumero(Integer pNumero) dans le cas d'une liste de CompteComptable comportant des éléments.
	 */
	@Test
	public void getByNumeroCase1() {

		List<CompteComptable> vListCompteComptable=new ArrayList<CompteComptable>();
		
		CompteComptable vCompteComptable1 = new CompteComptable(404, "CompteComptable 1");
		vListCompteComptable.add(vCompteComptable1);
		CompteComptable vCompteComptable2 = new CompteComptable(666, "CompteComptable 2");
		vListCompteComptable.add(vCompteComptable2);
		CompteComptable vCompteComptable3 = new CompteComptable(800, "CompteComptable 3");
		vListCompteComptable.add(vCompteComptable3);
		CompteComptable vCompteComptable4 = new CompteComptable(15000, "CompteComptable 4");
		vListCompteComptable.add(vCompteComptable4);
		
		when(comptabiliteDaoMock.getListCompteComptable()).thenReturn(vListCompteComptable);
		
		//Test des comptes comptables qui sont dans la liste.
		assertTrue(vListCompteComptable.toString(),compteComptableDaoCache.getByNumero(404).equals(vCompteComptable1));
		assertTrue(vListCompteComptable.toString(),compteComptableDaoCache.getByNumero(15000).equals(vCompteComptable4));
		assertTrue(vListCompteComptable.toString(),compteComptableDaoCache.getByNumero(666).equals(vCompteComptable2));
		assertTrue(vListCompteComptable.toString(),compteComptableDaoCache.getByNumero(800).equals(vCompteComptable3));
		
		//Test des comptes comptables qui ne sont pas dans la liste.
		assertNull(vListCompteComptable.toString(),compteComptableDaoCache.getByNumero(8));
		assertNull(vListCompteComptable.toString(),compteComptableDaoCache.getByNumero(-8));
		assertNull(vListCompteComptable.toString(),compteComptableDaoCache.getByNumero(0));
		
		//Test d'un numéro nul
		assertNull(vListCompteComptable.toString(),compteComptableDaoCache.getByNumero(null)); 
		
	}
	
	/**
	 * Test de la méthode getByNumero(Integer pNumero) dans le cas d'une liste de CompteComptable null.
	 */
	@Test
	public void getByNumeroCase2() {
		when(comptabiliteDaoMock.getListCompteComptable()).thenReturn(null); 
		assertNull("Erreur : l'objet retourné doit être nul",compteComptableDaoCache.getByNumero(404));
		assertNull("Erreur : l'objet retourné doit être nul",compteComptableDaoCache.getByNumero(0));
		assertNull("Erreur : l'objet retourné doit être nul",compteComptableDaoCache.getByNumero(-5));
		assertNull("Erreur : l'objet retourné doit être nul",compteComptableDaoCache.getByNumero(null));
	}
	
	/**
	 * Test de la méthode getByNumero(Integer pNumero) dans le cas d'une liste de CompteComptable vide.
	 */
	@Test
	public void getByNumeroCase3() {
		when(comptabiliteDaoMock.getListCompteComptable()).thenReturn(new ArrayList<CompteComptable>()); 
		assertNull("Erreur : l'objet retourné doit être nul",compteComptableDaoCache.getByNumero(404));
		assertNull("Erreur : l'objet retourné doit être nul",compteComptableDaoCache.getByNumero(0));
		assertNull("Erreur : l'objet retourné doit être nul",compteComptableDaoCache.getByNumero(-5));
		assertNull("Erreur : l'objet retourné doit être nul",compteComptableDaoCache.getByNumero(null));
	}
}
