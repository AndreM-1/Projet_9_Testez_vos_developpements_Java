package com.dummy.myerp.consumer.dao.contrat;

import java.util.List;

import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.NotFoundException;


/**
 * Interface de DAO des objets du package Comptabilite
 */
public interface ComptabiliteDao {

	/**
	 * Renvoie la liste des Comptes Comptables
	 * @return {@link List}
	 */
	List<CompteComptable> getListCompteComptable();


	/**
	 * Renvoie la liste des Journaux Comptables
	 * @return {@link List}
	 */
	List<JournalComptable> getListJournalComptable();


	// ==================== EcritureComptable ====================

	/**
	 * Renvoie la liste des Écritures Comptables
	 * @return {@link List}
	 */
	List<EcritureComptable> getListEcritureComptable();

	/**
	 * Renvoie l'Écriture Comptable d'id {@code pId}.
	 *
	 * @param pId l'id de l'écriture comptable
	 * @return {@link EcritureComptable}
	 * @throws NotFoundException : Si l'écriture comptable n'est pas trouvée
	 */
	EcritureComptable getEcritureComptable(Integer pId) throws NotFoundException;

	/**
	 * Renvoie l'Écriture Comptable de référence {@code pRef}.
	 *
	 * @param pReference la référence de l'écriture comptable
	 * @return {@link EcritureComptable}
	 * @throws NotFoundException : Si l'écriture comptable n'est pas trouvée
	 */
	EcritureComptable getEcritureComptableByRef(String pReference) throws NotFoundException;

	/**
	 * Charge la liste des lignes d'écriture de l'écriture comptable {@code pEcritureComptable}
	 *
	 * @param pEcritureComptable -
	 */
	void loadListLigneEcriture(EcritureComptable pEcritureComptable);

	/**
	 * Insert une nouvelle écriture comptable.
	 *
	 * @param pEcritureComptable -
	 */
	void insertEcritureComptable(EcritureComptable pEcritureComptable);

	/**
	 * Met à jour l'écriture comptable.
	 *
	 * @param pEcritureComptable -
	 */
	void updateEcritureComptable(EcritureComptable pEcritureComptable);

	/**
	 * Supprime l'écriture comptable d'id {@code pId}.
	 *
	 * @param pId l'id de l'écriture
	 */
	void deleteEcritureComptable(Integer pId);

	// ========================== Ajout André Monnier ==========================

	/**
	 * Renvoie un bean de type {@link SequenceEcritureComptable} à partir du couple (code journal, année)
	 * @param pCodeJournal : Le code du journal comptable
	 * @param pAnnee : L'année de l'écriture comptable associée au code du journal comptable.
	 * @return {@link SequenceEcritureComptable}
	 * @throws NotFoundException Si la séquence écriture comptable n'a pas été trouvée.
	 */
	SequenceEcritureComptable getSequenceEcritureComptable(String pCodeJournal, Integer pAnnee) throws NotFoundException;

	/**
	 * Met à jour la table MYERP.sequence_ecriture_comptable en ajoutant une nouvelle séquence écriture comptable.
	 * @param pCodeJournal : Le code du journal comptable
	 * @param pSequenceEcritureComptable : Un bean de type {@link SequenceEcritureComptable}
	 */
	void insertSequenceEcritureComptable(String pCodeJournal, SequenceEcritureComptable pSequenceEcritureComptable);

	/**
	 * Met à jour la table MYERP.sequence_ecriture_comptable en incrémentant la colonne derniere_valeur.
	 * @param pCodeJournal : Le code du journal comptable
	 * @param pSequenceEcritureComptable : Un bean de type {@link SequenceEcritureComptable}
	 */
	void updateSequenceEcritureComptable(String pCodeJournal, SequenceEcritureComptable pSequenceEcritureComptable);
}
