package com.atos.genericpattern;

import com.sap.me.common.RouterType;

public class RouterRef {
	private String revision;
	private String router;
	private RouterType routerType;
	private String handle;
	
	public String getHandle() {
		return handle;
	}
	public void setHandle(String handle) {
		this.handle = handle;
	}
	public String getRevision() {
		return revision;
	}
	public void setRevision(String revision) {
		this.revision = revision;
	}
	public String getRouter() {
		return router;
	}
	public void setRouter(String router) {
		this.router = router;
	}
	public RouterType getRouterType() {
		return routerType;
	}
	public void setRouterType(RouterType routerType) {
		this.routerType = routerType;
	}
	
	
}
