package com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dummy.myerp.consumer.ConsumerHelper;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;

/**
 * Classe permettant d'effectuer des tests unitaires sur la classe {@link EcritureComptableRM}
 * @author André Monnier
 *
 */
public class EcritureComptableRMTest {
	private EcritureComptableRM ecritureComptableRM=new EcritureComptableRM();
	private ResultSet resultSetMock=mock(ResultSet.class);
	private static DaoProxy daoProxyMock=mock(DaoProxy.class);
	private static ComptabiliteDao comptabiliteDaoMock=mock(ComptabiliteDao.class);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception{
		ConsumerHelper.configure(daoProxyMock);
		when(daoProxyMock.getComptabiliteDao()).thenReturn(comptabiliteDaoMock);
	}
	
	/**
	 * Test de la méthode mapRow(ResultSet pRS, int pRowNum)
	 * @throws SQLException
	 */
	@Test
	public void mapRow() throws SQLException {
		
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
		
		when(resultSetMock.getInt("id")).thenReturn(1); 
		when(resultSetMock.getString("reference")).thenReturn("OD-2018/00016");
		
		//On récupère la date d'aujourd'hui en format Calendar que l'on convertit en java.util.Date que l'on convertit en format long
		//permettant ainsi d'instancier un objet de type java.sql.Date
		Date sqlDate=new Date(Calendar.getInstance().getTime().getTime());
		when(resultSetMock.getDate("date")).thenReturn(sqlDate);
		when(resultSetMock.getString("libelle")).thenReturn("Libellé Test");
		when(resultSetMock.getString("journal_code")).thenReturn("VE2"); 
		
		EcritureComptable vEcritureComptable = ecritureComptableRM.mapRow(resultSetMock, 1);
		
		assertNotNull("Le bean de type EcritureComptable ne doit pas être nul", vEcritureComptable);
		assertTrue("Erreur pour l'attribut id",vEcritureComptable.getId()==1);
		assertTrue("Erreur pour l'attribut référence",vEcritureComptable.getReference().equals("OD-2018/00016"));
		assertTrue("Erreur pour l'attribut libelle",vEcritureComptable.getLibelle().equals("Libellé Test"));
		assertTrue("Erreur pour l'attribut date",vEcritureComptable.getDate().equals(sqlDate));
		assertTrue("Erreur pour l'objet JournalComptable",vEcritureComptable.getJournal().equals(vJournalComptable2));
	}
}
