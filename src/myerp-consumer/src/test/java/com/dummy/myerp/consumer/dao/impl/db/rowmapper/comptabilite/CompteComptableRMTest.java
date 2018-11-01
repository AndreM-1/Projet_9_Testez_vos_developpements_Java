package com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

import com.dummy.myerp.model.bean.comptabilite.CompteComptable;

/**
 * Classe permettant d'effectuer des tests unitaires sur la classe {@link CompteComptableRM}
 * @author André Monnier
 *
 */
public class CompteComptableRMTest {

	private CompteComptableRM compteComptableRM=new CompteComptableRM();
	private ResultSet resultSetMock=mock(ResultSet.class);
	
	/**
	 * Test de la méthode mapRow(ResultSet pRS, int pRowNum)
	 * @throws SQLException
	 */
	@Test
	public void mapRow() throws SQLException {
		
		when(resultSetMock.getInt("numero")).thenReturn(666);
		when(resultSetMock.getString("libelle")).thenReturn("CompteComptable 1");
		CompteComptable vCompteComptable = compteComptableRM.mapRow(resultSetMock, 1);
		
		assertNotNull("Le bean de type CompteComptable ne doit pas être nul",vCompteComptable);
		assertTrue("Erreur pour l'attribut numero",vCompteComptable.getNumero()==666);
		assertTrue("Erreur pour l'attribut libelle",vCompteComptable.getLibelle().equals("CompteComptable 1"));
	}
}
