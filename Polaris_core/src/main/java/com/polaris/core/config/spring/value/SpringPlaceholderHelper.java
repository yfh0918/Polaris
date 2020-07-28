package com.polaris.core.config.spring.value;

import java.util.Properties;
import java.util.Set;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.util.StringUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
//import com.polaris.core.config.ConfClient;
import com.polaris.core.util.StringUtil;

/**
 * Placeholder helper functions.
 */
public class SpringPlaceholderHelper {

  private static final String PLACEHOLDER_PREFIX = "${";
  private static final String PLACEHOLDER_SUFFIX = "}";
  private static final String VALUE_SEPARATOR = ":";
  private static final String SIMPLE_PLACEHOLDER_PREFIX = "{";
  private static final String EXPRESSION_PREFIX = "#{";
  private static final String EXPRESSION_SUFFIX = "}";
  private static boolean ignoreUnresolvablePlaceholders = true;
  private static final Logger logger = LoggerFactory.getLogger(SpringPlaceholderHelper.class);

  /**
   * Resolve placeholder property values, e.g.
   * <br />
   * <br />
   * "${somePropertyValue}" -> "the actual property value"
   */
  public static Object resolvePropertyValue(ConfigurableBeanFactory beanFactory, String beanName, String placeholder) {
    // resolve string value
    String strVal = beanFactory.resolveEmbeddedValue(placeholder);

    BeanDefinition bd = (beanFactory.containsBean(beanName) ? beanFactory
        .getMergedBeanDefinition(beanName) : null);

    // resolve expressions like "#{systemProperties.myProp}"
    return evaluateBeanDefinitionString(beanFactory, strVal, bd);
  }

  private static Object evaluateBeanDefinitionString(ConfigurableBeanFactory beanFactory, String value,
      BeanDefinition beanDefinition) {
    if (beanFactory.getBeanExpressionResolver() == null) {
      return value;
    }
    Scope scope = (beanDefinition != null ? beanFactory
        .getRegisteredScope(beanDefinition.getScope()) : null);
    return beanFactory.getBeanExpressionResolver()
        .evaluate(value, new BeanExpressionContext(beanFactory, scope));
  }

  /**
   * Extract keys from placeholder, e.g.
   * <ul>
   * <li>${some.key} => "some.key"</li>
   * <li>${some.key:${some.other.key:100}} => "some.key", "some.other.key"</li>
   * <li>${${some.key}} => "some.key"</li>
   * <li>${${some.key:other.key}} => "some.key"</li>
   * <li>${${some.key}:${another.key}} => "some.key", "another.key"</li>
   * <li>#{new java.text.SimpleDateFormat('${some.key}').parse('${another.key}')} => "some.key", "another.key"</li>
   * </ul>
   */
  public static Set<String> extractPlaceholderKeys(String propertyString) {
    Set<String> placeholderKeys = Sets.newHashSet();

    if (!isNormalizedPlaceholder(propertyString) && !isExpressionWithPlaceholder(propertyString)) {
      return placeholderKeys;
    }

    Stack<String> stack = new Stack<>();
    stack.push(propertyString);

    while (!stack.isEmpty()) {
      String strVal = stack.pop();
      int startIndex = strVal.indexOf(PLACEHOLDER_PREFIX);
      if (startIndex == -1) {
        placeholderKeys.add(strVal);
        continue;
      }
      int endIndex = findPlaceholderEndIndex(strVal, startIndex);
      if (endIndex == -1) {
        // invalid placeholder?
        continue;
      }

      String placeholderCandidate = strVal.substring(startIndex + PLACEHOLDER_PREFIX.length(), endIndex);

      // ${some.key:other.key}
      if (placeholderCandidate.startsWith(PLACEHOLDER_PREFIX)) {
        stack.push(placeholderCandidate);
      } else {
        // some.key:${some.other.key:100}
        int separatorIndex = placeholderCandidate.indexOf(VALUE_SEPARATOR);

        if (separatorIndex == -1) {
          stack.push(placeholderCandidate);
        } else {
          stack.push(placeholderCandidate.substring(0, separatorIndex));
          String defaultValuePart =
              normalizeToPlaceholder(placeholderCandidate.substring(separatorIndex + VALUE_SEPARATOR.length()));
          if (!Strings.isNullOrEmpty(defaultValuePart)) {
            stack.push(defaultValuePart);
          }
        }
      }

      // has remaining part, e.g. ${a}.${b}
      if (endIndex + PLACEHOLDER_SUFFIX.length() < strVal.length() - 1) {
        String remainingPart = normalizeToPlaceholder(strVal.substring(endIndex + PLACEHOLDER_SUFFIX.length()));
        if (!Strings.isNullOrEmpty(remainingPart)) {
          stack.push(remainingPart);
        }
      }
    }

    return placeholderKeys;
  }

  private static boolean isNormalizedPlaceholder(String propertyString) {
    return propertyString.startsWith(PLACEHOLDER_PREFIX) && propertyString.endsWith(PLACEHOLDER_SUFFIX);
  }

  private static boolean isExpressionWithPlaceholder(String propertyString) {
    return propertyString.startsWith(EXPRESSION_PREFIX) && propertyString.endsWith(EXPRESSION_SUFFIX)
        && propertyString.contains(PLACEHOLDER_PREFIX);
  }

  private static String normalizeToPlaceholder(String strVal) {
    int startIndex = strVal.indexOf(PLACEHOLDER_PREFIX);
    if (startIndex == -1) {
      return null;
    }
    int endIndex = strVal.lastIndexOf(PLACEHOLDER_SUFFIX);
    if (endIndex == -1) {
      return null;
    }

    return strVal.substring(startIndex, endIndex + PLACEHOLDER_SUFFIX.length());
  }

  private static int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
    int index = startIndex + PLACEHOLDER_PREFIX.length();
    int withinNestedPlaceholder = 0;
    while (index < buf.length()) {
      if (StringUtils.substringMatch(buf, index, PLACEHOLDER_SUFFIX)) {
        if (withinNestedPlaceholder > 0) {
          withinNestedPlaceholder--;
          index = index + PLACEHOLDER_SUFFIX.length();
        } else {
          return index;
        }
      } else if (StringUtils.substringMatch(buf, index, SIMPLE_PLACEHOLDER_PREFIX)) {
        withinNestedPlaceholder++;
        index = index + SIMPLE_PLACEHOLDER_PREFIX.length();
      } else {
        index++;
      }
    }
    return -1;
  }
  
	public static String parseStringValue(Properties properties,
			String value, Set<String> visitedPlaceholders) {

		StringBuilder result = new StringBuilder(value);

		int startIndex = value.indexOf(PLACEHOLDER_PREFIX);
		while (startIndex != -1) {
			int endIndex = findPlaceholderEndIndex(result, startIndex);
			if (endIndex != -1) {
				String placeholder = result.substring(startIndex + PLACEHOLDER_PREFIX.length(), endIndex);
				String originalPlaceholder = placeholder;
				if (!visitedPlaceholders.add(originalPlaceholder)) {
					throw new IllegalArgumentException(
							"Circular placeholder reference '" + originalPlaceholder + "' in property definitions");
				}
				// Recursive invocation, parsing placeholders contained in the placeholder key.
				placeholder = parseStringValue(properties, placeholder, visitedPlaceholders);
				// Now obtain the value for the fully resolved key...
				String propVal = properties.getProperty(placeholder);
				if (StringUtil.isEmpty(propVal) && VALUE_SEPARATOR != null) {
					int separatorIndex = placeholder.indexOf(VALUE_SEPARATOR);
					if (separatorIndex != -1) {
						String actualPlaceholder = placeholder.substring(0, separatorIndex);
						String defaultValue = placeholder.substring(separatorIndex + VALUE_SEPARATOR.length());
						propVal = properties.getProperty(actualPlaceholder);
						if (StringUtil.isEmpty(propVal)) {
							propVal = defaultValue;
						}
					}
				}
				if (StringUtil.isNotEmpty(propVal)) {
					
					// Recursive invocation, parsing placeholders contained in the
					// previously resolved placeholder value.
					propVal = parseStringValue(properties, propVal,  visitedPlaceholders);
					result.replace(startIndex, endIndex + PLACEHOLDER_SUFFIX.length(), propVal);
					if (logger.isTraceEnabled()) {
						logger.trace("Resolved placeholder '" + placeholder + "'");
					}
					startIndex = result.indexOf(PLACEHOLDER_PREFIX, startIndex + propVal.length());
				}
				else if (ignoreUnresolvablePlaceholders) {
					// Proceed with unprocessed value.
					startIndex = result.indexOf(PLACEHOLDER_PREFIX, endIndex + PLACEHOLDER_SUFFIX.length());
				}
				else {
					throw new IllegalArgumentException("Could not resolve placeholder '" +
							placeholder + "'" + " in value \"" + value + "\"");
				}
				visitedPlaceholders.remove(originalPlaceholder);
			}
			else {
				startIndex = -1;
			}
		}

		return result.toString();
	}
}