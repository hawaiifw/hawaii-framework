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
        PersonModelConverter converter = new PersonModelConverter(NullListConversionStrategy.RETURN_NULL);
        PersonInput source = null;
        final PersonInput target = converter.convert(source);
        assertThat(target, is(nullValue()));
    }

    @Test
    public void assureThatNulListReturnsNull() {
        PersonModelConverter converter = new PersonModelConverter(NullListConversionStrategy.RETURN_NULL);
        List<PersonInput> source = null;
        final List<PersonInput> target = converter.convert(source);
        assertThat(target, is(nullValue()));
    }

    @Test
    public void assureThatNulListReturnsEmptyList() {
        PersonModelConverter converter = new PersonModelConverter(NullListConversionStrategy.RETURN_EMPTY_LIST);
        List<PersonInput> source = null;
        final List<PersonInput> target = converter.convert(source);
        assertThat(target, is(not(nullValue())));
        assertThat(target.isEmpty(), is(true));
    }

    @Test
    public void assureThatNulListWillThrowAnException() {
        PersonModelConverter converter = new PersonModelConverter(NullListConversionStrategy.RAISE_ERROR);
        List<PersonInput> source = null;
        thrown.expect(IllegalArgumentException.class);
        converter.convert(source);
    }

}
