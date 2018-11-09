package com.dummy.myerp.consumer.dao.impl.db.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.dummy.myerp.consumer.ConsumerHelper;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.consumer.dao.impl.db.dao.ComptabiliteDaoImpl;
import com.dummy.myerp.consumer.db.AbstractDbConsumer;
import com.dummy.myerp.consumer.db.DataSourcesEnum;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.NotFoundException;

/**
 * Classe permettant d'effectuer des tests unitaires sur la classe {@link ComptabiliteDaoImpl}
 * @author André Monnier
 *
 */
public class ComptabiliteDaoImplTest {

	private static EmbeddedDatabase embeddedDatabase;
	private static DataSource dataSourceEDB;
	private static ComptabiliteDaoImpl comptabiliteDaoImpl;
	private static List<CompteComptable> listCompteComptableExpected = new ArrayList<CompteComptable>();
	private static List<JournalComptable> listJournalComptableExpected = new ArrayList<JournalComptable>();
	private static EcritureComptable ecritureComptableExpected = new EcritureComptable();
	private static List<LigneEcritureComptable> listLigneEcritureComptableExpected = new ArrayList<LigneEcritureComptable>();
	private static DaoProxy daoProxyMock=mock(DaoProxy.class);
	private static int tailleListEcritureComptableBDD=5;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//Implémentation d'une dateSource associée à une base de données embarquée de type H2.
		//Les scripts SQL ont été mis dans le répertoire src/test/resources.
		embeddedDatabase=new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.H2)
				.setScriptEncoding("UTF-8")
				.addScripts("02_create_schema_tables_test_unitaires.sql","21_insert_data_demo_test_unitaires.sql")
				.build(); 

		dataSourceEDB=embeddedDatabase;

		//Implémentation de la Map<DataSourcesEnum, DataSource>. On ne met que la
		//DataSourcesEnum et la DateSource dont on a besoin.
		Map<DataSourcesEnum, DataSource> vMapDataSource = new HashMap<DataSourcesEnum, DataSource>();
		for(DataSourcesEnum dataSourcesEnum:DataSourcesEnum.values()) {
			vMapDataSource.put(dataSourcesEnum, dataSourceEDB);
		}

		//On injecte cette map dans la classe abstraite AbstractDbConsumer via la méthode static configure(Map<DataSourcesEnum, DataSource> pMapDataSource).
		//Dans notre cas, la map en sortie de cette méthode sera la même que la map en entrée.
		AbstractDbConsumer.configure(vMapDataSource);

		@SuppressWarnings("resource")
		ApplicationContext vApplicationContext = new ClassPathXmlApplicationContext("classpath:com/dummy/myerp/consumer/sqlContext.xml");
		comptabiliteDaoImpl=vApplicationContext.getBean("ComptabiliteDaoImpl", ComptabiliteDaoImpl.class);

		createListCompteComptableExpected();
		createListJournalComptableExpected();
		createListLigneEcritureComptableExpected();
		createEcritureComptableExpected();

		//Configuration du daoProxy.
		ConsumerHelper.configure(daoProxyMock);
		when(daoProxyMock.getComptabiliteDao()).thenReturn(comptabiliteDaoImpl);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		embeddedDatabase.shutdown();
	}

	/**
	 * Construction de la liste de CompteComptable attendu.
	 */
	private static void createListCompteComptableExpected() {
		listCompteComptableExpected.add(new CompteComptable(401, "Fournisseurs"));
		listCompteComptableExpected.add(new CompteComptable(411, "Clients"));
		listCompteComptableExpected.add(new CompteComptable(4456, "Taxes sur le chiffre d'affaires déductibles"));
		listCompteComptableExpected.add(new CompteComptable(4457, "Taxes sur le chiffre d'affaires collectées par l'entreprise"));
		listCompteComptableExpected.add(new CompteComptable(512, "Banque"));
		listCompteComptableExpected.add(new CompteComptable(606, "Achats non stockés de matières et fournitures"));
		listCompteComptableExpected.add(new CompteComptable(706, "Prestations de services"));
	}

	/**
	 * Construction de la liste de JournalComptable attendu.
	 */
	private static void createListJournalComptableExpected() {
		listJournalComptableExpected.add(new JournalComptable("AC", "Achat"));
		listJournalComptableExpected.add(new JournalComptable("VE", "Vente"));
		listJournalComptableExpected.add(new JournalComptable("BQ", "Banque"));
		listJournalComptableExpected.add(new JournalComptable("OD", "Opérations Diverses"));
	}

	/**
	 * Construction de l'EcritureComptable attendu. On a choisi l'EcritureComptable avec l'id=-1.
	 */
	private static void createEcritureComptableExpected() { 
		ecritureComptableExpected.setId(-1);
		ecritureComptableExpected.setJournal(new JournalComptable("AC", "Achat"));
		ecritureComptableExpected.setReference("AC-2016/00001");

		//Pour la date, on passe par l'objet Calendar.
		Calendar vCalendar = Calendar.getInstance();
		//On met la date souhaitée dans le Calendar. Attention, la numérotation du mois commence à 0.
		vCalendar.clear();
		vCalendar.set(2016, 11, 31);
		ecritureComptableExpected.setDate(vCalendar.getTime());

		ecritureComptableExpected.setLibelle("Cartouches d’imprimante");
		ecritureComptableExpected.getListLigneEcriture().addAll(listLigneEcritureComptableExpected);
	}

	/**
	 * Construction de la liste de LigneEcritureComptable attendu qui correspond à l'EcritureComptable définie ci-dessus.
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
	 * Test de la méthode getInstance(). On s'assure que l'objet retourné est le même.
	 */
	@Test
	public void getInstance() {
		assertTrue("Erreur : L'objet retourné doit être le même.", ComptabiliteDaoImpl.getInstance().equals(comptabiliteDaoImpl));
	}

	/**
	 * Test de la méthode getListCompteComptable().
	 */
	@Test
	public void getListCompteComptable() {
		List<CompteComptable> listCompteComptableBDDEmbarque = comptabiliteDaoImpl.getListCompteComptable();

		//On vérifie la cohérence de la taille de listCompteComptableExpected et listCompteComptableBDDEmbarque.
		assertEquals("La taille de la liste CompteComptable retournée n'est pas correcte.",listCompteComptableExpected.size(),listCompteComptableBDDEmbarque.size());

		//La requête SELECT sur la BDD embarquée ne renvoie pas forcément les élements dans l'ordre du fichier 21_insert_data_demo_test_unitaires.sql.
		//C'est la raison pour laquelle on est obligé d'utiliser 2 boucles for. On s'assure de retrouver chaque élément de listCompteComptableExpected.
		for(int i=0;i<listCompteComptableExpected.size();i++) {
			boolean bResultTest=false;
			for(int j=0;j<listCompteComptableBDDEmbarque.size();j++) { 
				bResultTest=listCompteComptableBDDEmbarque.get(j).getNumero().equals(listCompteComptableExpected.get(i).getNumero())
						&&(listCompteComptableBDDEmbarque.get(j).getLibelle().equals(listCompteComptableExpected.get(i).getLibelle())); 
				if(bResultTest) { 
					break;
				}
			}
			assertTrue("Le CompteComptable suivant n'a pas été trouvé : Numéro : " + listCompteComptableExpected.get(i).getNumero()+" - Libellé : "
					+ listCompteComptableExpected.get(i).getLibelle(),bResultTest);
		}
	}

	/**
	 * Test de la méthode getListJournalComptable().
	 */
	@Test
	public void getListJournalComptable() {
		List<JournalComptable> listJournalComptableBDDEmbarque = comptabiliteDaoImpl.getListJournalComptable();

		//On vérifie la cohérence de la taille de listJournalComptableExpected et listJournalComptableBDDEmbarque.
		assertEquals("La taille de la liste CompteComptable retournée n'est pas correcte.",listJournalComptableExpected.size(),listJournalComptableBDDEmbarque.size());

		//La requête SELECT sur la BDD embarquée ne renvoie pas forcément les élements dans l'ordre du fichier 21_insert_data_demo_test_unitaires.sql.
		//C'est la raison pour laquelle on est obligé d'utiliser 2 boucles for. On s'assure de retrouver chaque élément de listJournalComptableExpected.
		for(int i=0;i<listJournalComptableExpected.size();i++) {
			boolean bResultTest=false;
			for(int j=0;j<listJournalComptableBDDEmbarque.size();j++) { 
				bResultTest=listJournalComptableBDDEmbarque.get(j).getCode().equals(listJournalComptableExpected.get(i).getCode())
						&&(listJournalComptableBDDEmbarque.get(j).getLibelle().equals(listJournalComptableExpected.get(i).getLibelle())); 
				if(bResultTest) { 
					break;
				}
			}
			assertTrue("Le JournalComptable suivant n'a pas été trouvé : Code : " + listJournalComptableExpected.get(i).getCode()+" - Libellé : "
					+ listJournalComptableExpected.get(i).getLibelle(),bResultTest);
		}
	}

	/**
	 * Test de la méthode getListEcritureComptable().
	 */
	@Test
	public void getListEcritureComptable() {
		List<EcritureComptable> listEcritureComptableBDDEmbarque=comptabiliteDaoImpl.getListEcritureComptable();

		//On vérifie que la taille de listEcritureComptableBDDEmbarque est bien égal à tailleListEcritureComptableBDD. 
		assertEquals("La taille de la liste d'EcritureComptable retournée n'est pas correcte.",tailleListEcritureComptableBDD,listEcritureComptableBDDEmbarque.size());

		boolean bResultTest=false;
		for (int i=0;i<listEcritureComptableBDDEmbarque.size();i++) { 
			bResultTest=testEgalite(listEcritureComptableBDDEmbarque.get(i));
			if(bResultTest)
				break;
		}
		assertTrue("L'EcritureComptable attendu n'a pa été trouvée",bResultTest);
	} 

	/**
	 * Test de la méthode getEcritureComptable(Integer pId).
	 * On s'attend à retrouver l'objet EcritureComptable d'id=-1.
	 * @throws Exception 
	 */
	@Test
	public void getEcritureComptableCase1() throws Exception {
		EcritureComptable ecritureComptableBDDEmbarque=comptabiliteDaoImpl.getEcritureComptable(ecritureComptableExpected.getId());
		assertTrue("L'EcritureComptable attendu n'a pa été trouvée",testEgalite(ecritureComptableBDDEmbarque));
	}

	/**
	 * Test de la méthode getEcritureComptable(Integer pId).
	 * On s'attend à retrouver un objet EcritureComptable non null.
	 * @throws Exception 
	 */
	@Test
	public void getEcritureComptableCase2() throws Exception {
		EcritureComptable ecritureComptableBDDEmbarque=comptabiliteDaoImpl.getEcritureComptable(-3);
		assertNotNull("L'EcritureComptable récupérée ne doit pas être nul",ecritureComptableBDDEmbarque);
	}

	/**
	 * Test de la méthode getEcritureComptable(Integer pId) avec un id qui ne figure pas dans le jeu de données de démos.
	 * On s'attend à lever une exception de type NotFoundException.
	 * @throws Exception 
	 */
	@Test(expected = NotFoundException.class)
	public void getEcritureComptableCase3() throws Exception { 
		comptabiliteDaoImpl.getEcritureComptable(-15);
	}

	/**
	 * Test de la méthode getEcritureComptableByRef(String pReference)
	 * On s'attend à retrouver l'objet EcritureComptable d'id=-1 à partir de sa référence.
	 * @throws Exception 
	 */
	@Test
	public void getEcritureComptableByRefCase1() throws Exception {
		EcritureComptable ecritureComptableBDDEmbarque=comptabiliteDaoImpl.getEcritureComptableByRef(ecritureComptableExpected.getReference());
		assertTrue("L'EcritureComptable attendu n'a pa été trouvée",testEgalite(ecritureComptableBDDEmbarque));
	}

	/**
	 * Test de la méthode getEcritureComptableByRef(String pReference)
	 * On s'attend à retrouver un objet EcritureComptable non null.
	 * @throws Exception 
	 */
	@Test
	public void getEcritureComptableByRefCase2() throws Exception {
		EcritureComptable ecritureComptableBDDEmbarque=comptabiliteDaoImpl.getEcritureComptableByRef("BQ-2016/00003");
		assertNotNull("L'EcritureComptable récupérée ne doit pas être nul",ecritureComptableBDDEmbarque);
	}

	/**
	 * Test de la méthode getEcritureComptableByRef(String pReference) avec une référence qui ne figure pas dans le jeu de données de démos.
	 * On s'attend à lever une exception de type NotFoundException.
	 * @throws Exception 
	 */
	@Test(expected = NotFoundException.class)
	public void getEcritureComptableByRefCase3() throws Exception {
		comptabiliteDaoImpl.getEcritureComptableByRef("ZZ-2016/00003");
	}

	/**
	 * Test des méthodes insertEcritureComptable(EcritureComptable pEcritureComptable) et insertListLigneEcritureComptable(EcritureComptable pEcritureComptable).
	 * @throws Exception
	 */
	@Test
	public void insertEcritureComptable() throws Exception {

		//Création d'une nouvelle EcritureComptable
		EcritureComptable vEcritureComptableExpected=new EcritureComptable();
		vEcritureComptableExpected.setId(-6);
		vEcritureComptableExpected.setJournal(new JournalComptable("OD", "Opérations Diverses"));
		vEcritureComptableExpected.setReference("OD-2018/00006");

		//Pour la date, on passe par l'objet Calendar.
		Calendar vCalendar = Calendar.getInstance();
		//On met la date souhaitée dans le Calendar. Attention, la numérotation du mois commence à 0.
		vCalendar.clear();
		vCalendar.set(2018, 10, 07);
		vEcritureComptableExpected.setDate(vCalendar.getTime());

		vEcritureComptableExpected.setLibelle("Opérations diverses et variées.");

		//Création de la liste de LigneEcritureComptable associée à l'EcritureComptable précédente.
		//Pour faciliter les tests, on va prendre la listLigneEcritureComptableExpected définie précédemment.
		vEcritureComptableExpected.getListLigneEcriture().addAll(listLigneEcritureComptableExpected); 

		//On s'adapte un peu pour nos tests.
		comptabiliteDaoImpl.setSQLinsertEcritureComptable( "INSERT INTO myerp.ecriture_comptable "
				+ "(id, journal_code, reference, date, libelle) VALUES (:id,:journal_code, :reference, :date, :libelle)");  

		ComptabiliteDaoImpl.setSgbdr("H2");

		//On fait enfin appel à la méthode que l'on veut tester.
		comptabiliteDaoImpl.insertEcritureComptable(vEcritureComptableExpected);

		//On fait plusieurs vérifications pour voir si l'ajout en base de données a bien réussi.
		List<EcritureComptable> listEcritureComptableBDDEmbarque=comptabiliteDaoImpl.getListEcritureComptable();

		//On vérifie que la taille de listEcritureComptableBDDEmbarque est bien égal à tailleListEcritureComptableBDD+1. 
		assertEquals("La taille de la liste d'EcritureComptable retournée n'est pas correcte.",tailleListEcritureComptableBDD+1,listEcritureComptableBDDEmbarque.size());

		//On vérifie ensuite que l'on arrive bien à récupérer l'EcritureComptable ajouté à partir de son id ou de sa référence.
		EcritureComptable ecritureComptableBDDEmbarqueById=comptabiliteDaoImpl.getEcritureComptable(vEcritureComptableExpected.getId());
		EcritureComptable ecritureComptableBDDEmbarqueByRef=comptabiliteDaoImpl.getEcritureComptableByRef(vEcritureComptableExpected.getReference());

		ecritureComptableExpected=vEcritureComptableExpected;

		assertTrue("L'EcritureComptable attendu n'a pa été trouvée",testEgalite(ecritureComptableBDDEmbarqueById));
		assertTrue("L'EcritureComptable attendu n'a pa été trouvée",testEgalite(ecritureComptableBDDEmbarqueByRef));

	}

	/**
	 * Tes des méthodes updateEcritureComptable(EcritureComptable pEcritureComptable), deleteListLigneEcritureComptable(Integer pEcritureId) et
	 * insertListLigneEcritureComptable(EcritureComptable pEcritureComptable).
	 * @throws Exception 
	 */
	@Test
	public void updateEcritureComptable() throws Exception {
		//On récupère le bean EcritureComptable que l'on souhaite mettre à jour.
		//On évite de récupérer le bean EcritureComptable d'id=-6 car JUnit ne garantit pas l'ordre d'exécution des méthodes de tests.
		EcritureComptable vEcritureComptableExpected=comptabiliteDaoImpl.getEcritureComptable(-1);

		//On modifie les attributs du bean récupéré.
		vEcritureComptableExpected.setJournal(new JournalComptable("BQ", "Banque"));
		vEcritureComptableExpected.setReference("BQ-2018/00001");

		//Pour la date, on passe par l'objet Calendar.
		Calendar vCalendar = Calendar.getInstance();
		//On met la date souhaitée dans le Calendar. Attention, la numérotation du mois commence à 0.
		vCalendar.clear();
		vCalendar.set(2018, 10, 15);
		vEcritureComptableExpected.setDate(vCalendar.getTime());
		vEcritureComptableExpected.setLibelle("Banque.");

		//On fait appel à la méthode que l'on veut tester.
		comptabiliteDaoImpl.updateEcritureComptable(vEcritureComptableExpected);

		//On fait plusieurs vérifications pour voir si l'update du bean EcritureComptable en base de données a bien réussi.
		//On vérifie que l'on arrive bien à récupérer l'EcritureComptable modifié à partir de son id ou de sa référence.
		EcritureComptable ecritureComptableBDDEmbarqueById=comptabiliteDaoImpl.getEcritureComptable(vEcritureComptableExpected.getId());
		EcritureComptable ecritureComptableBDDEmbarqueByRef=comptabiliteDaoImpl.getEcritureComptableByRef(vEcritureComptableExpected.getReference());

		ecritureComptableExpected=vEcritureComptableExpected;

		assertTrue("L'EcritureComptable attendu n'a pa été trouvée",testEgalite(ecritureComptableBDDEmbarqueById));
		assertTrue("L'EcritureComptable attendu n'a pa été trouvée",testEgalite(ecritureComptableBDDEmbarqueByRef));
	}

	/**
	 * Test de la méthode deleteEcritureComptable(Integer pId)
	 * @throws Exception 
	 */
	@Test(expected = NotFoundException.class)
	public void deleteEcritureComptable() throws Exception { 
		//On fait appel à la méthode que l'on veut tester.
		//On va supprimer l'EcritureComptable d'id=-5.
		comptabiliteDaoImpl.deleteEcritureComptable(-5);

		tailleListEcritureComptableBDD-=1;

		//Ensuite, on essaie de récupérer l'EcritureComptable que l'on a supprimée.
		//On s'attend à lever une NotFoundException.
		comptabiliteDaoImpl.getEcritureComptable(-5);	
	} 

	/**
	 * Test de la méthode getSequenceEcritureComptable(String pCodeJournal, Integer pAnnee)
	 * On s'attend à retrouver un bean SequenceEcritureComptable.
	 * @throws Exception 
	 */
	@Test
	public void getSequenceEcritureComptableCase1() throws Exception {
		SequenceEcritureComptable vSequenceEcritureComptableExpected = new SequenceEcritureComptable(2016,51);
		SequenceEcritureComptable seqEcritureComptableBDDEmbarque= comptabiliteDaoImpl.getSequenceEcritureComptable("BQ", 2016);

		boolean bResult=seqEcritureComptableBDDEmbarque.getAnnee().equals(vSequenceEcritureComptableExpected.getAnnee())
				&&seqEcritureComptableBDDEmbarque.getDerniereValeur().equals(vSequenceEcritureComptableExpected.getDerniereValeur());

		assertTrue("Le bean SequenceEcritureComptable attendu n'a pa été trouvé",bResult);
	}
	
	/**
	 * Test de la méthode getSequenceEcritureComptable(String pCodeJournal, Integer pAnnee) avec un couple (journalCode, annee)
	 * qui ne figure pas dans le jeu de données de démos. On s'attend à lever une exception de type NotFoundException.
	 * @throws Exception
	 */
	@Test(expected = NotFoundException.class)
	public void getSequenceEcritureComptableCase2() throws Exception {
		comptabiliteDaoImpl.getSequenceEcritureComptable("AC", 2030);  
	}
	
	/**
	 * Test de la méthode insertSequenceEcritureComptable(String pCodeJournal, SequenceEcritureComptable pSequenceEcritureComptable)
	 * @throws Exception 
	 */
	@Test
	public void insertSequenceEcritureComptable() throws Exception {
		SequenceEcritureComptable vSequenceEcritureComptableExpected=new SequenceEcritureComptable(2018,1);
		comptabiliteDaoImpl.insertSequenceEcritureComptable("AC", vSequenceEcritureComptableExpected); 
		
		SequenceEcritureComptable seqEcritureComptableBDDEmbarque=comptabiliteDaoImpl.getSequenceEcritureComptable("AC", vSequenceEcritureComptableExpected.getAnnee());
		
		boolean bResult=seqEcritureComptableBDDEmbarque.getAnnee().equals(vSequenceEcritureComptableExpected.getAnnee())
				&&seqEcritureComptableBDDEmbarque.getDerniereValeur().equals(vSequenceEcritureComptableExpected.getDerniereValeur());
		
		assertTrue("Erreur lors de l'ajout de la SequenceEcritureComptable en base de données. ",bResult);
	}
	
	/**
	 * Test de la méthode updateSequenceEcritureComptable (String pCodeJournal, SequenceEcritureComptable pSequenceEcritureComptable)
	 * @throws Exception 
	 */
	@Test
	public void updateSequenceEcritureComptable() throws Exception {
		SequenceEcritureComptable vSequenceEcritureComptableExpected=new SequenceEcritureComptable(2016,42);
		comptabiliteDaoImpl.updateSequenceEcritureComptable("VE", vSequenceEcritureComptableExpected);
		
		SequenceEcritureComptable seqEcritureComptableBDDEmbarque=comptabiliteDaoImpl.getSequenceEcritureComptable("VE",vSequenceEcritureComptableExpected.getAnnee());
		
		boolean bResult=seqEcritureComptableBDDEmbarque.getAnnee().equals(vSequenceEcritureComptableExpected.getAnnee())
				&&seqEcritureComptableBDDEmbarque.getDerniereValeur().equals(vSequenceEcritureComptableExpected.getDerniereValeur());
		
		assertTrue("Erreur lors de l'update de la SequenceEcritureComptable en base de données. ",bResult);
	}
}	
