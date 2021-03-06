/**
 * ******************************************************************************************
 * Copyright (C) 2015 - Food and Agriculture Organization of the United Nations (FAO).
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,this list
 *       of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice,this list
 *       of conditions and the following disclaimer in the documentation and/or other
 *       materials provided with the distribution.
 *    3. Neither the name of FAO nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.beans.administrative;

import java.math.BigDecimal;
import java.util.*;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractTransactionedBean;
import org.sola.clients.beans.administrative.validation.LeaseValidationGroup;
import org.sola.clients.beans.administrative.validation.MortgageValidationGroup;
import org.sola.clients.beans.administrative.validation.OwnershipValidationGroup;
import org.sola.clients.beans.administrative.validation.SimpleOwnershipValidationGroup;
import org.sola.clients.beans.administrative.validation.TotalShareSize;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.party.PartySummaryBean;
import org.sola.clients.beans.referencedata.LeaseConditionBean;
import org.sola.clients.beans.referencedata.MortgageTypeBean;
import org.sola.clients.beans.referencedata.RrrTypeBean;
import org.sola.clients.beans.referencedata.StatusConstants;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.beans.validation.Localized;
import org.sola.clients.beans.validation.NoDuplicates;
import org.sola.common.messaging.ClientMessage;
import org.sola.webservices.transferobjects.EntityAction;
import org.sola.webservices.transferobjects.administrative.RrrTO;

/**
 * Contains properties and methods to manage <b>RRR</b> object of the domain
 * model. Could be populated from the {@link RrrTO} object.
 */
public class RrrBean extends AbstractTransactionedBean {

    public enum RRR_ACTION {

        NEW, VARY, CANCEL, EDIT, VIEW;
    }
    public static final String CODE_OWNERSHIP = "ownership";
    public static final String CODE_OWNERSHIP_PERPETUITY = "ownershipPerp";
    public static final String CODE_APARTMENT = "apartment";
    public static final String CODE_STATE_OWNERSHIP = "stateOwnership";
    public static final String CODE_MORTGAGE = "mortgage";
    public static final String CODE_LIEN = "recordLien";
    public static final String CODE_AGRI_ACTIVITY = "agriActivity";
    public static final String CODE_COMMON_OWNERSHIP = "commonOwnership";
    public static final String CODE_CUSTOMARY_TYPE = "customaryType";
    public static final String CODE_FIREWOOD = "firewood";
    public static final String CODE_FISHING = "fishing";
    public static final String CODE_GRAZING = "grazing";
    public static final String CODE_LEASE = "lease";
    public static final String CODE_OCCUPATION = "occupation";
    public static final String CODE_OWNERSHIP_ASSUMED = "ownershipAssumed";
    public static final String CODE_SUPERFICIES = "superficies";
    public static final String CODE_TENANCY = "tenancy";
    public static final String CODE_USUFRUCT = "usufruct";
    public static final String CODE_WATERRIGHTS = "waterrights";
    public static final String CODE_ADMIN_PUBLIC_SERVITUDE = "adminPublicServitude";
    public static final String CODE_MONUMENT = "monument";
    public static final String CODE_LIFE_ESTATE = "lifeEstate";
    public static final String CODE_CAVEAT = "caveat";
    public static final String BA_UNIT_ID_PROPERTY = "baUnitId";
    public static final String TYPE_CODE_PROPERTY = "typeCode";
    public static final String RRR_TYPE_PROPERTY = "rrrType";
    public static final String EXPIRATION_DATE_PROPERTY = "expirationDate";
    public static final String SHARE_PROPERTY = "share";
    public static final String AMOUNT_PROPERTY = "amount";
    public static final String MORTGAGE_INTEREST_RATE_PROPERTY = "mortgageInterestRate";
    public static final String MORTGAGE_RANKING_PROPERTY = "mortgageRanking";
    public static final String MORTGAGE_TYPE_CODE_PROPERTY = "mortgageTypeCode";
    public static final String MORTGAGE_TYPE_PROPERTY = "mortgageType";
    public static final String NOTATION_PROPERTY = "notation";
    public static final String IS_PRIMARY_PROPERTY = "isPrimary";
    public static final String FIRST_RIGHTHOLDER_PROPERTY = "firstRightholder";
    public static final String SELECTED_SHARE_PROPERTY = "selectedShare";
    public static final String SELECTED_PROPERTY = "selected";
    public static final String SELECTED_RIGHTHOLDER_PROPERTY = "selectedRightHolder";
    public static final String DUE_DATE_PROPERTY = "dueDate";
    public static final String SELECTED_LEASE_CONDITION_PROPERTY = "selectedLeaseCondition";
    public static final String DEFINED_SHARES = "In defined shares as specified";
    public static final String UNDEVIDED_SHARES = "In undevided shares";
    public static final String JOINT = "Joint Claimants";
    
    
    
    private String baUnitId;
    private String nr;
    @Past(message = ClientMessage.CHECK_REGISTRATION_DATE, payload = Localized.class)
    private Date registrationDate;
    private String transactionId;
//    @NotNull(message = ClientMessage.CHECK_NOTNULL_EXPIRATION, payload = Localized.class, 
//            groups = {MortgageValidationGroup.class, LeaseValidationGroup.class})
    @Future(message = ClientMessage.CHECK_FUTURE_EXPIRATION, payload = Localized.class,
            groups = {MortgageValidationGroup.class})
    private Date expirationDate;
//    @NotNull(message = ClientMessage.CHECK_NOTNULL_MORTGAGEAMOUNT, payload = Localized.class, groups = {MortgageValidationGroup.class})
    private BigDecimal amount;
    private Date dueDate;
//    @NotNull(message = ClientMessage.CHECK_NOTNULL_MORTAGAETYPE, payload = Localized.class, groups = {MortgageValidationGroup.class})
    private MortgageTypeBean mortgageType;
    private BigDecimal mortgageInterestRate;
    private Integer mortgageRanking;
    private Double share;
    private SolaList<SourceBean> sourceList;
    @Valid
    private SolaList<RrrShareBean> rrrShareList;
    private RrrTypeBean rrrType;
    @Valid
    private BaUnitNotationBean notation;
    private boolean primary = false;
    @Valid
    private SolaList<PartySummaryBean> rightHolderList;
    private SolaList<LeaseConditionForRrrBean> leaseConditionList;
    private transient RrrShareBean selectedShare;
    private transient boolean selected;
    private transient PartySummaryBean selectedRightholder;
    private transient LeaseConditionForRrrBean selectedLeaseCondition;
    private String concatenatedName;

    public String getConcatenatedName() {
        return concatenatedName;
    }

    public void setConcatenatedName(String concatenatedName) {
        this.concatenatedName = concatenatedName;
    }

    public RrrBean() {
        super();
        registrationDate = Calendar.getInstance().getTime();
        sourceList = new SolaList();
        rrrShareList = new SolaList();
        rightHolderList = new SolaList();
        leaseConditionList = new SolaList<LeaseConditionForRrrBean>();
        notation = new BaUnitNotationBean();
    }

    public void setFirstRightholder(PartySummaryBean rightholder) {
        if (rightHolderList.size() > 0) {
            rightHolderList.set(0, rightholder);
        } else {
            rightHolderList.add(rightholder);
        }
        propertySupport.firePropertyChange(FIRST_RIGHTHOLDER_PROPERTY, null, rightholder);
    }

    public PartySummaryBean getFirstRightHolder() {
        if (rightHolderList != null && rightHolderList.size() > 0) {
            return rightHolderList.get(0);
        } else {
            return null;
        }
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        boolean oldValue = this.primary;
        this.primary = primary;
        propertySupport.firePropertyChange(IS_PRIMARY_PROPERTY, oldValue, primary);
    }

    public BaUnitNotationBean getNotation() {
        return notation;
    }

    public void setNotation(BaUnitNotationBean notation) {
        this.notation = notation;
        propertySupport.firePropertyChange(NOTATION_PROPERTY, null, notation);
    }

    public String getMortgageTypeCode() {
        if (mortgageType != null) {
            return mortgageType.getCode();
        } else {
            return null;
        }
    }

    public void setMortgageTypeCode(String mortgageTypeCode) {
        String oldValue = null;
        if (mortgageType != null) {
            oldValue = mortgageType.getCode();
        }
        setMortgageType(CacheManager.getBeanByCode(
                CacheManager.getMortgageTypes(), mortgageTypeCode));
        propertySupport.firePropertyChange(MORTGAGE_TYPE_CODE_PROPERTY,
                oldValue, mortgageTypeCode);
    }

    public MortgageTypeBean getMortgageType() {
        return mortgageType;
    }

    public void setMortgageType(MortgageTypeBean mortgageType) {
        if (this.mortgageType == null) {
            this.mortgageType = new MortgageTypeBean();
        }
        this.setJointRefDataBean(this.mortgageType, mortgageType, MORTGAGE_TYPE_PROPERTY);
    }

    public String getBaUnitId() {
        return baUnitId;
    }

    public void setBaUnitId(String baUnitId) {
        String oldValue = this.baUnitId;
        this.baUnitId = baUnitId;
        propertySupport.firePropertyChange(BA_UNIT_ID_PROPERTY, oldValue, baUnitId);
    }

    public String getTypeCode() {
        if (rrrType != null) {
            return rrrType.getCode();
        } else {
            return null;
        }
    }

    public void setTypeCode(String typeCode) {
        String oldValue = null;
        if (rrrType != null) {
            oldValue = rrrType.getCode();
        }
        setRrrType(CacheManager.getBeanByCode(
                CacheManager.getRrrTypes(), typeCode));
        propertySupport.firePropertyChange(TYPE_CODE_PROPERTY, oldValue, typeCode);
    }

    public RrrTypeBean getRrrType() {
        return rrrType;
    }

    public void setRrrType(RrrTypeBean rrrType) {
        if (this.rrrType == null) {
            this.rrrType = new RrrTypeBean();
        }
        this.setJointRefDataBean(this.rrrType, rrrType, RRR_TYPE_PROPERTY);
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date nextDueDate) {
        Date oldValue = this.dueDate;
        this.dueDate = nextDueDate;
        propertySupport.firePropertyChange(DUE_DATE_PROPERTY, oldValue, this.dueDate);
    }

    public BigDecimal getMortgageInterestRate() {
        return mortgageInterestRate;
    }

    public void setMortgageInterestRate(BigDecimal mortgageInterestRate) {
        this.mortgageInterestRate = mortgageInterestRate;
    }

    public Integer getMortgageRanking() {
        return mortgageRanking;
    }

    public void setMortgageRanking(Integer mortgageRanking) {
        this.mortgageRanking = mortgageRanking;
    }

    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Double getShare() {
        return share;
    }

    public void setShare(Double share) {
        this.share = share;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public SolaList<SourceBean> getSourceList() {
        return sourceList;
    }

    @Size(min = 1, message = ClientMessage.CHECK_SIZE_SOURCELIST, payload = Localized.class)
    @NoDuplicates(message = ClientMessage.CHECK_NODUPLICATED_SOURCELIST, payload = Localized.class)
    public ObservableList<SourceBean> getFilteredSourceList() {
        return sourceList.getFilteredList();
    }

    public void setSourceList(SolaList<SourceBean> sourceList) {
        this.sourceList = sourceList;
    }

    public SolaList<RrrShareBean> getRrrShareList() {
        return rrrShareList;
    }

    @Valid
    @Size(min = 1, message = ClientMessage.CHECK_SIZE_RRRSHARELIST, payload = Localized.class, groups = OwnershipValidationGroup.class)
    @TotalShareSize(message = ClientMessage.CHECK_TOTALSHARE_RRRSHARELIST, payload = Localized.class)
    public ObservableList<RrrShareBean> getFilteredRrrShareList() {
        return rrrShareList.getFilteredList();
    }

    public void setRrrShareList(SolaList<RrrShareBean> rrrShareList) {
        this.rrrShareList = rrrShareList;
    }

    public void removeSelectedRrrShare() {
        // Issue #256 Unlink Party from RRR when removing share. 
        if (selectedShare != null && rrrShareList != null) {
            if (getRightHolderList().size() > 0) {
                ListIterator<PartySummaryBean> it = getRightHolderList().listIterator();
                while (it.hasNext()) {
                    getRightHolderList().safeRemove(it.next(), EntityAction.DISASSOCIATE);
                }
            }
            rrrShareList.safeRemove(selectedShare, EntityAction.DELETE);
        }
    }

    public PartySummaryBean getSelectedRightHolder() {
        return selectedRightholder;
    }

    public void setSelectedRightHolder(PartySummaryBean selectedRightholder) {
        this.selectedRightholder = selectedRightholder;
        propertySupport.firePropertyChange(SELECTED_RIGHTHOLDER_PROPERTY, null, this.selectedRightholder);
    }

    public LeaseConditionForRrrBean getSelectedLeaseCondition() {
        return selectedLeaseCondition;
    }

    public void setSelectedLeaseCondition(LeaseConditionForRrrBean selectedLeaseCondition) {
        this.selectedLeaseCondition = selectedLeaseCondition;
        propertySupport.firePropertyChange(SELECTED_LEASE_CONDITION_PROPERTY, null, this.selectedLeaseCondition);
    }

    public SolaList<PartySummaryBean> getRightHolderList() {
        return rightHolderList;
    }

    @Size(min = 1, groups = {MortgageValidationGroup.class}, message = ClientMessage.CHECK_SIZE_RIGHTHOLDERLIST, payload = Localized.class)
    public ObservableList<PartySummaryBean> getFilteredRightHolderList() {
        return rightHolderList.getFilteredList();
    }

    @Size(min = 1, groups = {SimpleOwnershipValidationGroup.class, LeaseValidationGroup.class}, 
            message = ClientMessage.CHECK_SIZE_OWNERSLIST, payload = Localized.class)
    private ObservableList<PartySummaryBean> getFilteredOwnersList() {
        return rightHolderList.getFilteredList();
    }

    public SolaList<LeaseConditionForRrrBean> getLeaseConditionList() {
        return leaseConditionList;
    }

//    @Size(min = 1, groups = {SimpleOwnershipValidationGroup.class, LeaseValidationGroup.class}, 
//            message = ClientMessage.CHECK_SIZE_LEASE_CONDITIONS_LIST, payload = Localized.class)
    public ObservableList<LeaseConditionForRrrBean> getLeaseConditionFilteredList() {
        return leaseConditionList.getFilteredList();
    }
    
    public void setLeaseConditionList(SolaList<LeaseConditionForRrrBean> leaseConditionList) {
        this.leaseConditionList = leaseConditionList;
    }
    
    public ArrayList<LeaseConditionForRrrBean> getLeaseCustomConditions(){
        ArrayList<LeaseConditionForRrrBean> conditions = new ArrayList<LeaseConditionForRrrBean>();
        for(LeaseConditionForRrrBean cond : getLeaseConditionFilteredList()){
            if(cond.isCustomCondition()){
                conditions.add(cond);
            }
        }
        return conditions;
    }
    
    public ArrayList<LeaseConditionForRrrBean> getLeaseStandardConditions(){
        ArrayList<LeaseConditionForRrrBean> conditions = new ArrayList<LeaseConditionForRrrBean>();
        for(LeaseConditionForRrrBean cond : getLeaseConditionFilteredList()){
            if(!cond.isCustomCondition()){
                conditions.add(cond);
            }
        }
        return conditions;
    }
    
    public void setRightHolderList(SolaList<PartySummaryBean> rightHolderList) {
        this.rightHolderList = rightHolderList;
    }

    public RrrShareBean getSelectedShare() {
        return selectedShare;
    }

    public void setSelectedShare(RrrShareBean selectedShare) {
        RrrShareBean oldValue = this.selectedShare;
        this.selectedShare = selectedShare;
        propertySupport.firePropertyChange(SELECTED_SHARE_PROPERTY, oldValue, this.selectedShare);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        boolean oldValue = this.selected;
        this.selected = selected;
        propertySupport.firePropertyChange(SELECTED_PROPERTY, oldValue, this.selected);
    }

    public void removeSelectedRightHolder() {
        if (selectedRightholder != null) {
            getRightHolderList().safeRemove(selectedRightholder, EntityAction.DISASSOCIATE);
        }
    }
    
    /** Removes selected lease condition. */
    public void removeSelectedLeaseCondition() {
        if (selectedLeaseCondition != null) {
            getLeaseConditionList().safeRemove(selectedLeaseCondition, EntityAction.DISASSOCIATE);
        }
    }

    /** 
     * Adds lease conditions in the list 
     * @param leaseConditions List of {@link LeaseConditionBean} that needs to be added in the list
     */
    public void addLeaseConditions(List<LeaseConditionBean> leaseConditions){
        if(leaseConditions == null || getLeaseConditionList() == null){
            return;
        }
        for(LeaseConditionBean cond : leaseConditions){
            addLeaseCondition(cond);
        }
    }
    
    /** 
     * Adds lease condition in the list 
     * @param leaseCondition {@link LeaseConditionForRrrBean} that needs to be added in the list
     */
    public void addLeaseCondition(LeaseConditionForRrrBean leaseCondition){
        if(leaseCondition == null || getLeaseConditionList() == null){
            return;
        }
        if(leaseCondition.isCustomCondition()){
            leaseCondition.setLeaseCondition(null);
        }
        getLeaseConditionList().addAsNew(leaseCondition);
    }
    
    /** 
     * Adds lease condition in the list 
     * @param leaseCondition {@link LeaseConditionBean} that needs to be added in the list.
     * New {@link LeaseConditionForRrrBean} will be created and added in the list.
     */
    public void addLeaseCondition(LeaseConditionBean leaseCondition){
        if(leaseCondition == null || getLeaseConditionList() == null){
            return;
        }
        for(LeaseConditionForRrrBean leaseForRrr : getLeaseConditionList()){
            if(leaseForRrr.getLeaseConditionCode()!=null && 
                    leaseForRrr.getLeaseConditionCode().equals(leaseCondition.getCode())){
                if(leaseForRrr.getEntityAction() == EntityAction.DELETE || leaseForRrr.getEntityAction() == EntityAction.DISASSOCIATE){
                    leaseForRrr.setEntityAction(null);
                }
                return;
            }
        }
        LeaseConditionForRrrBean newLeaseForRrr = new LeaseConditionForRrrBean();
        newLeaseForRrr.setLeaseCondition(leaseCondition);
        getLeaseConditionList().addAsNew(newLeaseForRrr);
    }
    
    public void addOrUpdateRightholder(PartySummaryBean rightholder) {
        if (rightholder != null && rightHolderList != null) {
            if (rightHolderList.contains(rightholder)) {
                rightHolderList.set(rightHolderList.indexOf(rightholder), rightholder);
            } else {
                rightHolderList.addAsNew(rightholder);
            }
        }
    }

    public RrrBean makeCopyByAction(RRR_ACTION rrrAction) {
        RrrBean copy = this;

        if (rrrAction == RrrBean.RRR_ACTION.NEW) {
            copy.setStatusCode(StatusConstants.PENDING);
        }

        if (rrrAction == RRR_ACTION.VARY || rrrAction == RRR_ACTION.CANCEL) {
            // Make a copy of current bean with new ID
            copy = this.copy();
            copy.resetIdAndVerion(true, false);
        }

        if (rrrAction == RRR_ACTION.EDIT) {
            // Make a copy of current bean preserving all data
            copy = this.copy();
        }

        return copy;
    }

    /**
     * Generates new ID, RowVerion and RowID.
     *
     * @param resetChildren If true, will change ID fields also for child
     * objects.
     * @param removeBaUnitId If true, will set <code>BaUnitId</code> to null.
     */
    public void resetIdAndVerion(boolean resetChildren, boolean removeBaUnitId) {
        generateId();
        resetVersion();
        setTransactionId(null);
        setStatusCode(StatusConstants.PENDING);
        if (removeBaUnitId) {
            setBaUnitId(null);
        }
        if (resetChildren) {
            for (RrrShareBean shareBean : getRrrShareList()) {
                shareBean.resetVersion();
                shareBean.setRrrId(getId());
            }
            for(LeaseConditionForRrrBean leaseCondition: getLeaseConditionList()){
                leaseCondition.resetVersion();
            }
            getNotation().generateId();
            getNotation().resetVersion();
            if (removeBaUnitId) {
                getNotation().setBaUnitId(null);
            }
        }
    }
}
