package com.nielsmasdorp.speculum.presenters;

import com.nielsmasdorp.speculum.models.reddit.RedditResponse;
import com.nielsmasdorp.speculum.models.yahoo_weather.CurrentWeatherConditions;
import com.nielsmasdorp.speculum.services.GoogleCalendarService;
import com.nielsmasdorp.speculum.services.RedditService;
import com.nielsmasdorp.speculum.util.Constants;
import com.nielsmasdorp.speculum.views.IMainView;
import com.nielsmasdorp.speculum.services.YahooWeatherService;
import com.nielsmasdorp.speculum.views.MainActivity;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Niels on 12/14/2015.
 */
public class MainPresenter {

    YahooWeatherService mYahooWeatherService;
    GoogleCalendarService mGoogleCalendarService;
    RedditService mRedditService;
    IMainView mMainView;

    public MainPresenter(IMainView view) {

        mMainView = view;
        mYahooWeatherService = new YahooWeatherService();
        mRedditService = new RedditService();
        mGoogleCalendarService = new GoogleCalendarService((MainActivity) mMainView);
    }

    public void loadLatestCalendarEvent() {

        Observable<String> observable = mGoogleCalendarService.getLatestCalendarEvent();
        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        mMainView.onError(e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(String event) {

                        mMainView.displayLatestCalendarEvent(event);
                    }
                });
    }


    public void loadWeather(String location, boolean celsius) {

        String query = celsius ? Constants.WEATHER_QUERY_SECOND_CELSIUS : Constants.WEATHER_QUERY_SECOND_FAHRENHEIT;

        Observable<CurrentWeatherConditions> observable = mYahooWeatherService.getApi().getCurrentWeatherConditions(Constants.WEATHER_QUERY_FIRST +
                location + query, Constants.WEATHER_QUERY_FORMAT);
        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<CurrentWeatherConditions>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mMainView.onError(e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(CurrentWeatherConditions conditions) {

                        mMainView.displayCurrentWeather(conditions);
                    }
                });
    }

    public void loadTopRedditPost(String subreddit) {

        Observable<RedditResponse> observable = mRedditService.getApi().getTopRedditPostForSubreddit(subreddit, Constants.REDDIT_LIMIT);
        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<RedditResponse>() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        mMainView.onError(e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(RedditResponse redditResponse) {

                        mMainView.displayTopRedditPost(redditResponse);
                    }
                });
    }
}
