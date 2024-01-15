package org.hawaiiframework.converter;

public class PersonModelConverter extends AbstractModelConverter<PersonInput, PersonInput> {

  public PersonModelConverter(NullListConversionStrategy<PersonInput> strategy) {
    super(PersonInput.class, strategy);
  }
  /**
   * Converts the given source object into the target object.
   *
   * @param source the source object
   * @param target the target object
   */
  @Override
  public void convert(PersonInput source, PersonInput target) {
    target.setName(source.getName());
  }
}
