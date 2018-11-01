package com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dummy.myerp.consumer.ConsumerHelper;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;

/**
 * Classe permettant d'effectuer des tests unitaires sur la classe {@link LigneEcritureComptableRM}
 * @author André Monnier
 *
 */
public class LigneEcritureComptableRMTest {
	
	private LigneEcritureComptableRM ligneEcritureComptableRM =new LigneEcritureComptableRM();
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
		
		when(resultSetMock.getObject("compte_comptable_numero",Integer.class)).thenReturn(666);
		when(resultSetMock.getBigDecimal("credit")).thenReturn(new BigDecimal("50.30"));
		when(resultSetMock.getBigDecimal("debit")).thenReturn(null);
		when(resultSetMock.getString("libelle")).thenReturn("Libellé test");
		
		LigneEcritureComptable vLigneEcritureComptable=ligneEcritureComptableRM.mapRow(resultSetMock, 1);
		
		assertNotNull("Le bean de type LigneEcritureComptable ne doit pas être nul", vLigneEcritureComptable);
		assertEquals("Erreur pour l'objet CompteComptable",vCompteComptable2,vLigneEcritureComptable.getCompteComptable());
		assertEquals("Erreur pour l'attribut credit",new BigDecimal("50.30"),vLigneEcritureComptable.getCredit());
		assertNull("Erreur pour l'attribut debit",vLigneEcritureComptable.getDebit());
		assertEquals("Erreur pour l'attribut libelle","Libellé test",vLigneEcritureComptable.getLibelle());
		
	}
}