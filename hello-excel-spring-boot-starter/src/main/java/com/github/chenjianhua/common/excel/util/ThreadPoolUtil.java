package com.github.chenjianhua.common.excel.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chenjianhua
 * @date 2020/6/9
 */
@Slf4j
public class ThreadPoolUtil {
    /**
     * 核心线程用完以后，多余的线程任务会先放到任务队列中
     * 当任务队列满了以后，再有任务才会去创建非核心线程来运行线程任务。
     */
    public static class KtExportDiscardPolicy implements RejectedExecutionHandler {
        public KtExportDiscardPolicy() {
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            log.error("rejectedExecution 导出线程池队列已满");
        }
    }

    public static class KtExportNameThreadFactory implements ThreadFactory {

        private final String poolName;
        private AtomicInteger count = new AtomicInteger(1);

        public KtExportNameThreadFactory(String poolName) {
            this.poolName = poolName;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, poolName + "excel-thread-" + count.getAndIncrement());
            //设置为非守护线程
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            return t;
        }
    }

    private volatile static ExecutorService threadPool;

    public static ExecutorService getInstance() {
        if (null == threadPool) {
            synchronized (ThreadPoolUtil.class) {
                if (null == threadPool) {
                    ThreadFactory namedThreadFactory = new KtExportNameThreadFactory("export-threadPool-");
                    threadPool = new ThreadPoolExecutor(5, 10,
                            0, TimeUnit.SECONDS,
                            new ArrayBlockingQueue<>(256),
                            namedThreadFactory,
                            new KtExportDiscardPolicy());
                    log.info("导出线程池初始化完成");
                }
            }
        }
        return threadPool;
    }
}
