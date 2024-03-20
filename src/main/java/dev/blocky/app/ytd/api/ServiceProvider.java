/**
 * YouTube-Downloader - Downloader for YouTube videos, that extracts audio from the video.
 * Copyright (C) 2024 BlockyDotJar (aka. Dominic R.)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package dev.blocky.app.ytd.api;

import dev.blocky.app.ytd.api.entities.NoEmbed;
import dev.blocky.app.ytd.api.interceptor.ErrorResponseInterceptor;
import dev.blocky.app.ytd.api.services.NoEmbedService;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ServiceProvider
{
    public static <T> T createService(Class<T> clazz)
    {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(100);

        ConnectionPool connectionPool = new ConnectionPool(5, 5, SECONDS);

        ErrorResponseInterceptor errorResponseInterceptor = new ErrorResponseInterceptor();

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(errorResponseInterceptor)
                .retryOnConnectionFailure(true)
                .connectionPool(connectionPool)
                .dispatcher(dispatcher);

        OkHttpClient client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://noembed.com/")
                .client(client)
                .build();

        return retrofit.create(clazz);
    }

    public static NoEmbed getNoEmbed(String url) throws IOException
    {
        NoEmbedService msService = ServiceProvider.createService(NoEmbedService.class);
        Call<NoEmbed> msCall = msService.getEmbed(url);
        Response<NoEmbed> response = msCall.execute();
        return response.body();
    }
}

