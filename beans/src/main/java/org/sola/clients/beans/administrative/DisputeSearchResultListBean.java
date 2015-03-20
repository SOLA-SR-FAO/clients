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

import java.util.List;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingListBean;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.search.DisputeSearchParamsTO;

/**
 *
 * @author 17R
 */
public class DisputeSearchResultListBean extends AbstractBindingListBean{
    
    public static final String SELECTED_DISPUTE_SEARCH_RESULT_PROPERTY = "selectedDisputeSearchResult";
    private SolaObservableList<DisputeSearchResultBean> disputeSearchResults;
    private DisputeSearchResultBean selectedDisputeSearchResult;
    
    public DisputeSearchResultListBean(){
        super();
    }
    
    public ObservableList<DisputeSearchResultBean> getDisputeSearchResults() {
        if(disputeSearchResults == null){
            disputeSearchResults = new SolaObservableList<DisputeSearchResultBean>();
        }
        return disputeSearchResults;
    }
    
    public DisputeSearchResultBean getSelectedDisputeSearchResult() {
        return selectedDisputeSearchResult;
    }
    public void setSelectedDisputeSearchResult(DisputeSearchResultBean selectedDisputeSearchResult) {
        this.selectedDisputeSearchResult = selectedDisputeSearchResult;
        propertySupport.firePropertyChange(SELECTED_DISPUTE_SEARCH_RESULT_PROPERTY, null, this.selectedDisputeSearchResult);
    }
    
    
    /** Searches Disputes given parameters. */
    public void search(DisputeSearchParamsBean searchParams){
         if(searchParams == null){
            return;
        }
        getDisputeSearchResults().clear();
        DisputeSearchParamsTO searchParamsTO = TypeConverters.BeanToTrasferObject(searchParams, DisputeSearchParamsTO.class);
        TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getSearchService().searchDispute(searchParamsTO), 
                DisputeSearchResultBean.class, (List)getDisputeSearchResults());
    }
}
