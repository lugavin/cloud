package com.gavin.cloud.common.base.problem;

/**
 * An extension of the {@link Problem} interface for problems that extend {@link Exception}.
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
