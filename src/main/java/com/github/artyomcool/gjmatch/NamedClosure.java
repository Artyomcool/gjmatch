package com.github.artyomcool.gjmatch;

import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.Writable;

import java.io.IOException;
import java.io.Writer;

class NamedClosure<T> extends Closure<T> implements Writable {

    private final Closure<T> closure;
    protected final GString name;

    NamedClosure(GString name, Closure<T> closure) {
        super(closure);
        maximumNumberOfParameters = closure.getMaximumNumberOfParameters();
        this.name = name;
        this.closure = closure;
    }

    @Override
    public Writer writeTo(Writer out) throws IOException {
        out.write(name.toString());
        return out;
    }

    @Override
    public Closure<?> asWritable() {
        return this;
    }

    @Override
    public String toString() {
        return name.toString();
    }

    @Override
    public void setDelegate(Object delegate) {
        closure.setDelegate(delegate);
    }

    @Override
    public Object getDelegate() {
        return closure.getDelegate();
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return closure.getParameterTypes();
    }

    @SuppressWarnings("unused")
    public Object doCall(Object... args) {
        return call(args);
    }

    @Override
    public T call(Object... args) {
        if (args.length == 1 && args[0] instanceof Writer) {
            try {
                ((Writable)asWritable()).writeTo((Writer)args[0]);
            } catch (IOException e) {
                throwRuntimeException(e);
            }
            return null;
        }
        return closure.call(args);
    }

    @Override
    public Closure<T> curry(Object... arguments) {
        return new NamedClosure<>(name, closure.curry(arguments));
    }

}
