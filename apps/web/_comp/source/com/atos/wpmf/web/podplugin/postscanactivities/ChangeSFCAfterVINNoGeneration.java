package com.atos.wpmf.web.podplugin.postscanactivities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sap.me.demand.SFCBOHandle;
import com.sap.me.extension.Services;
import com.sap.me.frame.SystemBase;
import com.sap.me.frame.domain.BusinessException;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.productdefinition.InvalidOperationException;
import com.sap.me.productdefinition.OperationKeyData;
import com.sap.me.production.SfcConfiguration;
import com.sap.me.production.SfcIdHistory;
import com.sap.me.production.SfcStateServiceInterface;
import com.sap.me.production.podclient.PodSelectionModelInterface;
import com.sap.me.production.podclient.SfcChangeEvent;
import com.sap.me.production.podclient.SfcChangeListenerInterface;
import com.sap.me.production.podclient.SfcSelection;
import com.sap.me.wpmf.MessageType;
import com.sap.me.wpmf.util.MessageHandler;

public class ChangeSFCAfterVINNoGeneration extends com.sap.me.production.podclient.BasePodPlugin {
	
	
	private static final long serialVersionUID = 1L;
	private String operationRef;
	private String sfcRef;
	private SfcStateServiceInterface sfcStateServiceInterface;
	private final SystemBase dbBase = SystemBase.createSystemBase("jdbc/jts/wipPool");
	
	public ChangeSFCAfterVINNoGeneration(){
		getPodFieldData();
	}

	public void beforeLoad() throws Exception{
		
	    //get the sfc and operation value from pod
		getPodFieldData();
		

	}
	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		super.execute();
		//finding the serialised sfc and firing sfc change event
		sfcStateServiceInterface = (SfcStateServiceInterface) Services.getService("com.sap.me.production", "SfcStateService");
		if(this.sfcRef !=null){
			try {
				SFCBOHandle sfcboHandleForPrevSFC = new SFCBOHandle(this.sfcRef);
				String currSFC = getCurrSfcFromPreviousSFC(sfcboHandleForPrevSFC.getSFC());
				
				if(currSFC!=null && (!"".equals(currSFC)) && (!"null".equals(currSFC))){
					SFCBOHandle sfcboHandleForCurrSFC = new SFCBOHandle(currSFC);
					List<SfcSelection> sfcsToProcess = new ArrayList<SfcSelection>();
					SfcSelection sfcSelect = new SfcSelection();				
					sfcSelect.setInputId(sfcboHandleForCurrSFC.getSFC());
					sfcsToProcess.add(sfcSelect);
					getPodSelectionModel().setSfcs(sfcsToProcess);
					SfcChangeEvent sfcChangeEvent = new SfcChangeEvent(this);
					this.fireEvent(sfcChangeEvent, SfcChangeListenerInterface.class, "processSfcChange");
				}
				
			} catch (BusinessException e) {
				MessageHandler.handle("Invalid SFC"+e.getMessage() , null, MessageType.ERROR);
				e.printStackTrace();
			}
			
		}
	}

	public void closePlugin() {
		
		
	}
	private Connection getConnection(){
		Connection con = null;

		con = dbBase.getDBConnection();

		return con;
	}
	public String getCurrSfcFromPreviousSFC(String sfc) throws BusinessException {

        Connection con = null;

        String serializedSfc = null;

        String query = "SELECT SFC_BO FROM SFC_ID_HISTORY WHERE SFC = ?";

        PreparedStatement ps = null;

        try {

                con = getConnection();

                ps = con.prepareStatement(query);

                ps.setString(1, sfc);

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {

                	serializedSfc = rs.getString("SFC_BO");

                }

        } catch (SQLException e) {

                e.printStackTrace();

        } finally {

                try {

                        if (ps != null) {

                                ps.close();

                        }

                        if (con != null) {

                                con.close();

                        }

                } catch (Exception e2) {

                        e2.printStackTrace();

                }

        }

        return serializedSfc;

}

	
	
	
	
	public void processPluginLoaded() {
		
		
		
	}
	//get all the values that are set in operation pod
	private void getPodFieldData(){
		PodSelectionModelInterface selectionModel = getPodSelectionModel();
		if (selectionModel == null) {
			return;
		}
		List<OperationKeyData> operationList;
		try {
			operationList = selectionModel.getRouterStepsOrOperation();
			//String operationRef = null;
			if (operationList != null && operationList.size()!=0 && operationList.get(0) !=null) {
				operationRef=operationList.get(0).getRef();
			}
			// If operation is null, simply exit.
			if (operationRef == null) {
				return;
			}
			if(selectionModel.getSfcs()!=null){
				this.sfcRef=selectionModel.getSfcs().get(0).getInputId();
				SFCBOHandle sfcboHandle = new SFCBOHandle(CommonMethods.getSite(),this.sfcRef);
				this.sfcRef=sfcboHandle.getValue();
			}
			
		
		} catch (InvalidOperationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (BusinessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
}




