package com.mfarssac.moviedb.repository.room;
/*
 * Copyright (C) 2017 The Android Open Source Project
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
 *
 * The MovieDB App was created by M a r c  F a r s s a c
 *
 */

import android.arch.persistence.room.TypeConverter;

/**
 * {@link TypeConverter} for String[] to String
 * <p>
 * This stores the genre_ids as a String in the database, but returns it as a String[]
 */
class StringArrayConverter {
    @TypeConverter
    public static String arrayToString(String[] arrayOfStrings) {
        String separatorSeparatedString = null;
        if (arrayOfStrings != null) {
            separatorSeparatedString = arrayOfStrings[0];
            for (int i = 1; i < arrayOfStrings.length; i++)
                separatorSeparatedString += "|" + arrayOfStrings[i];

        }
        return separatorSeparatedString;
    }

    @TypeConverter
    public static String[] stringToArray(String stringOfItems) {
        String[] arrayOfStrings = new String[]{null};
        if (stringOfItems != null) {
                arrayOfStrings = stringOfItems.split("|");
                    }
        return arrayOfStrings;
    }
}