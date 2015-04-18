package com.atos.startvalidation;
import java.util.List;
import com.sap.me.productdefinition.OperationKeyData;
import com.sap.me.production.podclient.BasePodPlugin;
import com.sap.me.production.podclient.SfcSelection;
import com.sap.me.wpmf.MessageType;
import com.sap.me.wpmf.util.MessageHandler;

public class StartValidationPlugin extends BasePodPlugin {
	private static final long serialVersionUID = 1L;

	public void execute() throws Exception {
		MessageHandler.clear();
		List<SfcSelection> sfcList = getPodSelectionModel().getResolvedSfcs();
		List<OperationKeyData> oprkeyList = getPodSelectionModel()
				.getRouterStepsOrOperation();
		if (oprkeyList == null) 
		{
			
			MessageHandler.handle("Select an Operation to proceed. ", null,
					MessageType.ERROR);
			throw new Exception("Select an Operation to proceed.");
		}
		if (oprkeyList.size() > 1) 
		{
			MessageHandler.handle(" Select only one Operation to proceed.",
					null, MessageType.ERROR);
			throw new Exception("Select only one Operation to proceed.");
		}
		if (sfcList == null) {
			MessageHandler.handle(" Select atleast one SFC to proceed.", null,
					MessageType.ERROR);
			throw new Exception("Select atleast one SFC to proceed.");
			
		}
		if (sfcList != null && sfcList.size() > 0) {
			if (sfcList.size() > 1) {
				MessageHandler.handle(" Select only one SFC to proceed.", null,
						MessageType.ERROR);
				throw new Exception("Select only one SFC to proceed.");
				
			}
		}
	}
}
