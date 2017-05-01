package eu.f3rog.blade.sample.mvp.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import javax.inject.Inject;

import blade.Blade;
import butterknife.BindView;
import eu.f3rog.blade.sample.R;
import eu.f3rog.blade.sample.mvp.di.component.Component;
import eu.f3rog.blade.sample.mvp.presenter.PicturePresenter;
import eu.f3rog.blade.sample.mvp.view.PictureView;


@Blade
public class ActorAndPictureFragment
        extends ActorFragment
        implements PictureView {
    @BindView(R.id.picture)
    ImageView mPictureView;

    @Inject
    PicturePresenter mPicturePresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mvp_frag_actor_picture, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Component.forApp().inject(this);
        mPicturePresenter.getPicture(mId);
    }

    @Override
    public void showProgress() {
        super.showProgress();
        mPictureView.setImageResource(android.R.drawable.stat_notify_sync);
    }

    @Override
    public void showError() {
        mPictureView.setImageResource(android.R.drawable.stat_notify_error);
    }

    @Override
    public void show(@NonNull String pictureUrl) {
        mPictureView.setImageResource(android.R.drawable.stat_sys_data_bluetooth);
    }
}
