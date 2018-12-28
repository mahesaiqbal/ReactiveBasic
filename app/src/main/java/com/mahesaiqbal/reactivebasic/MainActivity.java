package com.mahesaiqbal.reactivebasic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mahesaiqbal.reactivebasic.model.Note;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Flowable;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Disposable disposable;

    /**
     * Simple example of Flowable just to show the syntax
     * the use of Flowable is best explained when used with BackPressure
     * Read the below link to know the best use cases to use Flowable operator
     * https://github.com/ReactiveX/RxJava/wiki/What%27s-different-in-2.0#when-to-use-flowable
     * -
     * Flowable : SingleObserver
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Flowable<Integer> flowableObservable = getFlowableObservable();

        SingleObserver<Integer> observer = getFlowableObserver();

        flowableObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .reduce(0, new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer result, Integer number) throws Exception {
                        //Log.e(TAG, "Result: " + result + ", new number: " + number);
                        return result + number;
                    }
                }).subscribe(observer);
    }

    private SingleObserver<Integer> getFlowableObserver() {
        return new SingleObserver<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
                disposable = d;
            }

            @Override
            public void onSuccess(Integer integer) {
                Log.d(TAG, "onSuccess: " + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }
        };
    }

    private Flowable<Integer> getFlowableObservable() {
        return Flowable.range(1, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
