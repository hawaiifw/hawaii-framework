package org.hawaiiframework.async;

import java.util.HashMap;
import java.util.Map;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.NodeId;

/**
 * Extended version of snakeyaml's Constructor class to facilitate mapping custom YAML keys to
 * JavaBean property names.
 *
 * @author Luke Taylor
 */
@SuppressWarnings("PMD")
public class YamlJavaBeanPropertyConstructor extends Constructor {

  private final Map<Class<?>, Map<String, Property>> properties = new HashMap<>();

  private final PropertyUtils propertyUtils = new PropertyUtils();

  /**
   * The constructor.
   *
   * @param theRoot The root class.
   */
  public YamlJavaBeanPropertyConstructor(Class<?> theRoot) {
    super(theRoot, new LoaderOptions());
    this.yamlClassConstructors.put(NodeId.mapping, new CustomPropertyConstructMapping());
  }

  /**
   * The constructor.
   *
   * @param theRoot The root class.
   * @param propertyAliases The aliases.
   */
  public YamlJavaBeanPropertyConstructor(
      Class<?> theRoot, Map<Class<?>, Map<String, String>> propertyAliases) {
    this(theRoot);
    for (var keyValue : propertyAliases.entrySet()) {
      Map<String, String> map = keyValue.getValue();
      if (map != null) {
        for (var aliasKeyValue : map.entrySet()) {
          addPropertyAlias(aliasKeyValue.getKey(), keyValue.getKey(), aliasKeyValue.getValue());
        }
      }
    }
  }

  /**
   * Adds an alias for a JavaBean property name on a particular type. The values of YAML keys with
   * the alias name will be mapped to the JavaBean property.
   *
   * @param alias the alias to map
   * @param type the type of property
   * @param name the property name
   */
  protected final void addPropertyAlias(String alias, Class<?> type, String name) {
    Map<String, Property> typeMap = this.properties.computeIfAbsent(type, k -> new HashMap<>());
    typeMap.put(alias, this.propertyUtils.getProperty(type, name));
  }

  /** Custom {@code ConstructMapping} to resolve properties. */
  private class CustomPropertyConstructMapping extends ConstructMapping {

    @Override
    protected Property getProperty(Class<?> type, String name) {
      Map<String, Property> forType = YamlJavaBeanPropertyConstructor.this.properties.get(type);
      Property property = forType == null ? null : forType.get(name);
      return property == null ? super.getProperty(type, name) : property;
    }
  }
}
