package com.atos.refreshpod;


import com.sap.me.frame.Utils;
import com.sap.me.wpmf.application.ConfiguratorFilterInterface;
import com.sap.me.wpmf.PodConfiguration;
import com.sap.me.wpmf.PodLayoutConfiguration;

/**
 * filter to modify POD Configuration prior to initialization
 */
public class TestConfiguratorFilter implements ConfiguratorFilterInterface {

    /**
     * Method called to allow modification of PodConfiguration object
     * prior to initialization of POD.
     *
     * @param podConf PodConfiguration object
     */
    public void modifyPodConfiguration(PodConfiguration podConf) {
      /*  String podName = podConf.getPodName();
        PodLayoutConfiguration layoutConf = podConf.getLayoutConfiguration();

        // overrides core templateHeader.jsp for this POD
        layoutConf.setHeaderAreaUrl("/com/atos/refreshpod/customHeader.jsp");*/
        
        String podName = podConf.getPodName();

        // optional check to limit to specific POD's
        if (!Utils.isBlank(podName) /*&& podName.equalsIgnoreCase("workCenterPod")*/) {

            PodLayoutConfiguration layoutConf = podConf.getLayoutConfiguration();

            // overrides core templateHeader.jsp for this POD
            layoutConf.setHeaderAreaUrl("/com/atos/refreshpod/customHeader.jsp");

        }
    }
}
