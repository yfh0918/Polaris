package com.polaris.core.config.value;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.BeanFactory;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class SpringValueRegistry {
  private static Map<BeanFactory, Multimap<String, SpringValue>> registry = Maps.newConcurrentMap();
  private static Object LOCK = new Object();

  public static void register(BeanFactory beanFactory, String key, SpringValue springValue) {
    if (!registry.containsKey(beanFactory)) {
      synchronized (LOCK) {
        if (!registry.containsKey(beanFactory)) {
          registry.put(beanFactory, LinkedListMultimap.<String, SpringValue>create());
        }
      }
    }
    registry.get(beanFactory).put(key, springValue);
  }

  public static Collection<SpringValue> get(BeanFactory beanFactory, String key) {
    Multimap<String, SpringValue> beanFactorySpringValues = registry.get(beanFactory);
    if (beanFactorySpringValues == null) {
      return null;
    }
    return beanFactorySpringValues.get(key);
  }



}
