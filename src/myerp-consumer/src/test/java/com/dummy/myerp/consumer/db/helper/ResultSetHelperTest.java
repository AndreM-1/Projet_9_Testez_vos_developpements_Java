package com.dummy.myerp.consumer.db.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import org.junit.Test;


/**
 * Classe permettant d'effectuer des tests unitaires sur la classe abstraite {@link ResultSetHelper}
 * @author André Monnier
 *
 */
public class ResultSetHelperTest {

	private ResultSet resultSetMock=mock(ResultSet.class);

	/**
	 * Test de la méthode getInteger(ResultSet pRS, String pColName).
	 * @throws SQLException
	 */
	@Test
	public void getIntegerCase1() throws SQLException {
		String vNomColonne="nom_colonne";
		when(resultSetMock.getInt(vNomColonne)).thenReturn(577);
		when(resultSetMock.wasNull()).thenReturn(false);
		assertEquals("Erreur dans la valeur retournée",new Integer(577),ResultSetHelper.getInteger(resultSetMock, vNomColonne));

		when(resultSetMock.getInt(vNomColonne)).thenReturn(0);
		when(resultSetMock.wasNull()).thenReturn(false);
		assertEquals("Erreur dans la valeur retournée",new Integer(0),ResultSetHelper.getInteger(resultSetMock, vNomColonne));

		when(resultSetMock.getInt(vNomColonne)).thenReturn(0);
		when(resultSetMock.wasNull()).thenReturn(true);
		assertNull("La valeur retournée doit être nul",ResultSetHelper.getInteger(resultSetMock, vNomColonne));

		when(resultSetMock.getInt(vNomColonne)).thenReturn(-350);
		when(resultSetMock.wasNull()).thenReturn(false);
		assertEquals("Erreur dans la valeur retournée",new Integer(-350),ResultSetHelper.getInteger(resultSetMock, vNomColonne));

	}

	/**
	 * Test de la méthode getInteger(ResultSet pRS, String pColName).
	 * Une exception de type SQLException est attendue.
	 * @throws SQLException
	 */
	@Test(expected = SQLException.class)
	public void getIntegerCase2() throws SQLException {
		String vNomColonne="nom_colonne";
		when(resultSetMock.getInt(vNomColonne)).thenThrow(SQLException.class);
		ResultSetHelper.getInteger(resultSetMock, vNomColonne);
	}

	/**
	 * Test de la méthode getLong(ResultSet pRS, String pColName).
	 * @throws SQLException
	 */
	@Test
	public void getLongCase1() throws SQLException {
		String vNomColonne="nom_colonne";
		when(resultSetMock.getLong(vNomColonne)).thenReturn(57774569695288L);
		when(resultSetMock.wasNull()).thenReturn(false);
		assertEquals("Erreur dans la valeur retournée",new Long(57774569695288L),ResultSetHelper.getLong(resultSetMock, vNomColonne));

		when(resultSetMock.getLong(vNomColonne)).thenReturn(0L);
		when(resultSetMock.wasNull()).thenReturn(false);
		assertEquals("Erreur dans la valeur retournée",new Long(0),ResultSetHelper.getLong(resultSetMock, vNomColonne));

		when(resultSetMock.getLong(vNomColonne)).thenReturn(0L);
		when(resultSetMock.wasNull()).thenReturn(true);
		assertNull("La valeur retournée doit être nul",ResultSetHelper.getLong(resultSetMock, vNomColonne));

		when(resultSetMock.getLong(vNomColonne)).thenReturn(-35019856L);
		when(resultSetMock.wasNull()).thenReturn(false);
		assertEquals("Erreur dans la valeur retournée",new Long(-35019856L),ResultSetHelper.getLong(resultSetMock, vNomColonne));
	}

	/**
	 * Test de la méthode getLong(ResultSet pRS, String pColName).
	 * Une exception de type SQLException est attendue.
	 * @throws SQLException
	 */
	@Test(expected = SQLException.class)
	public void getLongCase2() throws SQLException{
		String vNomColonne="nom_colonne";
		when(resultSetMock.getLong(vNomColonne)).thenThrow(SQLException.class);
		ResultSetHelper.getLong(resultSetMock, vNomColonne);
	}

	/**
	 * Test de la méthode getDate(ResultSet pRS, String pColName).
	 * @throws SQLException
	 */
	@Test
	public void getDateCase1() throws SQLException{
		//On récupère la date d'aujourd'hui en format Calendar que l'on convertit en java.util.Date que l'on convertit en format long
		//permettant ainsi d'instancier un objet de type java.sql.Date
		Calendar vCalendar=Calendar.getInstance();
		Date sqlDate=new Date(vCalendar.getTime().getTime());
		String vNomColonne="nom_colonne"; 
		
		when(resultSetMock.getDate(vNomColonne)).thenReturn(sqlDate);   

		Calendar vCalendarResult=Calendar.getInstance();
		vCalendarResult.setTime(ResultSetHelper.getDate(resultSetMock, vNomColonne));

		boolean comparaisonDate=vCalendarResult.get(Calendar.YEAR)==vCalendar.get(Calendar.YEAR) 
				&&vCalendarResult.get(Calendar.MONTH)==vCalendar.get(Calendar.MONTH) 
				&&vCalendarResult.get(Calendar.DAY_OF_MONTH)==vCalendar.get(Calendar.DAY_OF_MONTH)
				&&vCalendarResult.get(Calendar.HOUR_OF_DAY)==0
				&&vCalendarResult.get(Calendar.MINUTE)==0
				&&vCalendarResult.get(Calendar.SECOND)==0
				&&vCalendarResult.get(Calendar.MILLISECOND)==0;
			
		assertTrue("Erreur dans la date retournée",comparaisonDate);
	}
	
	/**
	 * Test de la méthode getDate(ResultSet pRS, String pColName).
	 * L'objet retourné doit être nul.
	 * @throws SQLException
	 */
	@Test
	public void getDateCase2() throws SQLException {
		String vNomColonne="nom_colonne";  
		when(resultSetMock.getDate(vNomColonne)).thenReturn(null); 
		assertNull("La valeur retournée doit être nul",ResultSetHelper.getDate(resultSetMock, vNomColonne));
	}
	
	/**
	 * Test de la méthode getDate(ResultSet pRS, String pColName).
	 * Une exception de type SQLException est attendue.
	 * @throws SQLException
	 */
	@Test(expected = SQLException.class)
	public void getDateCase3() throws SQLException {
		String vNomColonne="nom_colonne"; 
		when(resultSetMock.getDate(vNomColonne)).thenThrow(SQLException.class); 
		ResultSetHelper.getDate(resultSetMock, vNomColonne);
	}
}
