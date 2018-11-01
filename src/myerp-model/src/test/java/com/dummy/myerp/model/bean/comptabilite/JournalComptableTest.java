package com.dummy.myerp.model.bean.comptabilite;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Classe permettant d'effectuer des tests unitaires sur le bean {@link JournalComptable}
 * @author André Monnier
 *
 */
public class JournalComptableTest {

	/**
	 * Méthode qui permet de tester la méthode getByCode(List<? extends JournalComptable> pList, String pCode)
	 */
	@Test
	public void getByCode() {
	 
		List<JournalComptable> vListJournalComptable = new ArrayList<JournalComptable>();
		JournalComptable vJournalComptable1 = new JournalComptable("AC123", "JournalComptable 1");
		vListJournalComptable.add(vJournalComptable1);
		JournalComptable vJournalComptable2 = new JournalComptable("VE2", "JournalComptable 2");
		vListJournalComptable.add(vJournalComptable2);
		JournalComptable vJournalComptable3 = new JournalComptable("Autre", "JournalComptable 3");
		vListJournalComptable.add(vJournalComptable3);
		JournalComptable vJournalComptable4 = new JournalComptable("Teste", "JournalComptable 4");
		vListJournalComptable.add(vJournalComptable4);
	
	    //Test des journaux qui sont dans la liste.
		assertTrue(vListJournalComptable.toString(),JournalComptable.getByCode(vListJournalComptable, "AC123").equals(vJournalComptable1));
		assertTrue(vListJournalComptable.toString(),JournalComptable.getByCode(vListJournalComptable, "Teste").equals(vJournalComptable4));
		assertTrue(vListJournalComptable.toString(),JournalComptable.getByCode(vListJournalComptable, "VE2").equals(vJournalComptable2));
		assertTrue(vListJournalComptable.toString(),JournalComptable.getByCode(vListJournalComptable, "Autre").equals(vJournalComptable3));
		
		//Test des journaux qui ne sont pas dans la liste.
		assertNull(vListJournalComptable.toString(),JournalComptable.getByCode(vListJournalComptable,"ZXTYU")) ; 
		assertNull(vListJournalComptable.toString(),JournalComptable.getByCode(vListJournalComptable,"ac123")) ; 
		
		//Test d'une liste vide ou nulle.
		assertNull(vListJournalComptable.toString(),JournalComptable.getByCode(new ArrayList<JournalComptable>(),"AC123")); 
		assertNull(vListJournalComptable.toString(),JournalComptable.getByCode(new ArrayList<JournalComptable>(),"ZZZZZ")); 
		assertNull(vListJournalComptable.toString(),JournalComptable.getByCode(null,"AC123")); 
		
		//Test d'un code vide ou nulle
		assertNull(vListJournalComptable.toString(),JournalComptable.getByCode(vListJournalComptable, ""));
		assertNull(vListJournalComptable.toString(),JournalComptable.getByCode(vListJournalComptable, null));
		
		//Test d'une liste et d'un code nuls
		assertNull(vListJournalComptable.toString(),JournalComptable.getByCode(null, null));
	}
}