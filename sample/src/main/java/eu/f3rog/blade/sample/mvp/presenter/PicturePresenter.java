package eu.f3rog.blade.sample.mvp.presenter;

import blade.mvp.BasePresenter;
import eu.f3rog.blade.sample.mvp.view.PictureView;

public abstract class PicturePresenter extends BasePresenter<PictureView> {
    public abstract void getPicture(final long id);
}
