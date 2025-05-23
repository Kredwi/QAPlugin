package ru.kredwi.qa.exceptions;

public class QAException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public QAException() {super();};
	public QAException(String message) {super(message);};
}
