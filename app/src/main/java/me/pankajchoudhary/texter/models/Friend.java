/*
 * Copyright (c) 2021.
 *
 * This is a part of Texter Project (https://github.com/pannkajj/Texter/)
 */

package me.pankajchoudhary.texter.models;


public class Friend
{
    private long date;

    public Friend()
    {

    }

    public Friend(long date)
    {
        this.date = date;
    }

    public long getDate()
    {
        return date;
    }

    public void setDate(long date)
    {
        this.date = date;
    }
}
