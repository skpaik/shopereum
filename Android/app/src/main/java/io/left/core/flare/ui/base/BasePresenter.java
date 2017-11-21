package io.left.core.flare.ui.base;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.os.Bundle;

import io.left.core.utils.lib.rx.AppSchedulerProvider;
import io.left.core.utils.lib.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Base class that implements the Presenter interface and provides a base implementation for
 * attachView() and detachView(). It also handles keeping a reference to the mvpView that
 * can be accessed from the children classes by calling getMvpView().
 */
public abstract class BasePresenter<V extends MvpView> implements LifecycleObserver, Presenter<V> {

    /**
     * Marks a class as a LifecycleObserver.
     * It does not have any methods, instead,
     * relies process OnLifecycleEvent annotated methods.
     * <p>
     * class TestObserver implements LifecycleObserver {
     *
     * @OnLifecycleEvent(ON_CREATE) void onCreated(LifecycleOwner source) {}
     * @OnLifecycleEvent(ON_ANY) void onAny(LifecycleOwner source, Event event) {}
     * }
     */

    protected String TAG = getClass().getSimpleName();
    private V mMvpView;
    private Bundle stateBundle;

    private SchedulerProvider mSchedulerProvider = null;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    /*
    * Optional method for add  AppSchedulerProvider class in your Activity
    * */
    public void addScheduler() {
        this.mSchedulerProvider = new AppSchedulerProvider();
    }

    public SchedulerProvider getSchedulerProvider() {
        return mSchedulerProvider;
    }

    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    @Override
    public void attachView(V mvpView) {
        mMvpView = mvpView;
//        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void detachView() {
        mMvpView = null;

        if (mCompositeDisposable != null) mCompositeDisposable.dispose();

        mSchedulerProvider = null;
    }

    @Override
    public void attachLifecycle(Lifecycle lifecycle) {
        lifecycle.addObserver(this);
    }

    @Override
    public void detachLifecycle(Lifecycle lifecycle) {
        lifecycle.removeObserver(this);
    }

    public boolean isViewAttached() {
        return mMvpView != null;
    }

    /**
     * get MVP view of the Activity
     *
     * @return
     */
    public V getMvpView() {
        return mMvpView;
    }

    /**
     * get Activity bundle state
     *
     * @return
     */
    public Bundle getStateBundle() {
        return stateBundle == null ?
                stateBundle = new Bundle() : stateBundle;
    }

    public void checkViewAttached() {
        if (!isViewAttached()) throw new MvpViewNotAttachedException();
    }



    public static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("Please call Presenter.attachView(MvpView) before" +
                    " requesting data to the Presenter");
        }
    }

    @Override
    public void onPresenterDestroy() {
        if (stateBundle != null && !stateBundle.isEmpty()) {
            stateBundle.clear();
        }
    }

    @Override
    public void onPresenterCreated() {
        //NO-OP
    }
}

