package com.atos.wpmf.web.podplugin.intelligentpod.postscanactivities;

import java.util.Comparator;

import com.sap.me.production.AssembledComponent;

public class CompareAssemblyComp implements Comparator<AssembledComponent>{

	@Override
	public int compare(AssembledComponent o1, AssembledComponent o2) {
		return o1.getAssyId().compareTo(o2.getAssyId());
	}
	
}
