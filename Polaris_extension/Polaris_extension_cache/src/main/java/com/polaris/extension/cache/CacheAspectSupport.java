package com.polaris.extension.cache;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.springframework.expression.EvaluationContext;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public abstract class CacheAspectSupport {  
    private final ExpressionEvaluator evaluator    = new ExpressionEvaluator();  
    private KeyGenerator              keyGenerator = new SimpleKeyGenerator();  
    protected Cache                   cache;  
  
    /** 
     * Compute the key for the given caching operation. 
     *  
     * @return the generated key, or {@code null} if none can be generated 
     */  
    protected Object generateKey(Cacheable cacheable, CacheOperationContext context) {  
        return context.generateKey(cacheable, ExpressionEvaluator.NO_RESULT);  
    }  
  
    protected CacheOperationContext getOperationContext(Method method, Object[] args, Object target, Class<?> targetClass) {  
        return new CacheOperationContext(method, args, target, targetClass);  
    }  
  
    protected class CacheOperationContext {  
        private final Method         method;  
        private final Object[]       args;  
        private final Object         target;  
        private final Class<?>       targetClass;  
        private final MethodCacheKey methodCacheKey;  
  
        public CacheOperationContext(Method method, Object[] args, Object target, Class<?> targetClass) {  
            this.method = method;  
            this.args = extractArgs(method, args);  
            this.target = target;  
            this.targetClass = targetClass;  
            this.methodCacheKey = new MethodCacheKey(method, targetClass);  
        }  
  
        public Object getTarget() {  
            return this.target;  
        }  
  
        public Method getMethod() {  
            return method;  
        }  
  
        public Object[] getArgs() {  
            return this.args;  
        }  
  
        private Object[] extractArgs(Method method, Object[] args) {  
            if (!method.isVarArgs()) {  
                return args;  
            }  
            Object[] varArgs = ObjectUtils.toObjectArray(args[args.length - 1]);  
            Object[] combinedArgs = new Object[args.length - 1 + varArgs.length];  
            System.arraycopy(args, 0, combinedArgs, 0, args.length - 1);  
            System.arraycopy(varArgs, 0, combinedArgs, args.length - 1, varArgs.length);  
            return combinedArgs;  
        }  
  
        /** 
         * Compute the key for the given caching operation. 
         *  
         * @return the generated key, or {@code null} if none can be generated 
         */  
        public Object generateKey(Cacheable cacheable, Object result) {  
            if (StringUtils.hasText(cacheable.key())) {  
                EvaluationContext evaluationContext = createEvaluationContext(result);  
                return evaluator.key(cacheable.key(), this.methodCacheKey, evaluationContext);  
            }  
            return keyGenerator.generate(this.target, this.method, this.args);  
        }  
  
        private EvaluationContext createEvaluationContext(Object result) {  
            return evaluator.createEvaluationContext(Arrays.asList(new Cache[] { cache }), this.method, this.args, this.target, this.targetClass, result);  
        }  
  
    }  
  
    /** 
     * @param keyGenerator the keyGenerator to set 
     */  
    public void setKeyGenerator(KeyGenerator keyGenerator) {  
        this.keyGenerator = keyGenerator;  
    }  
  
    /** 
     * @param cache the cache to set 
     */  
    public void setCache(Cache cache) {  
        this.cache = cache;  
    }  
  
} 
