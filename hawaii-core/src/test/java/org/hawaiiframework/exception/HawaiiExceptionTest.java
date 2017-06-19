package org.hawaiiframework.exception;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class HawaiiExceptionTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void thatNullExceptionThrowsNullPointerException() throws Exception {
        thrown.expect(NullPointerException.class);
        HawaiiException.getCausingHawaiiException(null);
    }

    @Test
    public void thatFirstLevelHawaiiExceptionIsReturned() throws Exception {
        Throwable cause = new TestApiException();
        Throwable throwable = HawaiiException.getCausingHawaiiException(cause);
        assertEquals("Wrong exception", cause, throwable);
    }

    @Test
    public void thatFirstLevelOtherExceptionIsReturned() throws Exception {
        Throwable cause = new IllegalArgumentException();
        Throwable throwable = HawaiiException.getCausingHawaiiException(cause);
        assertEquals("Wrong exception", cause, throwable);
    }

    @Test
    public void thatContainedApiExceptionIsReturned() throws Exception {
        Throwable cause = new TestApiException();
        Throwable throwable = HawaiiException.getCausingHawaiiException(cause);
        Throwable result = HawaiiException.getCausingHawaiiException(throwable);
        assertEquals("Wrong exception", cause, result);
    }

    @Test
    public void thatContainedApiExceptionInTwoLevelsIsReturned() throws Exception {
        Throwable cause = new TestApiException();
        Throwable throwable = new IllegalStateException(HawaiiException.getCausingHawaiiException(cause));
        Throwable result = HawaiiException.getCausingHawaiiException(throwable);
        assertEquals("Wrong exception", cause, result);
    }

}
