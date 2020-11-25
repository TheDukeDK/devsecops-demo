package at.beeroftheworld.persistence;

public class PersistenceException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public PersistenceException(String message, Throwable innerException) {
		super(message, innerException);
	}
	
	public PersistenceException(String message) {
		super(message);
	}
	
}
