package cc.fyre.proton.command.processor;

@FunctionalInterface
public interface Processor<T, R> {

    R process(T var1);

}