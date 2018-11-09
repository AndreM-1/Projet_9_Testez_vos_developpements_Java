package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.dummy.myerp.business.contrat.BusinessProxy;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;

/**
 * Classe permettant d'effectuer des tests unitaires sur la classe {@link ComptabiliteManagerImpl}
 * @author André Monnier
 *
 */
public class ComptabiliteManagerImplTest {

	private ComptabiliteManagerImpl manager = new ComptabiliteManagerImpl();
	
	private static BusinessProxy businessProxyMock=mock(BusinessProxy.class);
	
	private static DaoProxy daoProxyMock=mock(DaoProxy.class);
	
	private static TransactionManager transactionManagerMock=mock(TransactionManager.class);
	
	private static ComptabiliteDao comptabiliteDaoMock=mock(ComptabiliteDao.class);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractBusinessManager.configure(businessProxyMock, daoProxyMock, transactionManagerMock);
		when(daoProxyMock.getComptabiliteDao()).thenReturn(comptabiliteDaoMock);
	}
	
	/**
	 * Test de la méthode addReference(EcritureComptable pEcritureComptable) dans le cas
	 * d'une séquence d'écriture comptable qui existe déjà.
	 * @throws Exception 
	 */
	@Test
	public void addReferenceCase1() throws Exception {
		EcritureComptable vEcritureComptable=new EcritureComptable();
		vEcritureComptable.setJournal(new JournalComptable("BQ", "Banque"));
		vEcritureComptable.setDate(new Date());
		vEcritureComptable.setLibelle("Libelle");
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1,"testLibelle"),
				null,new BigDecimal(123),null));
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2,"testLibelle"),
				null, null,new BigDecimal(123)));
		
		String referenceExpected="BQ-2018/00100";
		when(comptabiliteDaoMock.getSequenceEcritureComptable("BQ", 2018)).thenReturn(new SequenceEcritureComptable(2018,99));
		manager.addReference(vEcritureComptable); 	
		assertEquals("La référence de l'écriture comptable n'a pas été mise à jour correctement.",referenceExpected,vEcritureComptable.getReference());
	}
	
	/**
	 * Test de la méthode addReference(EcritureComptable pEcritureComptable) dans le cas
	 * d'une nouvelle séquence d'écriture comptable.
	 * @throws Exception 
	 */
	@Test
	public void addReferenceCase2() throws Exception{
		EcritureComptable vEcritureComptable=new EcritureComptable();
		vEcritureComptable.setJournal(new JournalComptable("OD", "Opérations Diverses"));
		vEcritureComptable.setDate(new Date());
		vEcritureComptable.setLibelle("Libelle");
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1,"testLibelle"),
				null,new BigDecimal(123),null));
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2,"testLibelle"),
				null, null,new BigDecimal(123)));
		
		String referenceExpected="OD-2018/00001";
		when(comptabiliteDaoMock.getSequenceEcritureComptable("OD", 2018)).thenThrow(NotFoundException.class);
		manager.addReference(vEcritureComptable); 
		assertEquals("La référence de l'écriture comptable n'a pas été mise à jour correctement.",referenceExpected,vEcritureComptable.getReference());
	}

	/**
	 * Méthode qui permet de tester la méthode checkEcritureComptableUnit(EcritureComptable pEcritureComptable) dans le cas nominal : Aucune exception
	 * ne doit être levée.
	 */
	@Test
	public void checkEcritureComptableUnitNominal() throws Exception {  
		EcritureComptable vEcritureComptable;
		vEcritureComptable = new EcritureComptable();
		vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
		vEcritureComptable.setReference("AC-2018/00001");
		vEcritureComptable.setDate(new Date());
		vEcritureComptable.setLibelle("Libelle");
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1,"testLibelle"),
				null,new BigDecimal(123),null));
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2,"testLibelle"),
				null, null,new BigDecimal(123)));
		manager.checkEcritureComptableUnit(vEcritureComptable);
	}

	/**
	 * Méthode qui permet de tester le non respect de la validation de bean pour le bean {@link EcritureComptable}
	 * @throws Exception
	 */
	@Test(expected = FunctionalException.class)
	public void checkEcritureComptableUnitViolation() throws Exception {
		EcritureComptable vEcritureComptable;
		vEcritureComptable = new EcritureComptable();
		manager.checkEcritureComptableUnit(vEcritureComptable);
	}


	/**
	 * Méthode qui permet de vérifier la violation de la règle de gestion RG_Compta_2 : Pour qu'une écriture comptable soit valide, elle doit être équilibrée :
	 * la somme des montants au crédit des lignes d'écriture doit être égale à la somme des montants au débit.
	 * @throws Exception
	 */
	@Test(expected = FunctionalException.class)
	public void checkEcritureComptableUnitRG2() throws Exception {
		EcritureComptable vEcritureComptable;
		vEcritureComptable = new EcritureComptable();
		vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
		vEcritureComptable.setReference("AC-2018/00001");
		vEcritureComptable.setDate(new Date());
		vEcritureComptable.setLibelle("Libelle");
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1,"testLibelle"),
				null, new BigDecimal(123),null));
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2,"testLibelle"),
				null, null,new BigDecimal(1234)));
		manager.checkEcritureComptableUnit(vEcritureComptable);
	}
	
	/**
	 * Méthode qui permet de vérifier la violation de la règle de gestion RG_Compta_3 : Une écriture comptable 
	 * doit contenir au moins deux lignes d'écriture : une au débit et une au crédit. Cas 1 : Une seule ligne 
	 * d'écriture. 
	 */
	@Test(expected = FunctionalException.class)
	public void checkEcritureComptableUnitRG3Case1() throws Exception {
		EcritureComptable vEcritureComptable;
		vEcritureComptable = new EcritureComptable();
		vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
		vEcritureComptable.setReference("AC-2018/00001");
		vEcritureComptable.setDate(new Date());
		vEcritureComptable.setLibelle("Libelle");
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1,"testLibelle"),
				null, new BigDecimal(123),null));
		manager.checkEcritureComptableUnit(vEcritureComptable);
	}

	/**
	 * Méthode qui permet de vérifier la violation de la règle de gestion RG_Compta_3 : Une écriture comptable 
	 * doit contenir au moins deux lignes d'écriture : une au débit et une au crédit. Cas 2 : Deux lignes 
	 * d'écriture au débit. 
	 */
	@Test(expected = FunctionalException.class)
	public void checkEcritureComptableUnitRG3Case2() throws Exception {
		EcritureComptable vEcritureComptable;
		vEcritureComptable = new EcritureComptable();
		vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
		vEcritureComptable.setReference("AC-2018/00001");
		vEcritureComptable.setDate(new Date());
		vEcritureComptable.setLibelle("Libelle");
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1,"testLibelle"),
				null, new BigDecimal(123),null));
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1,"testLibelle"),
				null, new BigDecimal(123),null));
		manager.checkEcritureComptableUnit(vEcritureComptable);
	}

	/**
	 * Méthode qui permet de vérifier la violation de la règle de gestion RG_Compta_3 : Une écriture comptable 
	 * doit contenir au moins deux lignes d'écriture : une au débit et une au crédit. Cas 3 : Deux lignes 
	 * d'écriture au crédit. 
	 */
	@Test(expected = FunctionalException.class)
	public void checkEcritureComptableUnitRG3Case3() throws Exception {
		EcritureComptable vEcritureComptable;
		vEcritureComptable = new EcritureComptable();
		vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
		vEcritureComptable.setReference("AC-2018/00001");
		vEcritureComptable.setDate(new Date());
		vEcritureComptable.setLibelle("Libelle");
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1,"testLibelle"),
				null, null,new BigDecimal(123)));
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1,"testLibelle"),
				null, null,new BigDecimal(123)));
		manager.checkEcritureComptableUnit(vEcritureComptable);
	}
	
	/**
	 * Méthode qui permet de vérifier la violation de la règle de gestion RG_Compta_5 au niveau du pattern de la référence.
	 * Rappel RG_Compta_5 : La référence d'une écriture comptable est composée du code du journal dans lequel figure l'écriture
	 * suivi de l'année et d'un numéro de séquence (propre à chaque journal) sur 5 chiffres incrémenté automatiquement à chaque 
	 * écriture. Le formatage de la référence est : XX-AAAA/#####
	 * @throws Exception
	 */
	@Test(expected = FunctionalException.class)
	public void checkEcritureComptableUnitRG5Case1() throws Exception {
		EcritureComptable vEcritureComptable = new EcritureComptable();
		vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
		vEcritureComptable.setReference("AC2018/00001");
		vEcritureComptable.setDate(new Date());
		vEcritureComptable.setLibelle("Libelle");
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "testLibelle"),
				null, new BigDecimal(123),null));
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2, "testLibelle"),
				null, null,new BigDecimal(123)));
		manager.checkEcritureComptableUnit(vEcritureComptable);
	}
	
	/**
	 * Méthode qui permet de vérifier la violation de la règle de gestion RG_Compta_5 au niveau du code journal.
	 * @throws Exception
	 */
	@Test(expected = FunctionalException.class)
	public void checkEcritureComptableUnitRG5Case2() throws Exception {
		EcritureComptable vEcritureComptable = new EcritureComptable();
		vEcritureComptable.setJournal(new JournalComptable("VE", "Vente"));
		vEcritureComptable.setReference("AC-2018/00001");
		vEcritureComptable.setDate(new Date());
		vEcritureComptable.setLibelle("Libelle");
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "testLibelle"),
				null, new BigDecimal(123),null));
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2, "testLibelle"),
				null, null,new BigDecimal(123)));
		manager.checkEcritureComptableUnit(vEcritureComptable);
	}
	
	/**
	 * Méthode qui permet de vérifier la violation de la règle de gestion RG_Compta_5 au niveau de l'année.
	 * @throws Exception
	 */
	@Test(expected = FunctionalException.class)
	public void checkEcritureComptableUnitRG5Case3() throws Exception {
		EcritureComptable vEcritureComptable = new EcritureComptable();
		vEcritureComptable.setJournal(new JournalComptable("VE", "Vente"));
		vEcritureComptable.setReference("VE-2015/00001");
		vEcritureComptable.setDate(new Date());
		vEcritureComptable.setLibelle("Libelle");
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "testLibelle"),
				null, new BigDecimal(123),null));
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2, "testLibelle"),
				null, null,new BigDecimal(123)));
		manager.checkEcritureComptableUnit(vEcritureComptable);
	}
	
	/**
	 * Méthode qui permet de vérifier la violation de la règle de gestion RG_Compta_7 dès la validation de bean.
	 * @throws Exception
	 */
	@Test(expected = FunctionalException.class)
	public void checkEcritureComptableUnitRG7() throws Exception {
		EcritureComptable vEcritureComptable = new EcritureComptable();
		vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
		vEcritureComptable.setReference("AC-2018/00001");
		vEcritureComptable.setDate(new Date());
		vEcritureComptable.setLibelle("Libelle");
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "testLibelle"),
				null, new BigDecimal(123.456),null));
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2, "testLibelle"),
				null, null,new BigDecimal(123.456)));
		manager.checkEcritureComptableUnit(vEcritureComptable);
	}
	
	/**
	 * Méthode qui permet de tester la méthode checkEcritureComptableContext(EcritureComptable pEcritureComptable) dans le cas nominal : Aucune exception
	 * ne doit être levée.
	 * @throws Exception
	 */
	@Test
	public void checkEcritureComptableContextNominal() throws Exception { 
		EcritureComptable vEcritureComptable1 = new EcritureComptable();
		vEcritureComptable1.setJournal(new JournalComptable("AC", "Achat"));
		vEcritureComptable1.setReference("AC-2018/00015");
		vEcritureComptable1.setDate(new Date());
		vEcritureComptable1.setLibelle("Libelle Achat");
		vEcritureComptable1.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "testLibelle"),
				null, new BigDecimal(123.45),null));
		vEcritureComptable1.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2, "testLibelle"),
				null, null,new BigDecimal(123.45)));
		
		EcritureComptable vEcritureComptable2 = new EcritureComptable();
		vEcritureComptable2.setJournal(new JournalComptable("OD", "Opérations Diverses"));
		vEcritureComptable2.setReference("OD-2018/00016");
		vEcritureComptable2.setDate(new Date());
		vEcritureComptable2.setLibelle("Libelle Opérations diverses");
		vEcritureComptable2.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "testLibelle"),
				null, new BigDecimal(543.2),null));
		vEcritureComptable2.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2, "testLibelle"),
				null, null,new BigDecimal(543.2)));
		
		when(comptabiliteDaoMock.getEcritureComptableByRef("OD-2018/00016")).thenThrow(new NotFoundException("Test : EcritureComptable non trouvée"));
		manager.checkEcritureComptableContext(vEcritureComptable2); 
	}
	
	/**
	 * Méthode qui permet de vérifier la violation de la règle de gestion RG_Compta_6 : La référence d'une écriture comptable doit être unique,
	 * il n'est pas possible de créer plusieurs écritures ayant la même référence. Cas 1 : Pas d'id pour les bean {@link EcritureComptable}
	 * @throws Exception
	 */
	@Test(expected = FunctionalException.class)
	public void checkEcritureComptableContextRG6Case1() throws Exception { 
		EcritureComptable vEcritureComptable1 = new EcritureComptable();
		vEcritureComptable1.setJournal(new JournalComptable("AC", "Achat"));
		vEcritureComptable1.setReference("AC-2018/00015");
		vEcritureComptable1.setDate(new Date());
		vEcritureComptable1.setLibelle("Libelle Achat");
		vEcritureComptable1.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "testLibelle"),
				null, new BigDecimal(123.45),null));
		vEcritureComptable1.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2, "testLibelle"),
				null, null,new BigDecimal(123.45)));
		
		EcritureComptable vEcritureComptable2 = new EcritureComptable();
		vEcritureComptable2.setJournal(new JournalComptable("OD", "Opérations Diverses"));
		vEcritureComptable2.setReference("AC-2018/00015");
		vEcritureComptable2.setDate(new Date()); 
		vEcritureComptable2.setLibelle("Libelle Opérations diverses");
		vEcritureComptable2.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "testLibelle"),
				null, new BigDecimal(543.2),null));
		vEcritureComptable2.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2, "testLibelle"),
				null, null,new BigDecimal(543.2)));
		
		when(comptabiliteDaoMock.getEcritureComptableByRef("AC-2018/00015")).thenReturn(vEcritureComptable1);
		manager.checkEcritureComptableContext(vEcritureComptable2); 
	} 
	
	/**
	 * Méthode qui permet de vérifier la violation de la règle de gestion RG_Compta_6 : La référence d'une écriture comptable doit être unique,
	 * il n'est pas possible de créer plusieurs écritures ayant la même référence. Cas 2 : Un id différent pour chaque bean {@link EcritureComptable}
	 * @throws Exception
	 */
	@Test(expected = FunctionalException.class)
	public void checkEcritureComptableContextRG6Case2() throws Exception { 
		EcritureComptable vEcritureComptable1 = new EcritureComptable();
		vEcritureComptable1.setId(1);
		vEcritureComptable1.setJournal(new JournalComptable("AC", "Achat"));
		vEcritureComptable1.setReference("AC-2018/00015");
		vEcritureComptable1.setDate(new Date());
		vEcritureComptable1.setLibelle("Libelle Achat");
		vEcritureComptable1.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "testLibelle"),
				null, new BigDecimal(123.45),null));
		vEcritureComptable1.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2, "testLibelle"),
				null, null,new BigDecimal(123.45)));
		
		EcritureComptable vEcritureComptable2 = new EcritureComptable();
		vEcritureComptable2.setId(2);
		vEcritureComptable2.setJournal(new JournalComptable("OD", "Opérations Diverses"));
		vEcritureComptable2.setReference("AC-2018/00015");
		vEcritureComptable2.setDate(new Date()); 
		vEcritureComptable2.setLibelle("Libelle Opérations diverses");
		vEcritureComptable2.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "testLibelle"),
				null, new BigDecimal(543.2),null));
		vEcritureComptable2.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2, "testLibelle"),
				null, null,new BigDecimal(543.2)));
		
		when(comptabiliteDaoMock.getEcritureComptableByRef("AC-2018/00015")).thenReturn(vEcritureComptable1);
		manager.checkEcritureComptableContext(vEcritureComptable2); 
	} 
	
	/**
	 * Méthode qui permet de tester la méthode checkEcritureComptable(EcritureComptable pEcritureComptable) dans le cas nominal : Aucune exception
	 * ne doit être levée.
	 */
	@Test
	public void checkEcritureComptableNominal() throws Exception {  
		EcritureComptable vEcritureComptable;
		vEcritureComptable = new EcritureComptable();
		vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
		vEcritureComptable.setReference("AC-2018/00001");
		vEcritureComptable.setDate(new Date());
		vEcritureComptable.setLibelle("Libelle");
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1,"testLibelle"),
				null,new BigDecimal(123),null));
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2,"testLibelle"),
				null, null,new BigDecimal(123)));
		when(comptabiliteDaoMock.getEcritureComptableByRef("AC-2018/00001")).thenThrow(new NotFoundException("Test : EcritureComptable non trouvée"));
		manager.checkEcritureComptable(vEcritureComptable);
	}

}
