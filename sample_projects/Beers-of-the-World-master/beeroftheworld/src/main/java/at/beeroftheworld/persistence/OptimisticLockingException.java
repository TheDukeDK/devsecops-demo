package at.beeroftheworld.persistence;

import org.jcouchdb.document.BaseDocument;

public class OptimisticLockingException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private BaseDocument localDocument;
	private BaseDocument serverDocument;
	
	public OptimisticLockingException(BaseDocument local, BaseDocument server) {
		localDocument = local;
		serverDocument = server;
	}
	
	public BaseDocument getLocalDocument() {
		return localDocument;
	}
	
	public BaseDocument getServerDocument() {
		return serverDocument;
	}
	
}
