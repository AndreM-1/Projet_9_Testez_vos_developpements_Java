package com.dummy.myerp.model.bean.comptabilite;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Classe permettant d'effectuer des tests unitaires sur le bean {@link CompteComptable}
 * @author André Monnier
 *
 */
public class CompteComptableTest {

	/**
	 * Méthode qui permet de tester la méthode getByNumero(List<? extends CompteComptable> pList, Integer pNumero)
	 */
	@Test
	public void getByNumero() {
		List<CompteComptable> vListCompteComptable=new ArrayList<CompteComptable>();
		CompteComptable vCompteComptable1 = new CompteComptable(404, "CompteComptable 1");
		vListCompteComptable.add(vCompteComptable1);
		CompteComptable vCompteComptable2 = new CompteComptable(666, "CompteComptable 2");
		vListCompteComptable.add(vCompteComptable2);
		CompteComptable vCompteComptable3 = new CompteComptable(800, "CompteComptable 3");
		vListCompteComptable.add(vCompteComptable3);
		CompteComptable vCompteComptable4 = new CompteComptable(15000, "CompteComptable 4");
		vListCompteComptable.add(vCompteComptable4);
	
		//Test des comptes comptables qui sont dans la liste.
		assertTrue(vListCompteComptable.toString(),CompteComptable.getByNumero(vListCompteComptable, 15000).equals(vCompteComptable4));
		assertTrue(vListCompteComptable.toString(),CompteComptable.getByNumero(vListCompteComptable, 800).equals(vCompteComptable3));
		assertTrue(vListCompteComptable.toString(),CompteComptable.getByNumero(vListCompteComptable, 666).equals(vCompteComptable2));
		assertTrue(vListCompteComptable.toString(),CompteComptable.getByNumero(vListCompteComptable, 404).equals(vCompteComptable1));
		
		//Test des comptes comptables qui ne sont pas dans la liste.
		assertNull(vListCompteComptable.toString(),CompteComptable.getByNumero(vListCompteComptable, 300));
		
		//Test d'une liste vide ou nulle.
		assertNull(vListCompteComptable.toString(),CompteComptable.getByNumero(new ArrayList<CompteComptable>(), 404));
		assertNull(vListCompteComptable.toString(),CompteComptable.getByNumero(new ArrayList<CompteComptable>(), 300));
		assertNull(vListCompteComptable.toString(),CompteComptable.getByNumero(null, 15000));
		
		//Test d'un numéro nul
		assertNull(vListCompteComptable.toString(),CompteComptable.getByNumero(vListCompteComptable, null));
		
		//Test d'une liste et d'un numéro nuls
		assertNull(vListCompteComptable.toString(),CompteComptable.getByNumero(null, null));	
	}
}