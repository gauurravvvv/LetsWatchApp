package com.example.letsmovie.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.letsmovie.R;
import com.example.letsmovie.adapters.EpisodesAdapter;
import com.example.letsmovie.adapters.ImageSliderAdapter;
import com.example.letsmovie.databinding.ActivityTVShowDetailsBinding;
import com.example.letsmovie.databinding.LayoutEpisodesBottomSheetBinding;
import com.example.letsmovie.models.TVShow;
import com.example.letsmovie.models.TVShowDetails;
import com.example.letsmovie.utilities.TempDataHolder;
import com.example.letsmovie.viewmodels.TVShowDetailsViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Locale;
import java.util.ResourceBundle;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TVShowDetailsActivity extends AppCompatActivity {

    private ActivityTVShowDetailsBinding activityTVShowDetailsBinding;
    private TVShowDetailsViewModel tvShowDetailsViewModel;
    private BottomSheetDialog episodesBottomSheetDialog;
    private LayoutEpisodesBottomSheetBinding layoutEpisodesBottomSheetBinding;
    private TVShow tvShow;
    private Boolean isTVShowAvailableInWatchlist = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTVShowDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_t_v_show_details);
        doInitialization();

    }

    private void doInitialization() {
        tvShowDetailsViewModel = new ViewModelProvider(this).get(TVShowDetailsViewModel.class);
        activityTVShowDetailsBinding.imageBack.setOnClickListener(v -> onBackPressed());
        tvShow = (TVShow) getIntent().getSerializableExtra("tvShow");
        checkTVShowInWatchlist();
        getTVShowDetails();
    }

    private void checkTVShowInWatchlist(){
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(tvShowDetailsViewModel.getTVShowFromWatchlist(String.valueOf(tvShow.getId()))
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(tvShow -> {
            isTVShowAvailableInWatchlist = true;
            activityTVShowDetailsBinding.imageWatchlist.setImageResource(R.drawable.ic_add);
            compositeDisposable.dispose();
        }));

    }

    private void getTVShowDetails() {
        activityTVShowDetailsBinding.setIsLoading(true);
        String tvShowId = String.valueOf(tvShow.getId());
        tvShowDetailsViewModel.getTVShowDetails(tvShowId).observe(
                this, tvShowDetailsResponse -> {
                    activityTVShowDetailsBinding.setIsLoading(false);
                    if (tvShowDetailsResponse.getTvShowDetails() != null) {
                        if (tvShowDetailsResponse.getTvShowDetails().getPictures() != null) {
                            loadImageSlider(tvShowDetailsResponse.getTvShowDetails().getPictures());
                        }

                        activityTVShowDetailsBinding.setTvShowImageURL(
                                tvShowDetailsResponse.getTvShowDetails().getImagePath()
                        );
                        activityTVShowDetailsBinding.imageTVShow.setVisibility(View.VISIBLE);
                        activityTVShowDetailsBinding.setDescription(
                                String.valueOf(HtmlCompat.fromHtml(tvShowDetailsResponse.getTvShowDetails().getDescription(),
                                        HtmlCompat.FROM_HTML_MODE_LEGACY))
                        );
                        activityTVShowDetailsBinding.textDescription.setVisibility(View.VISIBLE);
                        activityTVShowDetailsBinding.textReadMore.setVisibility(View.VISIBLE);
                        activityTVShowDetailsBinding.textReadMore.setOnClickListener(v -> {
                            if (activityTVShowDetailsBinding.textReadMore.getText().toString().equals("Read More")) {
                                activityTVShowDetailsBinding.textDescription.setMaxLines(Integer.MAX_VALUE);
                                activityTVShowDetailsBinding.textDescription.setEllipsize(null);
                                activityTVShowDetailsBinding.textReadMore.setText(R.string.show_less);
                            } else {
                                activityTVShowDetailsBinding.textDescription.setMaxLines(4);
                                activityTVShowDetailsBinding.textDescription.setEllipsize(TextUtils.TruncateAt.END);
                                activityTVShowDetailsBinding.textReadMore.setText(R.string.read_more);
                            }
                        });

                        activityTVShowDetailsBinding.setRating(String.format(Locale.getDefault(), "%.2f",
                                Double.parseDouble(tvShowDetailsResponse.getTvShowDetails().getRating())
                                )
                        );
                        if (tvShowDetailsResponse.getTvShowDetails().getGenres() != null) {
                            activityTVShowDetailsBinding.setGenre(tvShowDetailsResponse.getTvShowDetails().getGenres()[0]);

                        } else {
                            activityTVShowDetailsBinding.setGenre("N/A");
                        }

                        activityTVShowDetailsBinding.setRuntime(tvShowDetailsResponse.getTvShowDetails().getRunTime() + " Min");
                        activityTVShowDetailsBinding.viewDivider1.setVisibility(View.VISIBLE);
                        activityTVShowDetailsBinding.layoutMisc.setVisibility(View.VISIBLE);
                        activityTVShowDetailsBinding.viewDivider2.setVisibility(View.VISIBLE);

                        activityTVShowDetailsBinding.buttonWebsite.setOnClickListener(v -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(tvShowDetailsResponse.getTvShowDetails().getUrl()));
                            startActivity(intent);
                        });
                        activityTVShowDetailsBinding.buttonWebsite.setVisibility(View.VISIBLE);
                        activityTVShowDetailsBinding.buttonEpisodes.setVisibility(View.VISIBLE);
                        activityTVShowDetailsBinding.buttonEpisodes.setOnClickListener(v -> {
                            if (episodesBottomSheetDialog == null) {
                                episodesBottomSheetDialog = new BottomSheetDialog(TVShowDetailsActivity.this);
                                layoutEpisodesBottomSheetBinding = DataBindingUtil.inflate(
                                        LayoutInflater.from(TVShowDetailsActivity.this),
                                        R.layout.layout_episodes_bottom_sheet,
                                        findViewById(R.id.episodesContainer),
                                        false
                                );
                                episodesBottomSheetDialog.setContentView(layoutEpisodesBottomSheetBinding.getRoot());
                                layoutEpisodesBottomSheetBinding.episodesRecyclerView.setAdapter(
                                        new EpisodesAdapter(tvShowDetailsResponse.getTvShowDetails().getEpisodes())
                                );
                                layoutEpisodesBottomSheetBinding.textTitle.setText(
                                        String.format("Episodes | %s", tvShow.getName())
                                );
                                layoutEpisodesBottomSheetBinding.imageClose.setOnClickListener(v1 -> episodesBottomSheetDialog.dismiss());
                            }
                            FrameLayout frameLayout = episodesBottomSheetDialog.findViewById(
                                    com.google.android.material.R.id.design_bottom_sheet
                            );
                            if (frameLayout == null) {
                                BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
                                bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }

                            episodesBottomSheetDialog.show();
                        });

                        activityTVShowDetailsBinding.imageWatchlist.setOnClickListener(v ->

                        {
                            CompositeDisposable compositeDisposable = new CompositeDisposable();
                            if(isTVShowAvailableInWatchlist){
                                compositeDisposable.add(tvShowDetailsViewModel.removeTVShowFromWatchlist(tvShow)
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() ->
                                {
                                    isTVShowAvailableInWatchlist = false;
                                    TempDataHolder.IS_WATCHLIST_UPDATED = true;
                                    activityTVShowDetailsBinding.imageWatchlist.setImageResource(R.drawable.ic_eye);
                                    Toast.makeText(getApplicationContext(), "Removed From Watchlist!", Toast.LENGTH_SHORT).show();
                                    compositeDisposable.dispose();
                                }));

                            }else{
                                compositeDisposable.add(tvShowDetailsViewModel.addToWatchList(tvShow)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                            TempDataHolder.IS_WATCHLIST_UPDATED = true;
                                            activityTVShowDetailsBinding.imageWatchlist.setImageResource(R.drawable.ic_add);
                                            Toast.makeText(TVShowDetailsActivity.this, "Added To Watchlist", Toast.LENGTH_SHORT).show();
                                            compositeDisposable.dispose();
                                        })
                                );
                            }
                        });
                        activityTVShowDetailsBinding.imageWatchlist.setVisibility(View.VISIBLE);
                        loadBasicTVShowDetails();
                    }
                }
        );
    }

    private void loadImageSlider(String[] sliderImages) {
        activityTVShowDetailsBinding.sliderViewPager.setOffscreenPageLimit(1);
        activityTVShowDetailsBinding.sliderViewPager.setAdapter(new ImageSliderAdapter(sliderImages));
        activityTVShowDetailsBinding.sliderViewPager.setVisibility(View.VISIBLE);
        activityTVShowDetailsBinding.viewFadingEdge.setVisibility(View.VISIBLE);
        setUpSliderIndicator(sliderImages.length);
        activityTVShowDetailsBinding.sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentSliderIndicator(position);
            }
        });
    }

    private void setUpSliderIndicator(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.background_slider_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            activityTVShowDetailsBinding.layoutSliderIndicator.addView(indicators[i]);
        }
        activityTVShowDetailsBinding.layoutSliderIndicator.setVisibility(View.VISIBLE);
        setCurrentSliderIndicator(0);
    }

    private void setCurrentSliderIndicator(int position) {
        int childCount = activityTVShowDetailsBinding.layoutSliderIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) activityTVShowDetailsBinding.layoutSliderIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator)
                );
            } else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator_inactive)
                );
            }
        }
    }

    private void loadBasicTVShowDetails() {
        activityTVShowDetailsBinding.setTvShowName(tvShow.getName());
        activityTVShowDetailsBinding.setNetworkCountry(
                tvShow.getNetwork() + "(" +
                        tvShow.getCountry() + ")");
        activityTVShowDetailsBinding.setStatus(tvShow.getStatus());
        activityTVShowDetailsBinding.setStartedDate(tvShow.getStartDate());
        activityTVShowDetailsBinding.textName.setVisibility(View.VISIBLE);
        activityTVShowDetailsBinding.textNetworkCountry.setVisibility(View.VISIBLE);
        activityTVShowDetailsBinding.textStatus.setVisibility(View.VISIBLE);
        activityTVShowDetailsBinding.textStarted.setVisibility(View.VISIBLE);

    }

}