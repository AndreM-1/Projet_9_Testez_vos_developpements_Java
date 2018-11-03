package com.dummy.myerp.consumer.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;
import org.junit.Test;

import com.dummy.myerp.consumer.ConsumerHelper;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.technical.exception.TechnicalException;

/**
 * Classe permettant d'effectuer des tests unitaires sur la classe abstraite {@link AbstractDbConsumer}
 * @author André Monnier
 *
 */
public class AbstractDbConsumerTest {
	
	private DataSource dataSourceMock=mock(DataSource.class);
	
	private DaoProxy daoProxyMock=mock(DaoProxy.class);
	
	//spy permet de tester les méthodes non statiques d'une classe abstraite.
	private AbstractDbConsumer abstractDbConsumer=spy(AbstractDbConsumer.class);

	/**
	 * Test de la méthode configure(Map<DataSourcesEnum, DataSource> pMapDataSource) dans le cas où l'objet Map est null.
	 * On s'attend à lever une TechnicalException.
	 * @throws TechnicalException
	 */
	@Test(expected = TechnicalException.class)
	public void configureCase1() throws TechnicalException {
		AbstractDbConsumer.configure(null);
	}
	
	/**
	 * Test de la méthode configure(Map<DataSourcesEnum, DataSource> pMapDataSource) dans le cas où l'objet Map est vide.
	 * On s'attend à lever une TechnicalException.
	 * @throws TechnicalException
	 */
	@Test(expected = TechnicalException.class)
	public void configureCase2() throws TechnicalException {
		AbstractDbConsumer.configure(new HashMap<DataSourcesEnum, DataSource>());
	}
	
	/**
	 * Test de la méthode configure(Map<DataSourcesEnum, DataSource> pMapDataSource) dans le cas où l'objet Map est renseigné
	 * avec uniquement un couple clé-valeur null.
	 * On s'attend à lever une TechnicalException.
	 * @throws TechnicalException
	 */
	@Test(expected = TechnicalException.class)
	public void configureCase3() throws TechnicalException {
		Map<DataSourcesEnum, DataSource> vMapDataSource=new HashMap<DataSourcesEnum, DataSource>();
		vMapDataSource.put(null, null);
		AbstractDbConsumer.configure(vMapDataSource);
	}
	
	/**
	 * Test de la méthode configure(Map<DataSourcesEnum, DataSource> pMapDataSource) dans le cas où l'objet Map est renseigné
	 * avec uniquement une clé valide mais une valeur null.
	 * On s'attend à lever une TechnicalException.
	 * @throws TechnicalException
	 */
	@Test(expected = TechnicalException.class)
	public void configureCase4() throws TechnicalException {
		Map<DataSourcesEnum, DataSource> vMapDataSource=new HashMap<DataSourcesEnum, DataSource>();
		for(DataSourcesEnum dataSourceEnum:DataSourcesEnum.values()) {
			vMapDataSource.put(dataSourceEnum, null);
		}
		AbstractDbConsumer.configure(vMapDataSource);
	}
	
	/**
	 * Test de la méthode configure(Map<DataSourcesEnum, DataSource> pMapDataSource) dans le cas où l'objet Map est renseigné correctement.
	 */
	@Test
	public void configureCase5() throws TechnicalException {
		Map<DataSourcesEnum, DataSource> vMapDataSource=new HashMap<DataSourcesEnum, DataSource>();
		for(DataSourcesEnum dataSourceEnum:DataSourcesEnum.values()) {
			vMapDataSource.put(dataSourceEnum, dataSourceMock);
		}
		AbstractDbConsumer.configure(vMapDataSource);
		
		for(DataSourcesEnum dataSourceEnum:DataSourcesEnum.values()) {
			assertNotNull("L'objet DataSource n'a pas été initialisé correctement",abstractDbConsumer.getDataSource(dataSourceEnum));
			assertEquals("L'objet DataSource retourné est incorrect",dataSourceMock,abstractDbConsumer.getDataSource(dataSourceEnum));
		}
	}
	
	/**
	 * Test de la méthode getDaoProxy()
	 */
	@Test
	public void getDaoProxy() {
		ConsumerHelper.configure(daoProxyMock);
		assertTrue("Erreur : l'objet retourné n'est pas correct",AbstractDbConsumer.getDaoProxy().equals(daoProxyMock));
	}
}
