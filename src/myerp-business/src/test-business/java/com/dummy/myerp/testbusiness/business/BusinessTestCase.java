package com.dummy.myerp.testbusiness.business;


import javax.sql.DataSource;

import com.dummy.myerp.business.contrat.BusinessProxy;
import com.dummy.myerp.business.impl.TransactionManager;


/**
 * Classe mère des classes de test d'intégration de la couche Business
 */
public abstract class BusinessTestCase {

    static {
        SpringRegistry.init();
    }

    /** {@link BusinessProxy} */
    private static final BusinessProxy BUSINESS_PROXY = SpringRegistry.getBusinessProxy();
    /** {@link TransactionManager} */
    private static final TransactionManager TRANSACTION_MANAGER = SpringRegistry.getTransactionManager();
    /** {@link DataSource} */
    private static final DataSource DATA_SOURCE_TEST = SpringRegistry.getDataSourceTest();


    // ==================== Constructeurs ====================
    /**
     * Constructeur.
     */
    public BusinessTestCase() {
    }


    // ==================== Getters/Setters ====================
    public static BusinessProxy getBusinessProxy() {
        return BUSINESS_PROXY;
    }

    public static TransactionManager getTransactionManager() {
        return TRANSACTION_MANAGER;
    }

	public static DataSource getDataSourceTest() {
		return DATA_SOURCE_TEST;
	}
}
