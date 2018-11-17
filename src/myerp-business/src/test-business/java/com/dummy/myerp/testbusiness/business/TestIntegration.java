package com.dummy.myerp.testbusiness.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.transaction.TransactionStatus;

import com.dummy.myerp.consumer.dao.impl.db.dao.ComptabiliteDaoImpl;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.technical.exception.TechnicalException;

/**
 * Classe permettant d'effectuer des tests d'intégration 
 * au niveau du module business.
 * @author André Monnier
 *
 */
public class TestIntegration extends BusinessTestCase {

	private int nbLignesCompteComptableExpected=7;
	private int nbLignesJournalComptableExpected=4;
	private int nbLignesEcritureComptableExpected=5;
	private static EcritureComptable ecritureComptableExpected = new EcritureComptable();
	private static List<LigneEcritureComptable> listLigneEcritureComptableExpected = new ArrayList<LigneEcritureComptable>();

	private static final String[] TABNOMTABLESBDD= {"myerp.ligne_ecriture_comptable","myerp.ecriture_comptable","myerp.sequence_ecriture_comptable","myerp.compte_comptable","myerp.journal_comptable"};

	@BeforeClass
	public static void setUpBeforeClass() throws Exception{
		createListLigneEcritureComptableExpected();
	}
	
	@After
	public void tearDown() throws Exception {

		TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
		JdbcTemplate vJdbcTemplate = new JdbcTemplate(getDataSourceTest());
		
		//On efface les tables de la base de données.
		for(String str:TABNOMTABLESBDD) {
			String vSQL= "DELETE FROM "+ str;
			try {
				vJdbcTemplate.update(vSQL);
			} catch (DataAccessException e) {
				getTransactionManager().rollbackMyERP(vTS);
				throw new TechnicalException("Erreur d'accès à la base de données");
			}
		}
		
		//On remplit les tables de la base de données avec les données initiales du jeu de démo.
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		
		try {
			populator.addScripts(new ClassPathResource("21_insert_data_demo_test_integration.sql"));
			populator.execute(getDataSourceTest());
		} catch (ScriptException e) {
			getTransactionManager().rollbackMyERP(vTS);
			throw new TechnicalException("Erreur d'accès à la base de données");
		}
		
		//On réinitialise la séquence MYERP.ecriture_comptable_id_seq à 1
		try {
			vJdbcTemplate.update("ALTER SEQUENCE myerp.ecriture_comptable_id_seq RESTART 1");
			getTransactionManager().commitMyERP(vTS); 
		} catch (Exception e) {
			getTransactionManager().rollbackMyERP(vTS);
			throw new TechnicalException("Erreur d'accès à la base de données");
		} 
		
	}

	/**
	 * Construction de la liste de LigneEcritureComptable attendue.
	 */
	private static void createListLigneEcritureComptableExpected() {
		LigneEcritureComptable vLigneEcritureComptable1= new LigneEcritureComptable();
		vLigneEcritureComptable1.setCompteComptable(new CompteComptable(606, "Achats non stockés de matières et fournitures"));
		vLigneEcritureComptable1.setLibelle("Cartouches d’imprimante");
		vLigneEcritureComptable1.setDebit(new BigDecimal("43.95"));
		listLigneEcritureComptableExpected.add(vLigneEcritureComptable1);

		LigneEcritureComptable vLigneEcritureComptable2= new LigneEcritureComptable();
		vLigneEcritureComptable2.setCompteComptable(new CompteComptable(4456, "Taxes sur le chiffre d'affaires déductibles"));
		vLigneEcritureComptable2.setLibelle("TVA 20%");
		vLigneEcritureComptable2.setDebit(new BigDecimal("8.79"));
		listLigneEcritureComptableExpected.add(vLigneEcritureComptable2);

		LigneEcritureComptable vLigneEcritureComptable3= new LigneEcritureComptable();
		vLigneEcritureComptable3.setCompteComptable(new CompteComptable(401, "Fournisseurs"));
		vLigneEcritureComptable3.setLibelle("Facture F110001");
		vLigneEcritureComptable3.setCredit(new BigDecimal("52.74"));
		listLigneEcritureComptableExpected.add(vLigneEcritureComptable3);
	}
	
	/**
	 * Méthode qui permet de tester l'égalité entre 2 EcritureComptable.
	 */
	private static boolean testEgalite(EcritureComptable pEcritureComptable) {
		boolean bResult=pEcritureComptable.getId().equals(ecritureComptableExpected.getId())
				&&pEcritureComptable.getJournal().getCode().equals(ecritureComptableExpected.getJournal().getCode())
				&&pEcritureComptable.getJournal().getLibelle().equals(ecritureComptableExpected.getJournal().getLibelle())
				&&pEcritureComptable.getReference().equals(ecritureComptableExpected.getReference())
				&&pEcritureComptable.getDate().equals(ecritureComptableExpected.getDate()) 
				&&pEcritureComptable.getLibelle().equals(ecritureComptableExpected.getLibelle()); 
		
		if(pEcritureComptable.getListLigneEcriture().size()!=listLigneEcritureComptableExpected.size()) { 
			bResult=bResult&&false;
		}else {
			for(int i=0;i<pEcritureComptable.getListLigneEcriture().size();i++) {
				boolean bTest=false;
				bTest=testEgalite(pEcritureComptable.getListLigneEcriture().get(i),i); 
				bResult=bResult&&bTest;
			}
		}
		return bResult;
	}
	
	/**
	 * Méthode qui permet de tester l'égalité entre 2 LigneEcritureComptable.
	 */
	private static boolean testEgalite(LigneEcritureComptable pLigneEcritureComptable, int i) {
		boolean bResult=pLigneEcritureComptable.getCompteComptable().getNumero().equals(listLigneEcritureComptableExpected.get(i).getCompteComptable().getNumero())
				&&pLigneEcritureComptable.getCompteComptable().getLibelle().equals(listLigneEcritureComptableExpected.get(i).getCompteComptable().getLibelle());

		//Traitement des cas null pour les champs libelle, debit et credit.
		if(pLigneEcritureComptable.getLibelle()!=null) {
			if(listLigneEcritureComptableExpected.get(i).getLibelle()==null) {
				bResult=bResult&&false;
			}else {
				bResult=bResult&&pLigneEcritureComptable.getLibelle().equals(listLigneEcritureComptableExpected.get(i).getLibelle());
			}
		}else {
			bResult=bResult&&(listLigneEcritureComptableExpected.get(i).getLibelle()==null);
		}

		if(pLigneEcritureComptable.getDebit()!=null) {
			if(listLigneEcritureComptableExpected.get(i).getDebit()==null) {
				bResult=bResult&&false;
			}else {
				bResult=bResult&&pLigneEcritureComptable.getDebit().equals(listLigneEcritureComptableExpected.get(i).getDebit());
			}
		}else {
			bResult=bResult&&(listLigneEcritureComptableExpected.get(i).getDebit()==null);
		}

		if(pLigneEcritureComptable.getCredit()!=null) {
			if(listLigneEcritureComptableExpected.get(i).getCredit()==null) {
				bResult=bResult&&false;
			}else {
				bResult=bResult&&pLigneEcritureComptable.getCredit().equals(listLigneEcritureComptableExpected.get(i).getCredit());
			}
		}else {
			bResult=bResult&&(listLigneEcritureComptableExpected.get(i).getCredit()==null);
		}

		return bResult;
	}

	/**
	 * Méthode qui permet de tester la lecture de la base de données, i.e les méthodes
	 * getListCompteComptable(), getListJournalComptable(), getListEcritureComptable().
	 */
	@Test
	public void testSelect() {
		List<CompteComptable> vListCompteComptableBDD = getBusinessProxy().getComptabiliteManager().getListCompteComptable();
		List<JournalComptable> vListJournalComptableBDD=getBusinessProxy().getComptabiliteManager().getListJournalComptable();
		List<EcritureComptable> vListEcritureComptableBDD=getBusinessProxy().getComptabiliteManager().getListEcritureComptable();

		assertNotNull("La liste de CompteComptable retournée ne doit pas être nul.",vListCompteComptableBDD);
		assertNotNull("La liste de JournalComptable retournée ne doit pas être nul.",vListJournalComptableBDD); 
		assertNotNull("La liste d'EcritureComptable retournée ne doit pas être nul.",vListEcritureComptableBDD);

		assertEquals("La taille de la liste de CompteComptable est erronée.",nbLignesCompteComptableExpected,vListCompteComptableBDD.size());
		assertEquals("La taille de la liste de JournalComptable est erronée.",nbLignesJournalComptableExpected,vListJournalComptableBDD.size());
		assertEquals("La taille de la liste d'EcritureComptable est erronée.",nbLignesEcritureComptableExpected,vListEcritureComptableBDD.size());

	}

	/**
	 * Méthode qui permet de tester la suppression d'un bean de type {@link EcritureComptable} dans la base de données
	 * en fonction de son id, i.e la méthode deleteEcritureComptable(Integer pId)
	 */
	@Test
	public void testDelete() {
		getBusinessProxy().getComptabiliteManager().deleteEcritureComptable(-1);
		List<EcritureComptable> vListEcritureComptableBDD=getBusinessProxy().getComptabiliteManager().getListEcritureComptable();
		assertEquals("La taille de la liste d'EcritureComptable est erronée.",nbLignesEcritureComptableExpected-1,vListEcritureComptableBDD.size());
	}
	
	/**
	 * Méthode qui permet de tester l'ajout d'un bean de type {@link EcritureComptable} dans la base de données,
	 * i.e la méthode insertEcritureComptable(EcritureComptable pEcritureComptable). Dans ce cas là, une nouvelle ligne
	 * sera ajoutée dans la table sequence_ecriture_comptable.
	 * @throws Exception 
	 */
	@Test
	public void testInsertCase1() throws Exception { 
	
		EcritureComptable vEcritureComptableExpected = new EcritureComptable();
		vEcritureComptableExpected.setJournal(new JournalComptable("OD", "Opérations Diverses"));
		Calendar vCalendar = Calendar.getInstance();
		//On met la date souhaitée dans le Calendar. Attention, la numérotation du mois commence à 0.
		vCalendar.clear();
		vCalendar.set(2018, 10, 16);
		vEcritureComptableExpected.setDate(vCalendar.getTime());
		vEcritureComptableExpected.setLibelle("Nouvelle EcritureComptable Test");
		
		vEcritureComptableExpected.getListLigneEcriture().addAll(listLigneEcritureComptableExpected);
		
		//C'est ici que l'on va faire appel à la méthode addReference(EcritureComptable pEcritureComptable) 
		getBusinessProxy().getComptabiliteManager().addReference(vEcritureComptableExpected);
		ComptabiliteDaoImpl.setSgbdr("postgresql");
		
		getBusinessProxy().getComptabiliteManager().insertEcritureComptable(vEcritureComptableExpected);
		
		ecritureComptableExpected=vEcritureComptableExpected;
		
		List<EcritureComptable> vListEcritureComptableBDD = getBusinessProxy().getComptabiliteManager().getListEcritureComptable();

		assertEquals("La taille de la liste d'EcritureComptable est erronée.",nbLignesEcritureComptableExpected+1,vListEcritureComptableBDD.size());
		
		EcritureComptable ecritureComptableBDD=null;
		for (EcritureComptable ecritureComptable : vListEcritureComptableBDD) {
			if (ecritureComptable.getId().equals(1)) {
				ecritureComptableBDD = ecritureComptable;
			}
		}
		assertTrue("L'EcritureComptable attendu n'a pa été trouvée",testEgalite(ecritureComptableBDD));	
	}
	
	/**
	 * Méthode qui permet de tester l'ajout d'un bean de type {@link EcritureComptable} dans la base de données,
	 * i.e la méthode insertEcritureComptable(EcritureComptable pEcritureComptable). Dans ce cas là, aucune ligne
	 * ne sera ajoutée dans la table sequence_ecriture_comptable.
	 * @throws Exception 
	 */
	@Test
	public void testInsertCase2() throws Exception {  
	
		EcritureComptable vEcritureComptableExpected = new EcritureComptable();
		vEcritureComptableExpected.setJournal(new JournalComptable("OD", "Opérations Diverses"));
		Calendar vCalendar = Calendar.getInstance();
		//On met la date souhaitée dans le Calendar. Attention, la numérotation du mois commence à 0.
		vCalendar.clear();
		vCalendar.set(2016, 11, 31);
		vEcritureComptableExpected.setDate(vCalendar.getTime());
		vEcritureComptableExpected.setLibelle("Nouvelle EcritureComptable Test");
		
		vEcritureComptableExpected.getListLigneEcriture().addAll(listLigneEcritureComptableExpected);
		
		//C'est ici que l'on va faire appel à la méthode addReference(EcritureComptable pEcritureComptable) 
		getBusinessProxy().getComptabiliteManager().addReference(vEcritureComptableExpected);
		ComptabiliteDaoImpl.setSgbdr("postgresql");
		
		getBusinessProxy().getComptabiliteManager().insertEcritureComptable(vEcritureComptableExpected);
		
		ecritureComptableExpected=vEcritureComptableExpected;
		
		List<EcritureComptable> vListEcritureComptableBDD = getBusinessProxy().getComptabiliteManager().getListEcritureComptable();

		assertEquals("La taille de la liste d'EcritureComptable est erronée.",nbLignesEcritureComptableExpected+1,vListEcritureComptableBDD.size());	
			
		EcritureComptable ecritureComptableBDD=null;
		for (EcritureComptable ecritureComptable : vListEcritureComptableBDD) {
			if (ecritureComptable.getId().equals(1)) {
				ecritureComptableBDD = ecritureComptable;
			}
		}
		
		assertTrue("L'EcritureComptable attendu n'a pa été trouvée",testEgalite(ecritureComptableBDD));	 
	}
	
	/**
	 * Méthode qui permet de mettre à jour un bean de type {@link EcritureComptable} dans la base de données,
	 * i.e méthode updateEcritureComptable(EcritureComptable pEcritureComptable). 
	 * @throws Exception 
	 */
	@Test
	public void testUpdate() throws Exception {
		//On récupère un bean EcritureComptable depuis la base de données. On prend celui qui a pour id -1.
		List<EcritureComptable> vListEcritureComptableBDD = getBusinessProxy().getComptabiliteManager().getListEcritureComptable();
		EcritureComptable vEcritureComptableExpected=null;
		for (EcritureComptable ecritureComptable : vListEcritureComptableBDD) {
			if (ecritureComptable.getId().equals(-1)) {
				vEcritureComptableExpected = ecritureComptable;
			}
		}
		
		//On met à jour ce bean.
		vEcritureComptableExpected.setJournal(new JournalComptable("OD", "Opérations Diverses"));
		Calendar vCalendar = Calendar.getInstance();
		//On met la date souhaitée dans le Calendar. Attention, la numérotation du mois commence à 0.
		vCalendar.clear();
		vCalendar.set(2017, 2, 20);
		vEcritureComptableExpected.setDate(vCalendar.getTime());
		vEcritureComptableExpected.setLibelle("EcritureComptable Test Modifiée");
		
		//C'est ici que l'on va faire appel à la méthode addReference(EcritureComptable pEcritureComptable) 
		getBusinessProxy().getComptabiliteManager().addReference(vEcritureComptableExpected);
	
		//On met à jour la base de données.
		getBusinessProxy().getComptabiliteManager().updateEcritureComptable(vEcritureComptableExpected);
		
		ecritureComptableExpected=vEcritureComptableExpected;
		
		//On récupère de nouveau le bean EcritureComptable d'id -1.
		vListEcritureComptableBDD = getBusinessProxy().getComptabiliteManager().getListEcritureComptable();
		
		assertEquals("La taille de la liste d'EcritureComptable est erronée.",nbLignesEcritureComptableExpected,vListEcritureComptableBDD.size());
		
		EcritureComptable vEcritureComptableBDD=null;
		for (EcritureComptable ecritureComptable : vListEcritureComptableBDD) {
			if (ecritureComptable.getId().equals(-1)) {
				vEcritureComptableBDD = ecritureComptable;
			}
		}
		assertTrue("L'EcritureComptable attendu n'a pa été trouvée",testEgalite(vEcritureComptableBDD));	
		
	}
}
