package inf.msc.yawapp.model;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import inf.msc.yawapp.common.GenericCache;

@Module(
        injects = {
                WeatherSearchInteractor.class
        },
        complete = false,
        library = true
)
public class ModelModule {

    @Provides
    @Singleton
    public WeatherSearchInteractor provideWeatherSearchInteractor() {
        return new OpenWeatherMapInteractor();
    }

    @Provides
    @Singleton
    public GenericCache<WeatherData> provideWeatherDataCache() {
        return new GenericCache<WeatherData>();
    }

    @Provides
    @Singleton
    public FavouritesData provideFavouritesData(FavouritesDataImpl favouritesData) {
        return favouritesData;
    }

}
