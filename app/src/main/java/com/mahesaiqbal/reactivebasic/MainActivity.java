package com.mahesaiqbal.reactivebasic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mahesaiqbal.reactivebasic.model.Note;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // add to Composite observable
        // .map() operator is used to turn the note into all uppercase letters
        compositeDisposable.add(
                getNotesObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new Function<Note, Note>() {
                            @Override
                            public Note apply(Note note) throws Exception {
                                // Making the note to all uppercase
                                note.setNote(note.getNote().toUpperCase());
                                return note;
                            }
                        })
                        .subscribeWith(getNotesObserver())
        );
    }

    private DisposableObserver<Note> getNotesObserver() {
        return new DisposableObserver<Note>() {

            @Override
            public void onNext(Note note) {
                Log.d(TAG, "Note: " + note.getNote());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "All notes are emitted!");
            }
        };
    }

    private Observable<Note> getNotesObservable() {
        final List<Note> notes = prepareNotes();

        return Observable.create(new ObservableOnSubscribe<Note>() {
            @Override
            public void subscribe(ObservableEmitter<Note> emitter) throws Exception {
                for (Note note: notes) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(note);
                    }
                }

                if (!emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }
        });
    }

    private List<Note> prepareNotes() {
        List<Note> notes = new ArrayList<>();
        notes.add(new Note(1, "Belajar konsep dasar RxJava"));
        notes.add(new Note(2, "Belajar operator dasar RxJava"));
        notes.add(new Note(3, "Belajar tipe interface dasar RxJava"));
        notes.add(new Note(4, "Mulai membuat aplikasi Android dengan RxJava"));

        return notes;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        compositeDisposable.clear();
        Log.d(TAG, "All notes is cleared!");
    }
}
