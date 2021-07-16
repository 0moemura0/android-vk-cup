package com.omoemurao.android_vk_cup.news

import rx.Observable;
import rx.subjects.PublishSubject;

object RxBus {

    private val bus: PublishSubject<Any> = PublishSubject.create()
//    private static final BehaviorSubject<Object> bus = BehaviorSubject.create();


    // If multiple threads are going to emit events to this
    // then it must be made thread-safe like this instead
//    private final Subject<Object, Object> _bus = new SerializedSubject<>(PublishSubject.create());

    //    private static final BehaviorSubject<Object> bus = BehaviorSubject.create();
    // If multiple threads are going to emit events to this
    // then it must be made thread-safe like this instead
    //    private final Subject<Object, Object> _bus = new SerializedSubject<>(PublishSubject.create());
    fun send(o: Any?) {
        bus.onNext(o)
    }

    fun toObserverable(): Observable<Any> {
        return bus
    }

    fun hasObservers(): Boolean {
        return bus.hasObservers()
    }

}