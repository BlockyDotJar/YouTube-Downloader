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
package dev.blocky.app.ytd.api.interceptor;

import dev.blocky.app.ytd.api.exceptions.BadRequest;
import dev.blocky.app.ytd.api.exceptions.HTTPException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;

public class ErrorResponseInterceptor implements Interceptor
{
    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Request request = chain.request();
        Response response = chain.proceed(request);

        String body = response.peekBody(Long.MAX_VALUE).string();

        JSONObject json = new JSONObject(body);

        if (!response.isSuccessful())
        {
            String url = json.getString("url");
            String error = json.getString("error");

            if (response.code() == 400)
            {
                throw new BadRequest(url);
            }

            throw new HTTPException(STR."\{error}, \{url}");
        }
        return response;
    }
}
