package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.TransactionStatus;
import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;


/**
 * Comptabilite manager implementation.
 */
public class ComptabiliteManagerImpl extends AbstractBusinessManager implements ComptabiliteManager {

	// ==================== Attributs ====================


	// ==================== Constructeurs ====================
	/**
	 * Instantiates a new Comptabilite manager.
	 */
	public ComptabiliteManagerImpl() { 
	}


	// ==================== Getters/Setters ====================
	@Override
	public List<CompteComptable> getListCompteComptable() {
		return getDaoProxy().getComptabiliteDao().getListCompteComptable();
	}


	@Override
	public List<JournalComptable> getListJournalComptable() {
		return getDaoProxy().getComptabiliteDao().getListJournalComptable();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EcritureComptable> getListEcritureComptable() {
		return getDaoProxy().getComptabiliteDao().getListEcritureComptable();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void addReference(EcritureComptable pEcritureComptable) {
		// 1. On remonte depuis la persistance la dernière valeur de la séquence du journal pour l'année de l'écriture.
		TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
		Calendar vCalendar=Calendar.getInstance();
		vCalendar.setTime(pEcritureComptable.getDate());
		Integer vAnnee=Integer.valueOf(vCalendar.get(Calendar.YEAR));
		try {
			SequenceEcritureComptable vSeqEcritureComptable=getDaoProxy().getComptabiliteDao()
					.getSequenceEcritureComptable(pEcritureComptable.getJournal().getCode(), vAnnee);

			//2.a S'il y a un enregistrement pour le journal pour l'année concernée, on utilise la dernière valeur + 1.
			Integer vDerniereValeur=vSeqEcritureComptable.getDerniereValeur()+1;
			vSeqEcritureComptable.setDerniereValeur(vDerniereValeur);
			
			//3.a On met à jour la référence de l'écriture avec la référence calculée (RG_Compta_5).
			String vNumeroSequence="";
			if(vDerniereValeur<10000)
				vNumeroSequence+="0";
			if(vDerniereValeur<1000)
				vNumeroSequence+="0";
			if(vDerniereValeur<100)
				vNumeroSequence+="0";
			if(vDerniereValeur<10)
				vNumeroSequence+="0";

			vNumeroSequence+=vDerniereValeur;
			pEcritureComptable.setReference(pEcritureComptable.getJournal().getCode()+"-"+vAnnee+"/"+vNumeroSequence);
			
			//4.a On met à jour la table sequence_ecriture_comptable en incrémentant la colonne derniere_valeur.
			getDaoProxy().getComptabiliteDao().updateSequenceEcritureComptable(pEcritureComptable.getJournal().getCode(), vSeqEcritureComptable);
			getTransactionManager().commitMyERP(vTS);
			vTS = null;

		} catch (NotFoundException e) {
			//2.b S'il n'y a aucun enregistrement pour le journal pour l'année concernée, on utilise le numéro 1.
			Integer vDerniereValeur=1;
			SequenceEcritureComptable vSeqEcritureComptableToAdd= new SequenceEcritureComptable();
			vSeqEcritureComptableToAdd.setAnnee(vAnnee);
			vSeqEcritureComptableToAdd.setDerniereValeur(vDerniereValeur);
			
			//3.b On met à jour la référence de l'écriture avec la référence calculée (RG_Compta_5).
			String vNumeroSequence="0000"+vDerniereValeur;
			pEcritureComptable.setReference(pEcritureComptable.getJournal().getCode()+"-"+vAnnee+"/"+vNumeroSequence);
			
			//4.b On met à jour la table sequence_ecriture_comptable en ajoutant la nouvelle séquence d'écriture comptable.
			getDaoProxy().getComptabiliteDao().insertSequenceEcritureComptable(pEcritureComptable.getJournal().getCode(), vSeqEcritureComptableToAdd);
			getTransactionManager().commitMyERP(vTS);
			vTS = null;
		} finally {
			getTransactionManager().rollbackMyERP(vTS);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void checkEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
		this.checkEcritureComptableUnit(pEcritureComptable);
		this.checkEcritureComptableContext(pEcritureComptable);
	}


	/**
	 * Vérifie que l'Ecriture comptable respecte les règles de gestion unitaires,
	 * c'est à dire indépendemment du contexte (unicité de la référence, exercice comptable non cloturé...)
	 *
	 * @param pEcritureComptable -
	 * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
	 */
	protected void checkEcritureComptableUnit(EcritureComptable pEcritureComptable) throws FunctionalException {
		// ===== Vérification des contraintes unitaires sur les attributs de l'écriture
		Set<ConstraintViolation<EcritureComptable>> vViolations = getConstraintValidator().validate(pEcritureComptable);
		if (!vViolations.isEmpty()) {
			throw new FunctionalException("L'écriture comptable ne respecte pas les règles de gestion.",
					new ConstraintViolationException(
							"L'écriture comptable ne respecte pas les contraintes de validation",
							vViolations));
		}
		// ===== RG_Compta_3 : une écriture comptable doit avoir au moins 2 lignes d'écriture (1 au débit, 1 au crédit)
		int vNbrCredit = 0;
		int vNbrDebit = 0;
		for (LigneEcritureComptable vLigneEcritureComptable : pEcritureComptable.getListLigneEcriture()) {
			if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getCredit(),
					BigDecimal.ZERO)) != 0) {
				vNbrCredit++;
			}
			if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getDebit(),
					BigDecimal.ZERO)) != 0) {
				vNbrDebit++;
			}
		}
		// On test le nombre de lignes car si l'écriture à une seule ligne
		// avec un montant au débit et un montant au crédit ce n'est pas valable
		if (pEcritureComptable.getListLigneEcriture().size() < 2
				|| vNbrCredit < 1
				|| vNbrDebit < 1) {
			throw new FunctionalException(
					"RG_Compta_3 : Une écriture comptable doit contenir au moins deux lignes d'écriture: une au débit et une au crédit.");
		}

		// ===== RG_Compta_2 : Pour qu'une écriture comptable soit valide, elle doit être équilibrée :
		//la somme des montants au crédit des lignes d'écriture doit être égale à la somme des montants au débit.
		if (!pEcritureComptable.isEquilibree()) {
			throw new FunctionalException("RG_Compta_2 : L'écriture comptable n'est pas équilibrée.");
		}

		// ===== RG_Compta_5 : Format et contenu de la référence
		if(!pEcritureComptable.getJournal().getCode().equals(pEcritureComptable.getReference().split("-")[0])) {
			throw new FunctionalException("RG_Compta_5 : Erreur dans la référence au niveau du code journal.");
		}

		Calendar vCalendar=Calendar.getInstance();
		vCalendar.setTime(pEcritureComptable.getDate());

		if(vCalendar.get(Calendar.YEAR)!=Integer.valueOf(pEcritureComptable.getReference().split("-")[1].split("/")[0])) {
			throw new FunctionalException("RG_Compta_5 : Erreur dans la référence au niveau de l'année.");
		}
	}


	/**
	 * Vérifie que l'Ecriture comptable respecte les règles de gestion liées au contexte
	 * (unicité de la référence, année comptable non cloturé...)
	 *
	 * @param pEcritureComptable -
	 * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
	 */
	protected void checkEcritureComptableContext(EcritureComptable pEcritureComptable) throws FunctionalException {
		// ===== RG_Compta_6 : La référence d'une écriture comptable doit être unique
		if (StringUtils.isNoneEmpty(pEcritureComptable.getReference())) { 
			try {
				// Recherche d'une écriture ayant la même référence
				EcritureComptable vECRef = getDaoProxy().getComptabiliteDao().getEcritureComptableByRef(
						pEcritureComptable.getReference());

				// Si l'écriture à vérifier est une nouvelle écriture (id == null),
				// ou si elle ne correspond pas à l'écriture trouvée (id != idECRef),
				// c'est qu'il y a déjà une autre écriture avec la même référence
				if (pEcritureComptable.getId() == null
						|| !pEcritureComptable.getId().equals(vECRef.getId())) {
					throw new FunctionalException("Une autre écriture comptable existe déjà avec la même référence.");
				}
			} catch (NotFoundException vEx) {
				// Dans ce cas, c'est bon, ça veut dire qu'on n'a aucune autre écriture avec la même référence.
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insertEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
		this.checkEcritureComptable(pEcritureComptable);
		TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
		try {
			getDaoProxy().getComptabiliteDao().insertEcritureComptable(pEcritureComptable);
			getTransactionManager().commitMyERP(vTS);
			vTS = null;
		} finally {
			getTransactionManager().rollbackMyERP(vTS);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
		//Correction André Monnier : Avant de mettre à jour l'EcritureComptable en base de données, on vérifie que
		//le bean en question vérifie tous les critères nécessaires.
		this.checkEcritureComptable(pEcritureComptable);
		TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
		try {
			getDaoProxy().getComptabiliteDao().updateEcritureComptable(pEcritureComptable);
			getTransactionManager().commitMyERP(vTS);
			vTS = null;
		} finally {
			getTransactionManager().rollbackMyERP(vTS);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteEcritureComptable(Integer pId) {
		TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
		try {
			getDaoProxy().getComptabiliteDao().deleteEcritureComptable(pId);
			getTransactionManager().commitMyERP(vTS);
			vTS = null;
		} finally {
			getTransactionManager().rollbackMyERP(vTS);
		}
	}
}
