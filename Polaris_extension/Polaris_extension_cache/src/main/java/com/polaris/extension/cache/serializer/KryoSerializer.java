package com.polaris.extension.cache.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer<T> implements ObjectSerializer<T> {

    public Kryo getInstance() {
    	Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        //Fix the NPE bug when deserializing Collections.
        ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy())
                .setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());

        return kryo;
    }

    @Override
    public byte[] serialize(T t) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        Kryo kryo = getInstance();
        kryo.writeClassAndObject(output, t);
        output.flush();
        return byteArrayOutputStream.toByteArray();
    }

    @SuppressWarnings("unchecked")
	@Override
    public T deserialize(byte[] data) {
    	ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);
        Kryo kryo = getInstance();
        return (T) kryo.readClassAndObject(input);
    }

    @Override
    public T clone(T object) {
        return getInstance().copy(object);
    }
}