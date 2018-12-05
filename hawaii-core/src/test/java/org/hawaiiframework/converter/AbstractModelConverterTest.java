package org.hawaiiframework.converter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class AbstractModelConverterTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
        thrown.expect(IllegalArgumentException.class);
        converter.convert(source);
    }

}
