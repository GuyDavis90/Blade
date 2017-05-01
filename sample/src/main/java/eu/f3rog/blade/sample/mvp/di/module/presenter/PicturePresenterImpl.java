package eu.f3rog.blade.sample.mvp.di.module.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import blade.State;
import eu.f3rog.blade.sample.mvp.presenter.PicturePresenter;
import eu.f3rog.blade.sample.mvp.view.PictureView;
import rx.Observable;
import rx.SingleSubscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/* package */ final class PicturePresenterImpl extends PicturePresenter {

    @State
    long mActorId;

    @Nullable
    private Subscription mSubscription;
    @Nullable
    private Long mLoadedActorDetail;
    @Nullable
    private String mErrorMessage;

    public PicturePresenterImpl() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
    }

    private void startLoading() {
        mLoadedActorDetail = null;
        mErrorMessage = null;
        mSubscription = Observable
                .just(mActorId)
                .delay(2, TimeUnit.SECONDS)
                .toSingle()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Long>() {
                    @Override
                    public void onSuccess(@NonNull final Long actorDetail) {
                        mLoadedActorDetail = actorDetail;
                        showIn(getView());
                    }

                    @Override
                    public void onError(Throwable error) {
                        mErrorMessage = error.getMessage();
                        showIn(getView());
                    }
                });
    }

    private void showIn(@Nullable final PictureView view) {
        if (view == null) {
            return;
        }

        if (mErrorMessage != null) {
            view.showError();
        } else if (mLoadedActorDetail != null) {
            view.show("bloop");
        } else {
            view.showProgress();
        }
    }

    @Override
    public void onBind(@NonNull PictureView view) {
        super.onBind(view);
        showIn(view);
    }

    @Override
    public void getPicture(final long id) {
        if (mActorId != id) {
            mActorId = id;
            startLoading();
        }
    }
}
