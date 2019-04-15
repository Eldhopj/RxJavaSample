package com.example.rxjavasample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Commit 1:
 * Introduction to Observables and Observers
 * 
 *
 */


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.seekBar)
    SeekBar seekBar;

    Observable<Task> taskObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        createAnObservable();
        subscribeToObservable();
    }

    private void createAnObservable() {
        /**
         1.Create an Observable
         2.Apply an operator to the Observable
         3.Designate what thread to do the work on and what thread to emit the results to
         4.Subscribe an Observer to the Observable and view the results
         */
        taskObservable = Observable // NOTE : eventhow its a List<Task> we give only its dataType <Task>
                .fromIterable(DataSource.createTasksList()) // Pass a data set and turn it into observable
                .subscribeOn(Schedulers.io()) // worker thread (background). Anybody who want's to see the result subscribe on this thread
                .filter(new Predicate<Task>() { // Another background operator doing different job ,NOTE : there are a bunch of different operators to do different jobs
                    @Override
                    public boolean test(Task task) throws Exception {
                        for (int i = 0; i <5 ; i++) {
                            Thread.sleep(1000);
                            Log.d(TAG, "Filter Background Thread: "+ i);
                        }
                        return task.isComplete(); // It execute when ever a task completed
                    }
                })
                .observeOn(AndroidSchedulers.mainThread()); // Shows results on observer thread (main thread)
    }

    private void subscribeToObservable() {
        /**This observer will be notified if the data set changes.*/

        taskObservable.subscribe(new Observer<Task>() {
            //The onSubscribe() will call as soon as observable is subscribed to
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            //The onNext() method will run on the main thread so the task objects can be set to the UI there
            @Override
            public void onNext(Task task) {
                Log.d(TAG, "Name of the thread which method called: " + Thread.currentThread().getName());
                Log.d(TAG, "onNext : " + task.getDescription());
            }

            @Override
            public void onError(Throwable e) {

            }

            //The onComplete() called when the task is fully completed
            @Override
            public void onComplete() {

            }
        });
    }

}

