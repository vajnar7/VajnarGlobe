package com.vajnar.vajnargnss;


import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public abstract class AsyncTask<Params, Progress, Result>
{
    private final ExecutorService executor;

    private Handler handler;

    private volatile Result result;

    private final FutureTask<Result> postExecuteTask;

    protected AsyncTask()
    {
        postExecuteTask = new FutureTask<>(() -> onPostExecute(result), result);
        executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
    }

    public ExecutorService getExecutor()
    {
        return executor;
    }

    public Handler getHandler()
    {
        if (handler == null) {
            synchronized(AsyncTask.class) {
                handler = new Handler(Looper.getMainLooper());
            }
        }
        return handler;
    }

    public void execute()
    {
        execute(null);
    }

    public void execute(HashMap<String, Params> params)
    {
        onPreExecute();
        executor.execute(() -> {
            result = doInBackground(params);
            getHandler().post(postExecuteTask);
        });
    }

    public void cancel(boolean mayInterruptIfRunning)
    {
        if (executor != null) {
            executor.shutdownNow();
            onCancelled();
        }
    }

    public boolean isCancelled()
    {
        return executor == null || executor.isTerminated() || executor.isShutdown();
    }

    protected void onPreExecute()
    {
        // Override this method whereever you want to perform task before background execution get started
    }

    // used for push progress resport to UI
    protected void publishProgress(Progress value)
    {
        getHandler().post(() -> onProgressUpdate(value));
    }

    protected void onProgressUpdate(Progress value)
    {
        // Override this method whereever you want update a progress result
    }

    protected abstract Result doInBackground(HashMap<String, Params> params);

    protected void onPostExecute(Result result)
    {
    }

    protected void onCancelled() {}

    public Result get() throws ExecutionException, InterruptedException
    {
        return postExecuteTask.get();
    }
}


