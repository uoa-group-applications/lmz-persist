package nz.ac.auckland.lmz

import nz.ac.auckland.lmz.errors.ExpectedErrorException

public class TestException extends ExpectedErrorException {

    public TestException(String message, Map context) {
        super(message, context)
    }

    public TestException(String message, Map context, Throwable cause) {
        super(message, context, cause)
    }
}
