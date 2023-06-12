package org.hawaiiframework.converter;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThrows;

public class AbstractModelConverterTest {

    @Test
    public void assureThatNullObjectReturnsNull() {
        PersonModelConverter converter = new PersonModelConverter(DefaultNullListConversionStrategies.returnNull(PersonInput.class));
        PersonInput source = null;
        final PersonInput target = converter.convert(source);
        assertThat(target, is(nullValue()));
    }

    @Test
    public void assureThatNulListReturnsNull() {
        PersonModelConverter converter = new PersonModelConverter(DefaultNullListConversionStrategies.returnNull(PersonInput.class));
        List<PersonInput> source = null;
        final List<PersonInput> target = converter.convert(source);
        assertThat(target, is(nullValue()));
    }

    @Test
    public void assureThatNulListReturnsEmptyList() {
        PersonModelConverter converter = new PersonModelConverter(DefaultNullListConversionStrategies.returnEmptyList(PersonInput.class));
        List<PersonInput> source = null;
        final List<PersonInput> target = converter.convert(source);
        assertThat(target, is(not(nullValue())));
        assertThat(target.isEmpty(), is(true));
    }

    @Test
    public void assureThatNulListWillThrowAnException() {
        PersonModelConverter converter = new PersonModelConverter(DefaultNullListConversionStrategies.raiseError(PersonInput.class));
        List<PersonInput> source = null;
        assertThrows(IllegalArgumentException.class, () ->
                converter.convert(source)
        );
    }

}
