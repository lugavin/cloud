package com.gavin.cloud.common.base.problem;

/**
 * An extension of the {@link Problem} interface for problems that extend {@link Exception}. Since {@link Exception}
 * is a concrete type any class can only extend one exception type. {@link ThrowableProblem} is one choice, but we
 * don't want to force people to extend from this but choose their own super class. For this they can implement this
 * interface and get the same handling as {@link ThrowableProblem} for free. A common use case would be:
 *
 * <pre>{@code
 * public final class OutOfStockException extends BusinessException implements Exceptional
 * }</pre>
 *
 * @see Problem
 * @see Exception
 */
public interface Exceptional extends Problem {

    Exceptional getCause();

}
