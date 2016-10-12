/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hank.corelib.http.converter;

import com.google.gson.TypeAdapter;
import com.hank.corelib.http.entity.HttpResult;
import com.hank.corelib.http.entity.Result;
import com.hank.corelib.http.exception.ApiException;
import com.hank.corelib.http.exception.ErrorCode;
import com.hank.corelib.http.exception.TokenInvalidException;
import com.hank.corelib.http.exception.TokenNotExistException;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, Object> {

    private final TypeAdapter<T> adapter;

    GsonResponseBodyConverter(TypeAdapter<T> adapter) {
        this.adapter = adapter;
    }

    @Override
    public Object convert(ResponseBody value) throws IOException {
        try {
            HttpResult httpResult = (HttpResult) adapter.fromJson(value.charStream());
            Result result = httpResult.result;
            if(result==null){
                throw new ApiException(ApiException.NULL_RESULT);
            }else if (result.respCode == ErrorCode.TOKEN_NOT_EXIST) {
                throw new TokenNotExistException();
            } else if (result.respCode == ErrorCode.TOKEN_INVALID) {
                throw new TokenInvalidException();
            } else if (!result.success) {
                return new ApiException(httpResult.message);
            } else if (result.success) {
                return result.data;
            }
        } finally {
            value.close();
        }
        return null;
    }
}
