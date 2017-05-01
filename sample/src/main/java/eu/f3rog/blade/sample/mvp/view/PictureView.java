package eu.f3rog.blade.sample.mvp.view;

import android.support.annotation.NonNull;

import blade.mvp.IView;

public interface PictureView extends IView {
    void showProgress();

    void showError();

    void show(@NonNull final String pictureUrl);
}
