package com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

import com.dummy.myerp.model.bean.comptabilite.JournalComptable;

/**
 * Classe permettant d'effectuer des tests unitaires sur la classe {@link JournalComptableRM}
 * @author André Monnier
 *
 */
public class JournalComptableRMTest {

	private JournalComptableRM journalComptableRM=new JournalComptableRM();
	private ResultSet resultSetMock=mock(ResultSet.class);
	
	/**
	 * Test de la méthode mapRow(ResultSet pRS, int pRowNum)
	 * @throws SQLException
	 */
	@Test
	public void mapRow() throws SQLException {
		
		when(resultSetMock.getString("code")).thenReturn("AC123");
		when(resultSetMock.getString("libelle")).thenReturn("JournalComptable 1");
		JournalComptable vJournalComptable = journalComptableRM.mapRow(resultSetMock, 1);
		
		assertNotNull("Le bean de type JournalComptable ne doit pas être nul",vJournalComptable);
		assertTrue("Erreur pour l'attribut numero",vJournalComptable.getCode().equals("AC123"));
		assertTrue("Erreur pour l'attribut libelle",vJournalComptable.getLibelle().equals("JournalComptable 1"));
	}
}
