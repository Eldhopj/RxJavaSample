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
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Lesson 1:
 * Introduction to Observables and Observers
 * Lesson 2:
 * Disposables : Use this when there's no point in listening to a reactive data stream if it's no longer needed.
 *              Marking something as disposable makes it easy to remove observers from an observable.
 * Lesson 3:
 * Operators : Make a dataSet into a observable dataSet. Some operators manipulate (Transform) the dataSet
 *      Creating Operators : eg: create(), just(), fromArray(), range(), fromIterable() .......
 *      Filtering Operators : filter(), skip(), skipLast() ect...
 *      Combining Operators : concat(), merge() ect..
 *                            Visit for more detail on Filtering and Combining Operators : https://medium.com/@kevalpatel2106/what-should-you-know-about-rx-operators-54716a16b310
 *      Transforming Operators: map(), flatMap(), switchMap(), buffer(), concatMap()
 *
 */


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.seekBar)
    SeekBar seekBar;

    private Observable<Task> taskObservable;
    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        createAnObservable(); // Observable
        subscribeToObservable(); // Observer
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
                /*  io():Typically this is used for network calls, it Creates and returns a Scheduler intended for IO-bound work. Again it’s bounded like computation.
                    newThread(): Creates a new Thread for each unit of work. This is costly since it creates a separate thread everytime.
                    trampoline(): Useful for queueing operations. This runs the tasks on the current thread. So it’ll run your code after the current task on the thread is complete.
                *   computation(): For processing huge data, bitmap processing et. This should be used for parallel work since the thread pool is bound. I/O operations shouldn’t be done here.*/
                .subscribeOn(Schedulers.io()) // worker thread (background). Anybody who want's to see the result subscribe on this thread
                .filter(new Predicate<Task>() { // Used to filter the result ,NOTE : there are a bunch of different operators to do different jobs
                    @Override
                    public boolean test(Task task) {
                        return task.getPriority() == 1; //Shows only result which has priority 1
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

                disposable.add(d); // Add into disposable
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
        /**If we want to collect disposable out of box, Use Consumer insted of Observer it will return disposable out of the box*/
//       disposable.add(
//               taskObservable.subscribe(new Consumer<Task>() {
//           @Override
//           public void accept(Task task) throws Exception {
//
//           }
//       }));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear(); // Clearing all disposables
    }
//    NOTE :If u are using mvvm use onCleared() method insted of onDestroy
}

