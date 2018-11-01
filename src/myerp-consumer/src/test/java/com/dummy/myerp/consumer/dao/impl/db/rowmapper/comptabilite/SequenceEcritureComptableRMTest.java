package com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;

/**
 * Classe permettant d'effectuer des tests unitaires sur la classe {@link SequenceEcritureComptableRM}
 * @author André Monnier
 *
 */
public class SequenceEcritureComptableRMTest {

	private SequenceEcritureComptableRM sequenceEcritureComptableRM=new SequenceEcritureComptableRM();
	private ResultSet resultSetMock=mock(ResultSet.class);
	
	/**
	 * Test de la méthode mapRow(ResultSet pRS, int pRowNum)
	 * @throws SQLException
	 */
	@Test
	public void mapRow() throws SQLException {
		
		when(resultSetMock.getInt("annee")).thenReturn(2018);
		when(resultSetMock.getInt("derniere_valeur")).thenReturn(60);
		
		SequenceEcritureComptable vSequenceEcritureComptable = sequenceEcritureComptableRM.mapRow(resultSetMock, 1);
		
		assertNotNull("Le bean de type SequenceEcritureComptable ne doit pas être nul",vSequenceEcritureComptable);
		assertTrue("Erreur pour l'attribut annee",vSequenceEcritureComptable.getAnnee()==2018);
		assertTrue("Erreur pour l'attribut derniereValeur",vSequenceEcritureComptable.getDerniereValeur()==60);
	}
}