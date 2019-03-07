package com.gavin.cloud.common.web.context;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * one-request-per-thread: 一个请求就是一个线程, 每个线程维护各自实例对象的数据.
 * 注意: 因为所有的Servlet容器(如Tomcat)都采用了线程池, 因此, 在请求处理完成后, 需要将ThreadLocal保存的数据清空, 否则可能出现意想不到的情况.
 *
 * @author Gavin Lu
 * @see org.springframework.security.core.context.SecurityContextHolder
 * @see org.springframework.security.core.context.ThreadLocalSecurityContextHolderStrategy
 * @see com.netflix.zuul.context.ContextLifecycleFilter
 */
public final class RequestContext {

    private static final ThreadLocal<RequestContext> contextHolder = new ThreadLocal<>();

    private RequestContext() {
    }

    public static RequestContext getContext() {
        RequestContext context = contextHolder.get();
        if (context == null) {
            context = new RequestContext();
            contextHolder.set(context);
        }
        return context;
    }

    public void clearContext() {
        contextHolder.remove();
    }

    private String token;
    private String clientIP;

    public String getToken() {
        return token;
    }

    public RequestContext setToken(String token) {
        this.token = token;
        return this;
    }

    public String getClientIP() {
        return clientIP;
    }

    public RequestContext setClientIP(String clientIP) {
        this.clientIP = clientIP;
        return this;
    }

    public static void main(String[] args) throws Exception {
        final ExecutorService threadPool = Executors.newCachedThreadPool();
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(50);
        // 循环创建5个线程
        for (int i = 1; i <= 50; i++) {
            final int seq = i;
            threadPool.execute(() -> {
                try {
                    // 等待所有线程就绪
                    startLatch.await();
                    RequestContext.getContext().setToken(Integer.toString(seq));
                    Thread.sleep(TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS));
                    System.out.println("seq => " + seq + ", Token => " + RequestContext.getContext().getToken());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            });
        }
        // 模拟发令枪
        startLatch.countDown();
        System.out.println("即将开始执行...");
        // 等待所有线程执行完毕
        endLatch.await();
        System.out.println("所有线程执行完毕!");
        threadPool.shutdown();
    }

}
