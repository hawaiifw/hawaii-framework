package org.hawaiiframework.logging.logback;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IThrowableProxyConverterTest {

    public static final String STACK_TRACE_ELEMENT = "joepie";
    @Mock
    private IThrowableProxy iThrowableProxy;

    @Mock
    private StackTraceElementProxy stackTraceElementProxy;

    private IThrowableProxyConverter iThrowableProxyConverter;

    @Before
    public void setUp() {
        iThrowableProxyConverter = new IThrowableProxyConverter();
    }

    @Test
    public void thatClassNameAndStackTraceArePresentForANullPointerException() {
        when(stackTraceElementProxy.getSTEAsString()).thenReturn(STACK_TRACE_ELEMENT);
        when(iThrowableProxy.getClassName()).thenReturn(NullPointerException.class.getCanonicalName());
        when(iThrowableProxy.getMessage()).thenReturn(null); //null pointers have null value for message
        when(iThrowableProxy.getStackTraceElementProxyArray()).thenReturn(new StackTraceElementProxy[] {stackTraceElementProxy});
        assertThat(iThrowableProxyConverter.convert(iThrowableProxy),
                is(equalTo(NullPointerException.class.getCanonicalName() + CharacterConstants.NEW_LINE + CharacterConstants.INDENT
                        + STACK_TRACE_ELEMENT)));
    }

    @Test
    public void thatCommonStackFramesAreNotLoggedLikeExceptionPrintStacktrace() {
        final String lineSeparator = System.lineSeparator();

        ExceptionThrower thrower = new ExceptionThrower();
        try {
            thrower.xyz();
        } catch (Throwable t) {
            final StringWriter stringWriter = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(stringWriter);
            t.printStackTrace(printWriter);
            final String expected = stringWriter.toString();

            final ThrowableProxy p = new ThrowableProxy(t);
            final String converted = iThrowableProxyConverter.convert(p);

            assertThat(converted, is(equalTo(expected.substring(0, expected.length() - lineSeparator.length()))));
        }
    }
}
